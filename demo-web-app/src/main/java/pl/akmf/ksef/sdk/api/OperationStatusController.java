package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;
import pl.akmf.ksef.sdk.util.HttpClientBuilder;

import java.net.http.HttpClient;

import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class OperationStatusController {
    private final ExampleApiProperties exampleApiProperties;

    @GetMapping("{referenceNumber}/status")
    public PermissionStatusInfo getOperationStatusAsync(@PathVariable String referenceNumber,
                                                        @RequestHeader(name = AUTHORIZATION) String authToken) throws ApiException {
        try (HttpClient apiClient = HttpClientBuilder.createHttpBuilder().build()) {
            KSeFClient ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);

            return ksefClient.permissionOperationStatus(referenceNumber, authToken);
        }
    }
}
