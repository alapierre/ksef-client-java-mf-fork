package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.tokens.GenerateTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationToken;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenStatus;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenResponse;
import pl.akmf.ksef.sdk.client.model.auth.QueryTokensResponse;
import pl.akmf.ksef.sdk.client.model.auth.TokenPermissionType;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TokensController {
    private final DefaultKsefClient ksefClient;

    @PostMapping("/generate-token")
    public GenerateTokenResponse generateKsefToken(@RequestHeader(name = "Authorization") String authToken) throws ApiException {
        GenerateTokenRequest request = new GenerateTokenRequestBuilder()
                .withDescription("test description")
                .withPermissions(List.of(TokenPermissionType.INVOICEREAD, TokenPermissionType.INVOICEWRITE, TokenPermissionType.CREDENTIALSREAD))
                .build();
        return ksefClient.generateKsefToken(request, authToken);
    }

    @PostMapping("/tokens")
    public QueryTokensResponse queryKsefTokens(@RequestHeader(name = "Authorization") String authToken) throws ApiException {
        List<AuthenticationTokenStatus> status = List.of(AuthenticationTokenStatus.ACTIVE);
        Integer pageSize = 10;
        return ksefClient.queryKsefTokens(status, StringUtils.EMPTY, pageSize, authToken);
    }

    @GetMapping("/token")
    public AuthenticationToken getKsefToken(@RequestParam String referenceNumber, @RequestHeader(name = "Authorization") String authToken) throws ApiException {

        return ksefClient.getKsefToken(referenceNumber, authToken);
    }

    @PostMapping("/revoke-token")
    public void revokeKsefToken(@RequestParam String referenceNumber, @RequestHeader(name = "Authorization") String authToken) throws ApiException {

        ksefClient.revokeKsefToken(referenceNumber, authToken);
    }
}
