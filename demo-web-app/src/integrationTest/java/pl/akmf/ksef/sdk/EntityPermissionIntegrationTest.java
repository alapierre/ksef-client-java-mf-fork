package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.permission.entity.GrantEntityPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.permission.person.PersonPermissionsQueryRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.entity.EntityPermission;
import pl.akmf.ksef.sdk.client.model.permission.entity.EntityPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.entity.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.entity.SubjectIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermission;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionQueryType;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionsQueryRequest;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class EntityPermissionIntegrationTest extends BaseIntegrationTest {

    @Test
    void entityPermissionE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = TestUtils.generateRandomNIP();
        String subjectNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var grantReferenceNumber = grantPermission(subjectNip, authToken);

        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isOperationFinish(grantReferenceNumber, authToken));

        var permission = searchPermission(2, authToken);

        permission.forEach(e -> {
            var revokeReferenceNumber = revokePermission(e, authToken);

            await().atMost(30, SECONDS)
                    .pollInterval(2, SECONDS)
                    .until(() -> isOperationFinish(revokeReferenceNumber, authToken));
        });

        searchPermission(0, authToken);
    }

    private Boolean isOperationFinish(String referenceNumber, String authToken) throws ApiException {
        PermissionStatusInfo operations = defaultKsefClient.permissionOperationStatus(referenceNumber, authToken);
        return operations != null && operations.getStatus().getCode() == 200;
    }

    private String revokePermission(String operationId, String authToken) {
        try {
            return defaultKsefClient.revokeCommonPermission(operationId, authToken).getOperationReferenceNumber();
        } catch (ApiException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private List<String> searchPermission(int expectedRole, String authToken) throws ApiException {
        PersonPermissionsQueryRequest request = new PersonPermissionsQueryRequestBuilder()
                .withQueryType(PersonPermissionQueryType.PERMISSION_GRANTED_IN_CURRENT_CONTEXT)
                .build();

        var response = defaultKsefClient.searchGrantedPersonPermissions(request, 0, 10, authToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedRole, response.getPermissions().size());

        return response.getPermissions()
                .stream()
                .map(PersonPermission::getId)
                .toList();
    }

    private String grantPermission(String targetNip, String authToken) throws ApiException {
        var request = new GrantEntityPermissionsRequestBuilder()
                .withPermissions(List.of(
                        new EntityPermission(EntityPermissionType.INVOICEREAD, true),
                        new EntityPermission(EntityPermissionType.INVOICEWRITE, false)))
                .withDescription("EWE test grant")
                .withSubjectIdentifier(new SubjectIdentifier(SubjectIdentifierType.NIP, targetNip))
                .build();

        var response = defaultKsefClient.grantsPermissionEntity(request, authToken);
        Assertions.assertNotNull(response);

        return response.getOperationReferenceNumber();
    }
}
