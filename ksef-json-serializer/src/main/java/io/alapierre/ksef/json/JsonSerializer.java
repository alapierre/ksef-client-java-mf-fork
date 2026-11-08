package io.alapierre.ksef.json;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * JSON boundary used by the HTTP clients, independent of the JSON library.
 * Implementations must be safe to share between concurrent requests.
 *
 * <p>Input and output bytes use UTF-8. Serializing Java {@code null} produces
 * JSON {@code null}; deserializing JSON {@code null} returns Java {@code null}
 * for reference types. Null input buffers are programming errors. Empty,
 * whitespace-only, malformed, trailing or incompatible JSON must cause a
 * {@link JsonSerializationException}, rather than an absent result.
 * The HTTP client decides whether a response body needs to be parsed.
 *
 * <p>Adapters must preserve KSeF property names, enum values and date formats,
 * including the mappings declared by {@code io.alapierre.ksef.json.annotation}
 * ({@code JsonName}, {@code JsonValue} and {@code JsonCreator}).
 * Byte arrays use standard Base64 strings, not arrays of JSON numbers.
 * Default adapters encode OffsetDateTime (preserving its offset), Instant,
 * LocalDate and Duration as ISO-8601 strings. Decimal values must retain their
 * precision; concrete Number subtypes in untyped maps are implementation-specific.
 * Unknown-property handling is adapter configuration; bundled adapters ignore
 * unknown properties by default for API forward compatibility.
 */
public interface JsonSerializer {

    byte[] toJsonBytes(Object value) throws JsonSerializationException;

    <T> T fromJson(byte[] json, JsonType<T> type) throws JsonSerializationException;

    default <T> T fromJson(byte[] json, Class<T> type) throws JsonSerializationException {
        return fromJson(json, JsonType.of(type));
    }

    default String toJson(Object value) throws JsonSerializationException {
        return new String(toJsonBytes(value), StandardCharsets.UTF_8);
    }

    default <T> T fromJson(String json, Class<T> type) throws JsonSerializationException {
        return fromJson(Objects.requireNonNull(json, "json").getBytes(StandardCharsets.UTF_8), type);
    }

    default <T> T fromJson(String json, JsonType<T> type) throws JsonSerializationException {
        return fromJson(Objects.requireNonNull(json, "json").getBytes(StandardCharsets.UTF_8), type);
    }

    String name();
}
