package io.alapierre.ksef.json.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.JsonType;

import java.io.IOException;
import java.util.Objects;

/** Jackson 2 adapter, including support for the library-neutral JSON annotations. */
public final class JacksonJsonSerializer implements JsonSerializer {
    private final ObjectMapper objectMapper;

    /**
     * Creates a mapper with ISO-8601 dates and durations, precise untyped decimals,
     * preserved offsets and tolerance for
     * unknown properties. Null properties are included and enum mappings follow
     * the KSeF model annotations. No polymorphic default typing is enabled.
     */
    public JacksonJsonSerializer() {
        this(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    /**
     * Copies the supplied mapper's configuration and installs {@link KsefJsonModule}.
     * The supplied mapper is not mutated; later changes to it do not affect this
     * serializer. Custom ObjectMapper subclasses must support {@code copy()}.
     * Reads always reject additional JSON values after the first document.
     *
     * @param objectMapper fully configured mapper, including any time modules
     */
    public JacksonJsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper").copy()
                .registerModule(new KsefJsonModule());
    }

    @Override
    public byte[] toJsonBytes(Object value) throws JsonSerializationException {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (IOException e) {
            throw new JsonSerializationException("Cannot serialize JSON", e);
        }
    }

    @Override
    public <T> T fromJson(byte[] json, JsonType<T> type) throws JsonSerializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        try {
            return objectMapper.readerFor(objectMapper.constructType(type.getType()))
                    .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                    .readValue(json);
        } catch (IOException e) {
            throw new JsonSerializationException("Cannot deserialize JSON", e);
        }
    }

    @Override
    public String name() {
        return "jackson";
    }
}
