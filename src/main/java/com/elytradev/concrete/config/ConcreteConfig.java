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

import com.elytradev.concrete.common.ConcreteLog;
import com.elytradev.concrete.common.ShadingValidator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Configuration base class, uses annotated fields instead of direct configuration calls for configuration data.
 */
public abstract class ConcreteConfig {

	static {
		ShadingValidator.ensureShaded();
	}

	// The real configuration that all writing is done to.
	private final Configuration configuration;
	private final String modID;

	/**
	 * Create a new configuration with the given file.
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
		Class clazz = this.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(ConfigValue.class) != null) {
				ConfigValue cfgValue = field.getAnnotation(ConfigValue.class);
				String valueKey = cfgValue.key();
				String valueComment = cfgValue.comment();
				String valueCategory = cfgValue.category();
				String valueLangKey = cfgValue.langKey().isEmpty() ? valueKey : cfgValue.langKey();
				boolean valueShowInGui = cfgValue.showInGui();
				boolean valueRequiresMcRestart = cfgValue.requiresMcRestart();
				boolean valueRequiresWorldRestart = cfgValue.requiresWorldRestart();

				if (Objects.equals(valueKey, "")) {
					valueKey = field.getName();
				}

				try {
					Object fieldValue = field.get(this);

					if (field.getType().isArray()) {
						switch (cfgValue.type()) {
							case INTEGER: {
								Property property = configuration.get(valueCategory, valueKey,
										(int[]) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getIntList());
								break;
							}
							case BOOLEAN: {
								Property property = configuration.get(valueCategory, valueKey, (boolean[]) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getBooleanList());
								break;
							}
							case DOUBLE: {
								Property property = configuration.get(valueCategory, valueKey, (double[]) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getDoubleList());
								break;
							}
							case STRING: {
								Property property = configuration.get(valueCategory, valueKey, (String[]) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getStringList());
								break;
							}
						}
					} else {
						switch (cfgValue.type()) {
							case INTEGER: {
								Property property = configuration.get(valueCategory, valueKey, (Integer) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getInt());
								break;
							}
							case BOOLEAN: {
								Property property = configuration.get(valueCategory, valueKey, (Boolean) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getBoolean());
								break;
							}
							case DOUBLE: {
								Property property = configuration.get(valueCategory, valueKey, (Double) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getDouble());
								break;
							}
							case STRING: {
								Property property = configuration.get(valueCategory, valueKey, (String) fieldValue, valueComment);
								property.setLanguageKey(valueLangKey);
								property.setShowInGui(valueShowInGui);
								property.setRequiresMcRestart(valueRequiresMcRestart);
								property.setRequiresWorldRestart(valueRequiresWorldRestart);

								field.set(this, property.getString());
								break;
							}
						}
					}
				} catch (IllegalAccessException e) {
					ConcreteLog.error("Failed to access field when loading a concrete configuration. {}", e);
				}
			}
		}
		configuration.save();
	}

	/**
	 * Get the forge configuration that is written to, use if you need direct access.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Called on config changes, used to handle config gui modifications.
	 */
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		// Check that the modID is ours before doing a reload.
		if (Objects.equals(modID, event.getModID())) {
			loadConfig();
		}
	}

}
