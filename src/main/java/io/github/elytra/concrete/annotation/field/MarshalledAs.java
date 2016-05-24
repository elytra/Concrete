package io.github.elytra.concrete.annotation.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the wire format of a Message field. Required for primitives.
 * Non-primitives will have a Marshaller decided automatically, or will be
 * serialized using the methods on a Marshallable.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarshalledAs {}
