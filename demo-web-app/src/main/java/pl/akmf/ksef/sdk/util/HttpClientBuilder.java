package pl.akmf.ksef.sdk.util;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

public class HttpClientBuilder {

    public static HttpClient.Builder createHttpBuilder() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .executor(ForkJoinPool.commonPool());
    }
}
