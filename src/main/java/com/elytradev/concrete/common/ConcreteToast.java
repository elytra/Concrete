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

	private ConcreteToast(@Nonnull String title,
			@Nullable String subtitle,
			long timing,
			@Nonnull String texture,
			int titleColor,
			int subtitleColor,
			int textureX,
			int textureY) {
		this.title = title;
		this.subtitle = subtitle;
		this.timing = timing;
		this.texture = new ResourceLocation(texture);
		this.titleColor = titleColor;
		this.subtitleColor = subtitleColor;
		this.textureX = textureX;
		this.textureY = textureY;
	}

	public static ConcreteToastBuilder getBuilder(String title) {
		return new ConcreteToastBuilder(title);
	}

	@Nonnull
	@Override
	public Visibility draw(@Nonnull GuiToast toastGui, long delta) {
		toastGui.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		toastGui.drawTexturedModalRect(0, 0, textureX, textureY, 160, 32);

		if (this.subtitle == null) {
			toastGui.getMinecraft().fontRenderer.drawString(this.title, 30, 12, titleColor);
		}
		else {
			toastGui.getMinecraft().fontRenderer.drawString(this.title, 30, 7, titleColor);
			toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 30, 18, subtitleColor);
		}

		return delta < timing ? Visibility.SHOW : Visibility.HIDE;
	}


	public static class ConcreteToastBuilder {

		private String title;
		private String subtitle;
		private long timing = 5000;
		private String texture = "textures/gui/toasts.png";
		private int titleColor = -256;
		private int subtitleColor = -1;
		private int textureX = 0;
		private int textureY = 96;

		private ConcreteToastBuilder(String title) {
			this.title = title;
		}

		public ConcreteToastBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public ConcreteToastBuilder setSubtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public ConcreteToastBuilder setTiming(long timing) {
			this.timing = timing;
			return this;
		}

		public ConcreteToastBuilder setTexture(String texture) {
			this.texture = texture;
			return this;
		}

		public ConcreteToastBuilder setTitleColor(int titleColor) {
			this.titleColor = titleColor;
			return this;
		}

		public ConcreteToastBuilder setSubtitleColor(int subtitleColor) {
			this.subtitleColor = subtitleColor;
			return this;
		}

		public ConcreteToastBuilder setTextureX(int textureX) {
			this.textureX = textureX;
			return this;
		}

		public ConcreteToastBuilder setTextureY(int textureY) {
			this.textureY = textureY;
			return this;
		}


		public ConcreteToast create() {
			return new ConcreteToast(title,
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
