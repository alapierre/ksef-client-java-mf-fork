package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionsOperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.subunit.SubunitPermissionsGrantRequest;

@RestController
@RequiredArgsConstructor
public class SubUnitPermissionsController {
    private final DefaultKsefClient ksefClient;

    @PostMapping("/grant-sub-entity-permissions")
    public PermissionsOperationResponse grantPermissionsEntity(@RequestBody SubunitPermissionsGrantRequest request, @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.grantsPermissionSubUnit(request, authToken);
    }

    @PostMapping("/revoke-sub-entity-permissions")
    public PermissionsOperationResponse revokePermissionsEntity(@RequestBody String permissionId, @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.revokeAuthorizationsPermission(permissionId, authToken);
    }
}
