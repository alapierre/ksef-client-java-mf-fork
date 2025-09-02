package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.permission.euentityrepresentative.GrantEUEntityRepresentativePermissionsRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionsOperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.euentity.EuEntityPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.euentity.SubjectIdentifier;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EuEntityRepresentativePermissionsController {
    private final DefaultKsefClient ksefClient;

    @PostMapping(value = "/grant-eu-entity-representative-permissions")
    public PermissionsOperationResponse grantPermissionsEntity(@RequestBody SubjectIdentifier subjectIdentifier,
                                                               @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        var request = new GrantEUEntityRepresentativePermissionsRequestBuilder()
                .withSubjectIdentifier(subjectIdentifier)
                .withPermissions(List.of(EuEntityPermissionType.INVOICEREAD, EuEntityPermissionType.INVOICEWRITE))
                .withDescription("Representative access")
                .build();

        return ksefClient.grantsPermissionEUEntityRepresentative(request, authToken);
    }

    @PostMapping(value = "revoke-eu-entity-representative-permissions")
    public PermissionsOperationResponse revokePermissionsEntity(@RequestBody String permissionId,
                                                                @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.revokeAuthorizationsPermission(permissionId, authToken);
    }
}
