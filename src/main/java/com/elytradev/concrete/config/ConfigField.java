/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	William Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
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

import java.lang.reflect.Field;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

class ConfigField {
	private final ConcreteConfig concreteConfig;

	private final Field field;
	private final boolean isArray;
	private final Object defaultValue;

	private final String key;
	private final String comment;
	private final String category;
	private final Property.Type type;
	private final boolean showInGui;
	private final String langKey;
	private final boolean requiresWorldRestart;
	private final boolean requiresMcRestart;

	public ConfigField(ConcreteConfig concreteConfig, Field field) throws IllegalAccessException {
		field.setAccessible(true);

		this.concreteConfig = concreteConfig;

		this.field = field;
		isArray = field.getType().isArray();
		defaultValue = field.get(concreteConfig);

		ConfigValue cfgValue = field.getAnnotation(ConfigValue.class);
		comment = cfgValue.comment();
		category = cfgValue.category();
		type = cfgValue.type();
		showInGui = cfgValue.showInGui();
		requiresWorldRestart = cfgValue.requiresWorldRestart();
		requiresMcRestart = cfgValue.requiresMcRestart();

		if (!cfgValue.key().isEmpty()) {
			key = cfgValue.key();
		} else {
			key = field.getName();
		}

		if (!cfgValue.langKey().isEmpty()) {
			langKey = cfgValue.langKey();
		} else {
			langKey = key;
		}
	}

	public void load() throws IllegalAccessException {
		Configuration configuration = concreteConfig.getConfiguration();
		Property property = null;

		if (isArray) {
			switch (type) {
				case INTEGER: {
					property = configuration.get(category, key, (int[]) defaultValue, comment);
					field.set(concreteConfig, property.getIntList());
					break;
				}
				case BOOLEAN: {
					property = configuration.get(category, key, (boolean[]) defaultValue, comment);
					field.set(concreteConfig, property.getBooleanList());
					break;
				}
				case DOUBLE: {
					property = configuration.get(category, key, (double[]) defaultValue, comment);
					field.set(concreteConfig, property.getDoubleList());
					break;
				}
				case STRING: {
					property = configuration.get(category, key, (String[]) defaultValue, comment);
					field.set(concreteConfig, property.getStringList());
					break;
				}
			}
		} else {
			switch (type) {
				case INTEGER: {
					property = configuration.get(category, key, (Integer) defaultValue, comment);
					field.set(concreteConfig, property.getInt());
					break;
				}
				case BOOLEAN: {
					property = configuration.get(category, key, (Boolean) defaultValue, comment);
					field.set(concreteConfig, property.getBoolean());
					break;
				}
				case DOUBLE: {
					property = configuration.get(category, key, (Double) defaultValue, comment);
					field.set(concreteConfig, property.getDouble());
					break;
				}
				case STRING: {
					property = configuration.get(category, key, (String) defaultValue, comment);
					field.set(concreteConfig, property.getString());
					break;
				}
			}
		}

		if (property != null) {
			property.setShowInGui(showInGui);
			property.setLanguageKey(langKey);

			if (requiresMcRestart) {
				property.setRequiresMcRestart(true);
			} else if (requiresWorldRestart) {
				property.setRequiresWorldRestart(true);
			}
		}
	}
}
