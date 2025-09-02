package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.permission.indirect.GrantIndirectEntityPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.permission.person.PersonPermissionsQueryRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.indirect.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.indirect.SubjectIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.indirect.TargetIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.indirect.TargetIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionQueryType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static pl.akmf.ksef.sdk.client.model.permission.indirect.IndirectPermissionType.INVOICEWRITE;

class IndirectPermissionIntegrationTest extends BaseIntegrationTest {

    @Test
    void indirectPermissionE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = TestUtils.generateRandomNIP();
        String subjectNip = TestUtils.generateRandomNIP();
        String targetNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(subjectNip, subjectNip).authToken();

        var grantIndirectReferenceNumber = grantIndirectPermission(targetNip, contextNip, authToken);

        await().atMost(15, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isOperationFinish(grantIndirectReferenceNumber, authToken));

        checkGrantedPermission(1, authToken);
    }

    private String checkGrantedPermission(int expected, String authToken) throws ApiException {
        var request = new PersonPermissionsQueryRequestBuilder()
                .withQueryType(PersonPermissionQueryType.PERMISSION_GRANTED_IN_CURRENT_CONTEXT)
                .build();

        var response = defaultKsefClient.searchGrantedPersonPermissions(request, 0, 10, authToken);
        Assertions.assertEquals(expected, response.getPermissions().size());

        return response.getPermissions().getFirst().getId();
    }

    private String grantIndirectPermission(String targetNip, String contextNip, String authToken) throws ApiException {
        var request = new GrantIndirectEntityPermissionsRequestBuilder()
                .withSubjectIdentifier(new SubjectIdentifier(SubjectIdentifierType.NIP, targetNip))
                .withTargetIdentifier(new TargetIdentifier(TargetIdentifierType.NIP, contextNip))
                .withPermissions(List.of(INVOICEWRITE))
                .withDescription("e2e test")
                .build();

        var response = defaultKsefClient.grantsPermissionIndirectEntity(request, authToken);
        Assertions.assertNotNull(response);
        return response.getOperationReferenceNumber();
    }

    private Boolean isOperationFinish(String referenceNumber, String authToken) throws ApiException {
        PermissionStatusInfo operations = defaultKsefClient.permissionOperationStatus(referenceNumber, authToken);
        return operations != null && operations.getStatus().getCode() == 200;
    }
}
