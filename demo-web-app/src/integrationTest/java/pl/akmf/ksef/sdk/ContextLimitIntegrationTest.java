package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.session.BatchSessionLimit;
import pl.akmf.ksef.sdk.client.model.session.ChangeContextLimitRequest;
import pl.akmf.ksef.sdk.client.model.session.GetContextLimitResponse;
import pl.akmf.ksef.sdk.client.model.session.OnlineSessionLimit;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.util.IdentifierGeneratorUtils;

import java.io.IOException;

class ContextLimitIntegrationTest extends BaseIntegrationTest {

    @Disabled
    @Test
    void contextLimitE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        String accessToken = authWithCustomNip(contextNip, contextNip).accessToken();

        changeContextLimit(accessToken);

        GetContextLimitResponse limitAfterChanges = getExpectedResponseAfterChanges();

        getContextLimit(accessToken, limitAfterChanges);

        resetContextLimit(accessToken);

        GetContextLimitResponse baseLimits = getExpectedBaseResponse();

        getContextLimit(accessToken, baseLimits);
    }

    private void getContextLimit(String accessToken, GetContextLimitResponse expectedResponse) throws ApiException {
        GetContextLimitResponse response = ksefClient.getContextLimit(accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResponse, response);
    }

    private void changeContextLimit(String accessToken) throws ApiException {
        ChangeContextLimitRequest request = new ChangeContextLimitRequest();
        OnlineSessionLimit onlineSessionLimit = new OnlineSessionLimit(1, 10, 100);
        BatchSessionLimit batchSessionLimit = new BatchSessionLimit(1, 10, 100);

        request.setOnlineSession(onlineSessionLimit);
        request.setBatchSession(batchSessionLimit);

        ksefClient.changeContextLimitTest(request, accessToken);
    }

    private void resetContextLimit(String accessToken) throws ApiException {
        ksefClient.resetContextLimitTest(accessToken);
    }

    private GetContextLimitResponse getExpectedBaseResponse() {
        GetContextLimitResponse response = new GetContextLimitResponse();
        OnlineSessionLimit onlineSessionLimit = new OnlineSessionLimit(1, 3, 10000);
        BatchSessionLimit batchSessionLimit = new BatchSessionLimit(1, 3, 10000);

        response.setOnlineSession(onlineSessionLimit);
        response.setBatchSession(batchSessionLimit);

        return response;
    }

    private GetContextLimitResponse getExpectedResponseAfterChanges() {
        GetContextLimitResponse response = new GetContextLimitResponse();
        OnlineSessionLimit onlineSessionLimit = new OnlineSessionLimit(1, 10, 100);
        BatchSessionLimit batchSessionLimit = new BatchSessionLimit(1, 10, 100);

        response.setOnlineSession(onlineSessionLimit);
        response.setBatchSession(batchSessionLimit);

        return response;
    }
}