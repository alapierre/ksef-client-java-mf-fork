package pl.akmf.ksef.sdk.client.interfaces;

import java.util.List;

/** Receives a selected header on the calling thread. Shared listeners must be thread-safe. */
@FunctionalInterface
public interface ResponseHeaderListener {
    /**
     * @param method HTTP request method
     * @param path raw request path without the query string
     * @param headerName selected header name, in lowercase
     * @param values all values of this header in this response, in wire order
     */
    void onResponseHeader(String method, String path, String headerName, List<String> values);
}
