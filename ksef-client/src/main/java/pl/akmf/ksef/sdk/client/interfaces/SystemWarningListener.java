package pl.akmf.ksef.sdk.client.interfaces;

import java.util.List;

/** Receives X-System-Warning headers on the calling thread. Shared listeners must be thread-safe. */
@FunctionalInterface
public interface SystemWarningListener extends ResponseHeaderListener {
    /**
     * @param method HTTP request method
     * @param path raw request path without the query string
     * @param headerValues all X-System-Warning header values from this response, as sent by KSeF
     */
    void onSystemWarning(String method, String path, List<String> headerValues);

    @Override
    default void onResponseHeader(String method, String path, String headerName, List<String> values) {
        onSystemWarning(method, path, values);
    }
}
