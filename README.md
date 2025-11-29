# KSeF 2.0 SDK (Community Fork)

A community-maintained fork of the official KSeF 2.0 SDK — created to fix critical issues, update dependencies, and make the SDK usable in real-world integrations until the official version catches up.

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

The goal of this fork is not to create a competing SDK, but to provide a practical, temporary solution for developers who need a reliable, secure, and working client for KSeF integration today.

Specifically, this fork:

- Fixes blocking bugs and inconsistencies in the SDK.
- Updates dependencies to remove known security vulnerabilities.
- Aligns the SDK with the latest version of the KSeF 2.0 API.
- Publishes the library to a public Maven repository for easier integration in Java projects.

The intent is to support the developer community until the official SDK regains active maintenance and stability.

## 🧱 Differences from the Official SDK


| Area                  | Official SDK           | This Fork                                   |
|-----------------------|------------------------|---------------------------------------------|
| Dependency management | it varies              | Updated and secure dependencies             |
| Maven publishing	     | Github Packages        | Published to public Maven repository        |
| Maintenance           | Irregular / no updates | Community maintained                        |
| Build system          | Unmodified             | Cleaned up and improved for reproducibility |

## Public maven repo dependency

````xml
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-client</artifactId>
    <version>2.2.3</version>
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