package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OperationStatusController {

    private final DefaultKsefClient ksefClient;

    @GetMapping("{referenceNumber}/status")
    public PermissionStatusInfo getOperationStatusAsync(@PathVariable String referenceNumber,
                                                        @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        var statusInfo = ksefClient.permissionOperationStatus(referenceNumber, authToken);

        log.info("response " + statusInfo);
        return statusInfo;
    }
}
