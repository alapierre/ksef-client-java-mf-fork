# Korzystanie z biblioteki

Ten fork jest publikowany w [Maven Central](https://central.sonatype.com/artifact/io.alapierre.ksef-sdk/ksef-client)
pod współrzędnymi `io.alapierre.ksef-sdk:ksef-client`. Wymaga Javy 21.
Pobieranie biblioteki nie wymaga tokena GitHub ani konfiguracji GitHub Packages.

Poniższe przykłady używają wersji `2.3.15`, pierwszej zbudowanej przez Maven.
Informacje o zmianach w kolejnych wersjach znajdują się w [release notes](RELEASE_NOTES.md).

## Maven

W sekcji `<properties>` swojego `pom.xml` ustaw wersję:

```xml
<ksef.client.version>2.3.15</ksef.client.version>
```

W sekcji `<dependencies>` dodaj:

```xml
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-client</artifactId>
    <version>${ksef.client.version}</version>
</dependency>
```

Maven Central jest domyślnym repozytorium Mavena; dodatkowa sekcja `<repositories>`
nie jest potrzebna. Publikowany parent POM jest pobierany automatycznie.
Nie trzeba dodawać go jako zależności ani ustawiać właściwości `revision`.

## Gradle — Kotlin DSL

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.alapierre.ksef-sdk:ksef-client:2.3.15")
}
```

Zmiana systemu budowania tego repozytorium na Maven nie wymaga migracji aplikacji
korzystających z klienta. Można nadal używać Gradle.

## Współrzędne tego forka

Wcześniejsza instrukcja w tym pliku dotyczyła paczek MF
`pl.akmf.ksef-sdk:ksef-client` w GitHub Packages organizacji CIRFMF.
Powyższe przykłady dotyczą forka `io.alapierre.ksef-sdk:ksef-client`.
Dotychczasowi użytkownicy tego forka zachowują te same współrzędne zależności.

Jeżeli chcesz budować bibliotekę ze źródeł, zobacz
[instrukcję budowania i testowania](README.md#building-and-testing).
