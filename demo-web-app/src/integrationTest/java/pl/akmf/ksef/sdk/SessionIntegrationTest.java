package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.session.OpenOnlineSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.session.AuthenticationListItem;
import pl.akmf.ksef.sdk.client.model.session.AuthenticationListResponse;
import pl.akmf.ksef.sdk.client.model.session.EncryptionData;
import pl.akmf.ksef.sdk.client.model.session.FormCode;
import pl.akmf.ksef.sdk.client.model.session.SessionType;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryResponse;
import pl.akmf.ksef.sdk.client.model.session.SystemCode;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.util.IdentifierGeneratorUtils;

import java.io.IOException;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionIntegrationTest extends BaseIntegrationTest {
    private String expectedErrorMessage = "\"exceptionCode\":21304,\"exceptionDescription\":\"Brak uwierzytelnienia.\",\"details\":[\"Nieprawidłowy token.\"]";

    @Test
    void createSessionSearchSessionAndRevokeCurrentSession() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        AuthTokensPair accessTokensPair = authWithCustomNip(contextNip, contextNip);
        String accessToken = accessTokensPair.accessToken();

        EncryptionData encryptionData = new DefaultCryptographyService(createKSeFClient()).getEncryptionData();

        // Step 1: Open session and return referenceNumber
        openOnlineSession(encryptionData, accessToken);

        // Step 2: get active sessions and check quantity
        AuthenticationListResponse activeSessions = createKSeFClient().getActiveSessions(10, null, accessToken);
        while (Strings.isNotBlank(activeSessions.getContinuationToken())) {
            activeSessions = createKSeFClient().getActiveSessions(10, activeSessions.getContinuationToken(), accessToken);
        }

        Assertions.assertEquals(1, activeSessions.getItems().size());
        Assertions.assertEquals(true, activeSessions.getItems().get(0).getIsCurrent());

        // Step 3: revoke current session
        createKSeFClient().revokeCurrentSession(accessToken);

        // Step 4: get active sessions and check quantity after revoked current session
        AuthenticationListResponse activeSessionsAfterRevoke = createKSeFClient().getActiveSessions(10, null, accessToken);
        Assertions.assertEquals(0, activeSessionsAfterRevoke.getItems().size());
        while (Strings.isNotBlank(activeSessionsAfterRevoke.getContinuationToken())) {
            activeSessionsAfterRevoke = createKSeFClient().getActiveSessions(10, activeSessionsAfterRevoke.getContinuationToken(), accessToken);
        }

        // Step 5: refresh token should throw: 21304: Brak uwierzytelnienia. - Nieprawidłowy token.
        ApiException apiException = assertThrows(ApiException.class, () -> createKSeFClient().refreshAccessToken(accessTokensPair.refreshToken()));
        Assertions.assertTrue(apiException.getResponseBody().contains(expectedErrorMessage));
    }

    @Test
    void createSecondSessionAndRevokeSessionByReferenceNumber() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        AuthTokensPair firstAccessTokensPair = authWithCustomNip(contextNip, contextNip);

        EncryptionData encryptionData = new DefaultCryptographyService(createKSeFClient()).getEncryptionData();

        // Step 1: Open session and return referenceNumber
        openOnlineSession(encryptionData, firstAccessTokensPair.accessToken());

        // Step 2: get active sessions and check quantity
        AuthenticationListResponse activeSessions = createKSeFClient().getActiveSessions(10, null, firstAccessTokensPair.accessToken());
        Assertions.assertEquals(1, activeSessions.getItems().size());
        while (Strings.isNotBlank(activeSessions.getContinuationToken())) {
            activeSessions = createKSeFClient().getActiveSessions(10, activeSessions.getContinuationToken(), firstAccessTokensPair.accessToken());
        }
        String firstSessionReferenceNumber = activeSessions.getItems().get(0).getReferenceNumber();

        AuthTokensPair secondAccessTokensPair = authWithCustomNip(contextNip, contextNip);

        // Step 3: get active sessions after second auth and check quantity
        activeSessions = createKSeFClient().getActiveSessions(10, null, firstAccessTokensPair.accessToken());
        while (Strings.isNotBlank(activeSessions.getContinuationToken())) {
            activeSessions = createKSeFClient().getActiveSessions(10, activeSessions.getContinuationToken(), firstAccessTokensPair.accessToken());
        }
        Assertions.assertEquals(2, activeSessions.getItems().size());
        String secondSessionReferenceNumber = activeSessions.getItems()
                .stream()
                .filter(s -> !s.getIsCurrent())
                .map(AuthenticationListItem::getReferenceNumber)
                .findFirst()
                .orElseThrow();

        // first session is current
        Assertions.assertTrue(activeSessions.getItems()
                .stream()
                .filter(s -> s.getReferenceNumber().equals(firstSessionReferenceNumber))
                .map(AuthenticationListItem::getIsCurrent)
                .findFirst()
                .orElseThrow());

        // Step 4: revoke second session by reference number
        createKSeFClient().revokeSession(secondSessionReferenceNumber, firstAccessTokensPair.accessToken());

        // Step 5: get active sessions and check quantity after revoked second session
        AuthenticationListResponse activeSessionsAfterRevoke = createKSeFClient().getActiveSessions(10, null, firstAccessTokensPair.accessToken());
        Assertions.assertEquals(1, activeSessionsAfterRevoke.getItems().size());
        while (Strings.isNotBlank(activeSessionsAfterRevoke.getContinuationToken())) {
            activeSessionsAfterRevoke = createKSeFClient().getActiveSessions(10, activeSessionsAfterRevoke.getContinuationToken(), firstAccessTokensPair.accessToken());
        }

        // Step 6: refresh token should throw: 21304: Brak uwierzytelnienia. - Nieprawidłowy token.
        ApiException apiException = assertThrows(ApiException.class, () -> createKSeFClient().refreshAccessToken(secondAccessTokensPair.refreshToken()));
        Assertions.assertTrue(apiException.getResponseBody().contains(expectedErrorMessage));
    }

    @Test
    void searchSessions() throws JAXBException, IOException, ApiException, CertificateException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        String accessToken = authWithCustomNip(contextNip, contextNip).accessToken();

        EncryptionData encryptionData = new DefaultCryptographyService(createKSeFClient()).getEncryptionData();

        // Step 1: Open session and return referenceNumber
        openOnlineSession(encryptionData, accessToken);

        // Step 2: Search session
        SessionsQueryRequest request = new SessionsQueryRequest();
        request.setSessionType(SessionType.ONLINE);
        SessionsQueryResponse sessionsQueryResponse = createKSeFClient().getSessions(request, 10, null, accessToken);
        Assertions.assertEquals(1, sessionsQueryResponse.getSessions().size());
    }

    private String openOnlineSession(EncryptionData encryptionData, String accessToken) throws ApiException {
        OpenOnlineSessionRequest request = new OpenOnlineSessionRequestBuilder()
                .withFormCode(new FormCode(SystemCode.FA_2, "1-0E", "FA"))
                .withEncryptionInfo(encryptionData.encryptionInfo())
                .build();

        OpenOnlineSessionResponse openOnlineSessionResponse = createKSeFClient().openOnlineSession(request, accessToken);
        Assertions.assertNotNull(openOnlineSessionResponse);
        Assertions.assertNotNull(openOnlineSessionResponse.getReferenceNumber());
        return openOnlineSessionResponse.getReferenceNumber();
    }
}
