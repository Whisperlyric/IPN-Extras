package dev.whisperlyric.ipnextras;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for ReplayMod Preprocessor patterns.
 * Methods annotated with this can be used for automatic code transformation.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Pattern {
}