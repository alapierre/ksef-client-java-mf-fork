package pl.akmf.ksef.sdk.api.services;

import org.apache.commons.collections4.CollectionUtils;
import pl.akmf.ksef.sdk.api.ActiveSessionApi;
import pl.akmf.ksef.sdk.api.AuthenticationApi;
import pl.akmf.ksef.sdk.api.BatchInvoiceApi;
import pl.akmf.ksef.sdk.api.CertificateApi;
import pl.akmf.ksef.sdk.api.DownloadInvoiceApi;
import pl.akmf.ksef.sdk.api.EuSubjectAdministratorApi;
import pl.akmf.ksef.sdk.api.EuSubjectRepresentationApi;
import pl.akmf.ksef.sdk.api.ForAuthorizedSubjectApi;
import pl.akmf.ksef.sdk.api.GrantDirectlyApi;
import pl.akmf.ksef.sdk.api.InteractiveSessionApi;
import pl.akmf.ksef.sdk.api.NaturalPersonKseFApi;
import pl.akmf.ksef.sdk.api.OperationApi;
import pl.akmf.ksef.sdk.api.PublicKeyCertificateApi;
import pl.akmf.ksef.sdk.api.SearchPermissionApi;
import pl.akmf.ksef.sdk.api.SendStatusAndUpoApi;
import pl.akmf.ksef.sdk.api.SubUnitSubjectAdministratorApi;
import pl.akmf.ksef.sdk.api.SubjectForInvoiceApi;
import pl.akmf.ksef.sdk.api.TokensApi;
import pl.akmf.ksef.sdk.client.HttpApiClient;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.auth.AuthKsefTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationChallengeResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationInitResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationOperationStatusResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationToken;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenRefreshResponse;
import pl.akmf.ksef.sdk.client.model.auth.AuthenticationTokenStatus;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenRequest;
import pl.akmf.ksef.sdk.client.model.auth.GenerateTokenResponse;
import pl.akmf.ksef.sdk.client.model.auth.QueryTokensResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentStatusResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateEnrollmentsInfoResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateLimitsResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateListRequest;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateListResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateMetadataListResponse;
import pl.akmf.ksef.sdk.client.model.certificate.CertificateRevokeRequest;
import pl.akmf.ksef.sdk.client.model.certificate.QueryCertificatesRequest;
import pl.akmf.ksef.sdk.client.model.certificate.SendCertificateEnrollmentRequest;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;
import pl.akmf.ksef.sdk.client.model.invoice.AsyncInvoicesQueryStatus;
import pl.akmf.ksef.sdk.client.model.invoice.DownloadInvoiceRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InitAsyncInvoicesQueryResponse;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InvoicesAsyncQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.QueryInvoiceMetadataResponse;
import pl.akmf.ksef.sdk.client.model.permission.PermissionStatusInfo;
import pl.akmf.ksef.sdk.client.model.permission.PermissionsOperationResponse;
import pl.akmf.ksef.sdk.client.model.permission.entity.GrantEntityPermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.euentity.EuEntityPermissionsGrantRequest;
import pl.akmf.ksef.sdk.client.model.permission.euentity.GrantEUEntityRepresentativePermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.indirect.IndirectPermissionsGrantRequest;
import pl.akmf.ksef.sdk.client.model.permission.person.GrantPersonPermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.proxy.GrantProxyEntityPermissionsRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.EntityAuthorizationPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.EuEntityPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityAuthorizationPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityRolesResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEuEntityPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryPersonPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QuerySubordinateEntityRolesResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QuerySubunitPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.SubordinateEntityRolesQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.SubunitPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.subunit.SubunitPermissionsGrantRequest;
import pl.akmf.ksef.sdk.client.model.session.AuthenticationListResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionInvoiceStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionInvoicesResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryResponse;
import pl.akmf.ksef.sdk.client.model.session.batch.BatchPartSendingInfo;
import pl.akmf.ksef.sdk.client.model.session.batch.BatchPartStreamSendingInfo;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionResponse;
import pl.akmf.ksef.sdk.client.model.session.batch.PackagePartSignatureInitResponseType;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionRequest;
import pl.akmf.ksef.sdk.client.model.session.online.OpenOnlineSessionResponse;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceRequest;
import pl.akmf.ksef.sdk.client.model.session.online.SendInvoiceResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultKsefClient implements KSeFClient {
    private final AuthenticationApi authenticationApi;
    private final InteractiveSessionApi interactiveSessionApi;
    private final CertificateApi certificateApi;
    private final SearchPermissionApi searchPermissionApi;
    private final ForAuthorizedSubjectApi forAuthorizedSubjectApi;
    private final GrantDirectlyApi grantDirectlyApi;
    private final DownloadInvoiceApi downloadInvoiceApi;
    private final SubjectForInvoiceApi subjectForInvoiceApi;
    private final EuSubjectRepresentationApi euSubjectRepresentationApi;
    private final EuSubjectAdministratorApi euSubjectAdministratorApi;
    private final SubUnitSubjectAdministratorApi subUnitSubjectAdministratorApi;
    private final SendStatusAndUpoApi sendStatusAndUpoApi;
    private final BatchInvoiceApi batchInvoiceApi;
    private final NaturalPersonKseFApi naturalPersonKseFApi;
    private final OperationApi operationApi;
    private final TokensApi tokensApi;
    private final PublicKeyCertificateApi publicKeyCertificateApi;
    private final ActiveSessionApi activeSessionApi;

    public DefaultKsefClient(HttpApiClient httpApiClient) {
        authenticationApi = new AuthenticationApi(httpApiClient);
        interactiveSessionApi = new InteractiveSessionApi(httpApiClient);
        certificateApi = new CertificateApi(httpApiClient);
        searchPermissionApi = new SearchPermissionApi(httpApiClient);
        forAuthorizedSubjectApi = new ForAuthorizedSubjectApi(httpApiClient);
        grantDirectlyApi = new GrantDirectlyApi(httpApiClient);
        downloadInvoiceApi = new DownloadInvoiceApi(httpApiClient);
        subjectForInvoiceApi = new SubjectForInvoiceApi(httpApiClient);
        euSubjectRepresentationApi = new EuSubjectRepresentationApi(httpApiClient);
        euSubjectAdministratorApi = new EuSubjectAdministratorApi(httpApiClient);
        subUnitSubjectAdministratorApi = new SubUnitSubjectAdministratorApi(httpApiClient);
        sendStatusAndUpoApi = new SendStatusAndUpoApi(httpApiClient);
        batchInvoiceApi = new BatchInvoiceApi(httpApiClient);
        naturalPersonKseFApi = new NaturalPersonKseFApi(httpApiClient);
        operationApi = new OperationApi(httpApiClient);
        tokensApi = new TokensApi(httpApiClient);
        publicKeyCertificateApi = new PublicKeyCertificateApi(httpApiClient);
        activeSessionApi = new ActiveSessionApi(httpApiClient);
    }

    @Override
    public PermissionsOperationResponse grantsPermissionsProxyEntity(GrantProxyEntityPermissionsRequest body, String authenticationToken) throws ApiException {

        return forAuthorizedSubjectApi.apiV2PermissionsAuthorizationsGrantsPost(body, authenticationToken).getData();
    }

    @Override
    public PermissionsOperationResponse grantsPermissionIndirectEntity(IndirectPermissionsGrantRequest body, String authenticationToken) throws ApiException {
        return grantDirectlyApi.apiV2PermissionsIndirectGrantsPost(body, authenticationToken).getData();
    }

    @Override
    public OpenBatchSessionResponse openBatchSession(OpenBatchSessionRequest body, String authenticationToken) throws ApiException {

        return batchInvoiceApi.batchOpenWithHttpInfo(body, authenticationToken).getData();
    }

    @Override
    public void closeBatchSession(String referenceNumber, String authenticationToken) throws ApiException {
        batchInvoiceApi.apiV2SessionsBatchReferenceNumberClosePostWithHttpInfo(referenceNumber, authenticationToken);
    }

    @Override
    public void sendBatchParts(OpenBatchSessionResponse openBatchSessionResponse, List<BatchPartSendingInfo> parts) throws IOException, InterruptedException {
        if (CollectionUtils.isEmpty(parts)) {
            throw new IllegalArgumentException("No files to send.");
        }

        List<PackagePartSignatureInitResponseType> responsePartUploadRequests = openBatchSessionResponse.getPartUploadRequests();
        if (CollectionUtils.isEmpty(responsePartUploadRequests)) {
            throw new IllegalStateException("No information about parts to send.");
        }

        HttpClient client = HttpClient.newHttpClient();
        List<String> errors = new ArrayList<>();

        for (PackagePartSignatureInitResponseType responsePart : responsePartUploadRequests) {
            byte[] fileBytes = parts.stream()
                    .filter(p -> responsePart.getOrdinalNumber() == p.getOrdinalNumber())
                    .findFirst()
                    .orElseThrow()
                    .getData();

            HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(fileBytes);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(responsePart.getUrl())
                    .PUT(bodyPublisher);
            requestBuilder.header("Content-Type", "application/octet-stream");

            Map<String, String> headerEntryList = responsePart.getHeaders();
            if (headerEntryList != null) {
                headerEntryList.forEach(requestBuilder::header);
            }

            HttpResponse<String> responseResult = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            if (responseResult.statusCode() >= 400) {
                errors.add("Error sends part " + responsePart.getOrdinalNumber() + ": " +
                        responseResult.statusCode() + " " + responseResult.body());
            }
        }

        if (!errors.isEmpty()) {
            throw new IOException("Errors when sending parts:\n" + String.join("\n", errors));
        }
    }

    @Override
    public void sendBatchPartsWithStream(OpenBatchSessionResponse openBatchSessionResponse, List<BatchPartStreamSendingInfo> parts) throws IOException, InterruptedException {
        if (CollectionUtils.isEmpty(parts)) {
            throw new IllegalArgumentException("No files to send.");
        }

        List<PackagePartSignatureInitResponseType> responsePartUploadRequests = openBatchSessionResponse.getPartUploadRequests();
        if (CollectionUtils.isEmpty(responsePartUploadRequests)) {
            throw new IllegalStateException("No information about parts to send.");
        }

        HttpClient client = HttpClient.newHttpClient();
        List<String> errors = new ArrayList<>();

        for (PackagePartSignatureInitResponseType responsePart : responsePartUploadRequests) {
            InputStream dataStream = parts.stream()
                    .filter(p -> responsePart.getOrdinalNumber() == p.getOrdinalNumber())
                    .findFirst()
                    .orElseThrow()
                    .getDataStream();

            HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(dataStream.readAllBytes());

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(responsePart.getUrl())
                    .PUT(bodyPublisher);
            requestBuilder.header("Content-Type", "application/octet-stream");

            Map<String, String> headerEntryList = responsePart.getHeaders();
            if (headerEntryList != null) {
                headerEntryList.forEach(requestBuilder::header);
            }

            HttpResponse<String> responseResult = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            if (responseResult.statusCode() >= 400) {
                errors.add("Error sends part " + responsePart.getOrdinalNumber() + ": " +
                        responseResult.statusCode() + " " + responseResult.body());
            }
        }

        if (!errors.isEmpty()) {
            throw new IOException("Errors when sending parts:\n" + String.join("\n", errors));
        }
    }

    @Override
    public OpenOnlineSessionResponse openOnlineSession(OpenOnlineSessionRequest body, String authenticationToken) throws ApiException {
        return interactiveSessionApi.onlineSessionOpenWithHttpInfo(body, authenticationToken).getData();
    }

    @Override
    public void closeOnlineSession(String referenceNumber, String authenticationToken) throws ApiException {
        interactiveSessionApi.apiV2SessionsOnlineReferenceNumberClosePostWithHttpInfo(referenceNumber, authenticationToken);
    }

    @Override
    public SendInvoiceResponse onlineSessionSendInvoice(String referenceNumber, SendInvoiceRequest body, String authenticationToken) throws ApiException {
        return interactiveSessionApi.apiV2SessionsOnlineReferenceNumberInvoicesPostWithHttpInfo(referenceNumber, body, authenticationToken).getData();
    }

    @Override
    public CertificateLimitsResponse getCertificateLimits(String authenticationToken) throws ApiException {
        return certificateApi.apiV2CertificatesLimitsGetWithHttpInfo(authenticationToken).getData();
    }

    @Override
    public CertificateEnrollmentsInfoResponse getCertificateEnrollmentInfo(String authenticationToken) throws ApiException {
        return certificateApi.apiV2CertificatesEnrollmentsDataGetWithHttpInfo(authenticationToken).getData();
    }

    @Override
    public CertificateEnrollmentResponse sendCertificateEnrollment(SendCertificateEnrollmentRequest body, String authenticationToken) throws ApiException {
        return certificateApi.apiV2CertificatesEnrollmentsPostWithHttpInfo(body, authenticationToken).getData();
    }

    @Override
    public CertificateEnrollmentStatusResponse getCertificateEnrollmentStatus(String referenceNumber, String authenticationToken) throws ApiException {
        return certificateApi.apiV2CertificatesEnrollmentsReferenceNumberGetWithHttpInfo(referenceNumber, authenticationToken).getData();
    }

    @Override
    public CertificateListResponse getCertificateList(CertificateListRequest body, String authenticationToken) throws ApiException {
        return certificateApi.apiV2CertificatesRetrievePostWithHttpInfo(body, authenticationToken).getData();
    }

    @Override
    public void revokeCertificate(CertificateRevokeRequest body, String serialNumber, String authenticationToken) throws ApiException {
        certificateApi.apiV2CertificatesCertificateSerialNumberRevokePostWithHttpInfo(serialNumber, body, authenticationToken);
    }

    @Override
    public CertificateMetadataListResponse getCertificateMetadataList(QueryCertificatesRequest body,
                                                                      int pageSize, int pageOffset, String authenticationToken) throws ApiException {
        return certificateApi.apiV2CertificatesQueryPostWithHttpInfo(pageSize, pageOffset, body, authenticationToken).getData();
    }

    @Override
    public AuthenticationChallengeResponse getAuthChallenge() throws ApiException {
        return authenticationApi.apiV2AuthChallengePostWithHttpInfo().getData();
    }

    @Override
    public AuthenticationInitResponse submitAuthTokenRequest(String signedXml, boolean verifyCertificateChain) throws ApiException {
        var initResponse = authenticationApi.apiV2AuthTokenSignaturePostWithHttpInfo(signedXml, verifyCertificateChain).getData();

        if (initResponse.getAuthenticationToken() == null) {
            throw new ApiException();
        }

        return initResponse;
    }

    @Override
    public AuthenticationInitResponse authorizeByKSeFToken(AuthKsefTokenRequest body) throws ApiException {
        var initResponse = authenticationApi.apiV2AuthTokeKSeFPostWithHttpInfo(body).getData();

        if (initResponse.getAuthenticationToken() == null) {
            throw new ApiException();
        }

        return initResponse;
    }

    @Override
    public pl.akmf.ksef.sdk.client.model.session.AuthenticationOperationStatusResponse getAuthStatus(String referenceNumber, String authenticationToken) throws ApiException {

        return authenticationApi.apiV2AuthTokenTokenReferenceNumberGetWithHttpInfo(referenceNumber, authenticationToken).getData();
    }

    @Override
    public AuthenticationOperationStatusResponse redeemToken(String authenticationToken) throws ApiException {

        return authenticationApi.apiV2AuthRedeemTokenPost(authenticationToken).getData();
    }

    @Override
    public AuthenticationTokenRefreshResponse refreshAccessToken(String refreshToken) throws ApiException {
        var refreshTokenResponse = authenticationApi.apiV2AuthTokenRefreshPostWithHttpInfo(refreshToken).getData();

        if (refreshTokenResponse.getAccessToken() == null) {
            throw new ApiException();

        }

        return refreshTokenResponse;
    }

    @Override
    public void revokeAccessToken(String authenticationToken) throws ApiException {
        authenticationApi.apiV2AuthTokenDeleteWithHttpInfo(authenticationToken);
    }

    @Override
    public PermissionStatusInfo permissionOperationStatus(String referenceNumber, String authenticationToken) throws ApiException {
        return operationApi.apiV2PermissionsOperationsReferenceNumberGet(referenceNumber, authenticationToken).getData();
    }

    //----------------- Search Permission -------------------
    @Override
    public QueryPersonPermissionsResponse searchGrantedPersonPermissions(PersonPermissionsQueryRequest request,
                                                                         int pageOffset, int pageSize, String authenticationToken) throws ApiException {
        return searchPermissionApi.apiV2PermissionsQueryPersonsGrantsPost(pageOffset, pageSize, request, authenticationToken).getData();
    }

    @Override
    public QuerySubunitPermissionsResponse searchSubunitAdminPermissions(SubunitPermissionsQueryRequest request,
                                                                         int pageOffset, int pageSize, String authenticationToken) throws ApiException {
        return searchPermissionApi.apiV2PermissionsQuerySubunitsGrantsPost(pageOffset, pageSize, request, authenticationToken).getData();
    }

    @Override
    public QueryEntityRolesResponse searchEntityInvoiceRoles(int pageOffset, int pageSize, String authenticationToken) throws ApiException {
        return searchPermissionApi.apiV2PermissionsQueryEntitiesRolesGet(pageOffset, pageSize, authenticationToken).getData();
    }

    @Override
    public QuerySubordinateEntityRolesResponse searchSubordinateEntityInvoiceRoles(SubordinateEntityRolesQueryRequest request, int pageOffset, int pageSize, String authenticationToken) throws ApiException {
        return searchPermissionApi.apiV2PermissionsQuerySubordinateEntitiesRolesPost(pageOffset, pageSize, request, authenticationToken).getData();
    }

    @Override
    public QueryEntityAuthorizationPermissionsResponse searchEntityAuthorizationGrants(EntityAuthorizationPermissionsQueryRequest request, int pageOffset, int pageSize, String authenticationToken) throws ApiException {
        return searchPermissionApi.apiV2PermissionsQueryAuthorizationsGrantsPost(pageOffset, pageSize, request, authenticationToken).getData();
    }

    @Override
    public QueryEuEntityPermissionsResponse searchGrantedEuEntityPermissions(EuEntityPermissionsQueryRequest request,
                                                                             int pageOffset, int pageSize, String authenticationToken) throws ApiException {
        return searchPermissionApi.apiV2PermissionsQueryEuEntitiesGrantsPost(pageOffset, pageSize, request, authenticationToken).getData();
    }

    //------------------ END Search Permission------------------

    @Override
    public PermissionsOperationResponse grantsPermissionEUEntity(EuEntityPermissionsGrantRequest body, String authenticationToken) throws ApiException {
        return euSubjectAdministratorApi.apiV2PermissionsEuEntitiesAdministrationGrantsPost(body, authenticationToken).getData();
    }

    @Override
    public PermissionsOperationResponse grantsPermissionEUEntityRepresentative(GrantEUEntityRepresentativePermissionsRequest body, String authenticationToken) throws ApiException {
        return euSubjectRepresentationApi.apiV2PermissionsEuEntitiesGrantsPost(body, authenticationToken).getData();
    }

    @Override
    public byte[] getInvoice(String ksefReferenceNumber, String authenticationToken) throws ApiException {
        return downloadInvoiceApi.apiV2InvoicesKsefKsefReferenceNumberGet(ksefReferenceNumber, authenticationToken).getData();
    }

    @Override
    public byte[] getInvoice(DownloadInvoiceRequest request, String authenticationToken) throws ApiException {
        return downloadInvoiceApi.apiV2InvoicesDownloadPost(request, authenticationToken).getData();
    }

    @Override
    public QueryInvoiceMetadataResponse queryInvoiceMetadata(Integer pageOffset, Integer pageSize,
                                                             InvoiceMetadataQueryRequest request, String authenticationToken) throws ApiException {
        return downloadInvoiceApi.apiV2InvoicesQueryMetadataPost(pageOffset, pageSize, request, authenticationToken).getData();
    }

    @Override
    public InitAsyncInvoicesQueryResponse initAsyncQueryInvoice(InvoicesAsyncQueryRequest request, String authenticationToken) throws ApiException {
        return downloadInvoiceApi.apiV2InvoicesAsyncQueryPost(request, authenticationToken).getData();
    }

    @Override
    public AsyncInvoicesQueryStatus checkStatusAsyncQueryInvoice(String operationReferenceNumber, String authenticationToken) throws ApiException {
        return downloadInvoiceApi.apiV2InvoicesAsyncQueryOperationReferenceNumberGet(operationReferenceNumber, authenticationToken).getData();
    }

    @Override
    public PermissionsOperationResponse grantsPermissionEntity(GrantEntityPermissionsRequest body, String authenticationToken) throws ApiException {
        return subjectForInvoiceApi.apiV2PermissionsEntitiesGrantsPost(body, authenticationToken).getData();
    }

    //------------------ Start Session------------------

    @Override
    public SessionStatusResponse getSessionStatus(String referenceNumber, String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberGet(referenceNumber, authenticationToken).getData();
    }

    @Override
    public SessionInvoiceStatusResponse getSessionInvoiceStatus(String referenceNumber, String invoiceReferenceNumber
            , String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberInvoicesInvoiceReferenceNumberGet(referenceNumber, invoiceReferenceNumber, authenticationToken).getData();
    }

    @Override
    public byte[] getSessionInvoiceUpoByReferenceNumber(String referenceNumber, String invoiceReferenceNumber, String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberInvoicesInvoiceReferenceNumberUpoGet(referenceNumber, invoiceReferenceNumber, authenticationToken).getData();
    }

    @Override
    public byte[] getSessionInvoiceUpoByKsefNumber(String referenceNumber, String ksefNumber, String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberInvoicesKsefKsefNumberUpoGet(referenceNumber, ksefNumber, authenticationToken).getData();
    }

    @Override
    public byte[] getSessionUpo(String referenceNumber, String upoReferenceNumber, String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberUpoUpoReferenceNumberGet(referenceNumber, upoReferenceNumber, authenticationToken).getData();
    }

    @Override
    public SessionInvoicesResponse getSessionInvoices(String referenceNumber, Integer pageSize, Integer pageOffset, String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberInvoicesGet(referenceNumber, pageOffset, pageSize, authenticationToken).getData();
    }

    @Override
    public SessionInvoicesResponse getSessionFailedInvoices(String referenceNumber, String continuationToken,
                                                            Integer pageSize, String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsReferenceNumberInvoicesFailedGet(referenceNumber, continuationToken, pageSize, authenticationToken).getData();
    }

    @Override
    public SessionsQueryResponse getSessions(SessionsQueryRequest request, Integer pageSize, String continuationToken
            , String authenticationToken) throws ApiException {
        return sendStatusAndUpoApi.apiV2SessionsGet(request, pageSize, continuationToken, authenticationToken).getData();
    }

    @Override
    public AuthenticationListResponse getActiveSessions(Integer pageSize, String continuationToken, String authenticationToken) throws ApiException {
        return activeSessionApi.apiV2AuthSessionsGet(pageSize, continuationToken, authenticationToken).getData();
    }

    @Override
    public void revokeCurrentSession(String authenticationToken) throws ApiException {
        activeSessionApi.apiV2AuthSessionsCurrentDelete(authenticationToken);
    }

    @Override
    public void revokeSession(String referenceNumber, String authenticationToken) throws ApiException {
        activeSessionApi.apiV2AuthSessionsReferenceNumberDelete(referenceNumber, authenticationToken);
    }

    //------------------ END Session------------------

    //------------------ START Person permissions ------------------

    @Override
    public PermissionsOperationResponse grantsPermissionPerson(GrantPersonPermissionsRequest request, String authenticationToken) throws ApiException {
        return naturalPersonKseFApi.apiV2PermissionsPersonsGrantsPost(request, authenticationToken).getData();
    }

    //------------------ END Person permissions ------------------

    @Override
    public PermissionsOperationResponse grantsPermissionSubUnit(SubunitPermissionsGrantRequest body, String authenticationToken) throws ApiException {
        return subUnitSubjectAdministratorApi.apiV2PermissionsSubunitsGrantsPost(body, authenticationToken).getData();
    }

    @Override
    public PermissionsOperationResponse revokeCommonPermission(String permissionId, String authenticationToken) throws ApiException {
        return operationApi.apiV2PermissionsCommonGrantsPermissionIdDelete(permissionId, authenticationToken).getData();
    }

    @Override
    public PermissionsOperationResponse revokeAuthorizationsPermission(String permissionId, String authenticationToken) throws ApiException {
        return operationApi.apiV2PermissionsAuthorizationsGrantsPermissionIdDelete(permissionId, authenticationToken).getData();
    }

    //------------------ START Tokens ------------------

    @Override
    public GenerateTokenResponse generateKsefToken(GenerateTokenRequest tokenRequest, String authenticationToken) throws ApiException {
        return tokensApi.apiV2TokensPost(tokenRequest, authenticationToken).getData();
    }

    @Override
    public QueryTokensResponse queryKsefTokens(List<AuthenticationTokenStatus> statuses, String continuationToken,
                                               Integer pageSize, String authenticationToken) throws ApiException {

        return tokensApi.apiV2TokensGet(statuses, continuationToken, pageSize, authenticationToken).getData();
    }

    @Override
    public AuthenticationToken getKsefToken(String referenceNumber, String authenticationToken) throws ApiException {

        return tokensApi.apiV2TokensReferenceNumberGet(referenceNumber, authenticationToken).getData();
    }

    @Override
    public void revokeKsefToken(String referenceNumber, String authenticationToken) throws ApiException {

        tokensApi.apiV2TokensReferenceNumberDelete(referenceNumber, authenticationToken);
    }

    @Override
    public List<PublicKeyCertificate> retrievePublicKeyCertificate() throws ApiException {

        return publicKeyCertificateApi.apiV2SecurityPublicKeyCertificatesGet().getData();
    }
}
