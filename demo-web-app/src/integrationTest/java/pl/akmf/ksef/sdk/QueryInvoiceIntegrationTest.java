package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.invoices.InvoiceMetadataQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.invoices.InvoicesAsyncQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.session.OpenOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.session.SendInvoiceRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryDateRange;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryDateType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySubjectType;
import pl.akmf.ksef.sdk.client.model.session.EncryptionData;
import pl.akmf.ksef.sdk.client.model.session.FormCode;
import pl.akmf.ksef.sdk.client.model.session.SessionStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SystemCode;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceResponse;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class QueryInvoiceIntegrationTest extends BaseIntegrationTest {

    @Test
    void queryInvoiceE2ETest() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var cryptographyService = new DefaultCryptographyService(defaultKsefClient);
        EncryptionData encryptionData = cryptographyService.getEncryptionData();

        String sessionReferenceNumber = openOnlineSession(encryptionData, SystemCode.FA_3, "1-0E", "FA", authToken);

        checkIfSessionIsOpen(sessionReferenceNumber, authToken);

        sendInvoiceOnlineSession(contextNip, sessionReferenceNumber, encryptionData, cryptographyService, "/xml/invoices/sample/invoice-template_v3.xml", authToken);

        isInvoicesInSessionProcessed(sessionReferenceNumber, authToken);

        getInvoiceMetadata(authToken);

        fetchAsyncInvoice(authToken);
    }

    private void getInvoiceMetadata(String authToken) throws ApiException {
        var request = new InvoiceMetadataQueryRequestBuilder()
                .withSubjectType(InvoiceQuerySubjectType.SUBJECT1)
                .withDateRange(new InvoiceQueryDateRange(InvoiceQueryDateType.ISSUE,
                        OffsetDateTime.now().minusDays(10), OffsetDateTime.now().plusDays(10)))
                .build();

        var response = defaultKsefClient.queryInvoiceMetadata(0, 10, request, authToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getInvoices().size());
    }

    private void checkIfSessionIsOpen(String sessionReferenceNumber, String authToken) {
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionInProgress(sessionReferenceNumber, authToken));
    }

    private boolean isSessionInProgress(String sessionReferenceNumber, String authToken) throws ApiException {
        SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
        if (statusResponse != null && statusResponse.getStatus() != null && statusResponse.getStatus().getCode() > 400) {
            throw new RuntimeException("Could not open session: " + statusResponse.getStatus().getDescription());
        }
        return statusResponse != null && statusResponse.getStatus() != null && (statusResponse.getStatus().getCode() == 100 || statusResponse.getStatus().getCode() == 300);
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

    private void fetchAsyncInvoice(String authToken) throws ApiException {
        var request = new InvoicesAsyncQueryRequestBuilder()
                .build();

        var response = defaultKsefClient.initAsyncQueryInvoice(request, authToken);

        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isInvoiceFetched(response.getOperationReferenceNumber(), authToken));
    }

    private Boolean isInvoiceFetched(String operationReferenceNumber, String authToken) throws ApiException {
        var response = defaultKsefClient.checkStatusAsyncQueryInvoice(operationReferenceNumber, authToken);

        Assertions.assertNotNull(response);
        return response.getStatus().getCode().equals(200);
    }
}
