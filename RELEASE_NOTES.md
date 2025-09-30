# Changelog zmian – `## 2.0.0 (2025-07-17)`

> Info: 🔧 zmienione • ➕ dodane • ➖ usunięte

---

## 1. ksef-client

### 1.1 api.services

- **DefaultCertificateGenerator.java**: 🔧 metodę `generateSelfSignedCertificate` zastąpiono
  dwoma `generateSelfSignedCertificateRsa` i `generateSelfSignedCertificateEcdsa`
- **DefaultCryptographyService.java**: 🔧 wprowadzono zmiany w pobieraniu kluczy w konstruktorze; dodano dwie pomocnocze metody `parsePublicKeyFromPem`
  i `parsePrivateKeyFromPem`; dodano `encryptWithECDsaUsingPublicKey(byte[] content)` — domyślna metoda szyfrowania
  ECIES (ECDH + AES-GCM) na krzywej P-256.
- **DefaultKsefClient.java**: 🔧 zmieniono nazwę metody z `batchOpen` na `openBatchSession`; do metody `sendBatchParts`
  dodano nagłowek `Content-Type`; zmieniono sygnaturę metody `submitAuthTokenRequest` - dodano
  parametr `boolean verifyCertificateChain` i zmieniono `body` na `signedXml`; zmieniono nazwę metody `operations`
  na `permissionOperationStatus`; zmieniono nazwę metody `getInvoiceMetadane` na `queryInvoices`; dodano
  metody `getSessions`, `getActiveSessions`, `revokeCurrentSession`, `revokeSession`, `retrievePublicKeyCertificate`;
  dodano parametr `continuationToken` do
  metody `AuthenticationListResponse getActiveSessions(Integer pageSize, String continuationToken)`  
  dodano parametr `continuationToken` do
  metody `SessionInvoicesResponse getSessionFailedInvoices(String referenceNumber, String continuationToken, Integer pageSize)`
- **DefaultQrCodeService.java**: ➕ nowa usługa do generowania QrCodes
- **DefaultVerificationLinkService.java**: ➕ nowa usługa generowania linków do weryfikacji faktury

### 1.2 api.client.interfaces

- **CertificateGenerator.java**: 🔧 zmiany zgodnie z implementacja w `DefaultCertificateGenerator.java`
- **CryptographyService.java**: 🔧 zmiany zgodnie z implementacja w `DefaultCryptographyService.java`
- **KSeFClient.java**: 🔧 dodanie opisów do metod; zmiany zgodnie z implementacja w `DefaultKsefClient.java`
- **QrCodeService.java**: ➕ nowy interfejs do generowania QRcodes zgodnie z implementacją w `DefaultQrCodeService.java`
- **VerificationLinkService.java**: ➕ nowy interfejs do tworzenia linków weryfikacyjnych do faktury zgodnie z
  implementacją w `DefaultVerificationLinkService.java`

### 1.3 api.client.model

- **AuthenticationChallengeResponse.java**: 🔧 zmiany typu pola `timestamp` z `OffsetDateTime` na `Instant`
- **EntityAuthorizationGrant.java**: 🔧 dodanie pola `String id` i zmiana typu pola `authorizationScope` z `String`
  na `EntityAuthorizationScope`
- **EuEntityPermission.java**: 🔧 dodanie pola `String id`, zmiana pola `permissionType` na `permissionScope`
- **PersonPermission.java**: 🔧 dodanie pola `String id`
- **SubunitPermission.java**: 🔧 dodanie pola `String id` i usunięcie pola `canDelegate`
- **QrCodeResult.java**: ➕ nowa klasa
- **ContextIdentifierType.java**: ➕ nowa klasa w pakiecie qrcode
- **AuthenticationListResponse.java**: ➕ nowa klasa
- **AuthenticationMethod.java**: ➕ nowa klasa
- **AuthenticationOperationStatusResponse.java**: ➕ nowa klasa
- **CommonSessionStatus.java**: ➕ nowa klasa
- **SessionInvoice.java**: 🔧 dodanie pola `String invoiceFileName`
- **SessionsQueryRequest.java**: ➕ nowa klasa
- **SessionsQueryResponse.java**: ➕ nowa klasa
- **SessionsQueryResponseItem.java**: ➕ nowa klasa
- **SessionType.java**: ➕ nowa klasa
- **ApiException.java**: 🔧 dodanie metody `getApiException`
- **EncryptionMethod.java**: ➕ nowy enum
- **PersonPermissionQueryType.java**: ➕ nowy enum
- **SystemCode.java**: ➕ nowy enum
- **EntityAuthorizationScope.java**: ➕ nowy enum
- **CertificateInfo.java**: 🔧 usunięcie pola `thumbprint`
- **PersonPermissionsQueryRequest**: 🔧 dodanie pola `PersonPermissionQueryType queryType`

