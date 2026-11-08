package io.alapierre.ksef.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Captures a JSON target type without exposing a parser-specific type token.
 * Use {@code new JsonType<List<MyDto>>() {}} for generic types or
 * {@link #of(Class)} for a class. The anonymous subclass must directly extend
 * {@code JsonType} with a concrete type, not an unresolved type variable.
 *
 * @param <T> deserialized value type
 */
public abstract class JsonType<T> {
    private final Type type;

    protected JsonType() {
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType parameterized)
                || parameterized.getRawType() != JsonType.class) {
            throw new IllegalArgumentException("JsonType must directly specify a type parameter");
        }
        this.type = parameterized.getActualTypeArguments()[0];
        requireConcreteType(type);
    }

    private JsonType(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public static <T> JsonType<T> of(Class<T> type) {
        return new JsonType<T>(type) { };
    }

    public final Type getType() {
        return type;
    }

    private static void requireConcreteType(Type type) {
        if (type instanceof Class<?>) {
            return;
        }
        if (type instanceof ParameterizedType parameterized) {
            if (parameterized.getOwnerType() != null) {
                requireConcreteType(parameterized.getOwnerType());
            }
            for (Type argument : parameterized.getActualTypeArguments()) {
                requireConcreteType(argument);
            }
            return;
        }
        if (type instanceof java.lang.reflect.GenericArrayType array) {
            requireConcreteType(array.getGenericComponentType());
            return;
        }
        throw new IllegalArgumentException("JsonType requires concrete types, without type variables or wildcards");
    }
}
