package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.PermissionsOperationResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.PERMISSION_REVOKE_AUTHORIZATION;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_REVOKE_COMMON;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_STATUS;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_PERMISSION_ID;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class OperationApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public OperationApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Pobranie statusu operacji
     *
     * @param referenceNumber Numer referencyjny operacji (required)
     * @return ApiResponse&lt;PermissionsOperationStatusResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<PermissionStatusInfo> apiV2PermissionsOperationsReferenceNumberGet(String referenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(PERMISSION_STATUS.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_STATUS.getOperationId(), response.body(), response.statusCode(), response.headers());
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

    /**
     * Odebranie uprawnień o charakterze upoważnień
     * Rozpoczyna asynchroniczną operacje odbierania uprawnienia o podanym identyfikatorze. Ta metoda służy do odbierania uprawnień o charakterze upoważnień.
     *
     * @param permissionId Id uprawnienia. (required)
     * @return ApiResponse&lt;PermissionsOperationResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<PermissionsOperationResponse> apiV2PermissionsAuthorizationsGrantsPermissionIdDelete(String permissionId, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(PERMISSION_REVOKE_AUTHORIZATION.getUrl(), new HashMap<>())
                .replace(PATH_PERMISSION_ID, permissionId);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.delete(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_REVOKE_AUTHORIZATION.getOperationId(), response.body(), response.statusCode(), response.headers());
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

    /**
     * Odebranie uprawnień
     * Rozpoczyna asynchroniczną operacje odbierania uprawnienia o podanym identyfikatorze.  Ta metoda służy do odbierania uprawnień takich jak: - nadanych osobom fizycznym do pracy w KSeF - nadanych podmiotom do obsługi faktur - nadanych w sposób pośredni - administratora podmiotu podrzędnego - administratora podmiotu unijnego - reprezentanta podmiotu unijnego
     *
     * @param permissionId Id uprawnienia. (required)
     * @return ApiResponse&lt;PermissionsOperationResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<PermissionsOperationResponse> apiV2PermissionsCommonGrantsPermissionIdDelete(String permissionId, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(PERMISSION_REVOKE_COMMON.getUrl(), new HashMap<>())
                .replace(PATH_PERMISSION_ID, permissionId);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.delete(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_REVOKE_COMMON.getOperationId(), response.body(), response.statusCode(), response.headers());
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
