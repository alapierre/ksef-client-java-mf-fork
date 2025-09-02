package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionInvoiceStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionInvoicesResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static pl.akmf.ksef.sdk.api.Url.SESSION_INVOICE;
import static pl.akmf.ksef.sdk.api.Url.SESSION_INVOICE_FAILED;
import static pl.akmf.ksef.sdk.api.Url.SESSION_INVOICE_GET_BY_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.api.Url.SESSION_INVOICE_UPO_BY_INVOICE_REFERENCE;
import static pl.akmf.ksef.sdk.api.Url.SESSION_INVOICE_UPO_BY_KSEF;
import static pl.akmf.ksef.sdk.api.Url.SESSION_LIST;
import static pl.akmf.ksef.sdk.api.Url.SESSION_STATUS;
import static pl.akmf.ksef.sdk.api.Url.SESSION_UPO;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Headers.CONTINUATION_TOKEN;
import static pl.akmf.ksef.sdk.client.Parameter.DATE_CLOSED_FROM;
import static pl.akmf.ksef.sdk.client.Parameter.DATE_CLOSED_TO;
import static pl.akmf.ksef.sdk.client.Parameter.DATE_CREATED_FROM;
import static pl.akmf.ksef.sdk.client.Parameter.DATE_CREATED_TO;
import static pl.akmf.ksef.sdk.client.Parameter.DATE_MODIFIED_FROM;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_OFFSET;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_SIZE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_INVOICE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_KSEF_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_UPO_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.SESSION_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.STATUS;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class SendStatusAndUpoApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public SendStatusAndUpoApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Pobranie niepoprawnie przetworzonych faktur sesji
     * Zwraca listę niepoprawnie przetworzonych faktur przesłanych w sesji wraz z ich statusami.
     *
     * @param referenceNumber    Numer referencyjny sesji. (required)
     * @param xContinuationToken Token służący do pobrania kolejnej strony wyników. (optional)
     * @param pageSize           Rozmiar strony wyników. (optional, default to 10)
     * @return ApiResponse&lt;SessionInvoicesResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<SessionInvoicesResponse> apiV2SessionsReferenceNumberInvoicesFailedGet(String referenceNumber, String xContinuationToken, Integer pageSize, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));

        String uri = buildUrlWithParams(SESSION_INVOICE_FAILED.getUrl(), params)
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        if (xContinuationToken != null) {
            headers.put(CONTINUATION_TOKEN, xContinuationToken);
        }

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_INVOICE_FAILED.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie faktur sesji
     * Zwraca listę faktur przesłanych w sesji wraz z ich statusami, oraz informacje na temat ilości poprawnie i niepoprawnie przetworzonych faktur.
     *
     * @param referenceNumber Numer referencyjny sesji. (required)
     * @param pageOffset      Numer strony wyników. (optional, default to 0)
     * @param pageSize        Rozmiar strony wyników. (optional, default to 10)
     * @return ApiResponse&lt;SessionInvoicesResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<SessionInvoicesResponse> apiV2SessionsReferenceNumberInvoicesGet(String referenceNumber, Integer pageOffset, Integer pageSize, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));

        String uri = buildUrlWithParams(SESSION_INVOICE.getUrl(), params)
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_INVOICE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie statusu faktury z sesji
     * Zwraca fakturę przesłaną w sesji wraz ze statusem.
     *
     * @param referenceNumber        Numer referencyjny sesji. (required)
     * @param invoiceReferenceNumber Numer referencyjny faktury. (required)
     * @return ApiResponse&lt;SessionInvoice&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<SessionInvoiceStatusResponse> apiV2SessionsReferenceNumberInvoicesInvoiceReferenceNumberGet(String referenceNumber, String invoiceReferenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(SESSION_INVOICE_GET_BY_REFERENCE_NUMBER.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber)
                .replace(PATH_INVOICE_NUMBER, invoiceReferenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_INVOICE_GET_BY_REFERENCE_NUMBER.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie UPO faktury z sesji na podstawie numeru referencyjnego faktury
     * Zwraca UPO faktury przesłanego w sesji na podstawie jego numeru KSeF.
     *
     * @param referenceNumber        Numer referencyjny sesji. (required)
     * @param invoiceReferenceNumber Numer referencyjny faktury. (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<byte[]> apiV2SessionsReferenceNumberInvoicesInvoiceReferenceNumberUpoGet(String referenceNumber, String invoiceReferenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(SESSION_INVOICE_UPO_BY_INVOICE_REFERENCE.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber)
                .replace(PATH_INVOICE_NUMBER, invoiceReferenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_INVOICE_UPO_BY_INVOICE_REFERENCE.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        return new ApiResponse<>(
                response.statusCode(),
                response.headers(),
                response.body()
        );
    }

    /**
     * Pobranie UPO faktury z sesji na podstawie numeru KSeF
     * Zwraca UPO faktury przesłanego w sesji na podstawie jego numeru KSeF.
     *
     * @param referenceNumber Numer referencyjny sesji. (required)
     * @param ksefNumber      Numer KSeF faktury. (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<byte[]> apiV2SessionsReferenceNumberInvoicesKsefKsefNumberUpoGet(String referenceNumber, String ksefNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(SESSION_INVOICE_UPO_BY_KSEF.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber)
                .replace(PATH_KSEF_NUMBER, ksefNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_INVOICE_UPO_BY_KSEF.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        return new ApiResponse<>(
                response.statusCode(),
                response.headers(),
                response.body()
        );
    }

    /**
     * Pobranie statusu sesji
     * Sprawdza bieżący status sesji o podanym numerze referencyjnym.
     *
     * @param referenceNumber Numer referencyjny sesji. (required)
     * @return ApiResponse&lt;SessionStatusResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<SessionStatusResponse> apiV2SessionsReferenceNumberGet(String referenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(SESSION_STATUS.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_STATUS.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie UPO dla sesji
     * Zwraca XML zawierający zbiorcze UPO dla sesji.
     *
     * @param referenceNumber    Numer referencyjny sesji. (required)
     * @param upoReferenceNumber Numer referencyjny UPO. (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<byte[]> apiV2SessionsReferenceNumberUpoUpoReferenceNumberGet(String referenceNumber, String upoReferenceNumber, String authenticationToken) throws ApiException {
        if (referenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'referenceNumber' when calling apiV2SessionsReferenceNumberUpoUpoReferenceNumberGet");
        }
        if (upoReferenceNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'upoReferenceNumber' when calling apiV2SessionsReferenceNumberUpoUpoReferenceNumberGet");
        }

        String uri = buildUrlWithParams(SESSION_UPO.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber)
                .replace(PATH_UPO_REFERENCE_NUMBER, upoReferenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_UPO.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        return new ApiResponse<>(
                response.statusCode(),
                response.headers(),
                response.body());
    }

    /**
     * Pobranie listy sesji
     * Zwraca listę sesji spełniających podane kryteria wyszukiwania.
     *
     * @param request           enkapsulowane wszystkie pola requesta
     * @param pageSize          page size
     * @param continuationToken continuation token
     * @return ApiResponse&lt;SessionsQueryResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<SessionsQueryResponse> apiV2SessionsGet(SessionsQueryRequest request, Integer pageSize, String continuationToken, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));

        if(Objects.nonNull(request.getSessionType())){
            params.put(SESSION_TYPE, request.getSessionType().getValue());
        }

        params.put(REFERENCE_NUMBER, request.getReferenceNumber());

        if(Objects.nonNull(request.getDateCreatedFrom())){
            params.put(DATE_CREATED_FROM, request.getDateCreatedFrom().toString());
        }

        if(Objects.nonNull(request.getDateCreatedTo())){
            params.put(DATE_CREATED_TO, request.getDateCreatedTo().toString());
        }

        if(Objects.nonNull(request.getDateClosedFrom())){
            params.put(DATE_CLOSED_FROM, request.getDateClosedFrom().toString());
        }

        if(Objects.nonNull(request.getDateClosedTo())){
            params.put(DATE_CLOSED_TO, request.getDateClosedTo().toString());
        }

        if(Objects.nonNull(request.getDateModifiedFrom())){
            params.put(DATE_MODIFIED_FROM, request.getDateModifiedFrom().toString());
        }

        if(Objects.nonNull(request.getStatuses())){
            request.getStatuses().forEach(stat -> params.put(STATUS, stat.getValue()));
        }

        String uri = buildUrlWithParams(SESSION_LIST.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        if (continuationToken != null) {
            headers.put(CONTINUATION_TOKEN, continuationToken);
        }

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SESSION_LIST.getOperationId(), response.body(), response.statusCode(), response.headers());
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
