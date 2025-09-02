package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.builders.permission.entity.GrantEntityPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionsOperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.entity.EntityPermission;
import pl.akmf.ksef.sdk.client.model.permission.entity.EntityPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.entity.SubjectIdentifier;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EntityPermissionsController {
    private final DefaultKsefClient ksefClient;

    @PostMapping("/grant-permissions-for-entity")
    public PermissionsOperationResponse grantPermissionsEntity(@RequestBody SubjectIdentifier subjectIdentifier,
                                                               @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        var request = new GrantEntityPermissionsRequestBuilder()
                .withSubjectIdentifier(subjectIdentifier)
                .withPermissions(List.of(
                        new EntityPermission(EntityPermissionType.INVOICEREAD, true),
                        new EntityPermission(EntityPermissionType.INVOICEREAD, false))
                )
                .withDescription("Access for quarterly review")
                .build();

        return ksefClient.grantsPermissionEntity(request, authToken);
    }

    @PostMapping("/revoke-permissions-for-entity")
    public PermissionsOperationResponse revokePermissionsEntity(@RequestBody String permissionId,
                                                                @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.revokeCommonPermission(permissionId, authToken);
    }
}
