[![CI](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/ci.yml/badge.svg)](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/ci.yml) [![Maven Central](https://img.shields.io/maven-central/v/io.alapierre.ksef-sdk/ksef-client)](https://central.sonatype.com/artifact/io.alapierre.ksef-sdk/ksef-client)


# KSeF 2.0 SDK (Community Fork)

An independently maintained community fork of the official KSeF 2.0 SDK, focused on reliable integrations, API coverage, and backward compatibility.

For setup and maintenance, see [using the library](maven-package.md),
[building and testing](#building-and-testing), [releases](#releases),
[cryptographic limitations](#cryptographic-limitations-of-the-ksef-protocol),
and [release notes](RELEASE_NOTES.md).

## Independent development and upstream changes

We have decided to develop this fork independently and stop merging upstream releases.
We consider some recent upstream changes—particularly the circuit breaker integration
and changes to default behavior—too risky to adopt in existing integrations without
individual review. These changes can affect how a working integration sends requests
and handles failures, even when its own code has not changed.

We will continue to review upstream improvements and port selected fixes and API features
when they benefit this fork. Each change will be assessed for compatibility and tested
against the fork's existing functionality, including its batch processing helpers.
Backward compatibility and predictable behavior take priority over matching upstream's
implementation. Changes that affect existing behavior will be documented in the
[release notes](RELEASE_NOTES.md).

## 🚀 Motivation

This repository is a community-driven fork of the official KSeF 2.0 SDK
published by the Polish Ministry of Finance and Aplikacje Krytyczne team.

Unfortunately, as of today, the official SDK suffers from several problems that make it difficult or even impossible to use in production environments:

- ❌ Outdated API definitions – the SDK does not support the latest version of the KSeF 2.0 API.
- ⚠️ Known security vulnerabilities – dependencies include libraries with CVEs and no updates are provided.
- 🧩 Incomplete functionality – some API operations are missing or implemented incorrectly.
- 🐛 Bugs and inconsistencies – issues have been reported but remain unresolved for long periods.
- 🕒 Slow response times – maintainers rarely provide feedback or release updates.

This situation has caused understandable frustration among developers integrating with KSeF — especially as the official SDK is the only one referenced in government documentation.

## 🎯 Goal of This Project

The goal of this fork is to provide a reliable Java client for KSeF, maintained independently with the needs of existing integrations in mind.

Specifically, this fork:

- Fixes blocking bugs and inconsistencies in the SDK.
- Updates dependencies to remove known security vulnerabilities.
- Aligns the SDK with the latest version of the KSeF 2.0 API.
- Publishes the library to a public Maven repository for easier integration in Java projects.

Development will follow the KSeF API contract and the needs of this fork’s users, with upstream changes evaluated individually.

## 🧱 Differences from the Official SDK


| Area                  | Official SDK             | This Fork                                   |
|-----------------------|--------------------------|---------------------------------------------|
| Dependency management | it varies                | Updated and secure dependencies             |
| Maven publishing      | Github Packages          | Published to public Maven repository        |
| Maintenance           | Irregular / no updates   | Community maintained                        |
| Build system          | Gradle at the time of the fork | Maven Wrapper, generated sources under `target` |
| Circuit breaker       | Enabled by default       | Not enabled implicitly                      |
| Batch compression     | TAR.GZ-oriented defaults | Existing `BatchHelper` behavior remains ZIP |

## Some useful utilities

- `io.alapierre.ksef.batch.BatchHelper` - a utility class for preparing and sending ZIP batches of invoices without loading the entire batch into memory
- `io.alapierre.ksef.qr.VerificationLinkGenerator` - working version of the QR code link generator from the official SDK

The current release fixes the original [DefaultVerificationLinkService.java](ksef-client/src/main/java/pl/akmf/ksef/sdk/api/services/DefaultVerificationLinkService.java) to work properly with certs issued by Aplikacja Podatnika and MCU.

## Compression behavior

`BatchHelper` continues to create ZIP archives and explicitly declares
`CompressionType.Zip` when opening a batch session. This preserves the behavior of
existing integrations.

Callers that prepare an archive themselves can declare its format through the batch
request builder:

```java
OpenBatchSessionRequest request = OpenBatchSessionRequestBuilder.create()
        .withFormCode(SystemCode.FA_3, SchemaVersion.VERSION_1_0E, SessionValue.FA)
        .withBatchFile(fileSize, fileHash, CompressionType.TarGz)
        .addBatchFilePart(1, encryptedPartSize, encryptedPartHash)
        .withEncryption(encryptedSymmetricKey, initializationVector)
        .build();
```

The builder only describes an already prepared archive; it does not convert ZIP files
to TAR.GZ. The export response exposes `InvoiceExportPackage.compressionType`, but the
fork does not currently provide TAR.GZ export creation or extraction helpers.

## KSeF API 2.8 compatibility

The client supports the API 2.8 rate-limit model, including separate online and batch
session-close limits, anonymous limits and global IP limits. Changes made through the
test-data endpoint use `ApiRateLimitsChangeRequest`, matching the narrower request
contract of the API.

Session opening methods accept an optional `X-KSeF-Feature` value. For example, the
test-environment subject identifier validation feature can be enabled with:

```java
client.openOnlineSession(
        request,
        accessToken,
        Headers.SUBJECT_IDENTIFIER_VALIDATION
);
```

The previous overloads accepting `UpoVersion` remain available for source compatibility,
but are deprecated.

## Cryptographic limitations of the KSeF protocol

KSeF requires **AES-256-CBC with PKCS#7 padding** for invoice and batch-part
encryption, as specified in the Ministry of Finance documentation for
[interactive sessions](https://github.com/CIRFMF/ksef-api/blob/main/sesja-interaktywna.md)
and [batch sessions](https://github.com/CIRFMF/ksef-api/blob/main/sesja-wsadowa.md).
The client implements this in `DefaultCryptographyService` using the Java
transformation `AES/CBC/PKCS5Padding`, which provides the corresponding padding
for AES's 16-byte blocks.

There are two relevant security limitations:

- **CBC does not authenticate ciphertext.** CBC with padding can be vulnerable
  to padding-oracle attacks if an attacker can submit modified ciphertext and
  distinguish padding failures. A scanner warning identifies a risky construction;
  it does not by itself prove that a particular KSeF endpoint is exploitable.
- **KSeF defines one IV per session.** The same key and IV are reused for
  multiple invoices in an interactive session or separately encrypted parts of
  a batch. Reuse in CBC can reveal equal plaintext prefixes through equal
  ciphertext prefixes. Generating a random IV when opening the session does
  not remove this within-session limitation.

The IV-reuse problem was reported by this project's maintainer in
[CIRFMF/ksef-api#355](https://github.com/CIRFMF/ksef-api/issues/355).
These protocol constraints also affect independently implemented clients.
Replacing CBC with authenticated encryption such as AES-GCM, or supplying a
different IV for each invoice or batch part, requires support in the KSeF API;
changing only this client would break interoperability.

Use fresh encryption data for each new session (`getEncryptionData()` generates
a new key and IV), and retain HTTPS certificate validation. These measures do
not eliminate the protocol limitations described above. The KSeF encryption
helpers should not be used as a general-purpose encryption design for other data.

CodeQL and other scanner findings concerning these CBC operations must be
reviewed in this context. Protocol compatibility explains why the operations
remain; it does not make the risk disappear or justify dismissing unrelated
cryptographic findings. See [SECURITY.md](SECURITY.md) for vulnerability reporting.

## Using the library

The fork is published to Maven Central as `io.alapierre.ksef-sdk:ksef-client`.
No GitHub Packages repository or GitHub token is required. Java 21 remains the
minimum requirement. For example, to use the first Maven-built release:

```xml
<properties>
    <ksef.client.version>2.3.15</ksef.client.version>
</properties>

<!-- Inside your project's <dependencies> element: -->
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-client</artifactId>
    <version>${ksef.client.version}</version>
</dependency>
```

See [dependency setup](maven-package.md) for Maven and Gradle examples.

## JSON serialization — breaking migration

The HTTP clients require `io.alapierre.ksef.json.JsonSerializer`. **Constructors
accepting `ObjectMapper` have been removed**, including both `DefaultKsefClient`
overloads, `DefaultLighthouseKsefClient` and `ExceptionHandler`. Existing
applications must update client construction and recompile.

`ksef-client` depends only on the JSON contract and does not bring in Jackson or
any serializer implementation. Applications choose an adapter explicitly. For
Jackson 2, add `io.alapierre.ksef-sdk:ksef-json-serializer-jackson` alongside
`ksef-client`, using the same version; see [dependency setup](maven-package.md#moduły-serializacji-json).
Then construct the client with:

```java
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.jackson.JacksonJsonSerializer;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;

JsonSerializer json = new JacksonJsonSerializer();
var client = new DefaultKsefClient(httpClient, ksefApiProperties, json);
```

Alternatively, add `io.alapierre.ksef-sdk:ksef-json-serializer-gson` and use:

```java
import io.alapierre.ksef.json.gson.GsonJsonSerializer;

JsonSerializer json = new GsonJsonSerializer();
var client = new DefaultKsefClient(httpClient, ksefApiProperties, json);
```

Both default adapters write `OffsetDateTime`, `Instant`, `LocalDate` and `Duration`
as ISO-8601 strings, preserve date offsets and ignore unknown JSON properties.
They include null properties, encode `byte[]` as Base64 and preserve decimal
precision (including untyped maps). Concrete `Number` types in untyped maps may
differ. Both honor the library-neutral model annotations for names and enums.
To migrate an existing configured Jackson mapper:

```java
JsonSerializer json = new JacksonJsonSerializer(objectMapper);
var client = new DefaultKsefClient(httpClient, ksefApiProperties, json);
```

The adapter copies the supplied mapper's configuration and registers
`KsefJsonModule` on the copy. It leaves the original mapper unchanged; subsequent
changes to the original do not affect the serializer. Configure the supplied
mapper first, including `JavaTimeModule` where needed. Custom `ObjectMapper`
subclasses must support `copy()`.

`new GsonJsonSerializer(gson)` copies an existing Gson configuration, retaining
custom adapters and policies except for the required KSeF enum, time, Base64 and
neutral-name mappings and strict JSON syntax. Configure null inclusion and number
precision on the supplied Gson as appropriate. Gson maps fields; `JsonName` on a
public bean accessor is supported when it corresponds to a backing field.
Computed getter-only properties require a custom type adapter.

The same serializer can be supplied to `DefaultLighthouseKsefClient` and
`ExceptionHandler`; the `DefaultKsefClient` overload with response-header
observation also accepts it. There is no automatic implementation discovery or
fallback to Jackson. A non-null serializer is required.

Models no longer carry Jackson annotations. Their wire mappings use
`io.alapierre.ksef.json.annotation` from the contract module:

- `JsonName` declares the JSON property name for a field or bean accessor.
- `JsonValue` declares an enum's scalar wire value.
- `JsonCreator` declares an enum factory, preserving validation and alias handling.

If application code serializes KSeF models directly through Jackson, register
`new KsefJsonModule()` on that mapper before its first use. A plain `ObjectMapper`
no longer recognizes the model mappings. `JacksonJsonSerializer` performs this
registration automatically on its own copy.

The contract provides UTF-8 bytes for HTTP and convenience methods for strings.
`JsonType<T>` captures generic types without a library-specific type token:

```java
import io.alapierre.ksef.json.JsonType;
import java.util.List;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;

List<PublicKeyCertificate> certificates = json.fromJson(
        responseBytes, new JsonType<List<PublicKeyCertificate>>() {});
```

JSON `null` maps to Java `null` for reference types. Empty, malformed or trailing JSON
raises `JsonSerializationException`, a library-neutral `IOException` subtype.
A missing HTTP response body remains absent, and operations with no response
data do not parse it. JSON failures are reported through the clients' existing
`KsefApiException`/`SystemKSeFSDKException` types, with the neutral JSON exception
as the cause where parsing or encoding failed. API error handling retains HTTP
metadata when the server sends an unparseable error body.

Custom adapters implement `toJsonBytes`, `fromJson(byte[], JsonType<T>)` and
`name`; they must be safe for concurrent use, honor the neutral annotations and
preserve the KSeF date, enum, property-name, Base64 and null contracts. The bundled
implementations are Jackson 2 and Gson. Neither a plain
Gson instance nor a plain ObjectMapper can be passed to the client.

The Maven artifacts share the group `io.alapierre.ksef-sdk` and release version:

| Artifact | Contents and dependencies |
|----------|---------------------------|
| `ksef-json-serializer` | JSON interfaces, type token, exception and mapping annotations; no production dependencies outside the JDK. |
| `ksef-json-serializer-jackson` | Optional Jackson 2 adapter and `KsefJsonModule`; depends on the contract and Jackson, without a dependency on the client. |
| `ksef-json-serializer-gson` | Optional Gson adapter; depends on the contract and Gson, without a dependency on the client or Jackson. |
| `ksef-client` | API clients and models; depends on the JSON contract, without a production dependency on any JSON implementation. |

The new modules are part of the upcoming release and were not published with
version 2.3.15. Maven Enforcer prevents Jackson, Gson or a serializer adapter from
becoming a production dependency of the client, including through transitive
dependencies. Shared contract and HTTP tests exercise both adapters. Isolation
tests run the client with Gson without Jackson, and with a custom serializer
without either bundled adapter or JSON library.

## Java modules (JPMS)

All binary JARs declare stable names through `Automatic-Module-Name`; the names
do not depend on the artifact filename or version:

| Artifact | Java module name |
|----------|------------------|
| `ksef-client` | `io.alapierre.ksef.client` |
| `ksef-json-serializer` | `io.alapierre.ksef.json` |
| `ksef-json-serializer-jackson` | `io.alapierre.ksef.json.jackson` |
| `ksef-json-serializer-gson` | `io.alapierre.ksef.json.gson` |

For example, an application selecting Gson can declare:

```java
module my.application {
    requires java.net.http;
    requires io.alapierre.ksef.client;
    requires io.alapierre.ksef.json;
    requires io.alapierre.ksef.json.gson;
}
```

For Jackson, replace the last directive with
`requires io.alapierre.ksef.json.jackson;`. Place the application, chosen adapter,
client, contract and their runtime dependencies on the module path. The KSeF
artifacts are **automatic modules**, without explicit `module-info.class`
descriptors, so they do not declare which third-party modules must be resolved.
For a directory containing the application's runtime JARs, launch with:

```shell
java --module-path lib --add-modules ALL-MODULE-PATH --module my.application/com.example.Main
```

KSeF model packages are open to reflection as automatic modules; no extra
`--add-opens` is required for them. If you serialize your own DTOs in an explicit
application module, open their packages to the selected JSON library as needed
(`com.google.gson` or `com.fasterxml.jackson.databind`).

`./mvnw verify` compiles and runs named consumer modules for both adapters against
the packaged JARs and the client's runtime dependencies. It also renames the KSeF
JARs to verify that module names remain stable. These checks run without network
calls to KSeF. Automatic modules support modular applications, but cannot be used
directly as inputs to `jlink`.

## Maven migration — since 2.3.15

This repository now uses Maven instead of Gradle. Applications consuming the
published library can continue using their existing build tool, including Gradle.
The migration preserves the client coordinates, public Java API, automatic module
name (`io.alapierre.ksef.client`) and production dependency versions. The JSON
serializer abstraction described above is a subsequent, independent change.

If you build this repository from source, import the root `pom.xml` as a Maven
project in your IDE and use `./mvnw` (`mvnw.cmd` on Windows). Update custom CI
scripts that invoke `gradlew` or collect files from `ksef-client/build/libs`:
the client JARs are now in `ksef-client/target`. Run Maven from the repository root
to build the parent POM, serializer modules and client together.

The major library version remains `2`, corresponding to KSeF API 2. The build
migration does not require moving integrations to a `3.x` library version.

## Disclaimer

This is an independent community project, not an official release by the Ministry of Finance.
Use at your own discretion and verify compliance with your organization’s security policies.

We encourage the maintainers of the official SDK to continue improving it and to collaborate more openly with the community — we share the same goal: a stable, secure, and developer-friendly SDK for KSeF.

## Contributing

### Building and testing

Use JDK 21 and the Maven Wrapper, which pins Maven 3.9.11 and verifies its
distribution checksum. A separate Maven installation is not needed. On Windows,
replace `./mvnw` with `mvnw.cmd` (or `.\mvnw.cmd` in PowerShell).

```shell
./mvnw clean verify
```

Client Javadocs use sources expanded by Lombok into `ksef-client/target/delombok`
during `prepare-package`, including generated getters/setters and their JSON
annotations. This directory is used only for documentation; compilation and the
sources JAR use the original sources.

Run commands from the repository root:

| Task | Command |
|------|---------|
| Clean build, local tests, sources and Javadoc JARs | `./mvnw clean verify` |
| Run local tests during development | `./mvnw test` |
| Build only the JSON contract and Jackson adapter, with their tests | `./mvnw -pl ksef-json-serializer-jackson -am clean verify` |
| Build only the JSON contract and Gson adapter, with their tests | `./mvnw -pl ksef-json-serializer-gson -am clean verify` |
| Install the parent POM, serializers and client into the local Maven repository | `./mvnw clean install` |
| Regenerate the authentication classes from XSD | `./mvnw generate-sources` |

Each module writes its binary, sources and Javadoc JARs to its own `target`
directory and local test reports to `target/surefire-reports`. Live-test reports,
when enabled, are in `ksef-client/target/failsafe-reports`. CI collects local test
reports from all four modules.

The default build runs local tests only. They need no KSeF connection, token,
private certificate, or GitHub secret. HTTP contract tests use an in-memory
client; cryptographic tests generate their own keys. Tests cover request/response
mapping, error handling, ZIP contents and encrypted batch parts, certificate-link
signatures, and authentication XML validation against the bundled XSD.
Dependencies and the pinned Maven distribution are downloaded on the first run.
After dependencies and plugins for the requested goals have been cached, the
default build can also run with `-o`.

XJC generates the authentication model into
`ksef-client/target/generated-sources/jaxb` during `generate-sources`.
Do not edit or commit generated Java files. Change the schema or generator
configuration instead. Generated classes are included in the binary, sources,
and Javadoc JARs, retaining the existing packages and fluent `with…` methods.
`IpChangePolicyEnum` is retained in the source tree for compatibility with older
releases; it is no longer declared by the current schema.

### API compatibility and security checks

CI also compares public and protected Java APIs with the published baseline
using japicmp. Run the same check locally with:

```shell
./mvnw -Papi-compatibility clean verify
```

The baseline is pinned by `api.baseline.version` in the root POM (currently
`2.3.15`). Source or binary incompatibilities fail the check, and reports are
saved in `ksef-client/target/japicmp` and uploaded by CI. This profile needs the
baseline artifact from Maven Central on its first run. Use SNAPSHOT versions for
local development installs so published versions in your Maven cache remain intact.

Intentional incompatible changes need a documented migration path and a reviewed,
narrowly scoped japicmp exclusion in the same PR. Move the baseline only when
deliberately adopting a new published compatibility baseline. Do not disable the
whole check to accept one change. The check does not enforce SemVer major bumps:
the major library version follows the KSeF API. It does not replace tests of JSON
formats or runtime behavior.

The JSON migration explicitly excludes only the four removed `ObjectMapper`
constructor signatures from the compatibility check. Other methods and classes
remain checked against 2.3.15. Jackson is supplied on japicmp's comparison-only
classpath to read that old artifact; it is not a runtime dependency of the new
client. The serializer modules will need their own compatibility baselines once
their first versions are published.

The separate CodeQL workflow analyzes Java after a Maven build, including JAXB
generation, and also scans GitHub Actions. It runs on PRs, pushes to `main` and
weekly. GitHub's CodeQL Default setup must be disabled when using this workflow.
For findings involving KSeF's required CBC encryption, see
[cryptographic limitations](#cryptographic-limitations-of-the-ksef-protocol).
CodeQL and dependency update services are separate from the publication workflow.
Dependabot checks Maven dependencies and pinned GitHub Actions weekly; security
updates also require Dependabot alerts and security updates to be enabled in
repository settings. See [SECURITY.md](SECURITY.md) for private vulnerability reports.

### Optional KSeF TEST integration

The `ksef-live` profile explicitly enables `*LiveIT` tests using Maven Failsafe:

```shell
# Set KSEF_TOKEN and KSEF_NIP in your environment first.
./mvnw -Pksef-live clean verify
```

This sends one generated invoice, using the existing FA(2) test fixture, to
**KSeF TEST** and checks that it was accepted. It requires a token authorized for
the supplied NIP and a working test environment that accepts this fixture.
Missing credentials fail before connecting; authentication and processing waits
are bounded. These tests never run in the default build or release workflow.
An unavailable KSeF TEST environment does not block the normal build or release.
If the live profile is explicitly selected, a live-test failure fails that run.

The **KSeF TEST integration (manual)** GitHub workflow uses repository secrets
`KSEF_TOKEN` and `KSEF_NIP`. It is opt-in and is not a required release check.
Surefire and Failsafe reports are uploaded separately from normal build artifacts.

### Releases

JReleaser continues to publish to Maven Central and create GitHub Releases.
Pushing a new `v2.*` tag starts a **real publication**. Its commit must belong to
`origin/main`. Manual publication must be started from the `main` workflow with
an existing tag and `dry_run=false`. The workflow derives `revision` from the tag,
runs local tests and API compatibility checks, signs and stages artifacts, and
validates the publication POMs. It does not run live KSeF tests.

To check release configuration before tagging, use **Run workflow**, select the
branch containing your changes, leave `tag` empty, enter a release `version`
without `v` (for example `2.3.16`), and keep `dry_run=true`. This uses a separate
job with read-only repository permissions and no publication secrets. It stages
unsigned artifacts and runs `jreleaser:deploy` in dry-run mode. It validates the
deployment, but does not test production signing credentials or create a GitHub
Release. An optional existing tag can be used instead; it must contain the
workflow's supporting scripts.

The parent POM, JSON contract, Jackson and Gson adapters, and client are all published.
Module versions are aligned and resolved in the publication POMs; consumers do
not need to define Maven's `revision` property. Maven stages all reactor modules
in the same repository, which JReleaser validates and publishes together.

Releases run in [GitHub Actions](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/release-jreleaser.yml).
The configuration is maintained in the [release workflow](.github/workflows/release-jreleaser.yml)
and [jreleaser.yml](jreleaser.yml).

Contributions and pull requests are welcome.
If you have fixes, improvements, or API updates — please open an issue or submit a PR.

# A Final Note

This fork was created out of necessity, not rivalry.
We believe in collaboration over criticism — but also in reliable tools that developers can trust when building integrations with Poland’s national e-invoicing system.
