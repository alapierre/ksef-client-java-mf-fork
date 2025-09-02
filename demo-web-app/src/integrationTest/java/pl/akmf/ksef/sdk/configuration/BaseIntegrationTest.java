package pl.akmf.ksef.sdk.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.akmf.ksef.sdk.TestClientApplication;
import pl.akmf.ksef.sdk.api.builders.auth.AuthTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.auth.AuthTokenRequestSerializer;
import pl.akmf.ksef.sdk.api.builders.certificate.CertificateBuilders;
import pl.akmf.ksef.sdk.api.services.DefaultCertificateGenerator;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.services.DefaultSignatureService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.certificate.SelfSignedCertificate;
import pl.akmf.ksef.sdk.client.model.xml.AuthTokenRequest;
import pl.akmf.ksef.sdk.client.model.xml.SubjectIdentifierTypeEnum;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(
        classes = {TestClientApplication.class, IntegrationConfig.class},
        webEnvironment = RANDOM_PORT
)
@ContextConfiguration(classes = IntegrationConfig.class)
@EnableAutoConfiguration
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;
    protected int sleepTime = 350;

    @Autowired
    protected WireMockServer wireMock;

    @Autowired
    protected DefaultKsefClient defaultKsefClient;

    @BeforeEach
    public void prepare() {
        wireMock.start();
    }

    @AfterEach
    void clear() {
        wireMock.stop();
    }

    protected AuthTokensPair authWithCustomNip(String context, String subject) throws ApiException, JAXBException, IOException {
        var challenge = defaultKsefClient.getAuthChallenge();

        AuthTokenRequest authTokenRequest = new AuthTokenRequestBuilder()
                .withChallenge(challenge.getChallenge())
                .withContextNip(context)
                .withSubjectType(SubjectIdentifierTypeEnum.CERTIFICATE_SUBJECT)
                .build();

        var xml = AuthTokenRequestSerializer.authTokenRequestSerializer(authTokenRequest);

        var x500 = new CertificateBuilders()
                .buildForOrganization("Kowalski sp. z o.o", "VATPL-" + subject, "Kowalski");

        SelfSignedCertificate cert = new DefaultCertificateGenerator().generateSelfSignedCertificateRsa(x500);

        var signedXml = new DefaultSignatureService().sign(xml.getBytes(), cert.certificate(), cert.getPrivateKey());

        var submitAuthTokenResponse = defaultKsefClient.submitAuthTokenRequest(signedXml, false);

        //Czekanie na zakończenie procesu
        await().atMost(4, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionStatusReady(submitAuthTokenResponse.getReferenceNumber(), submitAuthTokenResponse.getAuthenticationToken().getToken()));

        var tokenResponse = defaultKsefClient.redeemToken(submitAuthTokenResponse.getAuthenticationToken().getToken());

        return new AuthTokensPair(tokenResponse.getAccessToken().getToken(), tokenResponse.getRefreshToken().getToken());
    }

    private boolean isSessionStatusReady(String referenceNumber, String tempAuthToken) throws ApiException {
        var checkAuthStatus = defaultKsefClient.getAuthStatus(referenceNumber, tempAuthToken);
        return checkAuthStatus != null && checkAuthStatus.getStatus().getCode() == 200;
    }

    public record AuthTokensPair(String authToken, String refreshToken) {

    }
}
