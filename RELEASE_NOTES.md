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
