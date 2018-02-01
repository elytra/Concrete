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

package com.elytradev.concrete.common;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class ConcreteToast implements IToast {

	private ResourceLocation texture;
	private String title;
	private String subtitle;
	private long timing;
	private int titleColor;
	private int subtitleColor;
	private int textureX, textureY;

	private ConcreteToast(@Nonnull String title, @Nullable String subtitle,
				long timing, @Nonnull String texture, int titleColor,
				int subtitleColor, int textureX, int textureY) {
		this.title = title;
		this.subtitle = subtitle;
		this.timing = timing;
		this.texture = new ResourceLocation(texture);
		this.titleColor = titleColor;
		this.subtitleColor = subtitleColor;
		this.textureX = textureX;
		this.textureY = textureY;
	}

	public static Builder builder(String title) {
		return new Builder(title);
	}

	@Nonnull
	@Override
	public Visibility draw(@Nonnull GuiToast toastGui, long delta) {
		toastGui.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1, 1, 1);
		toastGui.drawTexturedModalRect(0, 0, textureX, textureY, 160, 32);

		if (this.subtitle == null) {
			toastGui.getMinecraft().fontRenderer.drawString(title, 30, 12, titleColor);
		} else {
			toastGui.getMinecraft().fontRenderer.drawString(title, 30, 7, titleColor);
			toastGui.getMinecraft().fontRenderer.drawString(subtitle, 30, 18, subtitleColor);
		}

		return delta < timing ? Visibility.SHOW : Visibility.HIDE;
	}


	public static class Builder {
		private String title;
		private String subtitle;
		private long timing = 5000;
		private String texture = "textures/gui/toasts.png";
		private int titleColor = -256;
		private int subtitleColor = -1;
		private int textureX = 0;
		private int textureY = 96;

		private Builder(String title) {
			this.title = title;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setSubtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public Builder setTiming(long timing) {
			this.timing = timing;
			return this;
		}

		public Builder setTexture(String texture) {
			this.texture = texture;
			return this;
		}

		public Builder setTitleColor(int titleColor) {
			this.titleColor = titleColor;
			return this;
		}

		public Builder setSubtitleColor(int subtitleColor) {
			this.subtitleColor = subtitleColor;
			return this;
		}

		public Builder setTextureX(int textureX) {
			this.textureX = textureX;
			return this;
		}

		public Builder setTextureY(int textureY) {
			this.textureY = textureY;
			return this;
		}


		public ConcreteToast create() {
			return new ConcreteToast(title, subtitle, timing, texture,
					titleColor, subtitleColor, textureX, textureY);
		}
	}

}
