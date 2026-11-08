package io.alapierre.ksef.json;

import io.alapierre.ksef.json.jackson.JacksonJsonSerializer;
import io.alapierre.ksef.json.gson.GsonJsonSerializer;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificate;
import pl.akmf.ksef.sdk.client.model.certificate.publickey.PublicKeyCertificateUsage;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataInvoiceType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceExportStatus;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryFilters;
import pl.akmf.ksef.sdk.client.model.limit.GetRateLimitResponse;
import pl.akmf.ksef.sdk.client.model.testdata.TestDataPermission;
import pl.akmf.ksef.sdk.client.model.certificate.SendCertificateEnrollmentRequest;
import io.alapierre.ksef.json.annotation.JsonName;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class KsefJsonContractTest {
    @Parameterized.Parameters(name = "{0}")
    public static Object[][] serializers() {
        return new Object[][]{{"jackson", new JacksonJsonSerializer()}, {"gson", new GsonJsonSerializer()}};
    }

    private final JsonSerializer serializer;

    public KsefJsonContractTest(String name, JsonSerializer serializer) {
        this.serializer = serializer;
    }


    @Test
    public void preservesApiPropertyNamesInBothDirections() throws Exception {
        InvoiceQueryFilters filters = serializer.fromJson("{\"isSelfInvoicing\":true}", InvoiceQueryFilters.class);
        assertEquals(Boolean.TRUE, filters.getSelfInvoicing());
        Map<String, Object> json = serializer.fromJson(serializer.toJsonBytes(filters), new JsonType<>() { });
        assertEquals(Boolean.TRUE, json.get("isSelfInvoicing"));
        assertFalse(json.containsKey("selfInvoicing"));

        GetRateLimitResponse limits = serializer.fromJson(
                "{\"invoiceExportStatus\":{\"perSecond\":7,\"perMinute\":40,\"perHour\":200}}", GetRateLimitResponse.class);
        assertEquals(7, limits.getInvoiceStatusExport().getPerSecond());
        json = serializer.fromJson(serializer.toJson(limits), new JsonType<>() { });
        assertTrue(json.containsKey("invoiceExportStatus"));
        assertFalse(json.containsKey("invoiceStatusExport"));
    }

    @Test
    public void usesWireEnumValuesIncludingLegacyAlias() throws Exception {
        assertEquals("\"VatPefSp\"", serializer.toJson(InvoiceMetadataInvoiceType.VAT_PEF_SP));
        assertEquals(InvoiceMetadataInvoiceType.VAT_PEF_SP,
                serializer.fromJson("\"VatPefSp\"", InvoiceMetadataInvoiceType.class));
        assertEquals("\"KorVatRr\"", serializer.toJson(InvoiceMetadataInvoiceType.KOR_VAT_RR));
        assertEquals(InvoiceMetadataInvoiceType.KOR_VAT_SP,
                serializer.fromJson("\"KorVatRr\"", InvoiceMetadataInvoiceType.class));
        assertThrows(JsonSerializationException.class,
                () -> serializer.fromJson("\"not-an-invoice-type\"", InvoiceMetadataInvoiceType.class));
    }

    @Test
    public void decodesGenericCollectionsWithDatesAndEnums() throws Exception {
        List<PublicKeyCertificate> certificates = serializer.fromJson("""
                [{"certificate":"base64","publicKeyId":"key-1",
                  "validFrom":"2026-10-01T12:34:56.123456789+02:00",
                  "usage":["KsefTokenEncryption","SymmetricKeyEncryption"],"futureField":true}]
                """, new JsonType<List<PublicKeyCertificate>>() { });
        assertEquals("key-1", certificates.getFirst().getPublicKeyId());
        assertEquals(OffsetDateTime.parse("2026-10-01T12:34:56.123456789+02:00"), certificates.getFirst().getValidFrom());
        assertEquals(List.of(PublicKeyCertificateUsage.KSEFTOKENENCRYPTION,
                PublicKeyCertificateUsage.SYMMETRICKEYENCRYPTION), certificates.getFirst().getUsage());

        Map<String, List<PublicKeyCertificate>> nested = serializer.fromJson(
                "{\"keys\":[{\"certificateId\":\"cert-1\"}]}", new JsonType<>() { });
        assertEquals("cert-1", nested.get("keys").getFirst().getCertificateId());
    }

    @Test
    public void keepsPackageAliasInExportResponses() throws Exception {
        InvoiceExportStatus status = serializer.fromJson("{\"package\":{}}", InvoiceExportStatus.class);
        assertNotNull(status.getPackageParts());
        Map<String, Object> json = serializer.fromJson(serializer.toJson(status), new JsonType<>() { });
        assertTrue(json.containsKey("package"));
        assertFalse(json.containsKey("packageParts"));
    }

    @Test
    public void keepsPermissionAliasAndNestedEnumWireValues() throws Exception {
        var permission = new TestDataPermission("Read credentials", TestDataPermission.PermissionType.CREDENTIAL_READ);
        Map<String, Object> json = serializer.fromJson(serializer.toJson(permission), new JsonType<>() { });
        assertEquals("CredentialsRead", json.get("permissionType"));
        assertFalse(json.containsKey("permission"));
        assertEquals(TestDataPermission.PermissionType.CREDENTIAL_READ,
                serializer.fromJson("\"CredentialsRead\"", TestDataPermission.PermissionType.class));
    }

    @Test
    public void encodesCertificateBytesAsBase64() throws Exception {
        var request = new SendCertificateEnrollmentRequest();
        request.setCsr(new byte[]{0, 1, 2, (byte) 255});
        Map<String, Object> json = serializer.fromJson(serializer.toJson(request), new JsonType<>() { });
        assertEquals("AAEC/w==", json.get("csr"));
        assertArrayEquals(request.getCsr(), serializer.fromJson("{\"csr\":\"AAEC/w==\"}",
                SendCertificateEnrollmentRequest.class).getCsr());
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson("\"!bad-base64!\"", byte[].class));
    }

    @Test
    public void preservesDatesDurationsAndDecimalPrecision() throws Exception {
        var amount = new BigDecimal("12345678901234567890.123456789");
        assertEquals(amount, serializer.fromJson(serializer.toJson(amount), BigDecimal.class));
        Map<String, Object> amounts = serializer.fromJson("{\"amount\":12345678901234567890.123456789}",
                new JsonType<>() { });
        // Untyped maps may use different Number subclasses, but must not lose digits.
        assertEquals(amount, new BigDecimal(amounts.get("amount").toString()));
        assertEquals("\"2026-11-08T12:00:00.123456789+02:00\"", serializer.toJson(
                OffsetDateTime.parse("2026-11-08T12:00:00.123456789+02:00")));
        assertEquals("\"2026-11-08T10:00:00.123456789Z\"", serializer.toJson(
                Instant.parse("2026-11-08T10:00:00.123456789Z")));
        assertEquals(LocalDate.of(2026, 11, 8), serializer.fromJson("\"2026-11-08\"", LocalDate.class));
        assertEquals("\"PT1.000000001S\"", serializer.toJson(Duration.ofSeconds(1, 1)));
        assertEquals(Duration.ofSeconds(1, 1), serializer.fromJson("\"PT1.000000001S\"", Duration.class));
    }

    @Test
    public void rejectsMissingMalformedAndTrailingJson() throws Exception {
        assertEquals("null", serializer.toJson(null));
        assertNull(serializer.fromJson("null", InvoiceQueryFilters.class));
        for (String json : List.of("", " \n\t", "{", "{} {}", "null true", "{unquoted:true}", "{\"x\":1,}", "[]")) {
            assertThrows(json, JsonSerializationException.class,
                    () -> serializer.fromJson(json, InvoiceQueryFilters.class));
        }
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson(new byte[]{'"', (byte) 0xff, '"'}, String.class));
        assertArrayEquals("\"Zażółć gęślą jaźń\"".getBytes(StandardCharsets.UTF_8), serializer.toJsonBytes("Zażółć gęślą jaźń"));
    }

    @Test
    public void handlesNamesDeclaredOnlyOnBeanAccessors() throws Exception {
        var bean = serializer.fromJson("{\"wireName\":\"text\"}", AccessorName.class);
        assertEquals("text", bean.getValue());
        assertEquals("{\"wireName\":\"text\"}", serializer.toJson(bean));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void preservesAllModelEnumValuesAndAliases() throws Exception {
        Path classes = Path.of(InvoiceQueryFilters.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        int types = 0;
        try (var paths = Files.walk(classes.resolve("pl/akmf/ksef/sdk/client/model"))) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".class")).toList()) {
                String name = classes.relativize(path).toString().replace(java.io.File.separatorChar, '.').replaceAll("\\.class$", "");
                Class<?> type = Class.forName(name);
                if (!type.isEnum()) { continue; }
                types++;
                java.lang.reflect.Method valueMethod = null;
                java.lang.reflect.Method creator = null;
                for (var method : type.getMethods()) {
                    if (method.isAnnotationPresent(io.alapierre.ksef.json.annotation.JsonValue.class)) { valueMethod = method; }
                    if (method.isAnnotationPresent(io.alapierre.ksef.json.annotation.JsonCreator.class)) { creator = method; }
                }
                for (Object constant : type.getEnumConstants()) {
                    Object value = valueMethod == null ? ((Enum<?>) constant).name() : valueMethod.invoke(constant);
                    assertEquals(name, serializer.toJson(value), serializer.toJson(constant));
                    Object expected = creator == null ? constant : creator.invoke(null, value);
                    assertEquals(name, expected, serializer.fromJson(serializer.toJson(value), type));
                }
                assertThrows(name, JsonSerializationException.class, () -> serializer.fromJson("\"__unknown__\"", type));
            }
        }
        assertTrue("Expected all KSeF enum models", types >= 97);
    }

    public static class AccessorName {
        private String value;
        @JsonName("wireName")
        public String getValue() { return value; }
        @JsonName("wireName")
        public void setValue(String value) { this.value = value; }
    }

}
