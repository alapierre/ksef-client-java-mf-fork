package io.alapierre.ksef.batch;

import io.alapierre.ksef.batch.model.*;
import org.junit.Test;
import pl.akmf.ksef.sdk.api.builders.batch.OpenBatchSessionRequestBuilder;
import pl.akmf.ksef.sdk.client.interfaces.CryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.session.*;
import pl.akmf.ksef.sdk.client.model.session.batch.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class BatchValidationTest {
    private static final String HASH = Base64.getEncoder().encodeToString(new byte[32]);
    private static final FormCode FORM = new FormCode(SystemCode.FA_3, SchemaVersion.VERSION_1_0E, SessionValue.FA);

    @Test
    public void builderChecksFileAndPartBoundaries() {
        for (long size : new long[]{0, -1, 5_000_000_001L}) {
            rejects(() -> builder().withBatchFile(size, HASH));
        }
        for (String hash : new String[]{null, "", "not-base64", "A".repeat(44)}) {
            rejects(() -> builder().withBatchFile(1, hash));
            rejects(() -> builder().addBatchFilePart(1, 1, hash));
        }
        rejects(() -> builder().addBatchFilePart(0, 1, HASH));
        rejects(() -> builder().addBatchFilePart(1, 0, HASH));
        rejects(() -> builder().withBatchFile(1, HASH).build());
        rejects(() -> builder().addBatchFilePart(1, 1, HASH).build());
        var builder = builder().withBatchFile(5_000_000_000L, HASH);
        for (int i = 1; i <= 50; i++) builder.addBatchFilePart(i, 1, HASH);
        assertEquals(50, builder.build().getBatchFile().getFileParts().size());
        rejects(() -> builder.addBatchFilePart(51, 1, HASH));
        assertEquals(50, builder.build().getBatchFile().getFileParts().size());
        assertEquals(1, builder().withBatchFile(1, HASH).addBatchFilePart(1, 1, HASH).build().getBatchFile().getFileSize());
    }

    @Test
    public void helperRejectsNonPositivePartSizeBeforeReadingInvoices() {
        InvoiceSource source = () -> { throw new AssertionError("Source must not be read"); };
        for (int size : new int[]{0, -1}) {
            rejects(() -> new BatchHelper(null, null).prepareBatch(source, new BatchConfig(Path.of("unused"), size, true)));
        }
    }

    @Test
    public void helperRejectsTooManyPartsBeforeCryptographyAndCleansZip() throws Exception {
        assertRejectedZipSize(51, 1);
        assertRejectedZipSize(5_000_000_001L, 100_000_000);
    }

    private void assertRejectedZipSize(long size, int partSize) throws Exception {
        Path zip = Files.createTempFile("batch-validation-", ".zip");
        Path output = Files.createTempDirectory("batch-validation-parts-");
        try {
            // Sparse file: exercises the 5 GB boundary without allocating or reading 5 GB.
            try (RandomAccessFile file = new RandomAccessFile(zip.toFile(), "rw")) { file.setLength(size); }
            var helper = new BatchHelper(null, null) {
                @Override
                protected ZipContext createZipWithHashes(InvoiceSource source) {
                    return new ZipContext(zip.toFile(), List.of());
                }
            };
            try {
                helper.prepareBatch(() -> Collections.emptyIterator(), new BatchConfig(output, partSize, true));
                fail("Expected batch size validation");
            } catch (BatchProcessingException expected) {
                assertTrue(expected.getCause() instanceof IllegalArgumentException);
            }
            assertFalse(Files.exists(zip));
            try (var files = Files.list(output)) { assertEquals(0, files.count()); }
        } finally {
            Files.deleteIfExists(zip);
            Files.deleteIfExists(output);
        }
    }

    @Test
    public void helperStillPreparesZipAndSendsValidMetadataThroughBuilder() throws Exception {
        Path output = Files.createTempDirectory("batch-valid-");
        byte[] content = new byte[4096];
        new Random(17).nextBytes(content);
        byte[] key = new byte[32];
        byte[] iv = new byte[16];
        AtomicInteger encryptedCount = new AtomicInteger();
        CryptographyService crypto = (CryptographyService) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{CryptographyService.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getEncryptionData":
                            return new EncryptionData(key, iv, "encrypted-key", new EncryptionInfo("encrypted-key", "iv"));
                        case "getMetaData":
                            byte[] data = args[0] instanceof InputStream stream ? stream.readAllBytes() : (byte[]) args[0];
                            FileMetadata metadata = new FileMetadata();
                            metadata.setFileSize((long) data.length);
                            metadata.setHashSHA(hash(data));
                            return metadata;
                        case "encryptBytesWithAES256":
                            encryptedCount.incrementAndGet();
                            return aes(Cipher.ENCRYPT_MODE, (byte[]) args[0], key, iv);
                        default: throw new AssertionError(method.getName());
                    }
                });
        List<OpenBatchSessionRequest> sent = new ArrayList<>();
        AtomicInteger uploaded = new AtomicInteger();
        KSeFClient client = (KSeFClient) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{KSeFClient.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "openBatchSession":
                            var request = (OpenBatchSessionRequest) args[0];
                            sent.add(request);
                            var response = new OpenBatchSessionResponse();
                            List<PackagePartSignatureInitResponseType> instructions = new ArrayList<>();
                            for (var part : request.getBatchFile().getFileParts()) {
                                var instruction = new PackagePartSignatureInitResponseType();
                                instruction.setOrdinalNumber(part.getOrdinalNumber());
                                instructions.add(instruction);
                            }
                            response.setPartUploadRequests(instructions);
                            return response;
                        case "singleBatchPartSendingProcess":
                            var part = (BatchPartSendingInfo) args[0];
                            assertEquals(hash(part.getData()), part.getMetadata().getHashSHA());
                            assertEquals(part.getData().length, part.getMetadata().getFileSize().longValue());
                            uploaded.incrementAndGet();
                            return null;
                        default: throw new AssertionError(method.getName());
                    }
                });
        var helper = new BatchHelper(crypto, client);
        BatchResult result = null;
        try {
            InvoiceSource source = () -> List.of(new InvoiceItem("1", "invoice.xml", content, HASH)).iterator();
            result = helper.prepareBatch(source, new BatchConfig(output, 1024, true));
            assertNull(result.zipPath());
            assertTrue(result.parts().size() > 1);
            assertEquals(result.parts().size(), encryptedCount.get());
            ByteArrayOutputStream reconstructed = new ByteArrayOutputStream();
            int index = 1;
            for (var part : result.parts()) {
                assertEquals(index++, part.index());
                byte[] encrypted = Files.readAllBytes(part.cipherPath());
                assertEquals(hash(encrypted), part.cipherHash());
                reconstructed.write(aes(Cipher.DECRYPT_MODE, encrypted, key, iv));
            }
            byte[] zip = reconstructed.toByteArray();
            assertEquals(result.zipSize(), zip.length);
            assertEquals(result.zipHash(), hash(zip));
            try (var archive = new ZipInputStream(new ByteArrayInputStream(zip))) {
                assertEquals("invoice.xml", archive.getNextEntry().getName());
                assertTrue(Arrays.equals(content, archive.readAllBytes()));
                assertNull(archive.getNextEntry());
            }
            helper.sendBatch(result, "access", FORM);
            assertEquals(1, sent.size());
            assertEquals(result.parts().size(), uploaded.get());
            assertEquals(result.zipHash(), sent.get(0).getBatchFile().getFileHash());
        } finally {
            if (result != null) helper.removeEncryptedParts(result);
            Files.deleteIfExists(output);
        }
    }

    private static String hash(byte[] data) throws Exception {
        return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(data));
    }

    private static byte[] aes(int mode, byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    private OpenBatchSessionRequestBuilder builder() {
        return OpenBatchSessionRequestBuilder.create()
                .withFormCode(FORM.getSystemCode(), FORM.getSchemaVersion(), FORM.getValue())
                .withEncryption("key", "iv");
    }

    private void rejects(Runnable action) {
        try { action.run(); fail("Expected IllegalArgumentException"); }
        catch (IllegalArgumentException expected) { }
    }
}
