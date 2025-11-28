package io.alapierre.ksef.batch;

import io.alapierre.ksef.batch.model.BatchConfig;
import io.alapierre.ksef.batch.model.BatchPartInfo;
import io.alapierre.ksef.batch.model.BatchResult;
import io.alapierre.ksef.batch.model.InvoiceHash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.input.BoundedInputStream;
import pl.akmf.ksef.sdk.api.builders.batch.OpenBatchSessionRequestBuilder;
import pl.akmf.ksef.sdk.client.interfaces.CryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.session.*;
import pl.akmf.ksef.sdk.client.model.session.batch.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
@Slf4j
@RequiredArgsConstructor
public class BatchHelper {

    private final CryptographyService cryptographyService;
    private final KSeFClient client;

    /**
     * Przygotowuje paczkę wsadową: tworzy archiwum ZIP z faktur, dzieli je na części i szyfruje.
     *
     * @param source źródło faktur (np. katalog, lista w pamięci)
     * @param config konfiguracja (katalog wyjściowy, rozmiar części, czyszczenie)
     * @return wynik przygotowania zawierający ścieżki do zaszyfrowanych części, klucze i hashe
     * @throws BatchProcessingException w przypadku błędów IO lub kryptograficznych
     */
    public BatchResult prepareBatch(InvoiceSource source, BatchConfig config) {

        // 1. Utwórz ZIP i zbierz hashe faktur (zapis do pliku tymczasowego)
        ZipContext zipContext = createZipWithHashes(source);
        File plainZipFile = zipContext.file();
        log.info("Utworzono plik ZIP: {}, rozmiar: {}", plainZipFile.getAbsolutePath(), plainZipFile.length());

        List<BatchPartInfo> encryptedParts = new ArrayList<>();
        byte[] aesKey;
        final String encryptedCipherKey;
        byte[] iv;
        String zipHash;
        long zipSize = 0;

        try {
            // 2. Pobierz dane kryptograficzne (klucz AES i IV)
            EncryptionData encryptionData = cryptographyService.getEncryptionData();
            aesKey = encryptionData.cipherKey();
            iv = encryptionData.cipherIv();
            encryptedCipherKey = encryptionData.encryptedCipherKey();

            // 3. Oblicz hash całego pliku ZIP
            try (InputStream is = new FileInputStream(plainZipFile)) {
                FileMetadata meta = cryptographyService.getMetaData(is);
                zipHash = meta.getHashSHA();
                zipSize = meta.getFileSize();
            }
            log.info("ZIP Meta: size={}, hash={}", zipSize, zipHash);

            // 4. Podziel i zaszyfruj ZIP kawałkami (byte[])
            try (InputStream fis = new FileInputStream(plainZipFile)) {

                byte[] buffer = new byte[(int) config.maxPartSize()];
                int bytesRead;
                int partIndex = 1; // KSeF wymaga indeksowania od 1


                while ((bytesRead = fis.read(buffer)) > 0) {
                    // Jeśli przeczytano mniej niż bufor (ostatnia część), przytnij tablicę
                    byte[] partData;
                    if (bytesRead < buffer.length) {
                        partData = Arrays.copyOf(buffer, bytesRead);
                    } else {
                        partData = buffer;
                    }

                    // Szyfrowanie w pamięci (zwraca zaszyfrowane dane z paddingiem)
                    byte[] encryptedData = cryptographyService.encryptBytesWithAES256(partData, aesKey, iv);

                    // Oblicz hash zaszyfrowanej części
                    FileMetadata partMeta = cryptographyService.getMetaData(encryptedData);
                    String cipherHash = partMeta.getHashSHA();
                    long cipherSize = partMeta.getFileSize();

                    // Zapisz zaszyfrowaną część na dysk
                    Path partOutputPath = config.outputDir().resolve(String.format("part-%d.enc", partIndex));
                    Files.write(partOutputPath, encryptedData);

                    encryptedParts.add(new BatchPartInfo(
                            partIndex,
                            partOutputPath,
                            cipherSize,
                            cipherHash
                    ));
                    partIndex++;
                    log.debug("Przetworzono część {}, plainSize: {}, cipherSize: {}", partIndex, partData.length, cipherSize);
                }
            }

        } catch (Exception e) {
            log.warn("Problem z tworzeniem i szyfrowaniem paczek dla wysyłki wsadowej, czczenie pozostałości, usuwam {} plików paczek", encryptedParts.size());
            for (BatchPartInfo part : encryptedParts) {
                try {
                    Files.deleteIfExists(part.cipherPath());
                } catch (IOException ignored) {
                    log.warn("Nie udało się posprzątać pliku po błędzie: {}", part.cipherPath());
                }
            }
            throw new BatchProcessingException("Błąd podczas przygotowywania wysyłki batch", e);
        } finally {
            // Cleanup plain zip
            if (config.cleanup() && plainZipFile.exists()) {
                boolean deleted = plainZipFile.delete();
                if (!deleted) log.warn("Nie udało się usunąć tymczasowego pliku ZIP: {}", plainZipFile);
            }
        }

        // Jeśli włączono cleanup, nie zwracamy ścieżki do usuniętego pliku
        Path zipPath = (config.cleanup() || !plainZipFile.exists()) ? null : plainZipFile.toPath();

        return new BatchResult(
                zipPath,
                zipSize,
                zipHash,
                encryptedParts,
                zipContext.hashes(),
                iv,
                encryptedCipherKey
        );
    }

