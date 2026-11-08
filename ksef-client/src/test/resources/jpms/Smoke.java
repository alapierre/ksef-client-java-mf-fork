package sample;

import io.alapierre.ksef.json.JsonSerializer;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.KsefApiProperties;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryFilters;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class Smoke {
    public static void main(String[] args) throws Exception {
        JsonSerializer json = (JsonSerializer) Class.forName(args[0]).getConstructor().newInstance();
        require("io.alapierre.ksef.client".equals(DefaultKsefClient.class.getModule().getName()));
        require("io.alapierre.ksef.json".equals(JsonSerializer.class.getModule().getName()));
        require(("io.alapierre.ksef.json." + args[1]).equals(json.getClass().getModule().getName()));
        String absent = args[1].equals("gson") ? "com.fasterxml.jackson.databind.ObjectMapper" : "com.google.gson.Gson";
        try {
            Class.forName(absent);
            throw new AssertionError("Unexpected JSON dependency: " + absent);
        } catch (ClassNotFoundException expected) { }

        // Reflective DTO access and neutral accessor names must work on the module path.
        InvoiceQueryFilters filters = json.fromJson("{\"isSelfInvoicing\":true}", InvoiceQueryFilters.class);
        require(Boolean.TRUE.equals(filters.getSelfInvoicing()));
        require(json.toJson(filters).contains("\"isSelfInvoicing\":true"));
        require("\"AAEC/w==\"".equals(json.toJson(new byte[]{0, 1, 2, -1})));

        var properties = new KsefApiProperties() {
            public String getBaseUri() { return "https://example.invalid/v2"; }
            public String getSuffixUri() { return ""; }
            public String getQrUri() { return ""; }
            public Duration getRequestTimeout() { return Duration.ofSeconds(1); }
            public Map<String, String> getDefaultHeaders() { return Map.of(); }
        };
        var client = new DefaultKsefClient(null, properties, json) {
            @Override
            protected HttpResponse<byte[]> sendHttpRequest(HttpRequest request, HttpResponse.BodyHandler<byte[]> handler) {
                return new HttpResponse<>() {
                    public int statusCode() { return 200; }
                    public HttpRequest request() { return request; }
                    public Optional<HttpResponse<byte[]>> previousResponse() { return Optional.empty(); }
                    public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (k, v) -> true); }
                    public byte[] body() {
                        return "[{\"publicKeyId\":\"module-key\",\"usage\":[\"KsefTokenEncryption\"]}]"
                                .getBytes(StandardCharsets.UTF_8);
                    }
                    public Optional<SSLSession> sslSession() { return Optional.empty(); }
                    public URI uri() { return request.uri(); }
                    public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
                };
            }
        };
        var certificate = client.retrievePublicKeyCertificate().getFirst();
        require("module-key".equals(certificate.getPublicKeyId()));
        require(certificate.getUsage().size() == 1);
        System.out.println("JPMS client OK: " + json.name());
    }

    private static void require(boolean condition) {
        if (!condition) { throw new AssertionError(); }
    }
}
