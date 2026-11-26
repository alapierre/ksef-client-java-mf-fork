package pl.akmf.ksef.sdk.client.model.invoice;

import pl.akmf.ksef.sdk.client.Headers;
import pl.akmf.ksef.sdk.client.model.ApiResponse;

import java.net.http.HttpHeaders;
import java.util.Optional;

/** XML pobranej faktury i metadane zwrócone przez KSeF w tej samej odpowiedzi. */
public final class InvoiceDownloadResponse extends ApiResponse<byte[]> {

    public InvoiceDownloadResponse(int statusCode, HttpHeaders headers, byte[] xml) {
        super(statusCode, headers, xml);
    }

    /** @return oryginalne bajty XML faktury */
    public byte[] getXml() {
        return getData();
    }

    /** @return data nadania numeru KSeF w formacie przekazanym przez serwer, jeśli dostępna */
    public Optional<String> getAcquisitionDate() {
        return getHeaders().firstValue(Headers.X_MS_META_ACQUISITION_DATE);
    }

    /** @return data trwałego zapisu faktury w formacie przekazanym przez serwer, jeśli dostępna */
    public Optional<String> getPermanentStorageDate() {
        return getHeaders().firstValue(Headers.X_MS_META_PERMANENT_STORAGE_DATE);
    }

    /** @return skrót SHA-256 faktury zakodowany w Base64, jeśli serwer go zwrócił */
    public Optional<String> getSha256Base64() {
        return getHeaders().firstValue(Headers.X_MS_META_HASH);
    }
}
