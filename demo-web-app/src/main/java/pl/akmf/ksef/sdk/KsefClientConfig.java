package pl.akmf.ksef.sdk;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.HttpApiClient;

@Configuration
@RequiredArgsConstructor
public class KsefClientConfig {

    @Bean
    public DefaultKsefClient init() {
        var httpClient = new HttpApiClient.Builder()
                .build();

        return new DefaultKsefClient(httpClient);
    }
}
