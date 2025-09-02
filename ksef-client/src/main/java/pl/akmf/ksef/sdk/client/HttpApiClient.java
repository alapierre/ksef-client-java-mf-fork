package pl.akmf.ksef.sdk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.akmf.ksef.sdk.system.SystemKSeFSDKException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class HttpApiClient {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private final String baseUrl;

    private final HttpClient httpClient;
    private final Duration defaultTimeout;
    private Map<String, String> defaultHeaders;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static class Builder {
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(30);
        private HttpClient.Redirect redirectPolicy = HttpClient.Redirect.NORMAL;
        private HttpClient.Version version = HttpClient.Version.HTTP_2;
        private Executor executor = ForkJoinPool.commonPool();
        private Map<String, String> defaultHeaders = new ConcurrentHashMap<>();
        private String baseUrl;

        public Builder connectTimeout(Duration timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        public Builder requestTimeout(Duration timeout) {
            this.requestTimeout = timeout;
            return this;
        }

        public Builder redirectPolicy(HttpClient.Redirect policy) {
            this.redirectPolicy = policy;
            return this;
        }

        public Builder httpVersion(HttpClient.Version version) {
            this.version = version;
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder defaultHeader(String name, String value) {
            this.defaultHeaders.put(name, value);
            return this;
        }

        public HttpApiClient build() {
            return new HttpApiClient(this);
        }
    }

    private HttpApiClient(Builder builder) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(builder.connectTimeout)
                .followRedirects(builder.redirectPolicy)
                .version(builder.version)
                .executor(builder.executor)
                .build();

        this.defaultHeaders = new ConcurrentHashMap<>(builder.defaultHeaders);
        this.defaultTimeout = builder.requestTimeout;
        this.baseUrl = builder.baseUrl;
    }

    public HttpResponse<byte[]> get(String uri, Map<String, String> headers) {
        try {
            HttpRequest request = buildRequest(uri, GET, null, headers);

            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new SystemKSeFSDKException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemKSeFSDKException(e.getMessage(), e);
        }
    }

    public HttpResponse<byte[]> post(String uri, Object body, Map<String, String> headers) throws SystemKSeFSDKException {
        try {
            HttpRequest request = buildRequest(uri, POST, body, headers);

            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new SystemKSeFSDKException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemKSeFSDKException(e.getMessage(), e);
        }
    }

    public HttpResponse<byte[]> put(String uri, Object body, Map<String, String> headers) throws SystemKSeFSDKException {
        try {
            HttpRequest request = buildRequest(uri, PUT, body, headers);

            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new SystemKSeFSDKException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemKSeFSDKException(e.getMessage(), e);
        }
    }

    public HttpResponse<byte[]> delete(String uri, Map<String, String> headers) throws SystemKSeFSDKException {
        try {
            HttpRequest request = buildRequest(uri, DELETE, null, headers);
            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            throw new SystemKSeFSDKException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SystemKSeFSDKException(e.getMessage(), e);
        }
    }

    private HttpRequest buildRequest(String uri, String method, Object body, Map<String, String> additionalHeaders) throws JsonProcessingException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + uri))
                .timeout(defaultTimeout);

        defaultHeaders.forEach(builder::header);

        additionalHeaders.forEach(builder::header);

        if (body instanceof String) {
            var stringBody = (String) body;
            switch (method.toUpperCase()) {
                case GET:
                    builder.GET();
                    break;
                case POST:
                    builder.POST(HttpRequest.BodyPublishers.ofString(stringBody));
                    break;
                case PUT:
                    builder.PUT(HttpRequest.BodyPublishers.ofString(stringBody));
                    break;
                case DELETE:
                    builder.DELETE();
                    break;
                default:
                    builder.method(method, HttpRequest.BodyPublishers.ofString(stringBody));
            }

        } else {
            switch (method.toUpperCase()) {
                case GET:
                    builder.GET();
                    break;
                case POST:
                    builder.POST(body != null ?
                            HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body)) :
                            HttpRequest.BodyPublishers.noBody());
                    break;
                case PUT:
                    builder.PUT(body != null ?
                            HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body)) :
                            HttpRequest.BodyPublishers.noBody());
                    break;
                case DELETE:
                    builder.DELETE();
                    break;
                default:
                    builder.method(method, body != null ?
                            HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body)) :
                            HttpRequest.BodyPublishers.noBody());
            }

        }

        return builder.build();
    }

    public void setDefaultHeader(String name, String value) {
        defaultHeaders.put(name, value);
    }

    public void removeDefaultHeader(String name) {
        defaultHeaders.remove(name);
    }

    public Map<String, String> getDefaultHeaders() {
        return Map.copyOf(defaultHeaders);
    }

    public static HttpApiClient create() {
        return new Builder().build();
    }
}
