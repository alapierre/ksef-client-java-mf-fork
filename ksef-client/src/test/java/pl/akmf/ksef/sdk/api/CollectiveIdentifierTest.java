package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import pl.akmf.ksef.sdk.api.builders.collectiveidentifier.CollectiveIdentifierInvoicesQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.collectiveidentifier.CollectiveIdentifiersQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.collectiveidentifier.GenerateCollectiveIdentifierRequestBuilder;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifierInvoice;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifierInvoicePayment;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifierInvoicesQueryRequest;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifierInvoicesQueryResponse;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifiersByKsefNumberQueryResponse;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifiersQueryRequest;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifiersQueryResponse;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.GenerateCollectiveIdentifierRequest;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.GenerateCollectiveIdentifierResponse;
import pl.akmf.ksef.sdk.client.model.invoice.CurrencyCode;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CollectiveIdentifierTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    public void testSerializationGenerateRequest() throws Exception {
        GenerateCollectiveIdentifierRequest request = new GenerateCollectiveIdentifierRequestBuilder()
                .addInvoice(new CollectiveIdentifierInvoice("1111111111-20260101-111111111111-11",
                        new CollectiveIdentifierInvoicePayment(123.45, CurrencyCode.PLN),
                        "Faktura 1"))
                .addInvoice(new CollectiveIdentifierInvoice("1111111111-20260101-222222222222-22",
                        null,
                        null))
                .build();

        String json = objectMapper.writeValueAsString(request);
        assertNotNull(json);
        assertTrue(json.contains("1111111111-20260101-111111111111-11"));
        assertTrue(json.contains("123.45"));
        assertTrue(json.contains("PLN"));

        GenerateCollectiveIdentifierRequest deserialized = objectMapper.readValue(json, GenerateCollectiveIdentifierRequest.class);
        assertEquals(2, deserialized.getInvoices().size());
        assertEquals("1111111111-20260101-111111111111-11", deserialized.getInvoices().get(0).getKsefNumber());
        assertEquals(Double.valueOf(123.45), deserialized.getInvoices().get(0).getPayment().getAmount());
        assertEquals(CurrencyCode.PLN, deserialized.getInvoices().get(0).getPayment().getCurrency());
        assertEquals("Faktura 1", deserialized.getInvoices().get(0).getDescription());
    }

    @Test
    public void testDeserializationGenerateResponse() throws Exception {
        String json = "{\"collectiveIdentifierNumber\":\"1111111111-IZ202607-65ED02180000-E7\"}";
        GenerateCollectiveIdentifierResponse response = objectMapper.readValue(json, GenerateCollectiveIdentifierResponse.class);
        assertEquals("1111111111-IZ202607-65ED02180000-E7", response.getCollectiveIdentifierNumber());
    }

    @Test
    public void testSerializationInvoicesQueryRequest() throws Exception {
        CollectiveIdentifierInvoicesQueryRequest request = new CollectiveIdentifierInvoicesQueryRequestBuilder()
                .addCollectiveIdentifierNumber("1111111111-IZ202607-65ED02180000-E7")
                .addCollectiveIdentifierNumber("2222222222-IZ202607-65ED02180000-E8")
                .build();

        String json = objectMapper.writeValueAsString(request);
        assertNotNull(json);
        assertTrue(json.contains("1111111111-IZ202607-65ED02180000-E7"));
        assertTrue(json.contains("2222222222-IZ202607-65ED02180000-E8"));

        CollectiveIdentifierInvoicesQueryRequest deserialized = objectMapper.readValue(json, CollectiveIdentifierInvoicesQueryRequest.class);
        assertEquals(2, deserialized.getCollectiveIdentifierNumbers().size());
        assertEquals("1111111111-IZ202607-65ED02180000-E7", deserialized.getCollectiveIdentifierNumbers().get(0));
        assertEquals("2222222222-IZ202607-65ED02180000-E8", deserialized.getCollectiveIdentifierNumbers().get(1));
    }

    @Test
    public void testDeserializationInvoicesQueryResponse() throws Exception {
        String json = "{\n" +
                "  \"continuationToken\": \"token123\",\n" +
                "  \"invoices\": [\n" +
                "    {\n" +
                "      \"ksefNumber\": \"1111111111-20260101-111111111111-11\",\n" +
                "      \"collectiveIdentifierNumber\": \"1111111111-IZ202607-65ED02180000-E7\",\n" +
                "      \"payment\": {\n" +
                "        \"amount\": 250.0,\n" +
                "        \"currency\": \"PLN\"\n" +
                "      },\n" +
                "      \"description\": \"Opis faktury\",\n" +
                "      \"detailsHidden\": false\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        CollectiveIdentifierInvoicesQueryResponse response = objectMapper.readValue(json, CollectiveIdentifierInvoicesQueryResponse.class);
        assertEquals("token123", response.getContinuationToken());
        assertEquals(1, response.getInvoices().size());
        assertEquals("1111111111-20260101-111111111111-11", response.getInvoices().get(0).getKsefNumber());
        assertEquals("1111111111-IZ202607-65ED02180000-E7", response.getInvoices().get(0).getCollectiveIdentifierNumber());
        assertEquals(Double.valueOf(250.0), response.getInvoices().get(0).getPayment().getAmount());
        assertEquals("PLN", response.getInvoices().get(0).getPayment().getCurrency());
        assertEquals("Opis faktury", response.getInvoices().get(0).getDescription());
        assertEquals(Boolean.FALSE, response.getInvoices().get(0).getDetailsHidden());
    }

    @Test
    public void testClientGenerateCollectiveIdentifier() throws Exception {
        String expectedResponseJson = "{\"collectiveIdentifierNumber\":\"1111111111-IZ202607-65ED02180000-E7\"}";

        MockHttpClient mockHttpClient = new MockHttpClient(201, expectedResponseJson);
        TestApiProperties properties = new TestApiProperties("https://ksef-test.mf.gov.pl/api/v2");

        DefaultKsefClient client = new DefaultKsefClient(mockHttpClient, properties, objectMapper);

        GenerateCollectiveIdentifierRequest request = new GenerateCollectiveIdentifierRequestBuilder()
                .addInvoice(new CollectiveIdentifierInvoice("1111111111-20260101-111111111111-11"))
                .build();

        GenerateCollectiveIdentifierResponse response = client.generateCollectiveIdentifier(request, "test-access-token");

        assertNotNull(response);
        assertEquals("1111111111-IZ202607-65ED02180000-E7", response.getCollectiveIdentifierNumber());

        HttpRequest sentRequest = mockHttpClient.getLastRequest();
        assertNotNull(sentRequest);
        assertEquals("POST", sentRequest.method());
        assertEquals("https://ksef-test.mf.gov.pl/api/v2/collective-identifiers", sentRequest.uri().toString());
        assertEquals("Bearer test-access-token", sentRequest.headers().firstValue("Authorization").orElse(null));
        assertEquals("application/json", sentRequest.headers().firstValue("Content-Type").orElse(null));
        assertEquals("application/json", sentRequest.headers().firstValue("Accept").orElse(null));
    }

    @Test
    public void testClientGetCollectiveIdentifierInvoices() throws Exception {
        String expectedResponseJson = "{\n" +
                "  \"continuationToken\": \"next-page\",\n" +
                "  \"invoices\": [\n" +
                "    {\n" +
                "      \"ksefNumber\": \"1111111111-20260101-111111111111-11\",\n" +
                "      \"collectiveIdentifierNumber\": \"1111111111-IZ202607-65ED02180000-E7\",\n" +
                "      \"detailsHidden\": false\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        MockHttpClient mockHttpClient = new MockHttpClient(200, expectedResponseJson);
        TestApiProperties properties = new TestApiProperties("https://ksef-test.mf.gov.pl/api/v2");

        DefaultKsefClient client = new DefaultKsefClient(mockHttpClient, properties, objectMapper);

        CollectiveIdentifierInvoicesQueryRequest request = new CollectiveIdentifierInvoicesQueryRequestBuilder()
                .addCollectiveIdentifierNumber("1111111111-IZ202607-65ED02180000-E7")
                .build();

        CollectiveIdentifierInvoicesQueryResponse response = client.getCollectiveIdentifierInvoices(
                request,
                "token-current",
                20,
                "test-access-token"
        );

        assertNotNull(response);
        assertEquals("next-page", response.getContinuationToken());
        assertEquals(1, response.getInvoices().size());
        assertEquals("1111111111-IZ202607-65ED02180000-E7", response.getInvoices().get(0).getCollectiveIdentifierNumber());

        HttpRequest sentRequest = mockHttpClient.getLastRequest();
        assertNotNull(sentRequest);
        assertEquals("POST", sentRequest.method());
        assertEquals("https://ksef-test.mf.gov.pl/api/v2/collective-identifiers/invoices?pageSize=20", sentRequest.uri().toString());
        assertEquals("Bearer test-access-token", sentRequest.headers().firstValue("Authorization").orElse(null));
        assertEquals("application/json", sentRequest.headers().firstValue("Content-Type").orElse(null));
        assertEquals("token-current", sentRequest.headers().firstValue("x-continuation-token").orElse(null));
        assertEquals("application/json", sentRequest.headers().firstValue("Accept").orElse(null));
    }

    @Test
    public void testCollectiveIdentifiersQueryFilters() throws Exception {
        OffsetDateTime from = OffsetDateTime.parse("2026-07-10T12:00:00+02:00");
        OffsetDateTime to = from.plusDays(31);
        CollectiveIdentifiersQueryRequest request = new CollectiveIdentifiersQueryRequestBuilder()
                .withCollectiveIdentifierNumber("1111111111-IZ202607-65ED02180000-E7")
                .withDateCreatedFrom(from)
                .withDateCreatedTo(to)
                .withInvoiceCountFrom(10)
                .withInvoiceCountTo(100)
                .withCreatedInCurrentContext(false)
                .build();
        var json = objectMapper.readTree(objectMapper.writeValueAsString(request));
        assertEquals("1111111111-IZ202607-65ED02180000-E7", json.get("collectiveIdentifierNumber").asText());
        assertEquals(10, json.get("invoiceCountFrom").asInt());
        assertEquals(100, json.get("invoiceCountTo").asInt());
        assertEquals(false, json.get("createdInCurrentContext").asBoolean());
        var restored = objectMapper.treeToValue(json, CollectiveIdentifiersQueryRequest.class);
        assertEquals(from.toInstant(), restored.getDateCreatedFrom().toInstant());
        assertEquals(to.toInstant(), restored.getDateCreatedTo().toInstant());
    }

    @Test
    public void testClientGetCollectiveIdentifiers() throws Exception {
        MockHttpClient http = new MockHttpClient(200, """
                {"continuationToken":"next-page","collectiveIdentifiers":[{
                  "collectiveIdentifierNumber":"1111111111-IZ202607-65ED02180000-E7",
                  "dateCreated":"2026-07-10T12:00:00+02:00",
                  "invoiceCount":16,"createdInCurrentContext":true}]}
                """);
        DefaultKsefClient client = new DefaultKsefClient(http,
                new TestApiProperties("https://ksef-test.mf.gov.pl/api/v2"), objectMapper);
        CollectiveIdentifiersQueryRequest query = new CollectiveIdentifiersQueryRequestBuilder()
                .withDateCreatedFrom(OffsetDateTime.parse("2026-07-10T12:00:00+02:00"))
                .withDateCreatedTo(OffsetDateTime.parse("2026-08-10T12:00:00+02:00"))
                .build();
        CollectiveIdentifiersQueryResponse response = client.getCollectiveIdentifiers(query, "current", 20, "access");
        assertEquals("next-page", response.getContinuationToken());
        assertEquals(1, response.getCollectiveIdentifiers().size());
        var item = response.getCollectiveIdentifiers().get(0);
        assertEquals("1111111111-IZ202607-65ED02180000-E7", item.getCollectiveIdentifierNumber());
        assertEquals(Integer.valueOf(16), item.getInvoiceCount());
        assertEquals(Boolean.TRUE, item.getCreatedInCurrentContext());
        assertEquals(OffsetDateTime.parse("2026-07-10T10:00:00Z").toInstant(), item.getDateCreated().toInstant());
        assertEquals("POST", http.getLastRequest().method());
        assertEquals("https://ksef-test.mf.gov.pl/api/v2/collective-identifiers/query?pageSize=20", http.getLastRequest().uri().toString());
        assertEquals("application/json", http.getLastRequest().headers().firstValue("Content-Type").orElse(null));
        assertQueryHeaders(http.getLastRequest());
        client.getCollectiveIdentifiers(query, null, null, "access");
        assertEquals(null, http.getLastRequest().uri().getQuery());
        assertTrue(http.getLastRequest().headers().firstValue("x-continuation-token").isEmpty());
    }

    @Test
    public void testClientGetCollectiveIdentifiersByKsefNumber() throws Exception {
        MockHttpClient http = new MockHttpClient(200, """
                {"continuationToken":null,"collectiveIdentifiers":[{
                  "collectiveIdentifierNumber":"1111111111-IZ202607-65ED02180000-E7",
                  "dateCreated":"2026-07-10T12:00:00+02:00","createdInCurrentContext":false}]}
                """);
        DefaultKsefClient client = new DefaultKsefClient(http,
                new TestApiProperties("https://ksef-test.mf.gov.pl/api/v2"), objectMapper);
        String number = "1111111111-20260101-111111111111-11";
        CollectiveIdentifiersByKsefNumberQueryResponse response = client.getCollectiveIdentifiersByKsefNumber(number, "current", 200, "access");
        assertEquals(null, response.getContinuationToken());
        assertEquals(1, response.getCollectiveIdentifiers().size());
        var item = response.getCollectiveIdentifiers().get(0);
        assertEquals("1111111111-IZ202607-65ED02180000-E7", item.getCollectiveIdentifierNumber());
        assertEquals(Boolean.FALSE, item.getCreatedInCurrentContext());
        assertEquals(OffsetDateTime.parse("2026-07-10T10:00:00Z").toInstant(), item.getDateCreated().toInstant());
        assertEquals("GET", http.getLastRequest().method());
        assertEquals("https://ksef-test.mf.gov.pl/api/v2/collective-identifiers/ksef/" + number + "?pageSize=200", http.getLastRequest().uri().toString());
        assertTrue(http.getLastRequest().bodyPublisher().isEmpty());
        assertQueryHeaders(http.getLastRequest());
        client.getCollectiveIdentifiersByKsefNumber(number, null, null, "access");
        assertEquals(null, http.getLastRequest().uri().getQuery());
        assertTrue(http.getLastRequest().headers().firstValue("x-continuation-token").isEmpty());
    }

    private void assertQueryHeaders(HttpRequest request) {
        assertEquals("Bearer access", request.headers().firstValue("Authorization").orElse(null));
        assertEquals("application/json", request.headers().firstValue("Accept").orElse(null));
        assertEquals("current", request.headers().firstValue("x-continuation-token").orElse(null));
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
