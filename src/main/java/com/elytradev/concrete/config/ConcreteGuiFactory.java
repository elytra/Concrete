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

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * Do not use this class directly, create a subclass that has an empty constructor and pass your information with super.
 */
public class ConcreteGuiFactory implements IModGuiFactory {
	private final Configuration config;
	private final String modID;
	private final String title;

	protected ConcreteGuiFactory(ConcreteConfig concreteConfig) {
		this(concreteConfig.getConfiguration(), concreteConfig.getModID(), I18n.format(concreteConfig.getModID() + ".configgui.title"));
	}

	protected ConcreteGuiFactory(Configuration config, String modID, String title) {
		this.config = config;
		this.modID = modID;
		this.title = title;
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new GuiConfig(parentScreen, collectConfigElements(), modID, false, false, title);
	}

	private List<IConfigElement> collectConfigElements() {
		Set<String> categoryNames = config.getCategoryNames();
		List<IConfigElement> configElements;
		if (categoryNames.size() == 1) {
			configElements = new ConfigElement(config.getCategory(categoryNames.iterator().next())).getChildElements();
		} else {
			configElements = Lists.newArrayListWithCapacity(categoryNames.size());
			for (String categoryName : categoryNames) {
				configElements.add(new ConfigElement(config.getCategory(categoryName)));
			}
		}
		configElements.sort(Comparator.comparing(e -> I18n.format(e.getLanguageKey())));
		return configElements;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}
}