    /**
     * Wysyła przygotowane wcześniej części paczki jedna po drugiej.
     * Każda część jest wczytywana z dysku do pamięci (byte[]) tuż przed wysyłką, co oszczędza RAM w porównaniu do ładowania listy.
     *
     * @param authToken oAuth access token
     * @param result wynik przygotowania paczki (zawiera ścieżki do plików)
     */
    public OpenBatchSessionResponse sendBatch(BatchResult result, String authToken) throws ApiException {

        val batchRequest = prepareRequest(result);
        var session = client.openBatchSession(batchRequest, authToken);

        List<PackagePartSignatureInitResponseType> uploadInstructions = session.getPartUploadRequests();
        List<String> errors = new ArrayList<>();

        for (BatchPartInfo partInfo : result.parts()) {

            // 1. Znajdź instrukcję wysyłki dla danej części
            PackagePartSignatureInitResponseType instruction = uploadInstructions.stream()
                    .filter(i -> i.getOrdinalNumber() == partInfo.index())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Brak instrukcji wysyłki z KSeF dla części nr " + partInfo.index()));

            try {
                // 2. Wczytaj pojedynczą część do pamięci
                byte[] partData = Files.readAllBytes(partInfo.cipherPath());

                BatchPartSendingInfo info = new BatchPartSendingInfo();
                info.setOrdinalNumber(partInfo.index());
                info.setData(partData);
                FileMetadata meta = new FileMetadata();
                meta.setFileSize(partInfo.cipherSize());
                meta.setHashSHA(partInfo.cipherHash());
                info.setMetadata(meta);

                // 3. Wyślij pojedynczą część (używając wersji na byte[])
                client.singleBatchPartSendingProcess(info, instruction, errors);

                // 4. Sprawdź błędy natychmiast
                if (!errors.isEmpty()) {
                    String errorMsg = String.join("\n", errors);
                    throw new ApiException("Błąd wysyłki części " + partInfo.index() + ": " + errorMsg);
                }

            } catch (IOException e) {
                throw new BatchProcessingException("Błąd IO podczas odczytu części paczki: " + partInfo.cipherPath(), e);
            }
        }
        return session;
    }

    private OpenBatchSessionRequest prepareRequest(BatchResult batchResult) {

        var builder = OpenBatchSessionRequestBuilder.create()
                    .withFormCode(SystemCode.FA_2, SchemaVersion.VERSION_1_0E, SessionValue.FA)
                    .withOfflineMode(false)
                    .withBatchFile(batchResult.zipSize(), batchResult.zipHash())
                    .withEncryption(
                            batchResult.encryptedCipherKey(),
                            batchResult.encodedIv());

            // Najpierw dodaj części do buildera
            for (BatchPartInfo part : batchResult.parts()) {
                builder.addBatchFilePart(part.index(), part.cipherSize(), part.cipherHash());
            }

            return builder.endBatchFile().build();
    }

    /**
     * Usuwa zaszyfrowane części paczki z dysku. Należy wywołać po zakończeniu procesu wysyłki.
     *
     * @param result wynik przygotowania paczki zawierający ścieżki do plików
     */
    public void removeEncryptedParts(BatchResult result) {
        for (BatchPartInfo part : result.parts()) {
            try {
                Files.deleteIfExists(part.cipherPath());
                log.debug("Usunięto plik części: {}", part.cipherPath());
            } catch (IOException e) {
                log.warn("Nie udało się usunąć pliku części: {}", part.cipherPath(), e);
            }
        }
    }

    protected record ZipContext(File file, List<InvoiceHash> hashes) {}

    protected ZipContext createZipWithHashes(InvoiceSource source) {
        File zip;
        List<InvoiceHash> invoiceHashes = new ArrayList<>();

        try {
            zip = File.createTempFile("invoices", ".zip");

            try (OutputStream zipStream = new FileOutputStream(zip);
                 ZipOutputStream archive = new ZipOutputStream(zipStream)) {

                for (val item : source) {
                    archive.putNextEntry(new ZipEntry(item.fileName()));
                    archive.write(item.content());
                    archive.closeEntry();
                    invoiceHashes.add(new InvoiceHash(item.fileName(), item.hash()));
                }
                archive.finish();
            }
        } catch (IOException e) {
            throw new BatchProcessingException("Nie można utworzyć tymczasowego pliku ZIP", e);
        }
        return new ZipContext(zip, invoiceHashes);
    }

}