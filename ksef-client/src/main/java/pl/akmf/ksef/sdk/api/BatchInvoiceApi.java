package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.BATCH_SESSION_CLOSE;
import static pl.akmf.ksef.sdk.api.Url.BATCH_SESSION_OPEN;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class BatchInvoiceApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public BatchInvoiceApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Zamknięcie sesji wsadowej
     * Informuje system o tym, że wszystkie pliki zostały przekazane i można rozpocząć ich przetwarzanie.
     *
     * @param referenceNumber Numer referencyjny sesji (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public void apiV2SessionsBatchReferenceNumberClosePostWithHttpInfo(String referenceNumber, String authenticationToken) throws ApiException {
        if (referenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'referenceNumber' when calling apiV2SessionsBatchReferenceNumberClosePost");
        }

        String uri = buildUrlWithParams(BATCH_SESSION_CLOSE.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);

        var response = apiClient.post(uri, null, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(BATCH_SESSION_CLOSE.getOperationId(), response.body(), response.statusCode(), response.headers());
        }
    }

    /**
     * Otwarcie sesji wsadowej
     * Inicjalizacja wysyłki wsadowej paczki faktur.
     *
     * @param openBatchSessionRequest (optional)
     * @return ApiResponse&lt;OpenBatchSessionResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<OpenBatchSessionResponse> batchOpenWithHttpInfo(OpenBatchSessionRequest openBatchSessionRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(BATCH_SESSION_OPEN.getUrl(), openBatchSessionRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(BATCH_SESSION_OPEN.getOperationId(), response.body(), response.statusCode(), response.headers());
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
