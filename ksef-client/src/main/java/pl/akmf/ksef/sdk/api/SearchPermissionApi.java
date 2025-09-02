package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.EntityAuthorizationPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.EuEntityPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityAuthorizationPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityRolesResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEuEntityPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryPersonPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QuerySubordinateEntityRolesResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QuerySubunitPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.SubordinateEntityRolesQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.SubunitPermissionsQueryRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.PERMISSION_SEARCH_AUTHORIZATIONS_GRANT;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_SEARCH_ENTITY_ROLES;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_SEARCH_EU_ENTITY_GRANT;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_SEARCH_PERSON_PERMISSION;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_SEARCH_SUBORDINATE_PERMISSION;
import static pl.akmf.ksef.sdk.api.Url.PERMISSION_SEARCH_SUBUNIT_GRANT;
import static pl.akmf.ksef.sdk.api.Url.SECURITY_PUBLIC_KEY_CERTIFICATE;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_OFFSET;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_SIZE;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class SearchPermissionApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public SearchPermissionApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Pobranie listy uprawnień o charakterze uprawnień nadanych podmiotom
     *
     * @param pageOffset                                 (optional)
     * @param pageSize                                   (optional)
     * @param entityAuthorizationPermissionsQueryRequest (optional)
     * @return ApiResponse&lt;QueryEntityAuthorizationPermissionsResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QueryEntityAuthorizationPermissionsResponse> apiV2PermissionsQueryAuthorizationsGrantsPost(Integer pageOffset, Integer pageSize, EntityAuthorizationPermissionsQueryRequest entityAuthorizationPermissionsQueryRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(PERMISSION_SEARCH_AUTHORIZATIONS_GRANT.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, entityAuthorizationPermissionsQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_SEARCH_AUTHORIZATIONS_GRANT.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie listy uprawnień do obsługi faktur nadanych podmiotom
     *
     * @param pageOffset (optional)
     * @param pageSize   (optional)
     * @return ApiResponse&lt;QueryEntityRolesResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QueryEntityRolesResponse> apiV2PermissionsQueryEntitiesRolesGet(Integer pageOffset, Integer pageSize, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(PERMISSION_SEARCH_ENTITY_ROLES.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_SEARCH_ENTITY_ROLES.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie listy uprawnień nadanych podmiotom unijnym
     *
     * @param pageOffset                      (optional)
     * @param pageSize                        (optional)
     * @param euEntityPermissionsQueryRequest (optional)
     * @return ApiResponse&lt;QueryEuEntityPermissionsResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QueryEuEntityPermissionsResponse> apiV2PermissionsQueryEuEntitiesGrantsPost(Integer pageOffset, Integer pageSize, EuEntityPermissionsQueryRequest euEntityPermissionsQueryRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(PERMISSION_SEARCH_EU_ENTITY_GRANT.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, euEntityPermissionsQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_SEARCH_EU_ENTITY_GRANT.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie listy uprawnień do pracy w KSeF nadanych osobom fizycznym
     *
     * @param pageOffset                    (optional)
     * @param pageSize                      (optional)
     * @param personPermissionsQueryRequest (optional)
     * @return ApiResponse&lt;QueryPersonPermissionsResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QueryPersonPermissionsResponse> apiV2PermissionsQueryPersonsGrantsPost(Integer pageOffset, Integer pageSize, PersonPermissionsQueryRequest personPermissionsQueryRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(PERMISSION_SEARCH_PERSON_PERMISSION.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, personPermissionsQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_SEARCH_PERSON_PERMISSION.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie listy uprawnień do obsługi faktur nadanych podmiotom
     *
     * @param pageOffset                         (optional)
     * @param pageSize                           (optional)
     * @param subordinateEntityRolesQueryRequest (optional)
     * @return ApiResponse&lt;QuerySubordinateEntityRolesResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QuerySubordinateEntityRolesResponse> apiV2PermissionsQuerySubordinateEntitiesRolesPost(Integer pageOffset, Integer pageSize, SubordinateEntityRolesQueryRequest subordinateEntityRolesQueryRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(PERMISSION_SEARCH_SUBORDINATE_PERMISSION.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, subordinateEntityRolesQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_SEARCH_SUBORDINATE_PERMISSION.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie listy uprawnień administratora podmiotu podrzędnego
     *
     * @param pageOffset                     (optional)
     * @param pageSize                       (optional)
     * @param subunitPermissionsQueryRequest (optional)
     * @return ApiResponse&lt;QuerySubunitPermissionsResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QuerySubunitPermissionsResponse> apiV2PermissionsQuerySubunitsGrantsPost(Integer pageOffset, Integer pageSize, SubunitPermissionsQueryRequest subunitPermissionsQueryRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(PERMISSION_SEARCH_SUBUNIT_GRANT.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, subunitPermissionsQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(PERMISSION_SEARCH_SUBUNIT_GRANT.getOperationId(), response.body(), response.statusCode(), response.headers());
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