### 1.4 api.builders

- **PersonPermissionsQueryRequestBuilder.java**: 🔧 dodanie pola `PersonPermissionQueryType queryType`
- **OpenBatchSessionRequestBuilder.java**: 🔧 z `withBatchFile` usunięcie parametru `boolean offlineMode` i wydzielenie
  do osobnej metody `withOfflineMode(boolean offlineMode)` oraz zmiana typu przyjmowanego argumentu
  metody `withFormCode` z `String` na `SystemCode`

---

## 2. demo-web-app

### 2.1 integrationTest

- Wspólne: 🔧 `Thread.Sleep` → `org.awaitility.Awaitility.await`;
- **EntityPermissionIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym
- **EuEntityPermissionIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym
- **EuEntityRepresentativeE2EPermissionTest.java**: 🔧 zmiany w scenariuszu testowym
- **IndirectPermissionIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym
- **PersonPermissionIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym
- **ProxyPermissionIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym
- **SubUnitPermissionIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym
- **OnlineSessionV2E2EIntegrationTest.java**: 🔧 zmiany w scenariuszu testowym oraz dodano testy end-to-end dla faktury w
  wersji 3

- ➖ usunięto `publicKey.pem` z resources
- ➕ dodano klasę testową `SessionIntegrationTest.java`
- ➕ dodano plik `invoice-template_v3.xml` zawierający przykładową fakturę w wersji 3
-
- **AuthorizationIntegrationTest.java**: dodano testy end-to-end dla tokenu w wariantach `ECDsa` i `Rsa`.

### 2.2 api

- ➕ dodano usługi w `ActiveSessionController.java` które wywołują bezpośrednio klienta ksef.
- **AuthController.java**: ➕ `POST auth-with-ksef-certificate`
- **QrCodeController.java**: ➕`POST /qr/certificate` ➕`GET /qr/invoice/ksef` ➕`GET /qr/invoice/offline`
- ➖ usunięto `publicKey.pem` z resources

### 2.3 test - api.services

- ➕ dodano `QrCodeTests.java`
- ➕ dodano `VerificationLinkServiceTests.java`

---

## 3. .http

- 🔧 do `auth.http` dodano wywołanie usługi `POST auth-with-ksef-certificate`
- ➕ w `qr-code.http` dodano wywołanie usług ➕`POST /qr/certificate` ➕`GET /qr/invoice/ksef` ➕`GET /qr/invoice/offline`
- 🔧 w `searchPermissions.http` zaktualizowano payload
- 🔧 w `session.http` dodano wywołanie usług ➕ `POST /session/query/` ➕ `POST /session/active/{pageSize}`
  ➕ `DELETE /session/revoke/current` ➕ `DELETE /session/revoke/{sessionReferenceNumber}`
- 🔧 w `subunit-subject-permission.http` zaktualizowano payload
- ➖ usunięto `scenario/BatchSession_E2E_WorksCorrectly.http`

---

## 4. Podsumowanie

| Typ zmiany   | Liczba plików |
|--------------|---------------|
| ➕ dodane     | 30            |
| 🔧 zmienione | 80            |
| ➖ usunięte   | 3             |

---------------------------------------------------------------------------------------------------------------------------------


# Changelog zmian – `## 2.0.1 (2025-09-02)`

---
## 
-- **LICENSE.md**: ➕ dodano licencję

## 1. ksef-client
- **PublicKeyEnvironmentApi**: ➖ usunięto klasę

### 1.1 api.services

