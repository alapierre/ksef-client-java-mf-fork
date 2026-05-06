package pl.akmf.ksef.sdk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.auth.EncryptionMethod;
import pl.akmf.ksef.sdk.client.model.certificate.SelfSignedCertificate;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificateUsage;
import pl.akmf.ksef.sdk.configuration.BaseIntegrationTest;
import pl.akmf.ksef.sdk.system.SystemKSeFSDKException;
import pl.akmf.ksef.sdk.util.ExampleApiProperties;
import pl.akmf.ksef.sdk.util.HttpClientBuilder;
import pl.akmf.ksef.sdk.util.HttpClientConfig;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

class CertificateRotationTest extends BaseIntegrationTest {

    @Autowired
    private ExampleApiProperties apiProperties;

    // Re-certyfikacja: ten sam klucz RSA, ale nowy certyfikat.
    // modulus i eksponent muszą pozostać identyczne, mimo że thumbprint certyfikatu się zmienił.
    @Test
    void publicKeyRemainsTheSameAfterRecertification() throws CertificateException {
        String subject = "1111111111";

        KeyPair sharedRsa = generateRsaKeyPair("RSA", 2048);

        PublicKeyCertificate symetricKeyOld = preparePublicKeyCertificate(
                "KSeF-Symmetric-Old", subject, sharedRsa,
                ZonedDateTime.now().minusYears(1).toInstant(),
                ZonedDateTime.now().plusMonths(1).toInstant(),
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );
        PublicKeyCertificate symetricKeyNew = preparePublicKeyCertificate(
                "KSeF-Symmetric-New", subject, sharedRsa,
                ZonedDateTime.now().minusMinutes(180).toInstant(),
                ZonedDateTime.now().plusYears(2).toInstant(),
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );

        DefaultCryptographyService mockedCryptographyService = new DefaultCryptographyService(
                mockKsefClientWithCertificates(List.of(symetricKeyOld)));
        X509Certificate symmetricKeyCertificateBefore = mockedCryptographyService.getSymmetricKeyCertificate();

        mockedCryptographyService = new DefaultCryptographyService(mockKsefClientWithCertificates(List.of(symetricKeyOld, symetricKeyNew)));
        X509Certificate symmetricKeyCertificateAfter = mockedCryptographyService.getSymmetricKeyCertificate();

        Assertions.assertEquals(((RSAPublicKey) symmetricKeyCertificateBefore.getPublicKey()).getModulus(),
                ((RSAPublicKey) symmetricKeyCertificateAfter.getPublicKey()).getModulus());
        Assertions.assertEquals(((RSAPublicKey) symmetricKeyCertificateBefore.getPublicKey()).getPublicExponent(),
                ((RSAPublicKey) symmetricKeyCertificateAfter.getPublicKey()).getPublicExponent());

        Assertions.assertNotEquals(certificateService.getSha256Fingerprint(symmetricKeyCertificateBefore),
                certificateService.getSha256Fingerprint(symmetricKeyCertificateAfter));
    }

    // Rotacja planowa: okres przejściowy.
    // API zwraca dwa certyfikaty SymmetricKeyEncryption jednocześnie
    // serwis powinien wybrać ten z późniejszą datą ValidFrom
    @Test
    void newerCertificateIsPreferredInTransitionPeriodAfterPlannedRotation() throws CertificateException {
        String subject = "1111111111";

        KeyPair oldRsa = generateRsaKeyPair("RSA", 2048);
        KeyPair newRsa = generateRsaKeyPair("RSA", 2048);
        Instant oldValidFrom = ZonedDateTime.now().minusYears(1).toInstant();
        Instant oldValidTo = ZonedDateTime.now().minusYears(1).plusYears(2).toInstant();
        Instant newValidFrom = ZonedDateTime.now().minusDays(1).toInstant();
        Instant newValidTo = ZonedDateTime.now().minusDays(1).plusYears(2).toInstant();

        PublicKeyCertificate symetricKeyOld = preparePublicKeyCertificate(
                "KSeF-Symmetric-Old", subject, oldRsa,
                oldValidFrom,
                oldValidTo,
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );
        PublicKeyCertificate symetricKeyNew = preparePublicKeyCertificate(
                "KSeF-Symmetric-New", subject, newRsa,
                newValidFrom,
                newValidTo,
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );

        DefaultCryptographyService mockedCryptographyService = new DefaultCryptographyService(
                mockKsefClientWithCertificates(List.of(symetricKeyOld, symetricKeyNew)));
        X509Certificate symmetricKeyCertificate = mockedCryptographyService.getSymmetricKeyCertificate();

        // wybrany klucz musi odpowiadać nowszemu certyfikatowi
        Assertions.assertEquals(((RSAPublicKey) newRsa.getPublic()).getModulus(),
                ((RSAPublicKey) symmetricKeyCertificate.getPublicKey()).getModulus());
    }

