[![CI](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/ci.yml/badge.svg)](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/ci.yml) [![Maven Central](https://img.shields.io/maven-central/v/io.alapierre.ksef-sdk/ksef-client)](https://central.sonatype.com/artifact/io.alapierre.ksef-sdk/ksef-client)


# KSeF 2.0 SDK (Community Fork)

An independently maintained community fork of the official KSeF 2.0 SDK, focused on reliable integrations, API coverage, and backward compatibility.

For setup and maintenance, see [using the library](maven-package.md),
[building and testing](#building-and-testing), [releases](#releases),
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

## Maven migration — since 2.3.15

This repository now uses Maven instead of Gradle. Applications consuming the
published library can continue using their existing build tool, including Gradle.
The migration preserves the client coordinates, public Java API, automatic module
name (`io.alapierre.ksef.client`) and production dependency versions. JSON handling
still uses Jackson; the planned serializer abstraction is a separate change.

If you build this repository from source, import the root `pom.xml` as a Maven
project in your IDE and use `./mvnw` (`mvnw.cmd` on Windows). Update custom CI
scripts that invoke `gradlew` or collect files from `ksef-client/build/libs`:
the JARs are now in `ksef-client/target`. The reactor also builds a parent POM;
run Maven from the repository root so both modules are available.

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

Run commands from the repository root:

| Task | Command |
|------|---------|
| Clean build, local tests, sources and Javadoc JARs | `./mvnw clean verify` |
| Run local tests during development | `./mvnw test` |
| Install the parent POM and client into the local Maven repository | `./mvnw clean install` |
| Regenerate the authentication classes from XSD | `./mvnw generate-sources` |

The client artifacts are written to `ksef-client/target`. Test reports are in
`ksef-client/target/surefire-reports`; live-test reports, when enabled, are in
`ksef-client/target/failsafe-reports`.

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
Pushing a new `v2.*` tag starts a **real publication**. Manual runs require an
existing tag and default to a dry run. The workflow checks out that tag, derives
`revision` from it, runs local tests, signs and stages artifacts, and validates
the publication POMs. It does not run live KSeF tests.

Both the client and parent POM are published. Their versions are resolved in the
publication POMs; consumers do not need to define Maven's `revision` property.

Releases run in [GitHub Actions](https://github.com/alapierre/ksef-client-java-mf-fork/actions/workflows/release-jreleaser.yml).
The configuration is maintained in the [release workflow](.github/workflows/release-jreleaser.yml)
and [jreleaser.yml](jreleaser.yml).

Contributions and pull requests are welcome.
If you have fixes, improvements, or API updates — please open an issue or submit a PR.

# A Final Note

This fork was created out of necessity, not rivalry.
We believe in collaboration over criticism — but also in reliable tools that developers can trust when building integrations with Poland’s national e-invoicing system.
