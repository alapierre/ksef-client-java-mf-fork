package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.permission.person.GrantPersonPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.OperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.person.GrantPersonPermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.person.PersonPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.person.PersonPermissionsSubjectIdentifier;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;
import pl.akmf.ksef.sdk.util.HttpClientBuilder;

import java.net.http.HttpClient;
import java.util.List;

import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;

@RestController
@RequestMapping("/person-permission")
@RequiredArgsConstructor
public class PersonPermissionController {
    private final ExampleApiProperties exampleApiProperties;

    @PostMapping("/grant-permissions-for-person")
    public OperationResponse grantPermissionsPerson(
            @RequestBody PersonPermissionsSubjectIdentifier subjectIdentifier,
            @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            GrantPersonPermissionsRequest request = new GrantPersonPermissionsRequestBuilder()
                    .withSubjectIdentifier(subjectIdentifier)
                    .withPermissions(List.of(PersonPermissionType.INVOICEREAD, PersonPermissionType.INVOICEWRITE))
                    .withDescription("Access for quarterly review")
                    .build();

            return ksefClient.grantsPermissionPerson(request, authToken);
        }
    }

    @PostMapping("/revoke-permissions-for-person")
    public OperationResponse revokePermissionsPerson(
            @RequestBody String permissionId,
            @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            return ksefClient.revokeCommonPermission(permissionId, authToken);
        }
    }
}

