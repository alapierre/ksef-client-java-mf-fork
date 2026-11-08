package io.alapierre.ksef.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.Strictness;
import com.google.gson.ToNumberPolicy;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import io.alapierre.ksef.json.JsonType;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Gson adapter for the library-neutral KSeF JSON contract. */
public final class GsonJsonSerializer implements JsonSerializer {
    private final Gson gson;

    /** Creates an adapter with null properties, precise untyped numbers and ISO dates. */
    public GsonJsonSerializer() {
        this(new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .disableJdkUnsafe()
                .addReflectionAccessFilter(ReflectionAccessFilter.BLOCK_ALL_PLATFORM)
                .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
                .create());
    }

    /**
     * Copies the supplied Gson configuration, adding neutral annotation, time
     * and Base64 support. These wire mappings and strict JSON syntax take
     * precedence over supplied settings; other custom adapters and policies are
     * retained. The original Gson instance is unchanged.
     *
     * @param gson application configuration to copy
     */
    public GsonJsonSerializer(Gson gson) {
        Objects.requireNonNull(gson, "gson");
        this.gson = gson.newBuilder()
                .setStrictness(Strictness.STRICT)
                .setFieldNamingStrategy(new KsefFieldNamingStrategy(gson.fieldNamingStrategy()))
                .registerTypeAdapterFactory(new KsefTypeAdapterFactory())
                .create();
    }

    @Override
    public byte[] toJsonBytes(Object value) throws JsonSerializationException {
        try {
            return gson.toJson(value).getBytes(StandardCharsets.UTF_8);
        } catch (RuntimeException e) {
            throw new JsonSerializationException("Cannot serialize JSON", e);
        }
    }

    @Override
    public <T> T fromJson(byte[] json, JsonType<T> type) throws JsonSerializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        try {
            String text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(json)).toString();
            try (JsonReader reader = gson.newJsonReader(new StringReader(text))) {
                if (reader.peek() == JsonToken.END_DOCUMENT) {
                    throw new JsonSyntaxException("Expected a JSON value");
                }
                T result = gson.fromJson(reader, type.getType());
                if (reader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonSyntaxException("Unexpected data after JSON value");
                }
                return result;
            }
        } catch (IOException | RuntimeException e) {
            throw new JsonSerializationException("Cannot deserialize JSON", e);
        }
    }

    @Override
    public String name() {
        return "gson";
    }
}
