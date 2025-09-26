package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.permission.indirect.GrantIndirectEntityPermissionsRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.permission.person.PersonPermissionsQueryRequestBuilder;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.OperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.indirect.GrantIndirectEntityPermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.indirect.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.indirect.SubjectIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.indirect.TargetIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.indirect.TargetIdentifierType;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionQueryType;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryPersonPermissionsResponse;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.util.IdentifierGeneratorUtils;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static pl.akmf.ksef.sdk.client.model.permission.indirect.IndirectPermissionType.INVOICEWRITE;

class IndirectPermissionIntegrationTest extends BaseIntegrationTest {

    // TODO - do weryfikacji scenariusz
    @Test
    void indirectPermissionE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = IdentifierGeneratorUtils.generateRandomNIP();
        String contextAccessToken = authWithCustomNip(contextNip, contextNip).accessToken();
        String subjectNip = IdentifierGeneratorUtils.getRandomNip("9");
        String targetNip = IdentifierGeneratorUtils.getRandomNip("9");

        String grantIndirectReferenceNumber = grantIndirectPermission(targetNip, subjectNip, contextAccessToken);

        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> isOperationFinish(grantIndirectReferenceNumber, contextAccessToken));

        String permissionId = checkGrantedPermission(1, contextAccessToken);

        revokePermission(permissionId, contextAccessToken);

        checkGrantedPermission(0, contextAccessToken);
    }

    private String revokePermission(String operationId, String accessToken) {
        try {
            return createKSeFClient().revokeAuthorizationsPermission(operationId, accessToken).getOperationReferenceNumber();
        } catch (ApiException e) {
            Assertions.fail(e.getMessage());
        }
        return null;
    }

    private String checkGrantedPermission(int expected, String accessToken) throws ApiException {
        PersonPermissionsQueryRequest request = new PersonPermissionsQueryRequestBuilder()
                .withQueryType(PersonPermissionQueryType.PERMISSION_GRANTED_IN_CURRENT_CONTEXT)
                .build();

        QueryPersonPermissionsResponse response = createKSeFClient().searchGrantedPersonPermissions(request, 0, 10, accessToken);
        Assertions.assertEquals(expected, response.getPermissions().size());

        return response.getPermissions().getFirst().getId();
    }

    private String grantIndirectPermission(String targetNip, String subjectNip, String accessToken) throws ApiException {
        GrantIndirectEntityPermissionsRequest request = new GrantIndirectEntityPermissionsRequestBuilder()
                .withSubjectIdentifier(new SubjectIdentifier(SubjectIdentifierType.NIP, subjectNip))
                .withTargetIdentifier(new TargetIdentifier(TargetIdentifierType.NIP, targetNip))
                .withPermissions(List.of(INVOICEWRITE))
                .withDescription("E2E indirect grantE2E indirect grant")
                .build();

        OperationResponse response = createKSeFClient().grantsPermissionIndirectEntity(request, accessToken);
        Assertions.assertNotNull(response);
        return response.getOperationReferenceNumber();
    }

    private Boolean isOperationFinish(String referenceNumber, String accessToken) throws ApiException {
        PermissionStatusInfo operations = createKSeFClient().permissionOperationStatus(referenceNumber, accessToken);
        return operations != null && operations.getStatus().getCode() == 200;
    }
}
