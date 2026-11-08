package io.alapierre.ksef.json.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.JsonType;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JacksonJsonSerializerTest {
    private final JsonSerializer serializer = new JacksonJsonSerializer();

    @Test
    public void writesIsoDatesAndUtf8WithoutLosingPrecision() throws Exception {
        var date = OffsetDateTime.parse("2026-10-01T12:34:56.123456789+02:00");
        assertEquals("{\"validTo\":\"2026-10-01T12:34:56.123456789+02:00\"}",
                serializer.toJson(new DatePayload(date)));
        assertArrayEquals("\"Zażółć gęślą jaźń\"".getBytes(StandardCharsets.UTF_8),
                serializer.toJsonBytes("Zażółć gęślą jaźń"));
        assertEquals("Zażółć gęślą jaźń", serializer.fromJson("\"Zażółć gęślą jaźń\"", String.class));
    }

    @Test
    public void distinguishesJsonNullFromMissingAndInvalidInput() throws Exception {
        assertEquals("null", serializer.toJson(null));
        assertNull(serializer.fromJson("null", DatePayload.class));
        assertNull(serializer.fromJson("null", new JsonType<List<DatePayload>>() { }));
        for (String json : List.of("", " \n\t", "{", "[]", "{\"validTo\":\"yesterday\"}")) {
            var failure = assertThrows(JsonSerializationException.class,
                    () -> serializer.fromJson(json, DatePayload.class));
            assertNotNull(failure.getCause());
        }
        assertThrows(NullPointerException.class,
                () -> serializer.fromJson((byte[]) null, DatePayload.class));
    }

    @Test
    public void wrapsEncodingErrorsInNeutralException() {
        var failure = assertThrows(JsonSerializationException.class, () -> serializer.toJson(new Object()));
        assertNotNull(failure.getCause());
    }

    @Test
    public void suppliedMapperConfigurationIsNotChanged() throws Exception {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonSerializer custom = new JacksonJsonSerializer(mapper);
        assertEquals("{}", custom.toJson(new DatePayload(null)));
        assertThrows(JsonSerializationException.class,
                () -> custom.fromJson("{\"unknown\":true}", DatePayload.class));
        assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertThrows(JsonSerializationException.class,
                () -> custom.toJson(new DatePayload(OffsetDateTime.now())));
    }

    @Test
    public void readsNestedGenericTypesWithDatesAndUnknownProperties() throws Exception {
        Map<String, List<DatePayload>> values = serializer.fromJson("""
                {"dates":[{"validTo":"2026-10-01T12:34:56.123456789+02:00","futureField":true}]}
                """, new JsonType<>() { });
        assertEquals(OffsetDateTime.parse("2026-10-01T12:34:56.123456789+02:00"),
                values.get("dates").getFirst().validTo());
    }

    public record DatePayload(OffsetDateTime validTo) { }
}
