package io.alapierre.ksef.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Explicit JSON property name, used for both encoding and decoding.
 * A field and its bean accessors represent one property; the Java property name
 * must not also be emitted when this annotation supplies a different name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonName {
    String value();
}
