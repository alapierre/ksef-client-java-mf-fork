package io.alapierre.ksef.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonName;
import io.alapierre.ksef.json.annotation.JsonValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class KsefJsonModuleTest {
    @Test
    public void mapsNeutralNamesAndEnumFactoriesWithoutMutatingCallerMapper() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Payload payload = new Payload();
        payload.setKind(Kind.ALIAS);
        assertEquals("{\"kind\":\"ALIAS\"}", mapper.writeValueAsString(payload));
        var moduleIds = mapper.getRegisteredModuleIds();

        JsonSerializer serializer = new JacksonJsonSerializer(mapper);
        assertEquals("{\"wireName\":\"first\"}", serializer.toJson(payload));
        assertEquals(Kind.FIRST, serializer.fromJson("{\"wireName\":\"first\"}", Payload.class).getKind());
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson("\"invalid\"", Kind.class));
        assertEquals(moduleIds, mapper.getRegisteredModuleIds());
        assertEquals("{\"kind\":\"ALIAS\"}", mapper.writeValueAsString(payload));
    }

    @Test
    public void readsEnumValuesWithoutAnExplicitFactory() throws Exception {
        JsonSerializer serializer = new JacksonJsonSerializer();
        assertEquals("\"01\"", serializer.toJson(Code.FAILURE));
        assertEquals(Code.FAILURE, serializer.fromJson("\"01\"", Code.class));
        assertThrows(JsonSerializationException.class, () -> serializer.fromJson("\"99\"", Code.class));
    }

    @Test
    public void allowsDirectMapperUseWithExplicitModuleRegistration() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new KsefJsonModule());
        assertEquals(Kind.FIRST, mapper.readValue("{\"wireName\":\"first\"}", Payload.class).getKind());
    }

    public static class Payload {
        @JsonName("wireName")
        private Kind kind;

        public Kind getKind() { return kind; }
        public void setKind(Kind kind) { this.kind = kind; }
    }

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

    public enum Code {
        FAILURE;

        @JsonValue
        public String wireValue() { return "01"; }
    }
}
