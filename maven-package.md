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

## Moduły serializacji JSON

Przygotowywane wydanie wprowadza **niekompatybilną zmianę API**: klient wymaga
`JsonSerializer`, a konstruktory z `ObjectMapper` zostały usunięte. Aplikację
trzeba dostosować i ponownie skompilować.

`ksef-client` zależy od `ksef-json-serializer`, który zawiera kontrakt i neutralne
adnotacje mapowania. Nie pobiera przechodnio Jacksona ani żadnego adaptera.
Aplikacja musi jawnie wybrać implementację. Dla Jacksona 2 dodaj oba artefakty:

```xml
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-client</artifactId>
    <version>${ksef.client.version}</version>
</dependency>
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-json-serializer-jackson</artifactId>
    <version>${ksef.client.version}</version>
</dependency>
```

W Gradle, przy wspólnej zmiennej `ksefClientVersion`:

```kotlin
implementation("io.alapierre.ksef-sdk:ksef-client:$ksefClientVersion")
implementation("io.alapierre.ksef-sdk:ksef-json-serializer-jackson:$ksefClientVersion")
```

Przekaż `new JacksonJsonSerializer()` lub
`new JacksonJsonSerializer(objectMapper)` do konstruktora klienta. Adapter kopiuje
konfigurację przekazanego mappera i dodaje obsługę neutralnych adnotacji, nie
modyfikując oryginału. Jeżeli używasz mappera bezpośrednio do modeli KSeF,
zarejestruj na nim `new KsefJsonModule()` przed pierwszym użyciem.

Alternatywnie wybierz Gson: zastąp zależność od adaptera Jacksona następującą:

```xml
<dependency>
    <groupId>io.alapierre.ksef-sdk</groupId>
    <artifactId>ksef-json-serializer-gson</artifactId>
    <version>${ksef.client.version}</version>
</dependency>
```

W Gradle użyj `implementation("io.alapierre.ksef-sdk:ksef-json-serializer-gson:$ksefClientVersion")`.
Następnie przekaż adapter do klienta:

```java
var json = new io.alapierre.ksef.json.gson.GsonJsonSerializer();
var client = new DefaultKsefClient(httpClient, ksefApiProperties, json);
```

Adapter Gson nie zależy od Jacksona. Konstruktor `new GsonJsonSerializer(gson)`
kopiuje konfigurację aplikacji, dodając wymagane mapowania KSeF i ścisłe parsowanie
JSON. Domyślne adaptery obsługują Base64, daty i czas trwania w formacie ISO,
neutralne adnotacje oraz liczby dziesiętne bez utraty precyzji.

Możesz też dostarczyć własny `JsonSerializer` i pominąć oba gotowe adaptery.
Implementacja musi obsługiwać adnotacje `JsonName`, `JsonValue`
i `JsonCreator` z modułu abstrakcji oraz kontrakt dat, enumów, Base64 i wartości null.
Szczegóły migracji znajdują się w [README](README.md#json-serialization--breaking-migration).

Nowe moduły nie są dostępne w wydaniu `2.3.15` użytym w przykładach powyżej.
Do czasu publikacji można wykonać `./mvnw clean install` z katalogu głównego tego
repozytorium i użyć lokalnej wersji `2.3.15-SNAPSHOT`. Po publikacji ustaw wersję
wydania zawierającego moduły; wszystkie artefakty powinny mieć tę samą wersję.
Przy ręcznym kopiowaniu JAR-ów dołącz kontrakt oraz wybrany adapter z jego
zależnościami.

## Aplikacje modularne (JPMS)

JAR-y deklarują stałe nazwy modułów przez `Automatic-Module-Name`:
`io.alapierre.ksef.client`, `io.alapierre.ksef.json`,
`io.alapierre.ksef.json.jackson` i `io.alapierre.ksef.json.gson`.
Nazwy nie zależą od wersji ani nazwy pliku JAR.

Są to moduły automatyczne. Przykładowy `module-info.java`, sposób uruchomienia
z zależnościami na module path i zakres testów opisuje
[sekcja JPMS w README](README.md#java-modules-jpms).

## Współrzędne tego forka

Wcześniejsza instrukcja w tym pliku dotyczyła paczek MF
`pl.akmf.ksef-sdk:ksef-client` w GitHub Packages organizacji CIRFMF.
Powyższe przykłady dotyczą forka `io.alapierre.ksef-sdk:ksef-client`.
Dotychczasowi użytkownicy tego forka zachowują te same współrzędne zależności.

Jeżeli chcesz budować bibliotekę ze źródeł, zobacz
[instrukcję budowania i testowania](README.md#building-and-testing).
