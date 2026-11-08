package io.alapierre.ksef.json.gson;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.JsonIOException;
import io.alapierre.ksef.json.annotation.JsonName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Resolves neutral names on fields and their public bean accessors. */
final class KsefFieldNamingStrategy implements FieldNamingStrategy {
    private final FieldNamingStrategy fallback;

    KsefFieldNamingStrategy(FieldNamingStrategy fallback) {
        this.fallback = fallback;
    }

    @Override
    public String translateName(Field field) {
        JsonName annotation = field.getAnnotation(JsonName.class);
        String name = annotation == null ? null : annotation.value();
        for (Method method : field.getDeclaringClass().getMethods()) {
            JsonName accessor = method.getAnnotation(JsonName.class);
            if (accessor == null || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            String property = propertyName(method);
            boolean matches = field.getName().equals(property)
                    || ((field.getType() == boolean.class || field.getType() == Boolean.class)
                    && property != null && field.getName().equals("is" + capitalize(property)));
            if (matches) {
                if (name != null && !name.equals(accessor.value())) {
                    throw new JsonIOException("Conflicting JsonName annotations for " + field);
                }
                name = accessor.value();
            }
        }
        return name == null ? fallback.translateName(field) : name;
    }

    private static String propertyName(Method method) {
        String name = method.getName();
        String suffix;
        if (name.startsWith("get") && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
            suffix = name.substring(3);
        } else if (name.startsWith("set") && method.getParameterCount() == 1) {
            suffix = name.substring(3);
        } else if (name.startsWith("is") && method.getParameterCount() == 0
                && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
            suffix = name.substring(2);
        } else {
            return null;
        }
        if (suffix.isEmpty()) {
            return null;
        }
        return suffix.length() > 1 && Character.isUpperCase(suffix.charAt(0)) && Character.isUpperCase(suffix.charAt(1))
                ? suffix : Character.toLowerCase(suffix.charAt(0)) + suffix.substring(1);
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
