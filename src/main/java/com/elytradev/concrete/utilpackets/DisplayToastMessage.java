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

package com.elytradev.concrete.utilpackets;

import com.elytradev.concrete.common.ConcreteToast;
import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ReceivedOn(Side.CLIENT)
public class DisplayToastMessage extends Message {

	public String title;
	public String subtitle;
	@MarshalledAs("i64")
	public long timing;

	@MarshalledAs("i32")
	public int titleColor;
	@MarshalledAs("i32")
	public int subtitleColor;

	public String texture;
	@MarshalledAs("i32")
	public int textureX;
	@MarshalledAs("i32")
	public int textureY;

	public DisplayToastMessage(NetworkContext ctx) {
		super(ctx);
	}

	private DisplayToastMessage(NetworkContext ctx, @Nonnull String title,
				@Nullable String subtitle, long timing, @Nonnull String texture,
				int titleColor, int subtitleColor, int textureX, int textureY) {
		super(ctx);
		this.title = title;
		this.subtitle = subtitle;
		this.timing = timing;
		this.texture = texture;
		this.titleColor  = titleColor;
		this.subtitleColor = subtitleColor;
		this.textureX = textureX;
		this.textureY = textureY;
	}

	public static Builder builder(String title) {
		return new Builder(title);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		ConcreteToast toast = ConcreteToast.builder(title)
				.setSubtitle(subtitle)
				.setTiming(timing)
				.setTexture(texture)
				.setTitleColor(titleColor)
				.setSubtitleColor(subtitleColor)
				.setTextureX(textureX)
				.setTextureY(textureY)
				.create();
		Minecraft.getMinecraft().getToastGui().add(toast);
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


		public DisplayToastMessage create(NetworkContext ctx) {
			return new DisplayToastMessage(ctx, title, subtitle, timing,
					texture, titleColor, subtitleColor, textureX, textureY);
		}
	}
}