- **DefaultCryptographyService.java**: 🔧 wprowadzono zmiany w pobieraniu kluczy w konstruktorze; wydzielono/dodano metody do szyfrowania tokenu ksef 
- **DefaultKsefClient.java**: 🔧 zmieniono nazwę metody z `queryInvoices` na `queryInvoiceMetadata`; usunięcie metody `byte[] getPublicKey()`; uwspólnienie zwracanych typów - zmiana z `String` na `byte[]` dla `byte[] getInvoice(String ksefReferenceNumber)` i `byte[] getInvoice(DownloadInvoiceRequest request)` 

### 1.2 api.client.interfaces
- **KSeFClient.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **KsefEnviroments.java**: ➕ usunięto klasę
- **CryptographyService.java**: 🔧 zmiany zgodnie z implementacją w `DefaultCryptographyService.java`

### 1.3 api.client.model

- **InvoiceMetadata.java**: 🔧 dodanie pola `issueDate`, zmiana nazwwy pola z `invoiceDate` na `invoicingDate` i zmiana typu pola `currency` z `CurrencyCode`
  na `String`
- **InvoiceMetadataInvoiceType.java**: 🔧 Zmiana wartości enumów
- **InvoiceMetadataQueryRequest.java**: 🔧 zmiana nazwy z `InvoicesQueryRequest`
- **InvoiceQueryAmount.java**: 🔧 zmiana nazwy z `InvoicesAsynqQueryRequestAmount `
- **InvoiceQueryBuyer.java**: 🔧 zmiana nazwy z `InvoicesAsynqQueryRequestBuyer `
- **InvoiceQuerySeller.java**: 🔧 zmiana nazwy z `InvoicesAsynqQueryRequestSeller`
- **QueryInvoiceMetadataReponse.java**: 🔧 zmiana nazwy z `QueryInvoicesReponse`
- **InvoiceQueryDateType.java**: 🔧 Zmiana enuma z `ACQUSITION` na  `ACQUISITION`
- **BaseStatusInfo**: ➖ usunięto nieużywaną klasę
- **AuthStatus **: ➖ usunięto nieużywaną klasę
- **InvoiceQuerySystemCode.java **: ➕ dodano klasę według kontraktu
- **InvoiceMetadataQueryRequest.java**: 🔧 dodanie pól `systemCode` i `hasAttachment`
- **InvoicesAsynqQueryRequest.java**: 🔧 dodanie pola `hasAttachment` i zmiana `formCode` na `systemCode`
- **InvoiceQueryDocumentStructure.java**: 🔧 zmiana nazwy z `InvoiceQuerySystemCode` oraz dodanie wartości `FA1`
- **InvoiceMetadata.java**: 🔧 dodanie pola `hasAttachment`
- **InvoiceMetadataQueryRequest.java**: 🔧 zmiana nazwy pola z `systemCode` na `documentStructure`
- **InvoicesAsynqQueryRequest.java**: 🔧 zmiana nazwy pola z `systemCode` na `documentStructure`
- **OpenOnlineSessionResponse.java**: 🔧 dodanie pola `validUntil`
- **SessionsQueryResponseItem.java**: 🔧 dodanie pola `validUntil`
- **SessionStatusResponse.java**: 🔧 dodanie pola `validUntil`
- **Headers.java**: ➕ dodanie klasy zawierającej używane nagłówki
- **PublicKeyCertificate.java**: ➕ zmiana pola z `certificatePem` na `certificate`
- **Parameter.java**: ➕ dodanie klasy zawierającej używane w kliencie parametry zapytań
- **ApiClient.java**: ➖ usunięto klasę
- **HttpApiClient.java**: ➕ dodanie klasy zawierającej konfigurowalnego klienta http
- **CertificateType.java**: ➕ dodanie enuma
- **CertificateInfo.java**: ➕ dodanie pola `CertificateType type`
- **CertificateResponse.java**: ➕ dodanie pola `CertificateType certificateType`
- **SendCertificateEnrollmentRequest.java**: ➕ dodanie pola `CertificateType certificateType`
- **QueryInvoiceMetadataResponse.java**: ➕ dodanie pola `Boolean hasMore`
- **SessionInvoicesResponse.java**: ➕ dodanie pola `Boolean hasMore`

