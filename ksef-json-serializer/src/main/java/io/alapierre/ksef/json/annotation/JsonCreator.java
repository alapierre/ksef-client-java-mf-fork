package io.alapierre.ksef.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public static enum factory accepting one JSON scalar value.
 * Adapters must call this factory when decoding, preserving its validation and
 * alias handling. A factory failure is a JSON decoding failure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonCreator {
}
