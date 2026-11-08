package pl.akmf.ksef.sdk.api;

import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.JsonType;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;
import pl.akmf.ksef.sdk.client.model.testdata.TestDataUpdateCertificateRequest;
import pl.akmf.ksef.sdk.system.ExceptionHandler;

import javax.net.ssl.SSLSession;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class JacksonFreeClientTest {
    @Test
    public void runsWithCustomSerializerWithoutAnyJacksonClasses() throws Exception {
        try (URLClassLoader isolated = isolatedLoader(false)) {
            assertThrows(ClassNotFoundException.class,
                    () -> isolated.loadClass("com.google.gson.Gson"));
            assertNoJackson(isolated);
            Class<?> probe = isolated.loadClass(Probe.class.getName());
            assertEquals("custom-key", probe.getMethod("run").invoke(null));

            // Resolve every public model member and its annotations without a JSON library.
            Path classes = Path.of(DefaultKsefClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            try (var paths = Files.walk(classes.resolve("pl/akmf/ksef/sdk/client/model"))) {
                for (Path path : paths.filter(p -> p.toString().endsWith(".class")).toList()) {
                    String name = classes.relativize(path).toString().replace(File.separatorChar, '.').replaceAll("\\.class$", "");
                    Class<?> model = Class.forName(name, false, isolated);
                    model.getDeclaredConstructors();
                    for (var method : model.getDeclaredMethods()) { method.getDeclaredAnnotations(); }
                    for (var field : model.getDeclaredFields()) { field.getDeclaredAnnotations(); }
                }
            }
        }
    }

    @Test
    public void runsWithGsonWithoutAnyJacksonClasses() throws Exception {
        try (URLClassLoader isolated = isolatedLoader(true)) {
            assertNoJackson(isolated);
            Class<?> probe = isolated.loadClass(Probe.class.getName());
            assertEquals("custom-key", probe.getMethod("runWithGson").invoke(null));
        }
    }

    private static URLClassLoader isolatedLoader(boolean includeGson) throws Exception {
        List<URL> entries = new ArrayList<>();
        String classpath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        for (String entry : classpath.split(File.pathSeparator)) {
            if (!entry.contains("jackson-") && !entry.contains("ksef-json-serializer-jackson")
                    && (includeGson || (!entry.contains("/gson/") && !entry.contains("ksef-json-serializer-gson")))) {
                entries.add(Path.of(entry).toUri().toURL());
            }
        }
        return new URLClassLoader(entries.toArray(URL[]::new), ClassLoader.getPlatformClassLoader());
    }

    private static void assertNoJackson(ClassLoader isolated) {
            assertThrows(ClassNotFoundException.class,
                    () -> isolated.loadClass("com.fasterxml.jackson.databind.ObjectMapper"));
            assertThrows(ClassNotFoundException.class,
                    () -> isolated.loadClass("com.fasterxml.jackson.annotation.JsonValue"));
            assertThrows(ClassNotFoundException.class,
                    () -> isolated.loadClass("io.alapierre.ksef.json.jackson.JacksonJsonSerializer"));
    }

    /** Loaded in an isolated classloader with no JSON library or adapter. */
    public static class Probe {
        public static String run() throws Exception {
            JsonSerializer serializer = new JsonSerializer() {
                public String name() { return "custom"; }
                public byte[] toJsonBytes(Object value) {
                    if (!(value instanceof TestDataUpdateCertificateRequest)) { throw new AssertionError(value); }
                    return "{}".getBytes(StandardCharsets.UTF_8);
                }
                @SuppressWarnings("unchecked")
                public <T> T fromJson(byte[] json, JsonType<T> type) {
                    if (!type.getType().equals(new JsonType<List<PublicKeyCertificate>>() { }.getType())) {
                        throw new AssertionError(type.getType());
                    }
                    PublicKeyCertificate certificate = new PublicKeyCertificate();
                    certificate.setPublicKeyId("custom-key");
                    return (T) List.of(certificate);
                }
            };
            return runClient(serializer);
        }

        public static String runWithGson() throws Exception {
            JsonSerializer serializer = (JsonSerializer) Class.forName("io.alapierre.ksef.json.gson.GsonJsonSerializer")
                    .getConstructor().newInstance();
            return runClient(serializer);
        }

        private static String runClient(JsonSerializer serializer) throws Exception {
            var properties = new KsefApiProperties() {
                public String getBaseUri() { return "https://example.invalid/v2"; }
                public String getSuffixUri() { return ""; }
                public String getQrUri() { return ""; }
                public Duration getRequestTimeout() { return Duration.ofSeconds(1); }
                public Map<String, String> getDefaultHeaders() { return Map.of(); }
            };
            var client = new DefaultKsefClient(null, properties, serializer) {
                @Override
                protected HttpResponse<byte[]> sendHttpRequest(HttpRequest request, HttpResponse.BodyHandler<byte[]> handler) {
                    return new HttpResponse<>() {
                        public int statusCode() { return 200; }
                        public HttpRequest request() { return request; }
                        public Optional<HttpResponse<byte[]>> previousResponse() { return Optional.empty(); }
                        public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (k, v) -> true); }
                        public byte[] body() { return "[{\"publicKeyId\":\"custom-key\"}]".getBytes(StandardCharsets.UTF_8); }
                        public Optional<SSLSession> sslSession() { return Optional.empty(); }
                        public URI uri() { return request.uri(); }
                        public java.net.http.HttpClient.Version version() { return java.net.http.HttpClient.Version.HTTP_1_1; }
                    };
                }
            };
            new DefaultLighthouseKsefClient(serializer, null, "https://example.invalid", Duration.ofSeconds(1), Map.of());
            new ExceptionHandler(serializer);
            client.updateCertificate("cert-1", new TestDataUpdateCertificateRequest(), "access");
            return client.retrievePublicKeyCertificate().getFirst().getPublicKeyId();
        }
    }
}