### 1.4 api.builders
- **InvoiceMetadataQueryRequestBuilder.java**: 🔧 zmiana nazwy z `InvoicesQueryRequestBuilder`
- **InvoiceMetadataQueryRequestBuilder.java**: 🔧 usunięcie pola `isHidden` i dodanie `hasAttachment`
- **InvoiceMetadataQueryRequestBuilder.java**: 🔧 zmiana pola z `formCode` na `systemCode`
- **InvoicesAsynqQueryRequestBuilder.java**: 🔧 zmiana nazwy pola z `systemCode` na `documentStructure`
- **AuthTokenRequestBuilder.java**: 🔧zmiana typu pola `context` z `ContextIdentifier` na `TContextIdentifier`
- **UrlQueryParamsBuilder.java**: ➕ dodanie klasy obsługującej tworzenie zapytań
- **SendCertificateEnrollmentRequestBuilder.java**: ➕ dodanie pola `certificateType`
- 
### 1.5 api
- **BaseApi.java**: ➖ usunięto klasę
- **ActiveSessionApi.**: 🔧 refactor klasy umożliwiający wielowątkowość
- **AuthenticationApi.java**: 🔧 refactor klasy umożliwiający obsługę wielu wątków
- **BatchInvoiceApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **CertificateApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **DownloadInvoiceApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **EuSubjectAdministratorApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **EuSubjectRepresentationApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **ForAuthorizedSubjectApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **EGrantDirectlyApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **NaturalPersonKseFApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **OperationApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **PublicKeyCertificateApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **SearchPermissionApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **SendStatusAndUpoApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **SubjectForInvoiceApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **SubUnitSubjectAdministratorApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **TokensApi.java**: 🔧 refactor klasy umożliwiający wielowątkowość

### 1.6  resources 
- **AuthTokenRequest.xsd**: 🔧 aktualizacja pliku AuthTokenRequest.xsd

## 2. demo-web-app

### 2.1 integrationTest

- **OnlineSessionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego oraz refactor klasy zgodnie z nową konfiguracją klienta
- **QueryInvoiceIntegrationTest.java**: dodano nową klasę testową
- **AuthorizationIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **BaseIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **BatchIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **CertificateIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **EntityPermissionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **EuEntityPermissionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **EuEntityRepresentativeE2EPermissionTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **IndirectPermissionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **OnlineSessionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **PersonPermissionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **ProxyPermissionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **QueryInvoiceIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **SessionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **SubUnitPermissionIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta
- **TokensIntegrationTest.java**: 🔧 refactor klasy zgodnie z nową konfiguracją klienta

### 2.2 api
- **TokensController.java**: 🔧 refactor refactor zgodnie z nową implementacja klienta
- **SubUnitPermissionsController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **SessionController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **SearchPermissionTestEndpoint.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **ProxyPermissionsEntityController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **PersonPermissionController.java**: 🔧 refactor klasy umożliwiający wielowątkowość
- **OperationStatusController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **OnlineSessionController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **InvoicesController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **IndirectPermissionsEntityController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **EuEntityRepresentativePermissionsController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **EuEntityPermissionsController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **EntityPermissionsController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **CertificateController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **BatchSessionController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **AuthController.java**: 🔧 refactor zgodnie z nową implementacja klienta
- **KsefClientConfig.java**: 🔧 zmiana konfiguracji zgodnie z nową implementacja klienta

### 2.3 test - api.services


---

## 3. .http


## 4. Podsumowanie

| Typ zmiany  | Liczba plików |
|-------------|---------------|
| ➕ dodane    | 22           |
| 🔧 zmienione | 112          |
| ➖ usunięte  | 12           |

---
# Changelog zmian – `## 3.0.0 (2025-09-26)`

## 1. ksef-client

