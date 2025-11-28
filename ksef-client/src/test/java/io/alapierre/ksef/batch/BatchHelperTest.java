package io.alapierre.ksef.batch;

import io.alapierre.ksef.batch.model.BatchConfig;
import io.alapierre.ksef.batch.model.BatchPartInfo;
import io.alapierre.ksef.batch.model.BatchResult;
import lombok.val;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.*;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.KsefApiProperties;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.builders.batch.OpenBatchSessionRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.CryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.auth.AuthStatus;
import pl.akmf.ksef.sdk.client.model.auth.ContextIdentifier;
import pl.akmf.ksef.sdk.client.model.session.SchemaVersion;
import pl.akmf.ksef.sdk.client.model.session.SessionStatusResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionValue;
import pl.akmf.ksef.sdk.client.model.session.SystemCode;
import pl.akmf.ksef.sdk.client.model.session.batch.OpenBatchSessionRequest;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
public class BatchHelperTest {

    private HttpClient apiClient;
    private BatchHelper helper;
    private KSeFClient ksefClient;
    private CryptographyService cryptographyService;

    @Before
    public void setUp() {
        apiClient = createHttpBuilder().build();
        ExampleApiProperties exampleApiProperties = new ExampleApiProperties();
        ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties);
        cryptographyService = new DefaultCryptographyService(ksefClient);
        helper = new BatchHelper(cryptographyService, ksefClient);

    }

    @After
    public void tearDown() {
        if (apiClient != null) {
            apiClient.close();
        }
    }

    @Test
    public void createZip() {
        val result = helper.createZipWithHashes(new MockInvoiceSource("/xml/invoice-template.xml", "1234567890", 100));

        if(result.file() != null) {
            val f = result.file();
            System.out.println(f.getAbsolutePath());
            //f.delete();
        }
    }

    @Test
    public void testPrepare() throws Exception {

        Path outputDir = null;
        BatchResult res = null;

        try {
            val source = new MockInvoiceSource("/xml/invoice-template.xml", "1234567890", 100);
            outputDir = Files.createTempDirectory("invoices_");
            val config = new BatchConfig(
                    outputDir,
                    1024 * 100,
                    true
            );

            res = helper.prepareBatch(source, config);
            System.out.println(config.outputDir());
        } finally {
            if(res != null) {
                helper.removeEncryptedParts(res);
            }
            if(outputDir != null) {
                outputDir.toFile().delete();
            }
        }
    }

    @Test
    @Ignore("Test integracyjny - wymaga tokena i NIP")
    public void testSend() throws Exception {

        final String KSEF_TOKEN = System.getenv("KSEF_TOKEN");
        Objects.requireNonNull(KSEF_TOKEN, "KSEF_TOKEN env variable is not set");
        final String NIP = System.getenv("KSEF_NIP");
        Objects.requireNonNull(NIP, "KSEF_NIP env variable is not set");

        Path outputDir = null;
        BatchResult batchResult = null;

        try {
            val source = new MockInvoiceSource("/xml/invoice-template.xml", NIP, 100);
            outputDir = Files.createTempDirectory("invoices_");
            val config = new BatchConfig(
                    outputDir,
                    1024 * 100,
                    false
            );

            batchResult = helper.prepareBatch(source, config);
            System.out.println(config.outputDir());

            val authToken = auth(KSEF_TOKEN, NIP);
            val session = helper.sendBatch(batchResult, authToken);

            System.out.println("waiting before close session...");
            Thread.sleep(100);

            ksefClient.closeBatchSession(session.getReferenceNumber(), authToken);

            SessionStatusResponse status;
            int retry = 0;
            do {
                System.out.println("waiting...");
                Thread.sleep(2000);
                status = ksefClient.getSessionStatus(session.getReferenceNumber(), authToken);
                System.out.println("Current status: " + status.getStatus().getCode());
                retry++;
            } while (status.getStatus().getCode() != 200 && retry < 30); // Max 60 sec

            Assert.assertNotNull("Timeout", status);
            int code = status.getStatus().getCode();
            Assert.assertEquals("Oczekiwany status przetworzonej sesji wsadowej to 200", 200, code);

        } finally {
            if(batchResult != null) {
                helper.removeEncryptedParts(batchResult);
            }
            if(outputDir != null) {
                outputDir.toFile().delete();
            }
        }
    }

    public static HttpClient.Builder createHttpBuilder() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .executor(ForkJoinPool.commonPool());
    }

    protected String auth(String t, String n) throws Exception {

        var challenge = ksefClient.getAuthChallenge();

        var token = cryptographyService.encryptKsefTokenWithRSAUsingPublicKey(
                t,
                challenge.getTimestamp());

        var a = ksefClient.authenticateByKSeFToken(
                new AuthKsefTokenRequestBuilder()
                        .withChallenge(challenge.getChallenge())
                        .withContextIdentifier(new ContextIdentifier(ContextIdentifier.IdentifierType.NIP, n))
                        .withEncryptedToken(Base64.getEncoder().encodeToString(token))
                        .build());

        AuthStatus authStatus;

        do {
            authStatus = ksefClient.getAuthStatus(a.getReferenceNumber(), a.getAuthenticationToken().getToken());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Can't wait");
                System.exit(1);
            }

        } while (authStatus.getStatus().getCode() != 200);

        val oauth = ksefClient.redeemToken(a.getAuthenticationToken().getToken());

        return oauth.getAccessToken().getToken();
    }

    private static class ExampleApiProperties extends KsefApiProperties {

        @Override
        public String getBaseUri() {
            return "https://ksef-test.mf.gov.pl";
        }

        @Override
        public Duration getRequestTimeout() {
            return Duration.ofSeconds(5);
        }

        @Override
        public Map<String, String> getDefaultHeaders() {
            Map<String, String> headers = new HashMap<>();
            return headers;
        }
    }

    static String toStringBuilder(Object o) {
        ReflectionToStringBuilder b = new ReflectionToStringBuilder(o);
        return b.build();
    }

}

