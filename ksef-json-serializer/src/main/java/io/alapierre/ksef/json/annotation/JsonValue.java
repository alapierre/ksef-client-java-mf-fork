package io.alapierre.ksef.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public, zero-argument enum method returning its JSON scalar value.
 * Decoding uses {@link JsonCreator} when present, otherwise it matches these
 * values against enum constants. Unknown values must fail decoding.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonValue {
}