### 1.1 api.builders
- **AuthTokenRequestBuilder.java**: 🔧 usunięcie nadmiarowego sprawdzania 
- **CertificateBuilders.java**: 🔧 refaktory klasy 
- **RevokeCertificateRequestBuilder.java**: 🔧 zmiana nazwy na `CertificateRevokeRequestBuilder.java` i zmiana var na konkretny typ (strong-type)
- **SendCertificateEnrollmentRequestBuilder.java**: 🔧 dodanie `CertificateType certificateType`
- **GrantEUEntityPermissionsRequestBuilder.java**: 🔧 dodanie `String subjectName`
- **GrantIndirectEntityPermissionsRequestBuilder.java**: 🔧 zmiana typu `IndirectPermissionsGrantRequest` na `GrantIndirectEntityPermissionsRequest`
- **GrantProxyEntityPermissionsRequestBuilder.java**: 🔧 zmiana nazwy na `GrantAuthorizationPermissionsRequestBuilder.java`, zmiana typu `GrantProxyEntityPermissionsRequest` na `GrantAuthorizationPermissionsRequest`
- **SendInvoiceRequestBuilder.java**: 🔧 zmiana nazwy na `SendInvoiceOnlineSessionRequestBuilder.java`, zmiana typu `SendInvoiceRequest` na `SendInvoiceOnlineSessionRequest`
- **GenerateTokenRequestBuilder.java**: 🔧 zmiana nazwy na `KsefTokenRequestBuilder.java`, zmiana typu `GenerateTokenRequest` na `KsefTokenRequest`
- **InvoicesAsyncQueryRequestBuilder.java**: 🔧 zmiana nazwy na `InvoicesAsyncQueryFiltersBuilder.java`, dodanie pola `Boolean hasAttachment`, zmiana `InvoiceQuerySchemaType schemaType` na `InvoiceMetadataSchema formType` 

### 1.2 api.services
- **DefaultCertificateGenerator.java**: 🔧 zmiana nazwy na `DefaultCertificateService`, dodanie implementacji metod `String getSha256Fingerprint(X509Certificate certificate)`, `SelfSignedCertificate getPersonalCertificate(String givenName, String surname, String serialNumberPrefix, String serialNumber, String commonName)`, `SelfSignedCertificate getCompanySeal(String organizationName, String organizationIdentifier, String commonName)`
- **DefaultCryptographyService.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, dodanie metod `CsrResult generateCsrWithEcdsa(CertificateEnrollmentsInfoResponse certificateInfo)`
- **DefaultQrCodeService.java**: 🔧 dodanie parametru `String fontName` do metody `addLabelToQrCode`, obsługa wyjątków
- **DefaultVerificationLinkService.java**: 🔧 obsługa wyjątków
- **DefaultKsefClient.java**: 🔧 przeniesienie do pakietu wyżej

