package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.session.OpenOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.session.AuthenticationListResponse;
import pl.akmf.ksef.sdk.client.model.session.EncryptionData;
import pl.akmf.ksef.sdk.client.model.session.FormCode;
import pl.akmf.ksef.sdk.client.model.session.SessionStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionType;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryResponse;
import pl.akmf.ksef.sdk.client.model.session.SystemCode;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.security.cert.CertificateException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class SessionIntegrationTest extends BaseIntegrationTest {
    private EncryptionData encryptionData;

    @Test
    public void searchSessionAndRevokeCurrentSession() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var cryptographyService = new DefaultCryptographyService(defaultKsefClient);
        encryptionData = cryptographyService.getEncryptionData();

        // Step 1: Open session and return referenceNumber
        String sessionReferenceNumber = openOnlineSession(encryptionData, authToken);

        // Wait for session to be ready
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionInProgress(sessionReferenceNumber, authToken));

        // Step 2: get active sessions and check quantity
        AuthenticationListResponse activeSessions = defaultKsefClient.getActiveSessions(10, null, authToken);
        Assertions.assertEquals(1, activeSessions.getItems().size());

        // Step 3: revoke current session
        defaultKsefClient.revokeCurrentSession(authToken);

        // Step 4: get active sessions and check quantity after revoked current session
        AuthenticationListResponse activeSessionsAfterRevoke = defaultKsefClient.getActiveSessions(10, null, authToken);
        Assertions.assertEquals(0, activeSessionsAfterRevoke.getItems().size());
    }

    @Test
    public void searchSessions() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var cryptographyService = new DefaultCryptographyService(defaultKsefClient);
        encryptionData = cryptographyService.getEncryptionData();

        // Step 1: Open session and return referenceNumber
        String sessionReferenceNumber = openOnlineSession(encryptionData, authToken);

        // Wait for session to be ready
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionInProgress(sessionReferenceNumber, authToken));


        // Step 2: Search session
        SessionsQueryRequest request = new SessionsQueryRequest();
        request.setSessionType(SessionType.ONLINE);
        SessionsQueryResponse sessionsQueryResponse = defaultKsefClient.getSessions(request, 10, null, authToken);
        Assertions.assertEquals(1, sessionsQueryResponse.getSessions().size());
    }

    // Helper methods to check conditions
    private boolean isSessionInProgress(String sessionReferenceNumber, String authToken) throws ApiException {
        SessionStatusResponse statusResponse = defaultKsefClient.getSessionStatus(sessionReferenceNumber, authToken);
        if (statusResponse != null && statusResponse.getStatus() != null && statusResponse.getStatus().getCode() > 400) {
            throw new RuntimeException("Could not open session: " + statusResponse.getStatus().getDescription());
        }
        return statusResponse != null && statusResponse.getStatus() != null && (statusResponse.getStatus().getCode() == 100 || statusResponse.getStatus().getCode() == 300);
    }

    private String openOnlineSession(EncryptionData encryptionData, String authToken) throws ApiException {
        var request = new OpenOnlineSessionRequestBuilder()
                .withFormCode(new FormCode(SystemCode.FA_2, "1-0E", "FA"))
                .withEncryptionInfo(encryptionData.encryptionInfo())
                .build();

        OpenOnlineSessionResponse openOnlineSessionResponse = defaultKsefClient.openOnlineSession(request, authToken);
        Assertions.assertNotNull(openOnlineSessionResponse);
        Assertions.assertNotNull(openOnlineSessionResponse.getReferenceNumber());
        return openOnlineSessionResponse.getReferenceNumber();
    }
}
