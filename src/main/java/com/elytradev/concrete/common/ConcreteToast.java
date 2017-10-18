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

    private static final long DEFAULT_TIMING = 5000;
    private static final int DEFAULT_TITLE_COLOR = -256;
    private static final int DEFAULT_SUBTITLE_COLOR = -1;
    private static final int DEFAULT_X = 0;
    private static final int DEFAULT_Y = 96;

	public ConcreteToast(@Nonnull String title) {
		this(title, null, DEFAULT_TIMING, TEXTURE_TOASTS.toString(), DEFAULT_TITLE_COLOR, DEFAULT_SUBTITLE_COLOR, DEFAULT_X, DEFAULT_Y);
	}

	public ConcreteToast(@Nonnull String title, @Nullable String subtitle) {
		this(title, subtitle, DEFAULT_TIMING, TEXTURE_TOASTS.toString(), DEFAULT_TITLE_COLOR, DEFAULT_SUBTITLE_COLOR, DEFAULT_X, DEFAULT_Y);
	}

	public ConcreteToast(@Nonnull String title, @Nullable String subtitle, long timing) {
		this(title, subtitle, timing, TEXTURE_TOASTS.toString(), DEFAULT_TITLE_COLOR, DEFAULT_SUBTITLE_COLOR, DEFAULT_X, DEFAULT_Y);
	}

	public ConcreteToast(@Nonnull String title,
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

	@Nonnull
	@Override
	public Visibility draw(@Nonnull GuiToast toastGui, long delta) {
		toastGui.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		toastGui.drawTexturedModalRect(0, 0, textureX, textureY, 160, 32);

		if (this.subtitle == null)
		{
			toastGui.getMinecraft().fontRenderer.drawString(this.title, 30, 12, titleColor);
		}
		else {
			toastGui.getMinecraft().fontRenderer.drawString(this.title, 30, 7, titleColor);
			toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 30, 18, subtitleColor);
		}

		return delta < timing ? Visibility.SHOW : Visibility.HIDE;
	}

}