### 1.3 api
- **DefaultKsefClient.java**: 🔧 przeniesienie do głownego pakietu api, refaktoryzacja klienta, aktualizacja typów zgodnie ze zmianami w modelu api
- **HttpStatus.java**: ➕ dodano klase zawierającą używane statusy HTTP
- **HttpUtils.java**: ➕ dodano klase pomocniczą
- **KsefApiProperties.java**: ➕ dodano klase z konfiguracją
- **Url.java.**: 🔧 zmiana adresu usługi z `INVOICE_QUERY_STATUS("/api/v2/invoices/async-query/{operationReferenceNumber}"` na `INVOICE_EXPORT_STATUS("/api/v2/invoices/exports/{operationReferenceNumber}"`
- **ActiveSessionApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **AuthenticationApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **BatchInvoiceApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **CertificateApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **DownloadInvoiceApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **EuSubjectAdministratorApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **EuSubjectRepresentationApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **ForAuthorizedSubjectApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **GrantDirectlyApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **InteractiveSessionApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **NaturalPersonKseFApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **OperationApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **PublicKeyCertificateApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **SearchPermissionApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **SendStatusAndUpoApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **SubjectForInvoiceApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **SubUnitSubjectAdministratorApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **TokensApi.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`
- **UrlQueryParamsBuilder.java**: ➖ usunięcie klasy, przeniesienie metod do `DefaultKsefClient.java`

### 1.4 client.interfaces
- **CertificateGenerator.java**: 🔧 zmiana nazwy na `CertificateService`, dodanie metod `String getSha256Fingerprint(X509Certificate certificate)`, `SelfSignedCertificate getPersonalCertificate(String givenName, String surname, String serialNumberPrefix, String serialNumber, String commonName)`, `SelfSignedCertificate getCompanySeal(String organizationName, String organizationIdentifier, String commonName)`
- **CryptographyService.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, dodanie metod `CsrResult generateCsrWithEcdsa(CertificateEnrollmentsInfoResponse certificateInfo)`
- **QrCodeService.java**: 🔧 dodanie parametru `String fontName` do metody `addLabelToQrCode`, obsługa wyjątków
- **VerificationLinkService.java**: 🔧 obsługa wyjątków
- **KSeFClient.java**: 🔧 refaktoryzacja nazw parametrów, aktualizacja typów zgodnie ze zmianami w modelu api

### 1.5 client.model
- **auth.AuthenticationOperationStatusResponse.java**: 🔧 zmieniono nazwę klasy na `AuthOperationStatusResponse .java`
- **AuthStatus.java**: ➕ dodano klasę
- **GenerateTokenRequest.java**: 🔧 zmieniono nazwę klasy na `KsefTokenRequest.java`
- **AuthenticationInitResponse.java**: 🔧 zmieniono nazwę klasy na `SignatureResponse.java`
- **CertificateInfo.java**: 🔧 dodano parametr `CertificateType type`
- **CertificateResponse.java**: 🔧 dodano parametr `CertificateType type`
- **CertificateType.java**: ➕ dodano enuma
- **SendCertificateEnrollmentRequest.java**: 🔧 dodano parametr `CertificateType type`
- **AsyncInvoicesQueryStatus.java**: ➖ usunięto klasę
- **AuthorizedSubject.java**: ➕ dodano klasę
- **BuyerIdentifierType.java**: ➖ usunięto klasę
- **DownloadInvoiceBuyer.java**: 🔧 zmieniono typ parametru z `BuyerIdentifierType identifierType` na `IdentifierType identifierType`
- **IdentifierType.java**: ➕ dodano klasę
- **InvoiceMetadataQueryRequest.java**: 🔧 zmieniono nazwę klasy na `InvoiceExportFilters.java`
- **InvoiceExportPackage.java**: ➕ dodano klasę
- **InvoiceExportRequest.java**: ➕ dodano klasę
- **InvoiceExportStatus.java**: ➕ dodano klasę
- **InvoiceMetadata.java**: ➖ usunięto klasę
- **InvoiceMetadataBuyer.java**: 🔧 zmieniono `BuyerIdentifierType identifierType` na `IdentifierType identifierType`
- **InvoiceMetadataSeller .java**: 🔧 dodano pole `String nip`
- **InvoicePackagePart .java**: 🔧 dodano nowe pola
- **InvoiceQueryBuyer.java**: 🔧 zmieniono `BuyerIdentifierType identifierType` na `IdentifierType identifierType`
- **InvoicesAsyncQueryRequest.java**: 🔧 zmieniono nazwę klasy na `InvoiceSummary.java`, dodano dodatkowe pola
- **QueryInvoiceMetadataResponse.java**: 🔧 dodano pola klasy `Boolean hasMore; List<InvoiceSummary> invoices` usunięto `List<InvoiceMetadata> invoices`
- **ThirdSubject.java**: ➕ dodano klasę
- **EuEntityPermissionsGrantRequest.java**: 🔧 dodano pole `String subjectName`
- **IndirectPermissionsGrantRequest.java**: 🔧 zmieniono nazwę klasy na `GrantIndirectEntityPermissionsRequest.java`
- **GrantProxyEntityPermissionsRequest.java**: 🔧 zmieniono nazwę klasy na `GrantAuthorizationPermissionsRequest.java`
- **PermissionsOperationResponse.java**: 🔧 zmieniono nazwę klasy na `OperationResponse.java`
- **SendInvoiceRequest.java**: 🔧 zmieniono nazwę klasy na `SendInvoiceOnlineSessionRequest.java`
- **session.AuthenticationOperationStatusResponse.java**: 🔧 zmieniono nazwę klasy na `AuthenticationListItem.java`
- **AuthenticationListResponse.java**: 🔧 zmieniono typ pola `List<AuthenticationOperationStatusResponse> items` na `List<AuthenticationListItem> items`
- **SessionInvoicesResponse.java**: 🔧 dodano pole `Boolean hasMore`
- **SessionInvoiceStatusResponse.java**: 🔧 dodano pola `OffsetDateTime permanentStorageDate; String upoDownloadUrl;`

### 1.6 client
- **Headers.java**: 🔧 dodano stałe dla nagłówków
- **HttpApiClient.java**: ➖ usunięto klasę

### 1.7 system
- **KSeFNumberValidator.java**: ➖ usunięto klasę


## 2. demo-web-app

### 2.1 integrationTest
- **BaseIntegrationTest.java**: 🔧 dodanie przeciążonej metody `authWithCustomNip` w której subject przekazujemy w formie certyfikatów
- **AuthorizationIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego
- **BatchIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **CertificateIntegrationTest.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **EntityPermissionIntegrationTest.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **EuEntityPermissionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **EuEntityRepresentativeE2EPermissionTest.java**: 🔧 rename na `EuEntityRepresentativePermissionIntegrationTest.java`, aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **IndirectPermissionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **TokensIntegrationTest.java**: 🔧 rename na `KsefTokenIntegrationTest.java`, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **OnlineSessionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **PersonPermissionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **ProxyPermissionIntegrationTest.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **QrCodeOfflineIntegrationTest.java**: ➕ dodano klasę zawierającą scenariusze testowe z kodami qr do fv offline
- **QrCodeOnlineIntegrationTest.java**: ➕ dodano klasę zawierającą scenariusze testowe z kodami qr do fv online
- **QueryInvoiceIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **SessionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **SubUnitPermissionIntegrationTest.java**: 🔧 aktualizacja scenariusza testowego, aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- 
### 2.1.1 integrationTest.resources
- **invoice-template_v3.xml**: 🔧 - dodanie placeholdera do pola p_1 na fakturze
- **invoice-template.xml**: 🔧 - dodanie placeholdera do pola p_1 na fakturze

### 2.2 api
- **ActiveSessionController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **AuthController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych, dodanie usługi `GET prepare-sample-cert-auth-request` przygotowującą testowe dane do usługi `POST auth-with-ksef-certificate`
- **BatchSessionController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **CertificateController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **EntityPermissionsController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **EuEntityPermissionsController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **IndirectPermissionsEntityController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **InvoicesController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych, zmiany typów zmiennych wynikajacych z aktualizacji modelu api
- **OnlineSessionController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **OperationStatusController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **PersonPermissionController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **ProxyPermissionsEntityController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **QrCodeController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **SearchPermissionTestEndpoint.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **SessionController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych, użycie `response.getContinuationToken()` przy pobieraniu fv/statusów w sesji
- **SubUnitPermissionsController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych
- **TokensController.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF, zmiany w nazwenictwie zmiennych, użycie `response.getContinuationToken()` przy pobieraniu tokenów
- **StatusWaitingException.java**: ➕ dodano klasę
- **ExampleApiProperties.java**: ➕ dodano klasę
- **HttpClientBuilder.java**: ➕ dodano klasę
- **IdentifierGeneratorUtils.java**: ➕ dodano testową klasę do generowania identyfikatorów (np. nip, pesel)
- **KsefClientConfig.java**: 🔧 zmiana konfiguracji zgodnie z nową implementacja klienta

### 2.2.1 resources
- **invoice-template.xml**: 🔧 - dodanie placeholdera do pola p_1 na fakturze

### 2.3 test - api.services
- **QrCodeTests.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF
- **VerificationLinkServiceTests.java**: 🔧 aktualizacja związana z refaktorem serwisów i kliena KSeF


## 3. .http
- 🔧 do `auth.http` dodano wywołanie usługi `GET prepare-sample-cert-auth-request` przygotowującą testowe dane do usługi `POST auth-with-ksef-certificate`

## 4. Podsumowanie

| Typ zmiany  | Liczba plików |
|-------------|---------------|
| ➕ dodane    | 25            |
| 🔧 zmienione | 75            |
| ➖ usunięte  | 31            |

---