package io.alapierre.ksef.json.gson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Wire adapters for types used by KSeF models; independent of the model classes. */
final class KsefTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> raw = type.getRawType();
        if (raw == OffsetDateTime.class) {
            return (TypeAdapter<T>) stringAdapter(OffsetDateTime::parse,
                    value -> value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        if (raw == Instant.class) {
            return (TypeAdapter<T>) stringAdapter(Instant::parse, Instant::toString);
        }
        if (raw == LocalDate.class) {
            return (TypeAdapter<T>) stringAdapter(LocalDate::parse, LocalDate::toString);
        }
        if (raw == Duration.class) {
            return (TypeAdapter<T>) stringAdapter(Duration::parse, Duration::toString);
        }
        if (raw == byte[].class) {
            return (TypeAdapter<T>) stringAdapter(Base64.getDecoder()::decode, Base64.getEncoder()::encodeToString);
        }
        if (Enum.class.isAssignableFrom(raw) && raw != Enum.class) {
            Class<?> enumType = raw.isEnum() ? raw : raw.getSuperclass();
            return (TypeAdapter<T>) enumAdapter(gson, enumType);
        }
        return null;
    }

    private static <T> TypeAdapter<T> stringAdapter(Function<String, T> parse, Function<T, String> format) {
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter writer, T value) throws IOException {
                writer.value(format.apply(value));
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() != JsonToken.STRING) {
                    throw new JsonSyntaxException("Expected a JSON string at " + reader.getPath());
                }
                return parse.apply(reader.nextString());
            }
        }.nullSafe();
    }

    @SuppressWarnings("unchecked")
    private static TypeAdapter<Object> enumAdapter(Gson gson, Class<?> type) {
        Method valueMethod = annotatedMethod(type, JsonValue.class);
        Method creator = annotatedMethod(type, JsonCreator.class);
        if (valueMethod != null && (Modifier.isStatic(valueMethod.getModifiers())
                || valueMethod.getParameterCount() != 0 || valueMethod.getReturnType() == void.class)) {
            throw new JsonIOException("JsonValue requires a public instance method with no parameters: " + type.getName());
        }
        if (creator != null && (!Modifier.isStatic(creator.getModifiers())
                || creator.getParameterCount() != 1 || creator.getReturnType() != type)) {
            throw new JsonIOException("JsonCreator requires a public static enum factory with one parameter: " + type.getName());
        }
        TypeAdapter<Object> valueAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(
                valueMethod == null ? String.class : valueMethod.getGenericReturnType()));
        TypeAdapter<Object> inputAdapter = creator == null ? valueAdapter
                : (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(creator.getGenericParameterTypes()[0]));
        Map<Object, Object> constants = new HashMap<>();
        for (Object constant : type.getEnumConstants()) {
            Object value = wireValue(valueMethod, constant);
            if (value == null) {
                throw new JsonIOException("Enum wire values must not be null: " + type.getName());
            }
            constants.putIfAbsent(value, constant);
        }
        return new TypeAdapter<Object>() {
            @Override
            public void write(JsonWriter writer, Object value) throws IOException {
                valueAdapter.write(writer, wireValue(valueMethod, value));
            }

            @Override
            public Object read(JsonReader reader) throws IOException {
                Object value = inputAdapter.read(reader);
                Object constant = creator == null ? constants.get(value) : invoke(creator, null, value);
                if (constant == null) {
                    throw new JsonSyntaxException("Unknown enum value for " + type.getName());
                }
                return constant;
            }
        }.nullSafe();
    }

    private static Object wireValue(Method method, Object constant) {
        return method == null ? ((Enum<?>) constant).name() : invoke(method, constant);
    }

    private static Method annotatedMethod(Class<?> type, Class<? extends Annotation> annotation) {
        Method result = null;
        for (Method method : type.getMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                if (result != null) {
                    throw new JsonIOException("Multiple " + annotation.getSimpleName() + " methods on " + type.getName());
                }
                if (!method.trySetAccessible()) {
                    throw new JsonIOException("Cannot access enum mapping: " + method);
                }
                result = method;
            }
        }
        return result;
    }

    private static Object invoke(Method method, Object receiver, Object... arguments) {
        try {
            return method.invoke(receiver, arguments);
        } catch (InvocationTargetException e) {
            throw new JsonSyntaxException("Enum mapping failed: " + method, e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new JsonIOException("Cannot invoke enum mapping: " + method, e);
        }
    }
}
