package pl.akmf.ksef.sdk.util;

import pl.akmf.ksef.sdk.api.KsefApiProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ExampleApiProperties extends KsefApiProperties {

    @Override
    public String getBaseUri() {
        return "";
    }

    @Override
    public Duration getRequestTimeout() {
        return Duration.ofSeconds(5);
    }

    @Override
    public Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();

        return headers;
    }
}
