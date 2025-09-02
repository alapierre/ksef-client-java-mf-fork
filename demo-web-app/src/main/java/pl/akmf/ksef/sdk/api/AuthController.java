package pl.akmf.ksef.sdk.api;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.auth.AuthTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.auth.AuthTokenRequestSerializer;
import pl.akmf.ksef.sdk.api.builders.certificate.CertificateBuilders;
import pl.akmf.ksef.sdk.api.services.DefaultCertificateGenerator;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.services.DefaultSignatureService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationOperationStatusResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenRefreshResponse;
import pl.akmf.ksef.sdk.client.model.certificate.SelfSignedCertificate;
import pl.akmf.ksef.sdk.client.model.xml.AuthTokenRequest;
import pl.akmf.ksef.sdk.client.model.xml.SubjectIdentifierTypeEnum;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final DefaultKsefClient ksefClient;

    /**
     * Cały process autoryzacji krok po kroku
     * Zwraca token JWT oraz refreshToken
     * Inicjalizacja przyk�dowego identyfikatora - w tym przypadku NIP.
     *
     * @param context nip kontekstu w którym następuje próba uwierzytelnienia
     * @return AuthenticationOperationStatusResponse
     * @throws ApiException if fails to make API call
     */
    @PostMapping(value = "/auth-step-by-step/{context}")
    public AuthenticationOperationStatusResponse authStepByStep(@PathVariable String context) throws ApiException, JAXBException, IOException {
        //wykonanie auth challenge
        var challenge = ksefClient.getAuthChallenge();

        //xml niezbędny do uwierzytelnienia
        AuthTokenRequest authTokenRequest = new AuthTokenRequestBuilder()
                .withChallenge(challenge.getChallenge())
                .withContextNip(context)
                .withSubjectType(SubjectIdentifierTypeEnum.CERTIFICATE_SUBJECT)
                .build();

        var xml = AuthTokenRequestSerializer.authTokenRequestSerializer(authTokenRequest);

        //wygenerowanie certyfikatu oraz klucza prywatnego
        var x500 = new CertificateBuilders()
                .buildForOrganization("Kowalski sp. z o.o", "VATPL-" + context, "Kowalski");

        SelfSignedCertificate cert = new DefaultCertificateGenerator().generateSelfSignedCertificateRsa(x500);

        //podpisanie xml wygenerowanym certyfikatem oraz kluczem prywatnym
        var signedXml = new DefaultSignatureService().sign(xml.getBytes(), cert.certificate(), cert.getPrivateKey());

        // Przesłanie podpisanego XML do systemu KSeF
        var submitAuthTokenResponse = ksefClient.submitAuthTokenRequest(signedXml, false);

        //Czekanie na zakończenie procesu
        await().atMost(4, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionStatusReady(submitAuthTokenResponse.getReferenceNumber(), submitAuthTokenResponse.getAuthenticationToken().getToken()));

        //pobranie tokenów
        return ksefClient.redeemToken(submitAuthTokenResponse.getAuthenticationToken().getToken());
    }

    @PostMapping(value = "auth-with-ksef-certificate")
    public AuthenticationOperationStatusResponse authWithKsefCert(@RequestBody CertAuthRequest request) throws CertificateException, NoSuchAlgorithmException, InvalidKeySpecException, ApiException, JAXBException, IOException {
        var x500 = new CertificateBuilders().buildForOrganization("Kowalski sp. z o.o", "VATPL-1111111111", "Kowalski");
        SelfSignedCertificate selfSignedCertificate = new DefaultCertificateGenerator().generateSelfSignedCertificateRsa(x500);
        String privateKeyBase64 = Base64.getEncoder().encodeToString(selfSignedCertificate.getPrivateKey().getEncoded());
        String certInBase64 = Base64.getEncoder().encodeToString(selfSignedCertificate.certificate().getEncoded());


        // 1. Wczytaj certyfikat X.509
        byte[] certBytes = Base64.getDecoder().decode(request.getCertInBase64());
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));

        // 2. Wczytaj klucz prywatny (RSA)
        byte[] privateKeyBytes = Base64.getDecoder().decode(request.getPrivateKeyBase64());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 3. Pobierz challenge
        var challengeResponse = ksefClient.getAuthChallenge();
        String challenge = challengeResponse.getChallenge();


        var authTokenRequest = new AuthTokenRequestBuilder()
                .withChallenge(challenge)
                .withContextNip(request.getContextIdentifier())
                .withSubjectType(SubjectIdentifierTypeEnum.CERTIFICATE_SUBJECT)
                .build();

        // 5. Serializuj i podpisz
        var unsignedXml = AuthTokenRequestSerializer.authTokenRequestSerializer(authTokenRequest);
        String signedXml = new DefaultSignatureService().sign(unsignedXml.getBytes(StandardCharsets.UTF_8), cert, privateKey);

        // 6. Wyślij żądanie uwierzytelnienia
        var submitAuthTokenResponse = ksefClient.submitAuthTokenRequest(signedXml, false);

        await().atMost(4, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isSessionStatusReady(submitAuthTokenResponse.getReferenceNumber(), submitAuthTokenResponse.getAuthenticationToken().getToken()));

        //pobranie tokenów
        return ksefClient.redeemToken(submitAuthTokenResponse.getAuthenticationToken().getToken());
    }

    /**
     * Proces odświeżania tokenu jwt
     * Zwraca nowy token JWT oraz refreshToken
     *
     * @param refreshToken token służący do odświeżania
     * @return AuthenticationTokenRefreshResponse
     * @throws ApiException if fails to make API call
     */
    @GetMapping(value = "/refreshToken/{refreshToken}")
    public AuthenticationTokenRefreshResponse refreshToken(@PathVariable String refreshToken) throws Exception {
        return ksefClient.refreshAccessToken(refreshToken);
    }

    /**
     * Proces unieważniania tokenu jwt
     *
     * @return void
     * @throws ApiException if fails to make API call
     */
    @GetMapping(value = "/revoke")
    public void revokeToken(@RequestHeader(name = "Authorization") String authToken) throws Exception {
        ksefClient.revokeAccessToken(authToken);
    }

    private boolean isSessionStatusReady(String referenceNumber, String authToken) throws ApiException {
        var checkAuthStatus = ksefClient.getAuthStatus(referenceNumber, authToken);
        log.info("Polling: StatusCode= " + checkAuthStatus.getStatus().getCode() + ", " +
                "Description='" + checkAuthStatus.getStatus().getDescription());

        return checkAuthStatus.getStatus().getCode() == 200;
    }
}

class CertAuthRequest {
    String certInBase64;
    String contextIdentifier;
    String privateKeyBase64;

    public CertAuthRequest(String certInBase64, String contextIdentifier, String privateKeyBase64) {
        this.certInBase64 = certInBase64;
        this.contextIdentifier = contextIdentifier;
        this.privateKeyBase64 = privateKeyBase64;
    }

    public CertAuthRequest() {
    }

    public String getCertInBase64() {
        return certInBase64;
    }

    public void setCertInBase64(String certInBase64) {
        this.certInBase64 = certInBase64;
    }

    public String getContextIdentifier() {
        return contextIdentifier;
    }

    public void setContextIdentifier(String contextIdentifier) {
        this.contextIdentifier = contextIdentifier;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public void setPrivateKeyBase64(String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
    }
}
