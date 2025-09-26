package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.OperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.indirect.GrantIndirectEntityPermissionsRequest;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;
import pl.akmf.ksef.sdk.util.HttpClientBuilder;

import java.net.http.HttpClient;

import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class IndirectPermissionsEntityController {
    private final ExampleApiProperties exampleApiProperties;

    @PostMapping("/grant-indirect-permissions-for-entity")
    public OperationResponse grantPermissionsEntity(@RequestBody GrantIndirectEntityPermissionsRequest request,
                                                    @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            return ksefClient.grantsPermissionIndirectEntity(request, authToken);
        }
    }

    @PostMapping("/revoke-indirect-permissions-for-entity")
    public OperationResponse revokePermissionsEntity(@RequestBody String permissionId,
                                                     @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            return ksefClient.revokeAuthorizationsPermission(permissionId, authToken);
        }
    }
}
