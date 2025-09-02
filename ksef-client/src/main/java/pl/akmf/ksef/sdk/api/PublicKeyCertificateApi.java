package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ApiResponse;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.akmf.ksef.sdk.api.Url.SECURITY_PUBLIC_KEY_CERTIFICATE;
import static pl.akmf.ksef.sdk.client.Headers.ACCEPT;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.model.ApiException.getApiException;

public class PublicKeyCertificateApi {
    private final HttpApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public PublicKeyCertificateApi(HttpApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Pobranie certyfikatów
     * Zwraca informacje o kluczach publicznych używanych do szyfrowania danych przesyłanych do systemu KSeF.
     *
     * @return ApiResponse&lt;List&lt;PublicKeyCertificate&gt;&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<List<PublicKeyCertificate>> apiV2SecurityPublicKeyCertificatesGet() throws ApiException {
        Map<String, String> headers = new HashMap<>();

        var response = apiClient.get(SECURITY_PUBLIC_KEY_CERTIFICATE.getUrl(), headers);
        headers.put(ACCEPT, APPLICATION_JSON);

        if (response.statusCode() / 100 != 2) {
            throw getApiException(SECURITY_PUBLIC_KEY_CERTIFICATE.getOperationId(), response.body(), response.statusCode(), response.headers());
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
