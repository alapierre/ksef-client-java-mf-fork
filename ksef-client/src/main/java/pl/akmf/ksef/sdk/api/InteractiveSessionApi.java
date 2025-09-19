package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceRequest;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.SESSION_CLOSE;
import static pl.akmf.ksef.sdk.api.Url.SESSION_INVOICE_SEND;
import static pl.akmf.ksef.sdk.api.Url.SESSION_OPEN;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class InteractiveSessionApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public InteractiveSessionApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Zamknięcie sesji interaktywnej
     * Zamyka sesję interaktywną i rozpoczyna generowanie zbiorczego UPO.
     *
     * @param referenceNumber Numer referencyjny sesji (required)
     * @throws ApiException if fails to make API call
     */
    public void apiV2SessionsOnlineReferenceNumberClosePostWithHttpInfo(String referenceNumber, String authenticationToken) throws ApiException {
        if (referenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'referenceNumber' when calling apiV2SessionsOnlineReferenceNumberClosePost");
        }

        String uri = buildUrlWithParams(SESSION_CLOSE.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, null, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_CLOSE.getOperationId(), response.body(), response.statusCode(), response.headers());
        }
    }

    /**
     * Wysłanie faktury
     * Przyjmuje zaszyfrowaną fakturę oraz jej metadane i rozpoczyna jej przetwarzanie.
     *
     * @param referenceNumber    Numer referencyjny sesji (required)
     * @param sendInvoiceRequest (optional)
     * @return ApiResponse&lt;SendDocumentResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<SendInvoiceResponse> apiV2SessionsOnlineReferenceNumberInvoicesPostWithHttpInfo(String referenceNumber, SendInvoiceRequest sendInvoiceRequest, String authenticationToken) throws ApiException {
        if (referenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'referenceNumber' when calling apiV2SessionsOnlineReferenceNumberClosePost");
        }

        String uri = buildUrlWithParams(SESSION_INVOICE_SEND.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, sendInvoiceRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_INVOICE_SEND.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Otwarcie sesji interaktywnej
     * Inicjalizacja wysyłki interaktywnej faktur.
     *
     * @param openOnlineSessionRequest (optional)
     * @return ApiResponse&lt;OpenOnlineSessionResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<OpenOnlineSessionResponse> onlineSessionOpenWithHttpInfo(OpenOnlineSessionRequest openOnlineSessionRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(SESSION_OPEN.getUrl(), openOnlineSessionRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_OPEN.getOperationId(), response.body(), response.statusCode(), response.headers());
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
