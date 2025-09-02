package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentStatusResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentsInfoResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateLimitsResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateListRequest;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateListResponse;
import pl.akmf.ksef.sdk.client.model.certificate.QueryCertificatesRequest;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateMetadataListResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateRevokeRequest;
import pl.akmf.ksef.sdk.client.model.certificate.SendCertificateEnrollmentRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_ENROLLMENT;
import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_ENROLLMENT_DATA;
import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_LIMIT;
import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_METADATA;
import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_RETRIEVE;
import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_REVOKE;
import static pl.akmf.ksef.sdk.api.Url.CERTIFICATE_STATUS;
import static pl.akmf.ksef.sdk.api.UrlQueryParamsBuilder.buildUrlWithParams;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.AUTHORIZATION;
import static pl.akmf.ksef.sdk.client.Headers.BEARER;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_OFFSET;
import static pl.akmf.ksef.sdk.client.Parameter.PAGE_SIZE;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_CERTIFICATE_SERIAL_NUMBER;
import static pl.akmf.ksef.sdk.client.Parameter.PATH_REFERENCE_NUMBER;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class CertificateApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public CertificateApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Unieważnienie certyfikatu
     * Unieważnia certyfikat o podanym numerze seryjnym.
     *
     * @param certificateSerialNumber  Numer seryjny certyfikatu (required)
     * @param certificateRevokeRequest (optional)
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> apiV2CertificatesCertificateSerialNumberRevokePostWithHttpInfo(String certificateSerialNumber, CertificateRevokeRequest certificateRevokeRequest, String authenticationToken) throws ApiException {
        if (certificateSerialNumber == null) {
            throw new ApiException(400, "Missing the required parameter 'certificateSerialNumber' when calling apiV2CertificatesCertificateSerialNumberRevokePost");
        }

        String uri = buildUrlWithParams(CERTIFICATE_REVOKE.getUrl(), new HashMap<>())
                .replace(PATH_CERTIFICATE_SERIAL_NUMBER, certificateSerialNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);

        var response = apiClient.post(uri, certificateRevokeRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_REVOKE.getOperationId(), response.body(), response.statusCode(), response.headers());
        }

        return new ApiResponse<>(
                response.statusCode(),
                response.headers());
    }

    /**
     * Pobranie danych do wniosku certyfikacyjnego
     * Zwraca dane wymagane do przygotowania wniosku certyfikacyjnego.
     *
     * @return ApiResponse&lt;CertificateEnrollmentDataResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertificateEnrollmentsInfoResponse> apiV2CertificatesEnrollmentsDataGetWithHttpInfo(String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(CERTIFICATE_ENROLLMENT_DATA.getUrl(), headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_ENROLLMENT_DATA.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Wysyłka wniosku certyfikacyjnego
     * Przyjmuje wniosek certyfikacyjny i rozpoczyna jego przetwarzanie.
     *
     * @param enrollCertificateRequest (optional)
     * @return ApiResponse&lt;EnrollCertificateResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertificateEnrollmentResponse> apiV2CertificatesEnrollmentsPostWithHttpInfo(SendCertificateEnrollmentRequest enrollCertificateRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(CERTIFICATE_ENROLLMENT.getUrl(), enrollCertificateRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_ENROLLMENT.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie statusu przetwarzania wniosku certyfikacyjnego
     * Zwraca informacje o statusie wniosku certyfikacyjnego.
     *
     * @param referenceNumber Numer referencyjny wniosku certyfikacyjnego (required)
     * @return ApiResponse&lt;CertificateEnrollmentStatusResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertificateEnrollmentStatusResponse> apiV2CertificatesEnrollmentsReferenceNumberGetWithHttpInfo(String referenceNumber, String authenticationToken) throws ApiException {
        String uri = buildUrlWithParams(CERTIFICATE_STATUS.getUrl(), new HashMap<>())
                .replace(PATH_REFERENCE_NUMBER, referenceNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(uri, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_STATUS.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie danych o limitach certyfikatów
     * Zwraca informacje o limitach certyfikatów oraz informacje czy użytkownik może zawnioskować o certyfikat.
     *
     * @return ApiResponse&lt;CertificateLimitsResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertificateLimitsResponse> apiV2CertificatesLimitsGetWithHttpInfo(String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.get(CERTIFICATE_LIMIT.getUrl(), headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_LIMIT.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie listy metadanych certyfikatów
     * Zwraca listę certyfikatów spełniających podane kryteria wyszukiwania. W przypadku braku podania kryteriów wyszukiwania zwrócona zostanie nieprzefiltrowana lista.
     *
     * @param pageSize                       Rozmiar strony wyników (optional, default to 10)
     * @param pageOffset                     Numner strony wyników (optional, default to 0)
     * @param queryCertificatesRequest Kryteria filtrowania (optional)
     * @return ApiResponse&lt;QueryCertificatesResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertificateMetadataListResponse> apiV2CertificatesQueryPostWithHttpInfo(Integer pageSize, Integer pageOffset, QueryCertificatesRequest queryCertificatesRequest, String authenticationToken) throws ApiException {
        var params = new HashMap<String, String>();
        params.put(PAGE_SIZE, String.valueOf(pageSize));
        params.put(PAGE_OFFSET, String.valueOf(pageOffset));
        String uri = buildUrlWithParams(CERTIFICATE_METADATA.getUrl(), params);

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(uri, queryCertificatesRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_METADATA.getOperationId(), response.body(), response.statusCode(), response.headers());
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
     * Pobranie certyfikatu lub listy certyfikatów
     * Zwraca certyfikaty o podanych numerach seryjnych w formacie DER zakodowanym w Base64.
     *
     * @param certificateListRequest (optional)
     * @return ApiResponse&lt;RetrieveCertificatesResponse&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertificateListResponse> apiV2CertificatesRetrievePostWithHttpInfo(CertificateListRequest certificateListRequest, String authenticationToken) throws ApiException {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + authenticationToken);
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);

        var response = apiClient.post(CERTIFICATE_RETRIEVE.getUrl(), certificateListRequest, headers);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(CERTIFICATE_RETRIEVE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
