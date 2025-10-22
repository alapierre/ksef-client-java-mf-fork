package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenRefreshResponse;
import pl.akmf.ksef.sdk.client.model.auth.EncryptionMethod;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.util.IdentifierGeneratorUtils;

import java.io.IOException;

class AuthorizationIntegrationTest extends BaseIntegrationTest {

    @Test
    void refreshTokenE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        // given
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        AuthTokensPair token = authWithCustomNip(contextNip, contextNip, EncryptionMethod.ECDsa);
        String initialAccessToken = token.accessToken();
        String initialRefreshToken = token.refreshToken();

        //when
        AuthenticationTokenRefreshResponse refreshTokenResult = ksefClient.refreshAccessToken(initialRefreshToken);

        //then
        Assertions.assertNotNull(refreshTokenResult);
        Assertions.assertNotEquals(initialAccessToken, refreshTokenResult.getAccessToken().getToken());
    }
}
