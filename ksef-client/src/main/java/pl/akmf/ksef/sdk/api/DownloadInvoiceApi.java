package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.invoice.AsyncInvoicesQueryStatus;
import pl.akmf.ksef.sdk.client.model.invoice.DownloadInvoiceRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InitAsyncInvoicesQueryResponse;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InvoicesAsyncQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.QueryInvoiceMetadataResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.INVOICE_DOWNLOAD;
import static pl.akmf.ksef.sdk.api.Url.INVOICE_DOWNLOAD_BY_KSEF;
import static pl.akmf.ksef.sdk.api.Url.INVOICE_QUERY_ASYNC;
import static pl.akmf.ksef.sdk.api.Url.INVOICE_QUERY_METADATA;
import static pl.akmf.ksef.sdk.api.Url.INVOICE_QUERY_STATUS;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_OFFSET;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_SIZE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_KSEF_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_OPERATION_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class DownloadInvoiceApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public DownloadInvoiceApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Pobranie dokumentu po numerze KSeF
     * Zwraca dokument o podanym numerze KSeF.
     *
     * @param ksefReferenceNumber Numer KSeF dokumentu (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<byte[]> apiV2InvoicesKsefKsefReferenceNumberGet(String ksefReferenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(INVOICE_DOWNLOAD_BY_KSEF.getUrl(), new HashMap<>())
                .replace(PATH_KSEF_REFERENCE_NUMBER, ksefReferenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(INVOICE_DOWNLOAD_BY_KSEF.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        return new ApiResponse<>(
                response.statusCode(),
                response.headers(),
                response.body()
        );
    }

    /**
     * Sprawdza status asynchronicznego zapytania o pobranie faktur
     * Pobiera status wcześniej zainicjalizowanego zapytania asynchronicznego na podstawie identyfikatora operacji. Umożliwia śledzenie postępu przetwarzania zapytania oraz pobranie gotowych paczek z wynikami, jeśli są już dostępne.
     *
     * @param operationReferenceNumber Unikalny identyfikator operacji zwrócony podczas inicjalizacji zapytania. (required)
     * @return ApiResponse&lt;AsyncInvoicesQueryStatus&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<AsyncInvoicesQueryStatus> apiV2InvoicesAsyncQueryOperationReferenceNumberGet(String operationReferenceNumber, String authenticationToken) throws ApiException {
        if (operationReferenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'operationReferenceNumber' when calling apiV2InvoicesAsyncQueryOperationReferenceNumberGet");
        }

        String uri = buildUrlWithParams(INVOICE_QUERY_STATUS.getUrl(), new HashMap<>())
                .replace(PATH_OPERATION_REFERENCE_NUMBER, operationReferenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(INVOICE_QUERY_STATUS.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Inicjalizuje asynchroniczne zapytanie o pobranie faktur
     * Rozpoczyna asynchroniczny proces wyszukiwania faktur w systemie KSeF na podstawie przekazanych filtrów. Wymagane jest przekazanie informacji o szyfrowaniu w polu &#x60;Encryption&#x60;, które służą do zaszyfrowania wygenerowanych paczek z fakturami.
     *
     * @param invoicesAsyncQueryRequest Zestaw filtrów dla wyszukiwania faktur. (optional)
     * @return ApiResponse&lt;InitAsyncInvoicesQueryResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<InitAsyncInvoicesQueryResponse> apiV2InvoicesAsyncQueryPost(InvoicesAsyncQueryRequest invoicesAsyncQueryRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(INVOICE_QUERY_ASYNC.getUrl(), invoicesAsyncQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(INVOICE_QUERY_ASYNC.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie faktury  na podstawie numeru KSeF oraz danych faktury
     * Faktura zostanie zwrócona wyłącznie, jeśli wszystkie dane wejściowe są zgodne z danymi faktury w systemie.
     *
     * @param downloadInvoiceRequest (optional)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<byte[]> apiV2InvoicesDownloadPost(DownloadInvoiceRequest downloadInvoiceRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(INVOICE_DOWNLOAD.getUrl(), downloadInvoiceRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(INVOICE_DOWNLOAD.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        return new ApiResponse<>(
                response.statusCode(),
                response.headers(),
                response.body());
    }

    /**
     * Pobranie listy metadanych faktur
     * Zwraca listę metadanych faktur spełniające podane kryteria wyszukiwania. Wymagane uprawnienia: `InvoiceRead`.",
     *
     * @param pageOffset                  Indeks pierwszej strony wyników (domyślnie 0). (optional)
     * @param pageSize                    Rozmiar strony wyników(domyślnie 10). (optional)
     * @param invoiceMetadataQueryRequest Zestaw filtrów dla wyszukiwania metadanych. (optional)
     * @return ApiResponse&lt;QueryInvoicesReponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<QueryInvoiceMetadataResponse> apiV2InvoicesQueryMetadataPost(Integer pageOffset, Integer pageSize, InvoiceMetadataQueryRequest invoiceMetadataQueryRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(INVOICE_QUERY_METADATA.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, invoiceMetadataQueryRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(INVOICE_QUERY_METADATA.getOperationId(), response.body(), response.statusCode(), response.headers());
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
