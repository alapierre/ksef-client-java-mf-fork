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


| Area                  | Official SDK           | This Fork                                   |
|-----------------------|------------------------|---------------------------------------------|
| Dependency management | it varies              | Updated and secure dependencies             |
| Maven publishing      | Github Packages        | Published to public Maven repository        |
| Maintenance           | Irregular / no updates | Community maintained                        |
| Build system          | Unmodified             | Cleaned up and improved for reproducibility |

## Some useful utilities

- `io.alapierre.ksef.batch.BatchHelper` - a utility class for preparing and sanding batches of invoices without going to OutOfMemory Exceptions
- `io.alapierre.ksef.qr.VerificationLinkGenerator` - working version of the QR code link generator from the official SDK

The current release fixes the original [DefaultVerificationLinkService.java](ksef-client/src/main/java/pl/akmf/ksef/sdk/api/services/DefaultVerificationLinkService.java) to work properly with certs issued by Aplikacja Podatnika and MCU.

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

Contributions and pull requests are welcome.
If you have fixes, improvements, or API updates — please open an issue or submit a PR.

# A Final Note

This fork was created out of necessity, not rivalry.
We believe in collaboration over criticism — but also in reliable tools that developers can trust when building integrations with Poland’s national e-invoicing system.