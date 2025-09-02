package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.permission.entity.EntityAuthorizationPermissionsQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.permission.proxy.GrantProxyEntityPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.proxy.ProxyEntityPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.proxy.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.proxy.SubjectIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.search.EntityAuthorizationGrant;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class ProxyPermissionIntegrationTest extends BaseIntegrationTest {

    @Test
    void proxyPermissionE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = TestUtils.generateRandomNIP();
        String subjectNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var grantReferenceNumber = grantPermission(subjectNip, authToken);

        await().atMost(15, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isPermissionStatusReady(grantReferenceNumber, authToken));

        var permission = searchRole(1, authToken);

        permission.forEach(e -> {
            var revokeReferenceNumber = revokePermission(e, authToken);

            await().atMost(30, SECONDS)
                    .pollInterval(2, SECONDS)
                    .until(() -> isPermissionStatusReady(revokeReferenceNumber, authToken));
        });
        searchRole(0, authToken);
    }

    private Boolean isPermissionStatusReady(String grantReferenceNumber, String authToken) throws ApiException {
        var status = defaultKsefClient.permissionOperationStatus(grantReferenceNumber, authToken);
        return status != null && status.getStatus().getCode() == 200;
    }

    private String revokePermission(String operationId, String authToken) {
        try {
            return defaultKsefClient.revokeAuthorizationsPermission(operationId, authToken).getOperationReferenceNumber();
        } catch (ApiException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private String grantPermission(String subjectNip, String authToken) throws ApiException {
        var request = new GrantProxyEntityPermissionsRequestBuilder()
                .withSubjectIdentifier(new SubjectIdentifier(SubjectIdentifierType.NIP, subjectNip))
                .withPermission(ProxyEntityPermissionType.RRINVOICING)
                .withDescription("e2e test")
                .build();

        var response = defaultKsefClient.grantsPermissionsProxyEntity(request, authToken);
        Assertions.assertNotNull(response);
        return response.getOperationReferenceNumber();
    }

    private List<String> searchRole(int expectedRole, String authToken) throws ApiException {
        var request = new EntityAuthorizationPermissionsQueryRequestBuilder()
                .withQueryType(QueryType.GRANTED)
                .build();

        var response = defaultKsefClient.searchEntityAuthorizationGrants(request, 0, 10, authToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedRole, response.getAuthorizationGrants().size());

        return response.getAuthorizationGrants()
                .stream()
                .map(EntityAuthorizationGrant::getId)
                .toList();
    }
}
