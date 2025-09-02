package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.permission.euentity.EuEntityPermissionsQueryRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.permission.euentity.GrantEUEntityPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.euentity.ContextIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.euentity.ContextIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.euentity.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.euentity.SubjectIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.search.EuEntityPermission;
import pl.akmf.ksef.sdk.client.model.permission.search.EuEntityPermissionsQueryPermissionType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class EuEntityPermissionIntegrationTest extends BaseIntegrationTest {

    @Test
    void euEntityPermissionE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = TestUtils.generateRandomNIP();
        String subjectNip = TestUtils.generateRandomNIP();
        String nipVatEu = contextNip + "-" + TestUtils.generateRandomVatEu();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        var grantReferenceNumber = grantEuEntityPermission(subjectNip, nipVatEu, authToken);

        await().atMost(50, SECONDS)
                .pollInterval(10, SECONDS)
                .until(() -> isOperationFinish(grantReferenceNumber, authToken));

        List<EuEntityPermission> permissions = checkPermission(subjectNip, 4, authToken);

        EuEntityPermission invoiceWritePermission = permissions.stream()
                .filter(e -> e.getPermissionScope() == EuEntityPermissionsQueryPermissionType.INVOICEWRITE)
                .findFirst()
                .orElseThrow();
        var revokeReferenceNumber = revokePermission(invoiceWritePermission.getId(), authToken);

            await().atMost(30, SECONDS)
                    .pollInterval(2, SECONDS)
                    .until(() -> isOperationFinish(revokeReferenceNumber, authToken));

        checkPermission(subjectNip, 3, authToken);
    }

    private List<EuEntityPermission> checkPermission(String subjectContext, int expectedNumber, String authToken) throws ApiException {
        var request = new EuEntityPermissionsQueryRequestBuilder()
                .withAuthorizedFingerprintIdentifier(subjectContext)
                .build();

        var response = defaultKsefClient.searchGrantedEuEntityPermissions(request, 0, 10, authToken);

        Assertions.assertEquals(expectedNumber, response.getPermissions().size());

        return response.getPermissions();
    }

    private String revokePermission(String operationId, String authToken) {
        try {
            return defaultKsefClient.revokeCommonPermission(operationId, authToken).getOperationReferenceNumber();
        } catch (ApiException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private String grantEuEntityPermission(String subjectNip, String euContext, String authToken) throws ApiException {
        var request = new GrantEUEntityPermissionsRequestBuilder()
                .withSubject(new SubjectIdentifier(SubjectIdentifierType.FINGERPRINT, subjectNip))
                .withContext(new ContextIdentifier(ContextIdentifierType.NIPVATUE, euContext))
                .withDescription("e2e test")
                .build();

        var response = defaultKsefClient.grantsPermissionEUEntity(request, authToken);

        Assertions.assertNotNull(response);

        return response.getOperationReferenceNumber();
    }

    private Boolean isOperationFinish(String referenceNumber, String authToken) throws ApiException {
        PermissionStatusInfo operations = defaultKsefClient.permissionOperationStatus(referenceNumber, authToken);
        if (operations != null && operations.getStatus().getCode() >= 400) {
            throw new RuntimeException("Could not finish operation: " + operations.getStatus().getDescription());
        }
        return operations != null && operations.getStatus().getCode() == 200;
    }
}
