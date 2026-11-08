package io.alapierre.ksef.batch;

import io.alapierre.ksef.json.jackson.JacksonJsonSerializer;
import io.alapierre.ksef.json.jackson.KsefJsonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.alapierre.ksef.batch.model.BatchConfig;
import io.alapierre.ksef.batch.model.BatchResult;
import lombok.val;
import org.junit.*;
import pl.akmf.ksef.sdk.api.DefaultKsefClient;
import pl.akmf.ksef.sdk.api.KsefApiProperties;
import pl.akmf.ksef.sdk.api.builders.auth.AuthKsefTokenRequestBuilder;
import pl.akmf.ksef.sdk.api.services.DefaultCryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.CryptographyService;
import pl.akmf.ksef.sdk.client.interfaces.KSeFClient;
import pl.akmf.ksef.sdk.client.model.auth.AuthStatus;
import pl.akmf.ksef.sdk.client.model.auth.ContextIdentifier;
import pl.akmf.ksef.sdk.client.model.session.*;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
public class BatchSendingLiveIT {

    private HttpClient apiClient;
    private BatchHelper helper;
    private KSeFClient ksefClient;
    private CryptographyService cryptographyService;

    @Before
    public void setUp() {
        requireEnvironment("KSEF_TOKEN");
        requireEnvironment("KSEF_NIP");
        apiClient = createHttpBuilder().build();
        ExampleApiProperties exampleApiProperties = new ExampleApiProperties();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new KsefJsonModule()).registerModule(new JavaTimeModule());
        ksefClient = new DefaultKsefClient(apiClient, exampleApiProperties, new JacksonJsonSerializer(objectMapper));
        cryptographyService = new DefaultCryptographyService(ksefClient);
        helper = new BatchHelper(cryptographyService, ksefClient);

    }

    @After
    public void tearDown() {
        if (apiClient != null) {
            apiClient.close();
        }
    }

    @Test(timeout = 180_000)
    public void sendsOneInvoiceAndWaitsForSuccessfulProcessing() throws Exception {
        String token = requireEnvironment("KSEF_TOKEN");
        String nip = requireEnvironment("KSEF_NIP");

        Path outputDir = null;
        BatchResult batchResult = null;

        try {
            val source = new MockInvoiceSource("/xml/invoice-template.xml", nip, 1);
            outputDir = Files.createTempDirectory("invoices_");
            val config = new BatchConfig(
                    outputDir,
                    1024 * 100,
                    true
            );

            batchResult = helper.prepareBatch(source, config);
            val authToken = auth(token, nip);
            val session = helper.sendBatch(batchResult, authToken, new FormCode(SystemCode.FA_2, SchemaVersion.VERSION_1_0E, SessionValue.FA));

            ksefClient.closeBatchSession(session.getReferenceNumber(), authToken);

            SessionStatusResponse status;
            int retry = 0;
            do {
                Thread.sleep(2000);
                status = ksefClient.getSessionStatus(session.getReferenceNumber(), authToken);
                if (status.getStatus().getCode() >= 400) {
                    Assert.fail("Batch processing failed: " + status.getStatus().getCode());
                }
                retry++;
            } while (status.getStatus().getCode() != 200 && retry < 30); // Max 60 sec

            Assert.assertNotNull("Timeout", status);
            int code = status.getStatus().getCode();
            Assert.assertEquals("Oczekiwany status przetworzonej sesji wsadowej to 200", 200, code);
            Assert.assertEquals(Integer.valueOf(1), status.getInvoiceCount());
            Assert.assertEquals(Integer.valueOf(1), status.getSuccessfulInvoiceCount());
            Assert.assertEquals(Integer.valueOf(0), status.getFailedInvoiceCount());

        } finally {
            if(batchResult != null) {
                helper.removeEncryptedParts(batchResult);
            }
            if(outputDir != null) {
                Files.deleteIfExists(outputDir);
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

        for (int attempt = 0; attempt < 30; attempt++) {
            authStatus = ksefClient.getAuthStatus(a.getReferenceNumber(), a.getAuthenticationToken().getToken());

            if (authStatus.getStatus().getCode() == 200) {
                return ksefClient.redeemToken(a.getAuthenticationToken().getToken())
                        .getAccessToken().getToken();
            }
            if (authStatus.getStatus().getCode() >= 400) {
                throw new AssertionError("Authentication failed: " + authStatus.getStatus().getCode());
            }
            Thread.sleep(1000);
        }
        throw new AssertionError("Authentication did not complete within 30 attempts");
    }

    private static String requireEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be set when running the ksef-live profile");
        }
        return value;
    }

    private static class ExampleApiProperties extends KsefApiProperties {

        @Override
        public String getBaseUri() {
            return "https://api-test.ksef.mf.gov.pl/v2";
        }

        @Override
        public String getSuffixUri() {
            return "";
        }

        @Override
        public String getQrUri() {
            return "";
        }

        @Override
        public Duration getRequestTimeout() {
            return Duration.ofSeconds(5);
        }

        @Override
        public Map<String, String> getDefaultHeaders() {
            return Map.of();
        }
    }

}
