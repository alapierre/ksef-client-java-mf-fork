package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.permission.entity.EntityAuthorizationPermissionsQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.permission.proxy.GrantAuthorizationPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.OperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.proxy.GrantAuthorizationPermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.proxy.ProxyEntityPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.proxy.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.proxy.SubjectIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.search.EntityAuthorizationGrant;
import pl.akmf.ksef.sdk.client.model.permission.search.EntityAuthorizationPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityAuthorizationPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.util.IdentifierGeneratorUtils;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class ProxyPermissionIntegrationTest extends BaseIntegrationTest {

    @Test
    void proxyPermissionE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        String subjectNip = IdentifierGeneratorUtils.generateRandomNIP();
        String accessToken = authWithCustomNip(contextNip, contextNip).accessToken();

        String grantReferenceNumber = grantPermission(subjectNip, accessToken);

        await().atMost(15, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isPermissionStatusReady(grantReferenceNumber, accessToken));

        List<String> permission = searchRole(1, accessToken);

        permission.forEach(e -> {
            String revokeReferenceNumber = revokePermission(e, accessToken);

            await().atMost(30, SECONDS)
                    .pollInterval(2, SECONDS)
                    .until(() -> isPermissionStatusReady(revokeReferenceNumber, accessToken));
        });
        searchRole(0, accessToken);
    }

    private Boolean isPermissionStatusReady(String grantReferenceNumber, String accessToken) throws ApiException {
        PermissionStatusInfo status = createKSeFClient().permissionOperationStatus(grantReferenceNumber, accessToken);
        return status != null && status.getStatus().getCode() == 200;
    }

    private String revokePermission(String operationId, String accessToken) {
        try {
            return createKSeFClient().revokeAuthorizationsPermission(operationId, accessToken).getOperationReferenceNumber();
        } catch (ApiException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private String grantPermission(String subjectNip, String accessToken) throws ApiException {
        GrantAuthorizationPermissionsRequest request = new GrantAuthorizationPermissionsRequestBuilder()
                .withSubjectIdentifier(new SubjectIdentifier(SubjectIdentifierType.NIP, subjectNip))
                .withPermission(ProxyEntityPermissionType.SELFINVOICING)
                .withDescription("e2e test grant")
                .build();

        OperationResponse response = createKSeFClient().grantsPermissionsProxyEntity(request, accessToken);
        Assertions.assertNotNull(response);
        return response.getOperationReferenceNumber();
    }

    private List<String> searchRole(int expectedRole, String accessToken) throws ApiException {
        EntityAuthorizationPermissionsQueryRequest request = new EntityAuthorizationPermissionsQueryRequestBuilder()
                .withQueryType(QueryType.GRANTED)
                .build();

        QueryEntityAuthorizationPermissionsResponse response = createKSeFClient().searchEntityAuthorizationGrants(request, 0, 10, accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedRole, response.getAuthorizationGrants().size());

        return response.getAuthorizationGrants()
                .stream()
                .map(EntityAuthorizationGrant::getId)
                .toList();
    }
}
