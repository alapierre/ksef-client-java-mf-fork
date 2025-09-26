package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.permission.euentityrepresentative.GrantEUEntityRepresentativePermissionsRequestBuilder;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.OperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.euentity.EuEntityPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.euentity.SubjectIdentifier;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;
import pl.akmf.ksef.sdk.util.HttpClientBuilder;

import java.net.http.HttpClient;
import java.util.List;

import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class EuEntityRepresentativePermissionsController {
    private final ExampleApiProperties exampleApiProperties;

    @PostMapping(value = "/grant-eu-entity-representative-permissions")
    public OperationResponse grantPermissionsEntity(@RequestBody SubjectIdentifier subjectIdentifier,
                                                    @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            var request = new GrantEUEntityRepresentativePermissionsRequestBuilder()
                    .withSubjectIdentifier(subjectIdentifier)
                    .withPermissions(List.of(EuEntityPermissionType.INVOICEREAD, EuEntityPermissionType.INVOICEWRITE))
                    .withDescription("Representative access")
                    .build();

            return ksefClient.grantsPermissionEUEntityRepresentative(request, authToken);
        }
    }

    @PostMapping(value = "revoke-eu-entity-representative-permissions")
    public OperationResponse revokePermissionsEntity(@RequestBody String permissionId,
                                                     @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            return ksefClient.revokeAuthorizationsPermission(permissionId, authToken);
        }
    }
}
