package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.tokens.GenerateTokenRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationToken;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenStatus;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenResponse;
import pl.akmf.ksef.sdk.client.model.auth.QueryTokensResponse;
import pl.akmf.ksef.sdk.client.model.auth.TokenPermissionType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

class TokensIntegrationTest extends BaseIntegrationTest {

    @Test
    void checkGenerateTokenTest() throws IOException, ApiException, JAXBException {
        String contextNip = TestUtils.generateRandomNIP();
        var authenticationToken = authWithCustomNip(contextNip, contextNip).authToken();

        // step 1: generate tokens
        GenerateTokenRequest request = new GenerateTokenRequestBuilder()
                .withDescription("test description")
                .withPermissions(List.of(
                        TokenPermissionType.INVOICEREAD,
                        TokenPermissionType.INVOICEWRITE,
                        TokenPermissionType.CREDENTIALSREAD))
                .build();

        GenerateTokenResponse token = defaultKsefClient.generateKsefToken(request, authenticationToken);
        GenerateTokenResponse token2 = defaultKsefClient.generateKsefToken(request, authenticationToken);
        GenerateTokenResponse token3 = defaultKsefClient.generateKsefToken(request, authenticationToken);

        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.getToken());
        Assertions.assertNotNull(token.getReferenceNumber());

        // step 2: wait for token to become ACTIVE
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    AuthenticationToken ksefToken = defaultKsefClient.getKsefToken(token.getReferenceNumber(), authenticationToken);
                    return ksefToken != null && ksefToken.getStatus() == AuthenticationTokenStatus.ACTIVE;
                });

        AuthenticationToken ksefToken = defaultKsefClient.getKsefToken(token.getReferenceNumber(), authenticationToken);
        Assertions.assertNotNull(ksefToken);
        Assertions.assertEquals(AuthenticationTokenStatus.ACTIVE, ksefToken.getStatus());

        // step 3: filter active tokens
        List<AuthenticationTokenStatus> status = List.of(AuthenticationTokenStatus.ACTIVE);
        Integer pageSize = 10;
        QueryTokensResponse tokens = defaultKsefClient.queryKsefTokens(status, StringUtils.EMPTY, pageSize, authenticationToken);
        List<AuthenticationToken> filteredTokens = tokens.getTokens();
        Assertions.assertNotNull(filteredTokens);
        Assertions.assertEquals(3, filteredTokens.size());

        // step 4: revoke token and wait for REVOKED status
        defaultKsefClient.revokeKsefToken(token.getReferenceNumber(), authenticationToken);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    AuthenticationToken revokedToken = defaultKsefClient.getKsefToken(token.getReferenceNumber(), authenticationToken);
                    return revokedToken != null && revokedToken.getStatus() == AuthenticationTokenStatus.REVOKED;
                });

        AuthenticationToken ksefTokenAfterRevoke = defaultKsefClient.getKsefToken(token.getReferenceNumber(), authenticationToken);
        Assertions.assertNotNull(ksefTokenAfterRevoke);
        Assertions.assertEquals(AuthenticationTokenStatus.REVOKED, ksefTokenAfterRevoke.getStatus());

        // step 5: filter active tokens after revoking one
        QueryTokensResponse tokens2 = defaultKsefClient.queryKsefTokens(status, StringUtils.EMPTY, pageSize, authenticationToken);
        List<AuthenticationToken> filteredTokens2 = tokens2.getTokens();
        Assertions.assertNotNull(filteredTokens2);
        Assertions.assertEquals(2, filteredTokens2.size());
    }
}
