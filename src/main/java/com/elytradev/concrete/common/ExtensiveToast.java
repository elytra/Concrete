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
public class ExtensiveToast implements IToast {

	private ResourceLocation texture;
	private String title;
	private String subtitle;
	private long timing;
	private int titleColor;
	private int subtitleColor;
	private int textureX, textureY;

	public static final long DEFAULT_TIMING = 5000;
	public static final int DEFAULT_X = 0;
	public static final int DEFAULT_Y = 96;

	public ExtensiveToast(@Nonnull String title) {
		this(title, null, DEFAULT_TIMING, TEXTURE_TOASTS.toString(), -256, -1, DEFAULT_X, DEFAULT_Y);
	}

	public ExtensiveToast(@Nonnull String title, @Nullable String subtitle) {
		this(title, subtitle, DEFAULT_TIMING, TEXTURE_TOASTS.toString(), -256, -1, DEFAULT_X, DEFAULT_Y);
	}

	public ExtensiveToast(@Nonnull String title, @Nullable String subtitle, long timing) {
		this(title, subtitle, timing, TEXTURE_TOASTS.toString(), -256, -1, DEFAULT_X, DEFAULT_Y);
	}

	public ExtensiveToast(@Nonnull String title,
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

	@Override
	public Visibility draw(GuiToast toastGui, long delta) {
		toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		toastGui.drawTexturedModalRect(0, 0, textureX, textureY, 160, 32);

		if (this.subtitle == null)
		{
			toastGui.getMinecraft().fontRenderer.drawString(this.title, 18, 12, titleColor);
		}
		else {
			toastGui.getMinecraft().fontRenderer.drawString(this.title, 18, 7, titleColor);
			toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 18, 18, subtitleColor);
		}

		return delta < timing ? Visibility.SHOW : Visibility.HIDE;
	}

}
