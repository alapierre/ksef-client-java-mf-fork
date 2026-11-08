package pl.akmf.ksef.sdk.api;

import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.JsonType;
import io.alapierre.ksef.json.jackson.JacksonJsonSerializer;
import io.alapierre.ksef.json.gson.GsonJsonSerializer;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.model.*;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;
import pl.akmf.ksef.sdk.client.model.exceptions.BadRequestApiException;
import pl.akmf.ksef.sdk.client.model.exceptions.GoneApiException;
import pl.akmf.ksef.sdk.client.model.exceptions.TooManyRequestsApiException;
import pl.akmf.ksef.sdk.client.model.lighthouse.KsefMessagesResponse;
import pl.akmf.ksef.sdk.client.model.lighthouse.KsefStatusResponse;
import pl.akmf.ksef.sdk.client.model.testdata.TestDataUpdateCertificateRequest;
import pl.akmf.ksef.sdk.system.SystemKSeFSDKException;
import pl.akmf.ksef.sdk.system.headerobservation.ResponseHeaderObservationOptions;
import pl.akmf.ksef.sdk.system.headerobservation.ResponseHeaderObservationProperties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class JsonSerializerClientTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[][] serializers() {
        return new Object[][]{{"jackson", new JacksonJsonSerializer()}, {"gson", new GsonJsonSerializer()}};
    }

    private final JsonSerializer serializer;

    public JsonSerializerClientTest(String name, JsonSerializer serializer) {
        this.serializer = serializer;
    }

    private static final KsefApiProperties PROPERTIES = new KsefApiProperties() {
        public String getBaseUri() { return "https://example.invalid/v2"; }
        public String getSuffixUri() { return ""; }
        public String getQrUri() { return ""; }
        public Duration getRequestTimeout() { return Duration.ofSeconds(5); }
        public Map<String, String> getDefaultHeaders() { return Map.of(); }
    };

    @Test
    public void usesSelectedAdapterForHttpRequestsAndTypedResponses() throws Exception {
        StubHttpClient http = new StubHttpClient(204, "");
        var client = new DefaultKsefClient(http, PROPERTIES, serializer);
        var date = java.time.OffsetDateTime.parse("2026-11-08T12:00:00+02:00");
        client.updateCertificate("cert-1", new TestDataUpdateCertificateRequest(date), "access");
        assertEquals("{\"validTo\":\"2026-11-08T12:00:00+02:00\"}", new String(requestBody(http.lastRequest), StandardCharsets.UTF_8));
        http.status = 200;
        http.body = "[{\"publicKeyId\":\"key-1\",\"validFrom\":\"2026-11-08T12:00:00+02:00\"}]";
        var certificate = client.retrievePublicKeyCertificate().getFirst();
        assertEquals("key-1", certificate.getPublicKeyId());
        assertEquals(date, certificate.getValidFrom());

        var lighthouse = new DefaultLighthouseKsefClient(serializer, http, "https://example.invalid", Duration.ofSeconds(5), Map.of());
        http.body = "{\"status\":\"AVAILABLE\",\"messages\":[]}";
        assertEquals("AVAILABLE", lighthouse.getStatus().getStatus());
        http.body = "[{\"id\":\"message-1\",\"start\":\"2026-11-08T12:00:00+02:00\"}]";
        var message = lighthouse.getMessages().getFirst();
        assertEquals("message-1", message.getId());
        assertEquals(date, message.getStart());
    }

    @Test
    public void passesRequestAndGenericResponseThroughSuppliedSerializer() throws Exception {
        StubHttpClient http = new StubHttpClient(204, "");
        StubSerializer serializer = new StubSerializer();
        var client = new DefaultKsefClient(http, PROPERTIES, serializer);
        var request = new TestDataUpdateCertificateRequest();
        client.updateCertificate("cert-1", request, "access");
        assertSame(request, serializer.encodedValue);
        assertArrayEquals(serializer.encodedBytes, requestBody(http.lastRequest));
        assertTrue(serializer.decodedTypes.isEmpty()); // No JSON parsing for a 204 response.

        http.status = 200;
        http.body = "response bytes";
        var certificates = List.of(new PublicKeyCertificate());
        serializer.decodedValue = certificates;
        assertSame(certificates, client.retrievePublicKeyCertificate());
        assertEquals(new JsonType<List<PublicKeyCertificate>>() { }.getType(), serializer.decodedTypes.getFirst());
        assertArrayEquals(http.body.getBytes(StandardCharsets.UTF_8), serializer.decodedBytes);
    }

    @Test
    public void lighthouseUsesSuppliedSerializerForSuccessAndErrors() throws Exception {
        StubHttpClient http = new StubHttpClient(200, "response bytes");
        StubSerializer serializer = new StubSerializer();
        var client = new DefaultLighthouseKsefClient(serializer, http, "https://example.invalid",
                Duration.ofSeconds(5), Map.of());
        serializer.decodedValue = new KsefStatusResponse();
        assertSame(serializer.decodedValue, client.getStatus());
        serializer.decodedValue = new KsefMessagesResponse();
        assertSame(serializer.decodedValue, client.getMessages());
        http.status = 500;
        serializer.decodedValue = new ExceptionResponse();
        var error = assertThrows(KsefApiException.class, client::getStatus);
        assertSame(serializer.decodedValue, error.getExceptionResponse());
        assertEquals(List.of(KsefStatusResponse.class, KsefMessagesResponse.class, ExceptionResponse.class),
                serializer.decodedTypes);
    }

    @Test
    public void preservesRawXmlBodyAndHeaderObservation() throws Exception {
        StubHttpClient http = new StubHttpClient(202, "{}");
        StubSerializer serializer = new StubSerializer();
        var observation = new ResponseHeaderObservationProperties() {
            public ResponseHeaderObservationOptions getResponseHeaderObservationOptions() {
                return new ResponseHeaderObservationOptions(true, Set.of("X-System-Warning"));
            }
        };
        var client = new DefaultKsefClient(http, PROPERTIES, observation, serializer);
        String xml = "<AuthTokenRequest>żółć</AuthTokenRequest>";
        client.submitAuthTokenRequest(xml, false);
        assertArrayEquals(xml.getBytes(StandardCharsets.UTF_8), requestBody(http.lastRequest));
        assertNull(serializer.encodedValue);
        assertEquals(1, client.getResponseHeaderCaptureHandler().getCaptured().size());
    }

    @Test
    public void propagatesNeutralSerializationFailures() {
        StubHttpClient http = new StubHttpClient(200, "{}");
        StubSerializer serializer = new StubSerializer();
        serializer.failure = new JsonSerializationException("test failure");
        var client = new DefaultKsefClient(http, PROPERTIES, serializer);
        var writeError = assertThrows(SystemKSeFSDKException.class,
                () -> client.updateCertificate("cert-1", new TestDataUpdateCertificateRequest(), "access"));
        assertSame(serializer.failure, writeError.getCause());
        assertNull(http.lastRequest);
        var readError = assertThrows(KsefApiException.class, client::retrievePublicKeyCertificate);
        assertSame(serializer.failure, readError.getCause());

        http.status = 400;
        var apiError = assertThrows(KsefApiException.class, client::retrievePublicKeyCertificate);
        assertEquals(400, apiError.getCode());
        assertEquals("GET", apiError.getMethod());
        assertNotNull(apiError.getResponseHeaders());
        assertEquals(List.of(new JsonType<List<PublicKeyCertificate>>() { }.getType(),
                pl.akmf.ksef.sdk.client.model.exceptions.BadRequestProblemDetails.class,
                ExceptionResponse.class), serializer.decodedTypes);
    }

    @Test
    public void readsProblemDetailsThroughAdapterForAllSupportedStatuses() {
        Map<Integer, Class<? extends ApiException>> cases = Map.of(
                400, BadRequestApiException.class, 401, UnauthorizedApiException.class,
                403, ForbiddenApiException.class, 410, GoneApiException.class, 429, TooManyRequestsApiException.class);
        cases.forEach((status, expectedType) -> {
            StubHttpClient http = new StubHttpClient(status,
                    "{\"title\":\"API error\",\"status\":" + status + ",\"detail\":\"details\"}");
            http.contentType = "application/problem+json";
            var error = assertThrows(expectedType,
                    () -> new DefaultKsefClient(http, PROPERTIES, serializer).retrievePublicKeyCertificate());
            assertEquals((int) status, error.getCode());
        });
    }

    @Test
    public void fallsBackToLegacyErrorsAndHonorsRetryAfter() {
        for (int status : new int[]{400, 401, 403, 410, 429}) {
            StubHttpClient http = new StubHttpClient(status, status == 429
                    ? "{\"status\":{\"code\":429,\"description\":\"Slow down\"}}"
                    : "{\"exception\":{\"serviceCode\":\"legacy\"}}");
            var error = assertThrows(KsefApiException.class,
                    () -> new DefaultKsefClient(http, PROPERTIES, serializer).retrievePublicKeyCertificate());
            assertEquals(status, error.getCode());
            if (status == 429) {
                assertEquals(Integer.valueOf(7), error.getExceptionResponse().getStatus().getRetryAfterSeconds());
                assertEquals(Duration.ofSeconds(7), error.getExceptionResponse().getStatus().getRecommendedDelay());
            } else {
                assertEquals("legacy", error.getExceptionResponse().getException().getServiceCode());
            }
        }
    }

    @Test
    public void preservesHttpErrorForEmptyNullAndMalformedErrorBodies() {
        for (int status : new int[]{400, 401, 403, 410, 429}) {
            for (String body : Arrays.asList(null, "", "null", "{", "[]", "{}")) {
                var error = assertThrows(KsefApiException.class,
                        () -> new DefaultKsefClient(new StubHttpClient(status, body), PROPERTIES,
                                serializer).retrievePublicKeyCertificate());
                assertEquals(status, error.getCode());
                assertEquals("GET", error.getMethod());
            }
        }
    }

    @Test
    public void lighthousePreservesHttpMetadataWhenErrorBodyCannotBeDecoded() {
        for (String body : Arrays.asList(null, "", "null", "{", "[]")) {
            var client = new DefaultLighthouseKsefClient(serializer,
                    new StubHttpClient(503, body), "https://example.invalid", Duration.ofSeconds(5), Map.of());
            var error = assertThrows(KsefApiException.class, client::getStatus);
            assertEquals(503, error.getCode());
            assertEquals("GET", error.getMethod());
            assertEquals("7", error.getResponseHeaders().firstValue("Retry-After").orElseThrow());
        }
    }

    @Test
    public void onlyMissingBodyAndJsonNullRepresentAbsentSuccessfulData() throws Exception {
        for (String body : Arrays.asList(null, "null")) {
            assertNull(new DefaultKsefClient(new StubHttpClient(200, body), PROPERTIES,
                    serializer).retrievePublicKeyCertificate());
        }
        var error = assertThrows(KsefApiException.class,
                () -> new DefaultKsefClient(new StubHttpClient(200, ""), PROPERTIES,
                        serializer).retrievePublicKeyCertificate());
        assertTrue(error.getCause() instanceof JsonSerializationException);
    }

    private static byte[] requestBody(HttpRequest request) {
        var subscriber = HttpResponse.BodySubscribers.ofByteArray();
        request.bodyPublisher().orElseThrow().subscribe(new Flow.Subscriber<ByteBuffer>() {
            public void onSubscribe(Flow.Subscription subscription) { subscriber.onSubscribe(subscription); }
            public void onNext(ByteBuffer buffer) { subscriber.onNext(List.of(buffer)); }
            public void onError(Throwable error) { subscriber.onError(error); }
            public void onComplete() { subscriber.onComplete(); }
        });
        return subscriber.getBody().toCompletableFuture().join();
    }

    /** Deliberately has no JSON-library implementation or dependencies. */
    private static final class StubSerializer implements JsonSerializer {
        private final byte[] encodedBytes = "{\"custom\":true}".getBytes(StandardCharsets.UTF_8);
        private final List<Type> decodedTypes = new ArrayList<>();
        private Object encodedValue;
        private Object decodedValue;
        private byte[] decodedBytes;
        private JsonSerializationException failure;

        public byte[] toJsonBytes(Object value) throws JsonSerializationException {
            if (failure != null) { throw failure; }
            encodedValue = value;
            return encodedBytes;
        }

        @SuppressWarnings("unchecked")
        public <T> T fromJson(byte[] json, JsonType<T> type) throws JsonSerializationException {
            decodedTypes.add(type.getType());
            decodedBytes = json;
            if (failure != null) { throw failure; }
            return (T) decodedValue;
        }

        public String name() { return "stub"; }
    }

    private static final class StubHttpClient extends HttpClient {
        private int status;
        private String body;
        private String contentType = "application/json";
        private HttpRequest lastRequest;

        private StubHttpClient(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
            lastRequest = request;
            var headers = HttpHeaders.of(Map.of("Content-Type", List.of(contentType),
                    "Retry-After", List.of("7"), "X-System-Warning", List.of("warning")), (k, v) -> true);
            var subscriber = handler.apply(new HttpResponse.ResponseInfo() {
                public int statusCode() { return status; }
                public HttpHeaders headers() { return headers; }
                public Version version() { return Version.HTTP_1_1; }
            });
            subscriber.onSubscribe(new Flow.Subscription() {
                public void request(long n) { }
                public void cancel() { }
            });
            if (body != null) { subscriber.onNext(List.of(ByteBuffer.wrap(body.getBytes(StandardCharsets.UTF_8)))); }
            subscriber.onComplete();
            T data = body == null ? null : subscriber.getBody().toCompletableFuture().join();
            return new HttpResponse<>() {
                public int statusCode() { return status; }
                public HttpRequest request() { return request; }
                public Optional<HttpResponse<T>> previousResponse() { return Optional.empty(); }
                public HttpHeaders headers() { return headers; }
                public T body() { return data; }
                public Optional<SSLSession> sslSession() { return Optional.empty(); }
                public URI uri() { return request.uri(); }
                public Version version() { return Version.HTTP_1_1; }
            };
        }

        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest r, HttpResponse.BodyHandler<T> h) { throw new UnsupportedOperationException(); }
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest r, HttpResponse.BodyHandler<T> h, HttpResponse.PushPromiseHandler<T> p) { throw new UnsupportedOperationException(); }
        public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
        public Optional<Duration> connectTimeout() { return Optional.empty(); }
        public Redirect followRedirects() { return Redirect.NEVER; }
        public Optional<ProxySelector> proxy() { return Optional.empty(); }
        public SSLContext sslContext() { throw new UnsupportedOperationException(); }
        public SSLParameters sslParameters() { return new SSLParameters(); }
        public Optional<Authenticator> authenticator() { return Optional.empty(); }
        public Version version() { return Version.HTTP_1_1; }
        public Optional<Executor> executor() { return Optional.empty(); }
    }
}
