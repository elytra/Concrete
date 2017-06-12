package com.elytradev.concrete.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on fields in a ConcreteConfig implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigValue {

	/**
	 * The configuration comment for this field, provides information to end users.
	 * There's very few cases where you shouldn't include this.
	 */
	String comment() default "";

	/**
	 * The configuration category for this field. Leave empty if not applicable.
	 */
	String category() default Configuration.CATEGORY_GENERAL;

	/**
	 * The field key, the 'name' used in the configuration file. Leave empty if the field name should be used.
	 */
	String key() default "";

	/**
	 * The property type for this config field. Determines how the field will be serialized.
	 */
	Property.Type type();

}
