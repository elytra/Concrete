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

	public String texture;

	@MarshalledAs("i32")
	public int titleColor;

	@MarshalledAs("i32")
	public int subtitleColor;

	@MarshalledAs("i32")
	public int textureX;

	@MarshalledAs("i32")
	public int textureY;

	public DisplayToastMessage(NetworkContext ctx) {
		super(ctx);
	}

	private DisplayToastMessage(NetworkContext ctx,
			@Nonnull String title,
			@Nullable String subtitle,
			long timing,
			@Nonnull String texture,
			int titleColor,
			int subtitleColor,
			int textureX,
			int textureY) {
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

	public static DisplayToastMessageBuilder getBuilder(String title) {
		return new DisplayToastMessageBuilder(title);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		ConcreteToast toast = ConcreteToast.getBuilder(title)
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

	public static class DisplayToastMessageBuilder {

		private String title;
		private String subtitle;
		private long timing = 5000;
		private String texture = "textures/gui/toasts.png";
		private int titleColor = -256;
		private int subtitleColor = -1;
		private int textureX = 0;
		private int textureY = 96;

		private DisplayToastMessageBuilder(String title) {
			this.title = title;
		}

		public DisplayToastMessageBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public DisplayToastMessageBuilder setSubtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public DisplayToastMessageBuilder setTiming(long timing) {
			this.timing = timing;
			return this;
		}

		public DisplayToastMessageBuilder setTexture(String texture) {
			this.texture = texture;
			return this;
		}

		public DisplayToastMessageBuilder setTitleColor(int titleColor) {
			this.titleColor = titleColor;
			return this;
		}

		public DisplayToastMessageBuilder setSubtitleColor(int subtitleColor) {
			this.subtitleColor = subtitleColor;
			return this;
		}

		public DisplayToastMessageBuilder setTextureX(int textureX) {
			this.textureX = textureX;
			return this;
		}

		public DisplayToastMessageBuilder setTextureY(int textureY) {
			this.textureY = textureY;
			return this;
		}


		public DisplayToastMessage create(NetworkContext ctx) {
			return new DisplayToastMessage(ctx,
				title,
				subtitle,
				timing,
				texture,
				titleColor,
				subtitleColor,
				textureX,
				textureY);
		}
	}
}
