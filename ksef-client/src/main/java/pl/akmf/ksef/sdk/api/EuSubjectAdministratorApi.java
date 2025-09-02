package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.permission.PermissionsOperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.euentity.EuEntityPermissionsGrantRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.GRANT_EU_ADMINISTRATOR_PERMISSION;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class EuSubjectAdministratorApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public EuSubjectAdministratorApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Nadanie uprawnień administratora podmiotu unijnego
     *
     * @param euEntityPermissionsGrantRequest (optional)
     * @return ApiResponse&lt;PermissionsOperationResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<PermissionsOperationResponse> apiV2PermissionsEuEntitiesAdministrationGrantsPost(EuEntityPermissionsGrantRequest euEntityPermissionsGrantRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(GRANT_EU_ADMINISTRATOR_PERMISSION.getUrl(), euEntityPermissionsGrantRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(GRANT_EU_ADMINISTRATOR_PERMISSION.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        try {
            return new ApiResponse<>(
                    response.statusCode(),
                    response.headers(),
                    response.body() == null ? null : objectMapper.readValue(response.body(),
                            new TypeReference<>() {
                            })
            );
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }
}
