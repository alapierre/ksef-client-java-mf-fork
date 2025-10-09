package pl.akmf.ksef.sdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.peppol.PeppolProvidersListResponse;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

class PeppolProviderIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testPeppolProvider() throws ApiException {
        PeppolProvidersListResponse response = ksefClient.getPeppolProvidersList(0, 10);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(!response.getPeppolProviders().isEmpty());
    }
}