    // Rotacja planowa: nowy certyfikat opublikowany z wyprzedzeniem (validFrom w przyszłości).
    // Klient wybiera certyfikat ważny w chwili operacji.
    // Certyfikat z przyszłą datą validFrom nie może być użyty, nawet jeśli ma późniejszą datę.
    @Test
    void futureCertificatePublishedAndEarlyOldCertificateIsUsedAfterPlannedRotation() throws CertificateException {
        String subject = "1111111111";

        KeyPair currentRsa = generateRsaKeyPair("RSA", 2048);
        KeyPair futureRsa = generateRsaKeyPair("RSA", 2048);
        Instant currentValidFrom = ZonedDateTime.now().minusYears(1).toInstant();
        Instant currentValidTo = ZonedDateTime.now().minusYears(1).plusYears(2).toInstant();
        Instant futureValidFrom = ZonedDateTime.now().plusDays(7).toInstant(); // jeszcze nieważny
        Instant futureValidTo = ZonedDateTime.now().plusDays(7).plusYears(2).toInstant();

        PublicKeyCertificate symetricKeyCurrent = preparePublicKeyCertificate(
                "KSeF-Symmetric-Current", subject, currentRsa,
                currentValidFrom,
                currentValidTo,
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );
        PublicKeyCertificate symetricKeyFuture = preparePublicKeyCertificate(
                "KSeF-Symmetric-Future", subject, futureRsa,
                futureValidFrom,
                futureValidTo,
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );

        DefaultCryptographyService mockedCryptographyService = new DefaultCryptographyService(
                mockKsefClientWithCertificates(List.of(symetricKeyCurrent, symetricKeyFuture)));
        X509Certificate symmetricKeyCertificate = mockedCryptographyService.getSymmetricKeyCertificate();

        // certyfikat z przyszłą datą validFrom musi zostać zignorowany
        Assertions.assertEquals(((RSAPublicKey) currentRsa.getPublic()).getModulus(),
                ((RSAPublicKey) symmetricKeyCertificate.getPublicKey()).getModulus());
    }

    // Rotacja planowa: po zakończeniu okresu przejściowego.
    // API przestaje zwracać stary certyfikat, serwis musi używać nowego.
    @Test
    void afterTransitionOnlyNewCertificateIsUsedAfterPlannedRotation() {
        String subject = "1111111111";

        KeyPair sharedRsa = generateRsaKeyPair("RSA", 2048);

        PublicKeyCertificate symetricKeyOld = preparePublicKeyCertificate(
                "KSeF-Symmetric-Old", subject, sharedRsa,
                ZonedDateTime.now().minusYears(1).toInstant(),
                ZonedDateTime.now().plusMonths(1).toInstant(),
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );
        PublicKeyCertificate symetricKeyNew = preparePublicKeyCertificate(
                "KSeF-Symmetric-New", subject, sharedRsa,
                ZonedDateTime.now().minusMinutes(180).toInstant(),
                ZonedDateTime.now().plusYears(2).toInstant(),
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );

        DefaultCryptographyService mockedCryptographyService = new DefaultCryptographyService(
                mockKsefClientWithCertificates(List.of(symetricKeyOld)));
        PublicKeyCertificate symmetricKeyEncryptionBefore = mockedCryptographyService.getSymmetricKeyEncryption();

        mockedCryptographyService = new DefaultCryptographyService(mockKsefClientWithCertificates(List.of(symetricKeyNew)));
        PublicKeyCertificate symmetricKeyEncryptionAfter = mockedCryptographyService.getSymmetricKeyEncryption();

        Assertions.assertNotEquals(symmetricKeyEncryptionBefore.getCertificate(),
                symmetricKeyEncryptionAfter.getCertificate(),
                "Po rotacji planowej certyfikat (i klucz) powinien się zmienić.");
    }

