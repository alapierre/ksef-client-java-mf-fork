package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.Headers;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.invoice.CurrencyCode;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataInvoiceType;
import pl.akmf.ksef.sdk.client.model.limit.*;
import pl.akmf.ksef.sdk.client.model.session.EncryptionInfo;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionRequest;
import pl.akmf.ksef.sdk.client.model.testdata.TestDataUpdateCertificateRequest;
import pl.akmf.ksef.sdk.system.headerobservation.*;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.Locale;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Flow;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


import static org.junit.Assert.*;

public class Upstream327Test {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final TestApiProperties properties = new TestApiProperties("https://example.test/v2");

    @Test
    public void updatesCertificateWithJsonAndAcceptsBothSuccessStatuses() throws Exception {
        for (int status : new int[]{200, 204}) {
            MockHttpClient http = new MockHttpClient(status, "");
            DefaultKsefClient client = new DefaultKsefClient(http, properties, mapper);
            OffsetDateTime date = OffsetDateTime.parse("2026-10-01T12:00:00+02:00");
            client.updateCertificate("012ABC", new TestDataUpdateCertificateRequest(date), "access");
            HttpRequest request = http.getLastRequest();
            assertEquals("PUT", request.method());
            assertEquals("https://example.test/v2/testdata/certificates/012ABC", request.uri().toString());
            assertEquals("Bearer access", request.headers().firstValue("Authorization").orElse(null));
            assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(null));
            assertEquals(date.toInstant(), OffsetDateTime.parse(mapper.readTree(body(request)).get("validTo").asText()).toInstant());
        }
    }

    @Test
    public void rejectsBlankCertificateSerialBeforeSending() throws Exception {
        MockHttpClient http = new MockHttpClient(204, "");
        DefaultKsefClient client = new DefaultKsefClient(http, properties, mapper);
        for (String serial : new String[]{null, "", "  "}) {
            try {
                client.updateCertificate(serial, new TestDataUpdateCertificateRequest(), "access");
                fail("Expected invalid serial number");
            } catch (IllegalArgumentException expected) {
                assertNull(http.getLastRequest());
            }
        }
    }

    @Test(expected = ApiException.class)
    public void propagatesCertificateApiError() throws Exception {
        MockHttpClient http = new MockHttpClient(400, """
                {"exception":{"exceptionDetailList":[{"exceptionCode":21405,
                "exceptionDescription":"Validation error","details":["validTo"]}],
                "serviceCode":"test","timestamp":"2026-09-01T12:00:00Z"}}
                """);
        new DefaultKsefClient(http, properties, mapper).updateCertificate("012ABC",
                new TestDataUpdateCertificateRequest(), "access");
    }

    @Test
    public void observesHeadersOnlyAfterSubscriptionAndCanClearAndUnsubscribe() throws Exception {
        MockHttpClient http = new MockHttpClient(200, "[]");
        DefaultKsefClient client = new DefaultKsefClient(http, properties, mapper);
        var handler = client.getResponseHeaderCaptureHandler();
        client.retrievePublicKeyCertificate();
        assertTrue(handler.getCaptured().isEmpty());
        handler.subscribe("X-SYSTEM-WARNING");
        client.retrievePublicKeyCertificate();
        client.retrievePublicKeyCertificate();
        assertEquals(2, handler.getCaptured().size());
        for (var entry : handler.getCaptured().values()) {
            assertEquals(List.of("warning-1", "warning-2"), entry.get("x-system-warning"));
            assertEquals(1, entry.size());
        }
        handler.getCaptured().clear();
        assertEquals(2, handler.getCaptured().size());
        handler.clear();
        handler.unsubscribe("x-System-Warning");
        client.retrievePublicKeyCertificate();
        assertTrue(handler.getCaptured().isEmpty());
    }

    @Test
    public void supportsConfiguredObservationWithoutCircuitBreaker() throws Exception {
        for (boolean enabled : new boolean[]{false, true}) {
            var options = new ResponseHeaderObservationOptions(enabled, Set.of("X-System-Warning"));
            var config = new ResponseHeaderObservationProperties() {
                public ResponseHeaderObservationOptions getResponseHeaderObservationOptions() { return options; }
            };
            var client = new DefaultKsefClient(new MockHttpClient(200, "[]"), properties, config, mapper);
            assertEquals(0, client.retrievePublicKeyCertificate().size());
            assertEquals(enabled ? 1 : 0, client.getResponseHeaderCaptureHandler().getCaptured().size());
        }
    }

    @Test
    public void normalizesHeadersIndependentlyOfLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            var handler = new ResponseHeaderCaptureHandler();
            handler.subscribe("X-SYSTEM-WARNING");
            handler.capture(HttpRequest.newBuilder(URI.create("https://example.test/")).build(),
                    HttpHeaders.of(Map.of("x-system-warning", List.of("warning")), (k, v) -> true));
            assertEquals(1, handler.getCaptured().size());
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void mapsCollectiveIdentifierLimitsAndNewMetadataTypes() throws Exception {
        String sessionJson = "{\"collectiveIdentifier\":{\"maxInvoices\":500}}";
        assertEquals(500, mapper.readValue(sessionJson, GetContextLimitResponse.class).getCollectiveIdentifier().getMaxInvoices());
        assertEquals(500, mapper.readValue(sessionJson, ChangeContextLimitRequest.class).getCollectiveIdentifier().getMaxInvoices());
        String ratesJson = "{\"collectiveIdentifier\":{\"perSecond\":20,\"perMinute\":120,\"perHour\":240}}";
        var rates = mapper.readValue(ratesJson, GetRateLimitResponse.class).getCollectiveIdentifier();
        assertEquals(20, rates.getPerSecond());
        assertEquals(120, rates.getPerMinute());
        assertEquals(240, rates.getPerHour());
        assertEquals(240, mapper.readValue(ratesJson, EffectiveApiRateLimits.class).getCollectiveIdentifier().getPerHour());
        assertEquals(500, mapper.readTree(mapper.writeValueAsString(new ChangeContextLimitRequest(null, null,
                new CollectiveIdentifierSessionLimits(500)))).path("collectiveIdentifier").path("maxInvoices").asInt());
        assertEquals(InvoiceMetadataInvoiceType.VAT_PEF_SP, mapper.readValue("\"VatPefSp\"", InvoiceMetadataInvoiceType.class));
        assertEquals("\"KorVatRr\"", mapper.writeValueAsString(InvoiceMetadataInvoiceType.KOR_VAT_RR));
        assertEquals(InvoiceMetadataInvoiceType.KOR_VAT_SP, InvoiceMetadataInvoiceType.fromValue("KorVatRr"));
        assertEquals("key-id", new EncryptionInfo("encrypted", "iv", "key-id").getPublicKeyId());
    }

    @Test
    public void supportsApi280RateLimitContract() throws Exception {
        String json = """
                {
                  "onlineSessionClose":{"perSecond":20,"perMinute":60,"perHour":240},
                  "batchSessionClose":{"perSecond":20,"perMinute":40,"perHour":120},
                  "anonymous":{"perSecond":10,"perMinute":20,"perHour":30},
                  "global":{"perSecond":-1,"perMinute":-1,"perHour":-1}
                }
                """;

        var response = mapper.readValue(json, GetRateLimitResponse.class);
        assertEquals(240, response.getOnlineSessionClose().getPerHour());
        assertEquals(120, response.getBatchSessionClose().getPerHour());
        assertEquals(10, response.getAnonymous().getPerSecond());
        assertEquals(-1, response.getGlobal().getPerHour());

        var effective = mapper.readValue(json, EffectiveApiRateLimits.class);
        assertEquals(60, effective.getOnlineSessionClose().getPerMinute());
        assertEquals(40, effective.getBatchSessionClose().getPerMinute());

        var change = new ApiRateLimitsChangeRequest();
        change.setOnlineSession(new OnlineSessionRateLimit(1, 2, 3));
        var serialized = mapper.readTree(mapper.writeValueAsString(new SetRateLimitsRequest(change)))
                .path("rateLimits");
        assertEquals(3, serialized.path("onlineSession").path("perHour").asInt());
        assertFalse(serialized.has("onlineSessionClose"));
        assertFalse(serialized.has("batchSessionClose"));
        assertFalse(serialized.has("anonymous"));
        assertFalse(serialized.has("global"));
    }

    @Test
    public void sendsOptionalApiFeatureWhenOpeningSessions() throws Exception {
        MockHttpClient http = new MockHttpClient(201, "{}");
        DefaultKsefClient client = new DefaultKsefClient(http, properties, mapper);

        client.openBatchSession(new OpenBatchSessionRequest(), "access");
        assertFalse(http.getLastRequest().headers().firstValue(Headers.X_KSEF_FEATURE).isPresent());

        client.openBatchSession(new OpenBatchSessionRequest(), "access", Headers.SUBJECT_IDENTIFIER_VALIDATION);
        assertEquals(Headers.SUBJECT_IDENTIFIER_VALIDATION,
                http.getLastRequest().headers().firstValue(Headers.X_KSEF_FEATURE).orElse(null));

        client.openOnlineSession(new OpenOnlineSessionRequest(), "access", Headers.SUBJECT_IDENTIFIER_VALIDATION);
        assertEquals(Headers.SUBJECT_IDENTIFIER_VALIDATION,
                http.getLastRequest().headers().firstValue(Headers.X_KSEF_FEATURE).orElse(null));
    }

    @Test
    public void supportsApi280CurrencyCodes() throws Exception {
        for (String value : new String[]{"CNH", "VED", "XTS", "ZWG", "SLE"}) {
            assertEquals(value, mapper.readValue('"' + value + '"', CurrencyCode.class).getValue());
        }
    }

    private byte[] body(HttpRequest request) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CompletableFuture<byte[]> result = new CompletableFuture<>();
        request.bodyPublisher().orElseThrow().subscribe(new Flow.Subscriber<ByteBuffer>() {
            public void onSubscribe(Flow.Subscription subscription) { subscription.request(Long.MAX_VALUE); }
            public void onNext(ByteBuffer buffer) {
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                output.writeBytes(bytes);
            }
            public void onError(Throwable error) { result.completeExceptionally(error); }
            public void onComplete() { result.complete(output.toByteArray()); }
        });
        return result.get();
    }

    private static class TestApiProperties extends KsefApiProperties {
        private final String baseUri;

        public TestApiProperties(String baseUri) {
            this.baseUri = baseUri;
        }

        @Override
        public String getBaseUri() {
            return baseUri;
        }

        @Override
        public String getSuffixUri() {
            return "";
        }

        @Override
        public String getQrUri() {
            return "";
        }

        @Override
        public Duration getRequestTimeout() {
            return Duration.ofSeconds(5);
        }

        @Override
        public Map<String, String> getDefaultHeaders() {
            return new HashMap<>();
        }
    }

    private static class MockHttpClient extends HttpClient {
        private final int statusCode;
        private final String responseBody;
        private HttpRequest lastRequest;
        private HttpHeaders responseHeaders = HttpHeaders.of(Map.of("X-System-Warning", List.of("warning-1", "warning-2")), (k, v) -> true);

        public MockHttpClient(int statusCode, String responseBody) {
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public HttpRequest getLastRequest() {
            return lastRequest;
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            this.lastRequest = request;
            responseBodyHandler.apply(new HttpResponse.ResponseInfo() {
                public int statusCode() { return statusCode; }
                public HttpHeaders headers() { return responseHeaders; }
                public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
            });
            return new MockHttpResponse<>(statusCode, responseBody, request);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            this.lastRequest = request;
            return CompletableFuture.completedFuture(new MockHttpResponse<>(statusCode, responseBody, request));
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            return null;
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return null;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            return null;
        }

        @Override
        public SSLParameters sslParameters() {
            return null;
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public Version version() {
            return null;
        }

        @Override
        public Optional<Executor> executor() {
            return Optional.empty();
        }
    }

    private static class MockHttpResponse<T> implements HttpResponse<T> {
        private final int statusCode;
        private final String bodyString;
        private final HttpRequest request;

        public MockHttpResponse(int statusCode, String bodyString, HttpRequest request) {
            this.statusCode = statusCode;
            this.bodyString = bodyString;
            this.request = request;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(
                    Map.of("Content-Type", List.of("application/json")),
                    (k, v) -> true
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public T body() {
            if (bodyString == null) {
                return null;
            }
            return (T) bodyString.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return request != null ? request.uri() : URI.create("");
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
