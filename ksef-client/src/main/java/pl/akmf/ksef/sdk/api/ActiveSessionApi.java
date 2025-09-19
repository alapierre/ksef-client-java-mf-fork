package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.session.AuthenticationListResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.SESSION_ACTIVE_SESSIONS;
import static pl.akmf.ksef.sdk.api.Url.SESSION_REVOKE_CURRENT_SESSION;
import static pl.akmf.ksef.sdk.api.Url.SESSION_REVOKE_SESSION;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Headers.CONTINUATION_TOKEN;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_SIZE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class ActiveSessionApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public ActiveSessionApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Unieważnienie aktualnej sesji uwierzytelnienia
     * Unieważnia sesję powiązaną z tokenem użytym do wywołania tej operacji.  Unieważnienie sesji sprawia, że powiązany z nią refresh token przestaje działać i nie można już za jego pomocą uzyskać kolejnych access tokenów. **Aktywne access tokeny działają do czasu minięcia ich termin ważności.**  Sposób uwierzytelnienia: &#x60;RefreshToken&#x60; lub &#x60;ContextToken&#x60;.
     *
     * @throws ApiException if fails to make API call
     */
    public void apiV2AuthSessionsCurrentDelete(String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);

        var response = apiClient.delete(SESSION_REVOKE_CURRENT_SESSION.getUrl(), headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_REVOKE_CURRENT_SESSION.getOperationId(), response.body(), response.statusCode(), response.headers());
        }
    }

    /**
     * Pobranie listy aktywnych sesji
     * Zwraca listę aktywnych sesji uwierzytelnienia.
     *
     * @param pageSize          Rozmiar strony wyników. (optional, default to 10)
     * @param continuationToken
     * @return ApiResponse&lt;AuthenticationListResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AuthenticationListResponse> apiV2AuthSessionsGet(Integer pageSize, String continuationToken, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        String uri = buildUrlWithParams(SESSION_ACTIVE_SESSIONS.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        if (continuationToken != null) {
            headers.put(CONTINUATION_TOKEN, continuationToken);

        }
        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_ACTIVE_SESSIONS.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Unieważnienie sesji uwierzytelnienia
     * Unieważnia sesję o podanym numerze referencyjnym.  Unieważnienie sesji sprawia, że powiązany z nią refresh token przestaje działać i nie można już za jego pomocą uzyskać kolejnych access tokenów. **Aktywne access tokeny działają do czasu minięcia ich termin ważności.**
     *
     * @param referenceNumber Numer referencyjny sesji uwierzytelnienia. (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> apiV2AuthSessionsReferenceNumberDelete(String referenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(SESSION_REVOKE_SESSION.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);

        var response = apiClient.delete(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_REVOKE_SESSION.getOperationId(), response.body(), response.statusCode(), response.headers());
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
