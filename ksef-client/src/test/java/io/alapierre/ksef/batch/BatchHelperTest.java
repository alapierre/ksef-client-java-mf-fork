package io.alapierre.ksef.batch;

import io.alapierre.ksef.batch.model.InvoiceHash;
import io.alapierre.ksef.batch.model.InvoiceItem;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class BatchHelperTest {
    @Test
    public void zipPreservesInvoiceNamesContentsAndHashOrder() throws Exception {
        byte[] first = "<invoice>Zażółć gęślą jaźń</invoice>".getBytes(StandardCharsets.UTF_8);
        byte[] second = "<invoice>2</invoice>".getBytes(StandardCharsets.UTF_8);
        String firstHash = hash(first);
        String secondHash = hash(second);
        InvoiceSource source = () -> List.of(
                new InvoiceItem("1", "first.xml", first, firstHash),
                new InvoiceItem("2", "second.xml", second, secondHash)).iterator();

        // ZIP creation must not need either an API client or a cryptography service.
        var result = new BatchHelper(null, null).createZipWithHashes(source);
        try {
            assertEquals(List.of(new InvoiceHash("first.xml", firstHash),
                    new InvoiceHash("second.xml", secondHash)), result.hashes());
            try (var zip = new ZipInputStream(Files.newInputStream(result.file().toPath()))) {
                assertEquals("first.xml", zip.getNextEntry().getName());
                assertArrayEquals(first, zip.readAllBytes());
                assertEquals("second.xml", zip.getNextEntry().getName());
                assertArrayEquals(second, zip.readAllBytes());
                assertNull(zip.getNextEntry());
            }
        } finally {
            Files.deleteIfExists(result.file().toPath());
        }
    }

    private static String hash(byte[] content) throws Exception {
        return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(content));
    }
}