    // Rotacja awaryjna po incydencie bezpieczeństwa.
    // Klient otrzymał błąd 21470 i wywołuje initCryptographyService()
    // nowy klucz musi być inny niż skompromitowany i zgodny z certyfikatem zwróconym przez API.
    @Test
    void afterError21470ServiceUsesNewKeyAfterEmergencyRotation() throws CertificateException {
        String subject = "1111111111";

        KeyPair compromisedRsa = generateRsaKeyPair("RSA", 2048);
        KeyPair newRsa = generateRsaKeyPair("RSA", 2048);

        PublicKeyCertificate symetricKeyCompromised = preparePublicKeyCertificate(
                "KSeF-Symmetric-Compromised", subject, compromisedRsa,
                ZonedDateTime.now().minusYears(1).toInstant(),
                ZonedDateTime.now().plusYears(1).toInstant(),
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );
        PublicKeyCertificate symetricKeyNew = preparePublicKeyCertificate(
                "KSeF-Symmetric-New", subject, newRsa,
                ZonedDateTime.now().minusMinutes(180).toInstant(),
                ZonedDateTime.now().plusYears(2).toInstant(),
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION
        );

        DefaultCryptographyService mockedCryptographyService = new DefaultCryptographyService(
                mockKsefClientWithCertificates(List.of(symetricKeyCompromised)));
        X509Certificate symmetricKeyCertificateBefore = mockedCryptographyService.getSymmetricKeyCertificate();

        mockedCryptographyService = new DefaultCryptographyService(mockKsefClientWithCertificates(List.of(symetricKeyNew)));
        X509Certificate symmetricKeyCertificateAfter = mockedCryptographyService.getSymmetricKeyCertificate();

        Assertions.assertNotEquals(((RSAPublicKey) symmetricKeyCertificateBefore.getPublicKey()).getModulus(),
                ((RSAPublicKey) symmetricKeyCertificateAfter.getPublicKey()).getModulus(),
                "Po rotacji awaryjnej klucz publiczny powinien się zmienić.");
        Assertions.assertEquals(((RSAPublicKey) newRsa.getPublic()).getModulus(),
                ((RSAPublicKey) symmetricKeyCertificateAfter.getPublicKey()).getModulus());
    }

    private static String toPemBc(X509Certificate cert) {
        try (StringWriter sw = new StringWriter();
             JcaPEMWriter writer = new JcaPEMWriter(sw)) {

            writer.writeObject(cert);
            writer.flush();
            return sw.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKeyCertificate preparePublicKeyCertificate(
            String name,
            String subject,
            KeyPair sharedRsa,
            Instant validFrom,
            Instant validTo,
            PublicKeyCertificateUsage usage) {

        X509Certificate generatedCertificate = certificateService.getCompanySeal(
                name + " sp. z o.o",
                "VATPL-" + subject,
                name,
                EncryptionMethod.RSA).certificate();
        SelfSignedCertificate selfSignedCertificateWithSharedRsa = createSelfSignedCertificate(
                sharedRsa,
                generatedCertificate.getIssuerX500Principal(),
                validFrom,
                validTo,
                generatedCertificate.getSerialNumber());

        PublicKeyCertificate publicKeyCertificate = new PublicKeyCertificate();
        publicKeyCertificate.setCertificate(toPemBc(selfSignedCertificateWithSharedRsa.certificate()));
        publicKeyCertificate.setCertificateId(selfSignedCertificateWithSharedRsa.certificate().getSerialNumber().toString());
        publicKeyCertificate.setPublicKeyId(certificateService.getSha256Fingerprint(selfSignedCertificateWithSharedRsa.certificate()));
        publicKeyCertificate.setValidFrom(validFrom.atZone(ZoneId.systemDefault()).toOffsetDateTime());
        publicKeyCertificate.setValidTo(validTo.atZone(ZoneId.systemDefault()).toOffsetDateTime());
        publicKeyCertificate.setUsage(List.of(usage));

        return publicKeyCertificate;
    }

    // symulacja użycia serwisu gdzie klient API w retrievePublicKeyCertificate zwraca certyfikaty które mu przekażemy
    private KSeFClient mockKsefClientWithCertificates(List<PublicKeyCertificate> certificates) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        HttpClient apiClient = HttpClientBuilder.createHttpBuilder(new HttpClientConfig()).build();
        return new DefaultKsefClient(
                apiClient,
                apiProperties,
                objectMapper) {

            @Override
            public List<PublicKeyCertificate> retrievePublicKeyCertificate() {
                return certificates;
            }

        };
    }

    private SelfSignedCertificate createSelfSignedCertificate(
            KeyPair keyPair,
            X500Principal x500Name,
            Instant notBefore,
            Instant notAfter,
            BigInteger serial
    ) {
        X509Certificate cert = generateCertificate(
                keyPair,
                "SHA256WithRSA",
                x500Name,
                x500Name,
                notBefore,
                notAfter,
                serial
        );

        return new SelfSignedCertificate(cert, keyPair);
    }

    private X509Certificate generateCertificate(
            KeyPair keyPair,
            String signatureAlgorithm,
            X500Principal subject,
            X500Principal issuer,
            Instant notBeforeInstant,
            Instant notAfterInstant,
            BigInteger serialNumber
    ) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            Date notBefore = Date.from(notBeforeInstant);
            Date notAfter = Date.from(notAfterInstant);

            JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    serialNumber,
                    notBefore,
                    notAfter,
                    subject,
                    keyPair.getPublic()
            );

            ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm)
                    .setProvider("BC")
                    .build(keyPair.getPrivate());

            X509CertificateHolder certHolder = certBuilder.build(contentSigner);

            return new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certHolder);

        } catch (Exception e) {
            throw new SystemKSeFSDKException(e.getMessage(), e);
        }
    }

    private KeyPair generateRsaKeyPair(String algorithm, int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new SystemKSeFSDKException(e.getMessage(), e);
        }
    }
}