package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationToken;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenStatus;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenResponse;
import pl.akmf.ksef.sdk.client.model.auth.QueryTokensResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.TOKEN_GENERATE;
import static pl.akmf.ksef.sdk.api.Url.TOKEN_LIST;
import static pl.akmf.ksef.sdk.api.Url.TOKEN_REVOKE;
import static pl.akmf.ksef.sdk.api.Url.TOKEN_STATUS;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Headers.CONTINUATION_TOKEN;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_SIZE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.STATUS;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class TokensApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public TokensApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Pobranie listy wygenerowanych tokenów
     *
     * @param status             Status tokenów do zwrócenia. W przypadku braku parametru zwracane są wszystkie tokeny. Parametr można przekazać wielokrotnie. (optional)
     * @param xContinuationToken Token służący do pobrania kolejnej strony wyników. (optional)
     * @param pageSize           Rozmiar strony wyników. (optional, default to 10)
     * @return ApiResponse&lt;QueryTokensResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QueryTokensResponse> apiV2TokensGet(List<AuthenticationTokenStatus> status, String xContinuationToken, Integer pageSize, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        status.forEach(stat -> params.put(STATUS, stat.toString()));
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        String uri = buildUrlWithParams(TOKEN_LIST.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        if (xContinuationToken != null) {
            headers.put(CONTINUATION_TOKEN, xContinuationToken);

        }
        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(TOKEN_LIST.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Wygenerowanie nowego tokena
     *
     * @param generateTokenRequest (optional)
     * @return ApiResponse&lt;GenerateTokenResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<GenerateTokenResponse> apiV2TokensPost(GenerateTokenRequest generateTokenRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(TOKEN_GENERATE.getUrl(), generateTokenRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(TOKEN_GENERATE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Unieważnienie tokena
     * Unieważniony token nie pozwoli już na uwierzytelnienie się za jego pomocą. Unieważnienie nie może zostać cofnięte.
     *
     * @param referenceNumber Numer referencyjny tokena do unieważniania. (required)
     * @throws ApiException if fails to make API call
     */
    public void apiV2TokensReferenceNumberDelete(String referenceNumber, String authenticationToken) throws ApiException {
        String uri = TOKEN_REVOKE.getUrl()
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);

        var response = apiClient.delete(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(TOKEN_REVOKE.getOperationId(), response.body(), response.statusCode(), response.headers());
        }
    }

    /**
     * Pobranie statusu tokena
     *
     * @param referenceNumber Numer referencyjny tokena. (required)
     * @return ApiResponse&lt;AuthenticationToken&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AuthenticationToken> apiV2TokensReferenceNumberGet(String referenceNumber, String authenticationToken) throws ApiException {
        String uri = TOKEN_STATUS.getUrl()
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(TOKEN_STATUS.getOperationId(), response.body(), response.statusCode(), response.headers());
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
