package pl.akmf.ksef.sdk.system.headerobservation;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHeaderCaptureHandler {

    private final Set<String> subscribedHeaders = ConcurrentHashMap.newKeySet();

    // store: requestKey = (header + values)
    private final ConcurrentHashMap<String, Map<String, List<String>>> store = new ConcurrentHashMap<>();

    public void subscribe(String header) {
        subscribedHeaders.add(header.toLowerCase(Locale.ROOT));
    }

    public void unsubscribe(String header) {
        subscribedHeaders.remove(header.toLowerCase(Locale.ROOT));
    }

    public void capture(HttpRequest request, HttpHeaders headers) {
        if (subscribedHeaders.isEmpty()) return;

        String requestKey = buildRequestKey(request);
        Map<String, List<String>> captured = new HashMap<>();

        for (String h : subscribedHeaders) {

            List<String> headerValues = headers.allValues(h);
            if (!headerValues.isEmpty()) {
                captured.put(h, List.copyOf(headerValues));
            }
        }

        if (!captured.isEmpty()) {
            store.put(requestKey, Map.copyOf(captured));
        }
    }

    /**
     * Returns captured headers. Call {@link #clear()} after consuming them to release stored entries.
     */
    public Map<String, Map<String, List<String>>> getCaptured() {
        return new HashMap<>(store);
    }

    public void clear() {
        store.clear();
    }

    private String buildRequestKey(HttpRequest request) {
        URI uri = request.uri();
        return request.method() + " " + uri.getPath()
                + (uri.getQuery() != null ? "?" + uri.getQuery() : "")
                + " :: " + UUID.randomUUID();
    }
}
