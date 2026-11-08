package io.alapierre.ksef.qr;

import org.junit.Test;
import pl.akmf.ksef.sdk.api.KsefApiProperties;
import pl.akmf.ksef.sdk.api.services.DefaultVerificationLinkService;
import pl.akmf.ksef.sdk.client.model.qrcode.ContextIdentifierType;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import static org.junit.Assert.*;

public class VerificationLinkTest {
    @Test
    public void rsaCertificateLinkHasVerifiablePssSignature() throws Exception {
        verifyCertificateLink("RSA");
    }

    @Test
    public void ecCertificateLinkHasVerifiableSignature() throws Exception {
        verifyCertificateLink("EC");
    }

    private void verifyCertificateLink(String algorithm) throws Exception {
        var generator = KeyPairGenerator.getInstance(algorithm);
        if (algorithm.equals("RSA")) {
            generator.initialize(2048);
        } else {
            generator.initialize(new ECGenParameterSpec("secp256r1"));
        }
        var keys = generator.generateKeyPair();
        var service = new DefaultVerificationLinkService(new KsefApiProperties() {
            public String getBaseUri() { return "https://example.test/v2"; }
            public String getSuffixUri() { return ""; }
            public String getQrUri() { return ""; }
            public Duration getRequestTimeout() { return Duration.ofSeconds(1); }
            public Map<String, String> getDefaultHeaders() { return Map.of(); }
        });
        byte[] hash = new byte[32];
        java.util.Arrays.fill(hash, (byte) 0xff);
        String link = service.buildCertificateVerificationUrl("1111111111", ContextIdentifierType.NIP,
                "2222222222", "ABC123", Base64.getEncoder().encodeToString(hash), keys.getPrivate());
        String expectedPath = "example.test/v2/client-app/certificate/Nip/2222222222/1111111111/ABC123/"
                + Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        assertTrue(link.startsWith("https://" + expectedPath + "/"));
        String encodedSignature = link.substring(link.lastIndexOf('/') + 1);
        assertTrue(encodedSignature.matches("[A-Za-z0-9_-]+"));
        byte[] signature = Base64.getUrlDecoder().decode(encodedSignature);

        Signature verifier = Signature.getInstance(algorithm.equals("RSA") ? "RSASSA-PSS" : "SHA256withECDSA");
        if (algorithm.equals("RSA")) {
            verifier.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
        }
        verifier.initVerify(keys.getPublic());
        verifier.update(expectedPath.getBytes(StandardCharsets.UTF_8));
        assertTrue(verifier.verify(signature));
        verifier.update(expectedPath.replace("ABC123", "ABC124").getBytes(StandardCharsets.UTF_8));
        assertFalse("Changing the certificate serial must invalidate the signature", verifier.verify(signature));
    }
}
