package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.session.OpenOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.session.SendInvoiceRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.session.EncryptionData;
import pl.akmf.ksef.sdk.client.model.session.FormCode;
import pl.akmf.ksef.sdk.client.model.session.SessionInvoiceStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionInvoicesResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SystemCode;
import pl.akmf.ksef.sdk.client.model.session.UpoPageResponse;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceResponse;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class OnlineSessionIntegrationTest extends BaseIntegrationTest {

    private EncryptionData encryptionData;

    @Test
    void onlineSessionE2EIntegrationTest() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var cryptographyService = new DefaultCryptographyService(defaultKsefClient);
        encryptionData = cryptographyService.getEncryptionData();

        // Step 1: Open session and return referenceNumber
        String sessionReferenceNumber = openOnlineSession(encryptionData, SystemCode.FA_2, "1-0E", "FA", authToken);

        // Wait for session to be ready
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionInProgress(sessionReferenceNumber, authToken));

        // Step 2: Send invoice
        sendInvoiceOnlineSession(contextNip, sessionReferenceNumber, encryptionData, cryptographyService, "/xml/invoices/sample/invoice-template.xml", authToken);

        // Wait for invoice to be processed
        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isInvoicesInSessionProcessed(sessionReferenceNumber, authToken));

        // Step 3: Check status
        getOnlineSessionStatus(sessionReferenceNumber, authToken);

        // Wait before closing session
        await().atMost(10, SECONDS)
                .pollDelay(sleepTime, MILLISECONDS)
                .until(() -> true);

        // Step 4: Close session
        closeOnlineSession(sessionReferenceNumber, authToken);

        // Wait for session to be closed
        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isSessionClosed(sessionReferenceNumber, authToken));

        // Step 5: Get documents
        SessionInvoiceStatusResponse sessionInvoice = getOnlineSessionDocuments(sessionReferenceNumber, authToken);
        String ksefNumber = sessionInvoice.getKsefNumber();
        String invoiceReferenceNumber = sessionInvoice.getReferenceNumber();

        // Step 6: Get status after close
        String upoReferenceNumber = getOnlineSessionStatusAfterCloseSession(sessionReferenceNumber, authToken);

        // Step 7: Get UPO
        getOnlineSessionInvoiceUpo(sessionReferenceNumber, ksefNumber, authToken);
        getOnlineSessionInvoiceUpoByInvoiceReferenceNumber(sessionReferenceNumber, invoiceReferenceNumber, authToken);

        // Step 8: Get session UPO
        getOnlineSessionUpo(sessionReferenceNumber, upoReferenceNumber, authToken);

        // Step 9: Get invoice
        getInvoice(ksefNumber, authToken);
    }

    @Test
    void onlineSessionV3E2EIntegrationTest() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var cryptographyService = new DefaultCryptographyService(defaultKsefClient);
        encryptionData = cryptographyService.getEncryptionData();

        // Step 1: Open session and return referenceNumber
        String sessionReferenceNumber = openOnlineSession(encryptionData, SystemCode.FA_3, "1-0E", "FA", authToken);

        // Wait for session to be ready
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionInProgress(sessionReferenceNumber, authToken));

        // Step 2: Send invoice
        sendInvoiceOnlineSession(contextNip, sessionReferenceNumber, encryptionData, cryptographyService, "/xml/invoices/sample/invoice-template_v3.xml", authToken);

        // Wait for invoice to be processed
        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isInvoicesInSessionProcessed(sessionReferenceNumber, authToken));

        // Step 3: Check status
        getOnlineSessionStatus(sessionReferenceNumber, authToken);

        // Wait before closing session
        await().atMost(10, SECONDS)
                .pollDelay(sleepTime, MILLISECONDS)
                .until(() -> true);

        // Step 4: Close session
        closeOnlineSession(sessionReferenceNumber, authToken);

        // Wait for session to be closed
        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isSessionClosed(sessionReferenceNumber, authToken));

        // Step 5: Get documents
        SessionInvoiceStatusResponse sessionInvoice = getOnlineSessionDocuments(sessionReferenceNumber, authToken);
        String ksefNumber = sessionInvoice.getKsefNumber();
        String invoiceReferenceNumber = sessionInvoice.getReferenceNumber();

        // Step 6: Get status after close
        String upoReferenceNumber = getOnlineSessionStatusAfterCloseSession(sessionReferenceNumber, authToken);

        // Step 7: Get UPO
        getOnlineSessionInvoiceUpo(sessionReferenceNumber, ksefNumber, authToken);
        getOnlineSessionInvoiceUpoByInvoiceReferenceNumber(sessionReferenceNumber, invoiceReferenceNumber, authToken);

        // Step 8: Get session UPO
        getOnlineSessionUpo(sessionReferenceNumber, upoReferenceNumber, authToken);

        // Step 9: Get invoice
        getInvoice(ksefNumber, authToken);
    }

    // Helper methods to check conditions
    private boolean isSessionInProgress(String sessionReferenceNumber, String authToken) throws ApiException {
        SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
        if (statusResponse != null && statusResponse.getStatus() != null && statusResponse.getStatus().getCode() > 400) {
            throw new RuntimeException("Could not open session: " + statusResponse.getStatus().getDescription());
        }
        return statusResponse != null && statusResponse.getStatus() != null
               && (statusResponse.getStatus().getCode() == 100 || statusResponse.getStatus().getCode() == 300);
    }

    private boolean isInvoicesInSessionProcessed(String sessionReferenceNumber, String authToken) {
        try {
            SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
            return statusResponse != null &&
                   statusResponse.getSuccessfulInvoiceCount() != null &&
                   statusResponse.getSuccessfulInvoiceCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSessionClosed(String sessionReferenceNumber, String authToken) {
        try {
            SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
            return statusResponse != null &&
                   statusResponse.getStatus() != null &&
                   statusResponse.getStatus().getCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private void getOnlineSessionStatus(String sessionReferenceNumber, String authToken) throws ApiException {
        SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
        Assertions.assertNotNull(statusResponse);
        Assertions.assertNotNull(statusResponse.getSuccessfulInvoiceCount());
        Assertions.assertEquals(1, (int) statusResponse.getSuccessfulInvoiceCount());
        Assertions.assertNull(statusResponse.getFailedInvoiceCount());
        Assertions.assertNull(statusResponse.getUpo());
        Assertions.assertEquals(100, (int) statusResponse.getStatus().getCode());
    }

    private String getOnlineSessionStatusAfterCloseSession(String sessionReferenceNumber, String authToken) throws ApiException {
        SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
        Assertions.assertNotNull(statusResponse);
        Assertions.assertNotNull(statusResponse.getSuccessfulInvoiceCount());
        Assertions.assertEquals(1, (int) statusResponse.getSuccessfulInvoiceCount());
        Assertions.assertNull(statusResponse.getFailedInvoiceCount());
        Assertions.assertNotNull(statusResponse.getUpo());
        Assertions.assertEquals(200, (int) statusResponse.getStatus().getCode());
        UpoPageResponse upoPageResponse = statusResponse.getUpo().getPages().getFirst();
        Assertions.assertNotNull(upoPageResponse);
        Assertions.assertNotNull(upoPageResponse.getReferenceNumber());

        return upoPageResponse.getReferenceNumber();
    }

    private String openOnlineSession(EncryptionData encryptionData, SystemCode systemCode, String schemaVersion, String value, String authToken) throws ApiException {
        var request = new OpenOnlineSessionRequestBuilder()
                .withFormCode(new FormCode(systemCode, schemaVersion, value))
                .withEncryptionInfo(encryptionData.encryptionInfo())
                .build();

        OpenOnlineSessionResponse openOnlineSessionResponse = defaultKsefClient.openOnlineSession(request, authToken);
        Assertions.assertNotNull(openOnlineSessionResponse);
        Assertions.assertNotNull(openOnlineSessionResponse.getReferenceNumber());
        return openOnlineSessionResponse.getReferenceNumber();
    }

    private void sendInvoiceOnlineSession(String nip, String sessionReferenceNumber, EncryptionData encryptionData,
                                          DefaultCryptographyService cryptographyService, String path, String authToken) throws IOException, ApiException {
        String invoiceTemplate = new String(Objects.requireNonNull(BaseIntegrationTest.class.getResourceAsStream(path))
                .readAllBytes(), StandardCharsets.UTF_8)
                .replace("#nip#", nip)
                .replace("#invoice_number#", UUID.randomUUID().toString());

        var invoice = invoiceTemplate.getBytes(StandardCharsets.UTF_8);

        var encryptedInvoice = cryptographyService.encryptBytesWithAES256(invoice,
                encryptionData.cipherKey(),
                encryptionData.cipherIv());

        var invoiceMetadata = cryptographyService.getMetaData(invoice);
        var encryptedInvoiceMetadata = cryptographyService.getMetaData(encryptedInvoice);

        var sendInvoiceRequest = new SendInvoiceRequestBuilder()
                .withInvoiceHash(invoiceMetadata.getHashSHA())
                .withInvoiceSize(invoiceMetadata.getFileSize())
                .withEncryptedInvoiceHash(encryptedInvoiceMetadata.getHashSHA())
                .withEncryptedInvoiceSize(encryptedInvoiceMetadata.getFileSize())
                .withEncryptedInvoiceContent(Base64.getEncoder().encodeToString(encryptedInvoice))
                .build();

        SendInvoiceResponse sendInvoiceResponse = defaultKsefClient.onlineSessionSendInvoice(sessionReferenceNumber, sendInvoiceRequest, authToken);
        Assertions.assertNotNull(sendInvoiceResponse);
        Assertions.assertNotNull(sendInvoiceResponse.getReferenceNumber());
    }

    private void closeOnlineSession(String sessionReferenceNumber, String authToken) throws ApiException {
        defaultKsefClient.closeOnlineSession(sessionReferenceNumber, authToken);
    }

    private SessionInvoiceStatusResponse getOnlineSessionDocuments(String sessionReferenceNumber, String authToken) throws ApiException {
        SessionInvoicesResponse sessionInvoices = defaultKsefClient.getSessionInvoices(sessionReferenceNumber, 10, 0, authToken);
        Assertions.assertEquals(1, sessionInvoices.getInvoices().size());
        SessionInvoiceStatusResponse invoice = sessionInvoices.getInvoices().getFirst();
        Assertions.assertNotNull(invoice);
        Assertions.assertNotNull(invoice.getOrdinalNumber());
        Assertions.assertNotNull(invoice.getInvoiceNumber());
        Assertions.assertNotNull(invoice.getKsefNumber());
        Assertions.assertNotNull(invoice.getReferenceNumber());
        Assertions.assertNotNull(invoice.getInvoiceHash());
        Assertions.assertNotNull(invoice.getInvoicingDate());
        Assertions.assertNotNull(invoice.getStatus());
        Assertions.assertEquals(200, invoice.getStatus().getCode());

        return invoice;
    }

    private void getOnlineSessionInvoiceUpo(String sessionReferenceNumber, String ksefNumber, String authToken) throws ApiException {
        var upoResponse = defaultKsefClient.getSessionInvoiceUpoByKsefNumber(sessionReferenceNumber, ksefNumber, authToken);

        Assertions.assertNotNull(upoResponse);
    }

    private void getOnlineSessionInvoiceUpoByInvoiceReferenceNumber(String sessionReferenceNumber, String invoiceReferenceNumber, String authToken) throws ApiException {
        var upoResponse = defaultKsefClient.getSessionInvoiceUpoByReferenceNumber(sessionReferenceNumber, invoiceReferenceNumber, authToken);

        Assertions.assertNotNull(upoResponse);
    }

    private void getOnlineSessionUpo(String sessionReferenceNumber, String upoReferenceNumber, String authToken) throws ApiException {
        var sessionUpo = defaultKsefClient.getSessionUpo(sessionReferenceNumber, upoReferenceNumber, authToken);

        Assertions.assertNotNull(sessionUpo);
    }

    private void getInvoice(String ksefNumber, String authToken) throws ApiException {
        var invoice = defaultKsefClient.getInvoice(ksefNumber, authToken);
        Assertions.assertNotNull(invoice);

//        DownloadInvoiceRequest request = new DownloadInvoiceRequest();
//        request.setKsefNumber(ksefNumber);
//        invoice = defaultKsefClient.getInvoice(request);
//        Assertions.assertNotNull(invoice);
    }
}