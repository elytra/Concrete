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

import com.elytradev.concrete.common.ConcreteLog;
import com.elytradev.concrete.common.ShadingValidator;
import com.google.common.collect.Maps;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

/**
 * Configuration base class, uses annotated fields instead of direct configuration calls for configuration data.
 */
public abstract class ConcreteConfig {

	static {
		ShadingValidator.ensureShaded();
	}

	// The real configuration that all writing is done to.
	private final Configuration configuration;

	// A map of default values that are cached on first load.
	private Map<Field, Object> fieldToDefaultValue = null;

	private final String modID;

	/**
	 * Create a new configuration with the given file, using the file name as the mod ID.
	 *
	 * @param configFile
	 */
	@Deprecated
	protected ConcreteConfig(File configFile) {
		this(configFile, FilenameUtils.removeExtension(configFile.getName()));
	}

	/**
	 * Create a new configuration with the given file and register it to the Forge
	 * event bus to handle {@link OnConfigChangedEvent} with the given mod ID.
	 *
	 * @param configFile
	 * @param modID
	 */
	protected ConcreteConfig(File configFile, String modID) {
		this.configuration = new Configuration(configFile);
		this.modID = modID;
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Saves config values, and writes to disk.
	 */
	public void loadConfig() {
		if (fieldToDefaultValue == null) {
			fieldToDefaultValue = Maps.newHashMap();
			for (Class<?> cursor = this.getClass(); cursor != null && cursor != Object.class; cursor = cursor.getSuperclass()) {
				for (Field field : cursor.getDeclaredFields()) {
					if (field.isAnnotationPresent(ConfigValue.class)) {
						try {
							field.setAccessible(true);
							fieldToDefaultValue.put(field, field.get(this));
						} catch (IllegalAccessException e) {
							ConcreteLog.error("Failed to access field when loading a concrete configuration.", e);
						}
					}
				}
			}
		}

		for (Map.Entry<Field, Object> fieldEntry : fieldToDefaultValue.entrySet()) {
			Field field = fieldEntry.getKey();
			Object defaultValue = fieldEntry.getValue();

			ConfigValue cfgValue = field.getAnnotation(ConfigValue.class);
			String valueKey = cfgValue.key();
			String valueComment = cfgValue.comment();
			String valueCategory = cfgValue.category();
			String valueLangKey = cfgValue.langKey();
			boolean showValueInGui = cfgValue.showInGui();
			boolean valueRequiresMcRestart = cfgValue.requiresMcRestart();
			boolean valueRequiresWorldRestart = cfgValue.requiresWorldRestart();

			if (valueKey.isEmpty()) {
				valueKey = field.getName();
			}

			if (valueLangKey.isEmpty()) {
				valueLangKey = modID + ".configgui." + valueKey;
			}

			Property property = null;

			try {
				if (field.getType().isArray()) {
					switch (cfgValue.type()) {
						case INTEGER: {
							property = configuration.get(valueCategory, valueKey, (int[]) defaultValue, valueComment);
							field.set(this, property.getIntList());
							break;
						}
						case BOOLEAN: {
							property = configuration.get(valueCategory, valueKey, (boolean[]) defaultValue, valueComment);
							field.set(this, property.getBooleanList());
							break;
						}
						case DOUBLE: {
							property = configuration.get(valueCategory, valueKey, (double[]) defaultValue, valueComment);
							field.set(this, property.getDoubleList());
							break;
						}
						case STRING: {
							property = configuration.get(valueCategory, valueKey, (String[]) defaultValue, valueComment);
							field.set(this, property.getStringList());
							break;
						}
					}
				} else {
					switch (cfgValue.type()) {
						case INTEGER: {
							property = configuration.get(valueCategory, valueKey, (Integer) defaultValue, valueComment);
							field.set(this, property.getInt());
							break;
						}
						case BOOLEAN: {
							property = configuration.get(valueCategory, valueKey, (Boolean) defaultValue, valueComment);
							field.set(this, property.getBoolean());
							break;
						}
						case DOUBLE: {
							property = configuration.get(valueCategory, valueKey, (Double) defaultValue, valueComment);
							field.set(this, property.getDouble());
							break;
						}
						case STRING: {
							property = configuration.get(valueCategory, valueKey, (String) defaultValue, valueComment);
							field.set(this, property.getString());
							break;
						}
					}
				}
			} catch (IllegalAccessException e) {
				ConcreteLog.error("Failed to access field when loading a concrete configuration.", e);
			}

			if (property != null) {
				property.setShowInGui(showValueInGui);
				property.setLanguageKey(valueLangKey);
				property.setRequiresMcRestart(valueRequiresMcRestart);

				if (valueRequiresWorldRestart) {
					property.setRequiresWorldRestart(true);
				}
			}
		}

		for (String categoryName : configuration.getCategoryNames()) {
			ConfigCategory category = configuration.getCategory(categoryName);
			category.setLanguageKey(modID + ".configgui.ctgy." + categoryName);
		}

		configuration.save();
	}

	/**
	 * Get the Forge configuration that is written to, use if you need direct access.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Get this configuration's mod ID, used in config GUIs.
	 */
	public String getModID() {
		return modID;
	}

	/**
	 * Called on config changes, used to handle config GUI modifications.
	 */
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		// Check that the modID is ours before doing a reload.
		if (modID.equals(event.getModID())) {
			loadConfig();
		}
	}

}
