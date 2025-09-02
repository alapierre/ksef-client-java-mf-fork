package pl.akmf.ksef.sdk;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.akmf.ksef.sdk.api.builders.certificate.CertificateMetadataListRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.certificate.RevokeCertificateRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.certificate.SendCertificateEnrollmentRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentStatusResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentsInfoResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateListRequest;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateRevocationReason;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateType;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class CertificateIntegrationTest extends BaseIntegrationTest {

    @Test
    void certificateE2EIntegrationTest() throws JAXBException, IOException, ApiException {
        String contextNip = TestUtils.generateRandomNIP();
        var authToken = authWithCustomNip(contextNip, contextNip).authToken();

        getCertificateLimitAsync(authToken);

        var enrollmentInfo = getEnrolmentInfo(authToken);

        var referenceNumber = sendEnrollment(enrollmentInfo, authToken);

        await().atMost(30, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> isEnrolmentStatusReady(referenceNumber, authToken));

        var enrolmentStatus = getEnrolmentStatus(referenceNumber, authToken);

        getCertificateList(enrolmentStatus.getCertificateSerialNumber(), authToken);

        revokeCertificate(enrolmentStatus.getCertificateSerialNumber(), authToken);

        getMedataCertificateList(enrolmentStatus.getCertificateSerialNumber(), authToken);
    }

    private Boolean isEnrolmentStatusReady(String referenceNumber, String authToken) {
        try {
            var response = defaultKsefClient.getCertificateEnrollmentStatus(referenceNumber, authToken);
            return response != null &&
                    response.getStatus().getCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private void getMedataCertificateList(String certificateSerialNumber, String authToken) throws ApiException {
        var request = new CertificateMetadataListRequestBuilder()
                .build();

        var response = defaultKsefClient.getCertificateMetadataList(request, 10, 0, authToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getCertificates().getFirst().getCertificateSerialNumber(), certificateSerialNumber);
    }

    private void revokeCertificate(String serialNumber, String authToken) throws ApiException {
        var request = new RevokeCertificateRequestBuilder()
                .withRevocationReason(CertificateRevocationReason.KEYCOMPROMISE)
                .build();

        defaultKsefClient.revokeCertificate(request, serialNumber, authToken);
    }

    private void getCertificateList(String certificateSerialNumber, String authToken) throws ApiException {
        var certificateResponse =
                defaultKsefClient.getCertificateList(new CertificateListRequest(List.of(certificateSerialNumber)), authToken);

        Assertions.assertNotNull(certificateResponse);
        Assertions.assertEquals(1, certificateResponse.getCertificates().size());
    }

    private CertificateEnrollmentStatusResponse getEnrolmentStatus(String referenceNumber, String authToken) throws ApiException {
        var response = defaultKsefClient.getCertificateEnrollmentStatus(referenceNumber, authToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatus().getCode());
        return response;
    }

    private String sendEnrollment(CertificateEnrollmentsInfoResponse enrollmentInfo, String authToken) throws ApiException {
        var csr = new DefaultCryptographyService(defaultKsefClient).generateCsr(enrollmentInfo);

        var request = new SendCertificateEnrollmentRequestBuilder()
                .withValidFrom(LocalDateTime.now().toString())
                .withCsr(csr.csr())
                .withCertificateName("certificate")
                .withCertificateType(CertificateType.Authentication)
                .build();

        var response = defaultKsefClient.sendCertificateEnrollment(request, authToken);
        Assertions.assertNotNull(response);

        return response.getReferenceNumber();
    }

    private CertificateEnrollmentsInfoResponse getEnrolmentInfo(String authToken) throws ApiException {
        var response = defaultKsefClient.getCertificateEnrollmentInfo(authToken);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getOrganizationIdentifier());

        return response;
    }

    private void getCertificateLimitAsync(String authToken) throws ApiException {
        var response = defaultKsefClient.getCertificateLimits(authToken);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getCanRequest());
    }
}
