/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
	 * The configuration category for this field.
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

	/**
	 * Whether or not this field should be shown on configuration GUIs.
	 */
	boolean showInGui() default true;

	/**
	 * The language key, used in configuration GUIs. Leave empty if the default should be used: modID.configgui.key
	 */
	String langKey() default "";

	/**
	 * Whether or not this field can be edited while a world is running.
	 */
	boolean requiresWorldRestart() default false;

	/**
	 * Whether or not this field requires Minecraft to be restarted when changed.
	 * Setting this flag to true will also disable editing of this field while a world is running.
	 */
	boolean requiresMcRestart() default false;

}
