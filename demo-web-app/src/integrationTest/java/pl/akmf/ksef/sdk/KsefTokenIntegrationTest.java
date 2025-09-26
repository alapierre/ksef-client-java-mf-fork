package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.tokens.KsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthKsefTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.AuthStatus;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationChallengeResponse;
import pl.akmf.ksef.sdk.client.model.auth.SignatureResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthOperationStatusResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationToken;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenStatus;
import pl.akmf.ksef.sdk.client.model.auth.ContextIdentifier;
import pl.akmf.ksef.sdk.client.model.auth.ContextIdentifierType;
import pl.akmf.ksef.sdk.client.model.auth.EncryptionMethod;
import pl.akmf.ksef.sdk.client.model.auth.KsefTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenResponse;
import pl.akmf.ksef.sdk.client.model.auth.QueryTokensResponse;
import pl.akmf.ksef.sdk.client.model.auth.TokenPermissionType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.util.IdentifierGeneratorUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class KsefTokenIntegrationTest extends BaseIntegrationTest {

    @Test
    void checkGenerateTokenTest() throws IOException, ApiException, JAXBException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        String accessToken = authWithCustomNip(contextNip, contextNip).accessToken();

        // step 1: generate tokens
        KsefTokenRequest request = new KsefTokenRequestBuilder()
                .withDescription("test description")
                .withPermissions(List.of(
                        TokenPermissionType.INVOICEREAD,
                        TokenPermissionType.INVOICEWRITE,
                        TokenPermissionType.CREDENTIALSREAD))
                .build();

        GenerateTokenResponse token = createKSeFClient().generateKsefToken(request, accessToken);
        GenerateTokenResponse token2 = createKSeFClient().generateKsefToken(request, accessToken);
        GenerateTokenResponse token3 = createKSeFClient().generateKsefToken(request, accessToken);

        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.getToken());
        Assertions.assertNotNull(token.getReferenceNumber());

        // step 2: wait for token to become ACTIVE
        Awaitility.await()
                .atMost(10, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    AuthenticationToken ksefToken = createKSeFClient().getKsefToken(token.getReferenceNumber(), accessToken);
                    return ksefToken != null && ksefToken.getStatus() == AuthenticationTokenStatus.ACTIVE;
                });

        AuthenticationToken ksefToken = createKSeFClient().getKsefToken(token.getReferenceNumber(), accessToken);
        Assertions.assertNotNull(ksefToken);
        Assertions.assertEquals(AuthenticationTokenStatus.ACTIVE, ksefToken.getStatus());

        // step 3: filter active tokens
        List<AuthenticationTokenStatus> status = List.of(AuthenticationTokenStatus.ACTIVE);
        Integer pageSize = 10;
        QueryTokensResponse tokens = createKSeFClient().queryKsefTokens(status, StringUtils.EMPTY, pageSize, accessToken);
        List<AuthenticationToken> filteredTokens = tokens.getTokens();
        Assertions.assertNotNull(filteredTokens);
        Assertions.assertEquals(3, filteredTokens.size());

        // step 4: revoke token and wait for REVOKED status
        createKSeFClient().revokeKsefToken(token.getReferenceNumber(), accessToken);

        Awaitility.await()
                .atMost(10, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    AuthenticationToken revokedToken = createKSeFClient().getKsefToken(token.getReferenceNumber(), accessToken);
                    return revokedToken != null && revokedToken.getStatus() == AuthenticationTokenStatus.REVOKED;
                });

        AuthenticationToken ksefTokenAfterRevoke = createKSeFClient().getKsefToken(token.getReferenceNumber(), accessToken);
        Assertions.assertNotNull(ksefTokenAfterRevoke);
        Assertions.assertEquals(AuthenticationTokenStatus.REVOKED, ksefTokenAfterRevoke.getStatus());

        // step 5: filter active tokens after revoking one
        QueryTokensResponse tokens2 = createKSeFClient().queryKsefTokens(status, StringUtils.EMPTY, pageSize, accessToken);
        List<AuthenticationToken> filteredTokens2 = tokens2.getTokens();
        Assertions.assertNotNull(filteredTokens2);
        Assertions.assertEquals(2, filteredTokens2.size());
    }

    // @Test
    // [ECDSA is not supported yet]
    void tokenE2EIntegrationTestWithECDsa() throws JAXBException, IOException, ApiException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, CertificateException {
        tokenTest(EncryptionMethod.ECDsa);
    }

    @Test
    void tokenE2EIntegrationTestWithRSA() throws JAXBException, IOException, ApiException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, CertificateException {
        tokenTest(EncryptionMethod.Rsa);
    }

    private void tokenTest(EncryptionMethod encryptionMethod) throws JAXBException, IOException, ApiException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, CertificateException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        AuthTokensPair authToken = authWithCustomNip(contextNip, contextNip);
        KsefTokenRequest request = new KsefTokenRequestBuilder()
                .withDescription("test description")
                .withPermissions(List.of(TokenPermissionType.INVOICEREAD, TokenPermissionType.INVOICEWRITE))
                .build();
        GenerateTokenResponse ksefToken = createKSeFClient().generateKsefToken(request, authToken.accessToken());
        AuthenticationChallengeResponse challenge = createKSeFClient().getAuthChallenge();

        byte[] encryptedToken;
        switch (encryptionMethod) {
            case Rsa -> encryptedToken = new DefaultCryptographyService(createKSeFClient())
                    .encryptKsefTokenWithRSAUsingPublicKey(ksefToken.getToken(), challenge.getTimestamp());
            case ECDsa -> encryptedToken = new DefaultCryptographyService(createKSeFClient())
                    .encryptKsefTokenWithECDsaUsingPublicKey(ksefToken.getToken(), challenge.getTimestamp());
            default -> throw new IllegalArgumentException();
        }

        AuthKsefTokenRequest authTokenRequest = new AuthKsefTokenRequestBuilder()
                .withChallenge(challenge.getChallenge())
                .withContextIdentifier(new ContextIdentifier(ContextIdentifierType.NIP, contextNip))
                .withEncryptedToken(Base64.getEncoder().encodeToString(encryptedToken))
                .build();

        SignatureResponse response = createKSeFClient().authenticateByKSeFToken(authTokenRequest);

        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isAuthStatusReady(response.getReferenceNumber(), response.getAuthenticationToken().getToken()));

        AuthOperationStatusResponse tokenResponse = createKSeFClient().redeemToken(response.getAuthenticationToken().getToken());
        Assertions.assertNotNull(tokenResponse);
    }

    private Boolean isAuthStatusReady(String referenceNumber, String tempToken) throws ApiException {
        AuthStatus authStatus = createKSeFClient().getAuthStatus(referenceNumber, tempToken);
        return authStatus != null && authStatus.getStatus().getCode() == 200;
    }
}
