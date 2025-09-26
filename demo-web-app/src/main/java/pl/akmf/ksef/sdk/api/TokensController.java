package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.tokens.KsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationToken;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenStatus;
import pl.akmf.ksef.sdk.client.model.auth.KsefTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenResponse;
import pl.akmf.ksef.sdk.client.model.auth.QueryTokensResponse;
import pl.akmf.ksef.sdk.client.model.auth.TokenPermissionType;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;
import pl.akmf.ksef.sdk.util.HttpClientBuilder;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.CONTINUATION_TOKEN;

@RestController
@RequiredArgsConstructor
public class TokensController {
    private final ExampleApiProperties exampleApiProperties;

    @PostMapping("/generate-token")
    public GenerateTokenResponse generateKsefToken(@RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);
            KsefTokenRequest request = new KsefTokenRequestBuilder()
                    .withDescription("test description")
                    .withPermissions(List.of(TokenPermissionType.INVOICEREAD, TokenPermissionType.INVOICEWRITE, TokenPermissionType.CREDENTIALSREAD))
                    .build();

            return ksefClient.generateKsefToken(request, authToken);
        }
    }

    @PostMapping("/tokens")
    public List<AuthenticationToken> queryKsefTokens(@RequestHeader(name = AUTHORIZATION) String authToken,
                                                     @RequestHeader(name = CONTINUATION_TOKEN, required = false, defaultValue = "") String continuationToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            List<AuthenticationTokenStatus> status = List.of(AuthenticationTokenStatus.ACTIVE);
            Integer pageSize = 10;

            QueryTokensResponse response = ksefClient.queryKsefTokens(status, continuationToken, pageSize, authToken);
            List<AuthenticationToken> authenticationTokens = new ArrayList<>(response.getTokens());
            while (Strings.isNotBlank(response.getContinuationToken())) {
                response = ksefClient.queryKsefTokens(status, response.getContinuationToken(), pageSize, authToken);
                authenticationTokens.addAll(response.getTokens());
            }

            return authenticationTokens;
        }
    }

    @GetMapping("/token")
    public AuthenticationToken getKsefToken(@RequestParam String referenceNumber, @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            return ksefClient.getKsefToken(referenceNumber, authToken);
        }
    }

    @PostMapping("/revoke-token")
    public void revokeKsefToken(@RequestParam String referenceNumber, @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            ksefClient.revokeKsefToken(referenceNumber, authToken);
        }
    }
}
