package io.alapierre.ksef.json.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.JsonType;
import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonName;
import io.alapierre.ksef.json.annotation.JsonValue;
import org.junit.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GsonJsonSerializerTest {
    private final JsonSerializer serializer = new GsonJsonSerializer();

    @Test
    public void readsNestedTypesWithTimesAndNeutralEnumMappings() throws Exception {
        Map<String, List<Payload>> result = serializer.fromJson("""
                {"items":[{"wireName":"first","validTo":"2026-11-08T12:00:00.123456789+02:00","future":true}]}
                """, new JsonType<>() { });
        assertEquals(Kind.FIRST, result.get("items").getFirst().kind);
        assertEquals(OffsetDateTime.parse("2026-11-08T12:00:00.123456789+02:00"), result.get("items").getFirst().validTo);
        assertEquals("\"first\"", serializer.toJson(Kind.ALIAS));
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson("\"invalid\"", Kind.class));
    }

    @Test
    public void copiesUserConfigurationAndKeepsUnrelatedCustomAdapters() throws Exception {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(CustomValue.class, new TypeAdapter<CustomValue>() {
                    public void write(JsonWriter writer, CustomValue value) throws IOException { writer.value(value.text()); }
                    public CustomValue read(JsonReader reader) throws IOException { return new CustomValue(reader.nextString()); }
                }).create();
        JsonSerializer configured = new GsonJsonSerializer(gson);
        NamedBean bean = new NamedBean();
        bean.javaName = "value";
        bean.otherName = "other";
        assertEquals("{\"wireName\":\"value\",\"other_name\":\"other\"}", configured.toJson(bean));
        assertEquals("{\"java_name\":\"value\",\"other_name\":\"other\"}", gson.toJson(bean));
        assertEquals("\"custom\"", configured.toJson(new CustomValue("custom")));
        assertEquals(new CustomValue("custom"), configured.fromJson("\"custom\"", CustomValue.class));
        assertEquals("{}", configured.toJson(new NamedBean()));
    }

    @Test
    public void includesNullsInDefaultConfiguration() throws Exception {
        assertEquals("{\"wireName\":null,\"otherName\":null}", serializer.toJson(new NamedBean()));
        assertEquals("null", serializer.toJson(null));
        assertNull(serializer.fromJson("null", NamedBean.class));
    }

    @Test
    public void normalizesSyntaxDateAndReflectionFailures() {
        for (String json : List.of("", "  ", "{", "{} true", "null true", "/* comment */ {}", "{unquoted:true}")) {
            var error = assertThrows(JsonSerializationException.class, () -> serializer.fromJson(json, NamedBean.class));
            assertNotNull(error.getCause());
        }
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson("\"yesterday\"", OffsetDateTime.class));
        assertThrows(JsonSerializationException.class, () -> serializer.toJson(new Thread()));
        assertThrows(NullPointerException.class, () -> serializer.fromJson((byte[]) null, NamedBean.class));
    }

    @Test
    public void handlesNumericEnumValuesAndRejectsUnknownValues() throws Exception {
        assertEquals("7", serializer.toJson(NumericCode.VALUE));
        assertEquals(NumericCode.VALUE, serializer.fromJson("7", NumericCode.class));
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson("8", NumericCode.class));
    }

    @Test
    public void rejectsConflictingFieldAndAccessorNames() {
        assertThrows(JsonSerializationException.class, () -> serializer.toJson(new ConflictingName()));
    }

    @Test
    public void handlesPublicMappingsOnNonPublicEnums() throws Exception {
        assertEquals("\"internal\"", serializer.toJson(InternalCode.VALUE));
        assertEquals(InternalCode.VALUE, serializer.fromJson("\"internal\"", InternalCode.class));
    }

    private enum InternalCode {
        VALUE;
        @JsonValue
        public String wireValue() { return "internal"; }
        @JsonCreator
        public static InternalCode fromWireValue(String value) {
            if ("internal".equals(value)) { return VALUE; }
            throw new IllegalArgumentException("Unknown code");
        }
    }

    public static class Payload {
        @JsonName("wireName")
        public Kind kind;
        public OffsetDateTime validTo;
    }

    public static class NamedBean {
        @JsonName("wireName")
        public String javaName;
        public String otherName;
    }

    public static class ConflictingName {
        @JsonName("one")
        private String value;
        @JsonName("two")
        public String getValue() { return value; }
    }

    public record CustomValue(String text) { }

    public enum Kind {
        FIRST, ALIAS;
        @JsonValue
        public String wireValue() { return "first"; }
        @JsonCreator
        public static Kind fromWireValue(String value) {
            if ("first".equals(value)) { return FIRST; }
            throw new IllegalArgumentException("Unknown kind");
        }
    }

    public enum NumericCode {
        VALUE;
        @JsonValue
        public int wireValue() { return 7; }
    }
}
