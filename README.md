[![CI](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/ci.yml/badge.svg)](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/ci.yml) [![Maven Central](https://img.shields.io/maven-central/v/io.alapierre.ksef-sdk/ksef-client)](https://central.sonatype.com/artifact/io.alapierre.ksef-sdk/ksef-client)


# KSeF 2.0 SDK (Community Fork)

An independently maintained community fork of the official KSeF 2.0 SDK, focused on reliable integrations, API coverage, and backward compatibility.

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
| Build system          | Unmodified               | Cleaned up and improved for reproducibility |
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

## Public maven repo dependency

````xml
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-client</artifactId>
    <version>${ksef.client.version}</version>
</dependency>
````

## Disclaimer

This is an independent community project, not an official release by the Ministry of Finance.
Use at your own discretion and verify compliance with your organization’s security policies.

We encourage the maintainers of the official SDK to continue improving it and to collaborate more openly with the community — we share the same goal: a stable, secure, and developer-friendly SDK for KSeF.

## Contributing

### Building and testing

Use JDK 21 and the Maven Wrapper (`mvnw.cmd` on Windows):

```shell
./mvnw clean verify
```

The default build runs local tests only. They need no KSeF connection, token,
private certificate, or GitHub secret. HTTP contract tests use an in-memory
client; cryptographic tests generate their own keys. Tests cover request/response
mapping, error handling, ZIP contents and encrypted batch parts, certificate-link
signatures, and authentication XML validation against the bundled XSD.
Dependencies and the pinned Maven distribution are downloaded on the first run.
After dependencies have been cached, the default build can also run with `-o`.

XJC generates the authentication model into
`ksef-client/target/generated-sources/jaxb` during `generate-sources`.
Do not edit or commit generated Java files. Change the schema or generator
configuration instead. Generated classes are included in the binary, sources,
and Javadoc JARs, retaining the existing packages and fluent `with…` methods.
`IpChangePolicyEnum` is retained in the source tree for compatibility with older
releases; it is no longer declared by the current schema.

The Maven coordinates remain `io.alapierre.ksef-sdk:ksef-client` and the automatic
module name remains `io.alapierre.ksef.client`. The build migration does not
change the client's public API or production dependency versions.

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
Their failure remains visible and does not block releases.

The **KSeF TEST integration (manual)** GitHub workflow uses repository secrets
`KSEF_TOKEN` and `KSEF_NIP`. It is opt-in and is not a required release check.
Surefire and Failsafe reports are uploaded separately from normal build artifacts.

### Releases

JReleaser continues to publish to Maven Central and create GitHub Releases.
Push a tag such as `v2.3.15` **containing the Maven migration**, or dispatch the
release workflow with an existing tag. Manual runs default to a dry run. Tags
such as `v2.4.0-rc.1` are marked as prereleases. The major version continues to
correspond to KSeF API 2; the build migration does not require a 3.x release.

The workflow checks out the exact tag, derives the Maven `revision` from it,
runs all local tests, signs the artifacts and stages them in
`target/staging-deploy` before invoking JReleaser. The parent POM is published
alongside the client. Flattened publication POMs contain resolved versions so
consumers do not need to supply `revision`. The default development version is
`2.3.15-SNAPSHOT`; it can be overridden with `-Drevision=...`.

Existing publication secrets are reused: `SIGNING_KEY`, `SIGNING_PASSWORD`,
`MAVENCENTRAL_USERNAME`, `MAVENCENTRAL_TOKEN`, and optionally
`JRELEASER_GITHUB_TOKEN` (otherwise the workflow token is used). Maven's BC signer
reads the exported private signing key directly from the environment; it does
not need `SIGNING_KEY_ID` or a locally imported keyring.

To verify signed staging locally, using your configured GPG signer:

```shell
./mvnw -Prelease -Drevision=2.3.15 \
  -DaltDeploymentRepository="staging::file://$(pwd)/target/staging-deploy" clean deploy
```

This command stages locally; publication is a separate JReleaser operation.
Neither local verification nor release requires KSeF TEST credentials.

Contributions and pull requests are welcome.
If you have fixes, improvements, or API updates — please open an issue or submit a PR.

# A Final Note

This fork was created out of necessity, not rivalry.
We believe in collaboration over criticism — but also in reliable tools that developers can trust when building integrations with Poland’s national e-invoicing system.
