package io.alapierre.ksef.qr;

import io.alapierre.ksef.Environment;
import lombok.val;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Ignore;
import org.junit.Test;
import pl.akmf.ksef.sdk.api.KsefApiProperties;
import pl.akmf.ksef.sdk.api.services.DefaultVerificationLinkService;
import pl.akmf.ksef.sdk.client.model.qrcode.ContextIdentifierType;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Map;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 30.11.2025
 */
public class KeysTest {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    @Ignore
    public void test() throws Exception {

        val pass = System.getenv("SIGN_TEST_KEY_PASSWORD");
        PrivateKey k;
        X509Certificate c;

        try (InputStream key = new FileInputStream("src/test/resources/SignTest.key")) {
             k = CryptoUtils.loadPrivateKey(key, pass.toCharArray());
        }

        try (InputStream key = new FileInputStream("src/test/resources/SignTest.crt")) {
            c = CryptoUtils.loadCertificate(key);
        }

        val service =  new DefaultVerificationLinkService(new DefaultKsefApiProperties());

        val link = service.buildCertificateVerificationUrl(
                "1111111111",
                ContextIdentifierType.NIP,
                "1111111111",
                "111111111111",
                "wFtmN/WAHC87wjZ1Io413FdigZ+/0YgRIVFgmGKb7eU",
                k
                );

        System.out.println(link);

    }

    static class DefaultKsefApiProperties extends KsefApiProperties {
        @Override
        public String getBaseUri() {
            return Environment.TEST.getUrl();
        }

        @Override
        public Duration getRequestTimeout() {
            return null;
        }

        @Override
        public Map<String, String> getDefaultHeaders() {
            return Map.of();
        }
    }

}
