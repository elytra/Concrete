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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration base class, uses annotated fields instead of direct configuration calls for configuration data.
 */
public abstract class ConcreteConfig {

	static {
		ShadingValidator.ensureShaded();
	}

	private static final Map<Class<? extends ConcreteConfig>, List<ConfigField>> serializers = Maps.newHashMap();

	private final Class<? extends ConcreteConfig> clazz = this.getClass().asSubclass(ConcreteConfig.class);

	// The real configuration that all writing is done to.
	private final Configuration configuration;

	// An optional mod ID that is used to handle OnConfigChangedEvent.
	private String modID;

	/**
	 * Create a new configuration with the given file and register it to the Forge
	 * event bus with the given mod ID.
	 *
	 * @param configFile
	 * @param modID
	 */
	protected ConcreteConfig(File configFile, String modID) {
		this(configFile);
		this.modID = modID;
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Create a new configuration with the given file.
	 *
	 * @param configFile
	 */
	protected ConcreteConfig(File configFile) {
		this.configuration = new Configuration(configFile);
	}

	/**
	 * Saves config values, and writes to disk.
	 */
	public void loadConfig() {
		try {
			List<ConfigField> fields = serializers.get(clazz);
			if (fields == null) {
				fields = Lists.newArrayList();
				for (Class<?> cursor = clazz; cursor != null && cursor != Object.class; cursor = cursor.getSuperclass()) {
					for (Field f : cursor.getDeclaredFields()) {
						if (f.isAnnotationPresent(ConfigValue.class)) {
							fields.add(new ConfigField(this, f));
						}
					}
				}
				serializers.put(clazz, fields);
			}
			for (ConfigField field : fields) {
				field.load();
			}
		} catch (IllegalAccessException e) {
			ConcreteLog.error("Failed to access field when loading a concrete configuration.", e);
		}
		configuration.save();
	}

	/**
	 * Get the Forge configuration that is written to, use if you need direct access.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (Objects.equals(modID, event.getModID())) {
			loadConfig();
		}
	}

}
