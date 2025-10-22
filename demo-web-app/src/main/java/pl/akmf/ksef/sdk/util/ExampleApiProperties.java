package pl.akmf.ksef.sdk.util;

import pl.akmf.ksef.sdk.api.KsefApiProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ExampleApiProperties extends KsefApiProperties {

    @Override
    public String getBaseUri() {
        return "https://ksef-test.mf.gov.pl/";
    }

    @Override
    public Duration getRequestTimeout() {
        return Duration.ofSeconds(100);
    }

    @Override
    public Map<String, String> getDefaultHeaders() {

        return new HashMap<>();
    }
}
