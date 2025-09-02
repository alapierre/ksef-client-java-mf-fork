package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthKsefTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationChallengeResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationInitResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationOperationStatusResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenRefreshResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.AUTH_CHALLENGE;
import static pl.akmf.ksef.sdk.api.Url.AUTH_KSEF_TOKEN;
import static pl.akmf.ksef.sdk.api.Url.AUTH_TOKEN_REEDEM;
import static pl.akmf.ksef.sdk.api.Url.AUTH_TOKEN_SIGNATURE;
import static pl.akmf.ksef.sdk.api.Url.AUTH_TOKEN_STATUS;
import static pl.akmf.ksef.sdk.api.Url.JWT_TOKEN_REFRESH;
import static pl.akmf.ksef.sdk.api.Url.JWT_TOKEN_REVOKE;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.VERIFY_CERTIFICATE_CHAIN;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class AuthenticationApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public AuthenticationApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Inicjalizacja mechanizmu uwierzytelnienia i autoryzacji
     *
     * @return ApiResponse&lt;AuthenticationChallengeResponse&gt;
     * @throws ApiException if fails to make API call
     */

    public ApiResponse<AuthenticationChallengeResponse> apiV2AuthChallengePostWithHttpInfo() throws ApiException {
        Map<String, String> headers = new HashMap<>();

        var response = apiClient.post(AUTH_CHALLENGE.getUrl(), null, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(AUTH_CHALLENGE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Unieważnienie tokena autoryzacyjnego
     * Unieważnia aktualny (przekazany w nagłówku wywołania tej metody) token.
     * Po unieważnieniu tokena nie będzie można za jego pomocą wykonywać żadnych operacji.
     *
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> apiV2AuthTokenDeleteWithHttpInfo(String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);

        var response = apiClient.post(JWT_TOKEN_REVOKE.getUrl(), null, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(JWT_TOKEN_REVOKE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * [Mock] Odświeżenie tokena autoryzacyjnego
     *
     * @return ApiResponse&lt;AuthenticationTokenRefreshResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AuthenticationTokenRefreshResponse> apiV2AuthTokenRefreshPostWithHttpInfo(String refreshToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + refreshToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(JWT_TOKEN_REFRESH.getUrl(), null, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(JWT_TOKEN_REFRESH.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Rozpoczęcie procesu uwierzytelniania
     * Rozpoczyna proces uwierzytelniania na podstawie podpisanego XMLa.
     *
     * @param body (required)
     * @return ApiResponse&lt;AuthenticationInitResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AuthenticationInitResponse> apiV2AuthTokenSignaturePostWithHttpInfo(String body, boolean verifyCertificateChain) throws ApiException {
        if (body == null) {
            throw new ApiException(400, "Missing the required parameter 'body' when calling apiV2AuthTokenSignaturePost");
        }

        var params = new HashMap<String, String>();
        params.put(VERIFY_CERTIFICATE_CHAIN, String.valueOf(verifyCertificateChain));

        String uri = buildUrlWithParams(AUTH_TOKEN_SIGNATURE.getUrl(), params);
        Map<String, String> headers = new HashMap<>();
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, body, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(AUTH_TOKEN_SIGNATURE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Rozpoczęcie procesu uwierzytelniania tokenem
     * Rozpoczyna proces uwierzytelniania na podstawie tokenu
     *
     * @param body (required)
     * @return ApiResponse&lt;AuthenticationInitResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AuthenticationInitResponse> apiV2AuthTokeKSeFPostWithHttpInfo(AuthKsefTokenRequest body) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(AUTH_KSEF_TOKEN.getUrl(), body, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(AUTH_KSEF_TOKEN.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie statusu operacji uwierzytelniania
     * Zwraca status trwającej operacji uwierzytelniania
     *
     * @param referenceNumber numer referencyjny związany z procesem uwierzytelnienia
     * @return ApiResponse&lt;AuthenticationOperationStatusResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<pl.akmf.ksef.sdk.client.model.session.AuthenticationOperationStatusResponse> apiV2AuthTokenTokenReferenceNumberGetWithHttpInfo(String referenceNumber, String authenticationToken) throws ApiException {
        if (referenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'tokenReferenceNumber' when calling apiV2AuthTokenTokenReferenceNumberGet");
        }

        String uri = buildUrlWithParams(AUTH_TOKEN_STATUS.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(AUTH_TOKEN_STATUS.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie tokenów uwierzytelnienia
     * Zwraca token dostępowy oraz token odswieżający
     *
     * @return ApiResponse&lt;AuthenticationOperationStatusResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AuthenticationOperationStatusResponse> apiV2AuthRedeemTokenPost(String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(AUTH_TOKEN_REEDEM.getUrl(), null, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(AUTH_TOKEN_REEDEM.getOperationId(), response.body(), response.statusCode(), response.headers());
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
