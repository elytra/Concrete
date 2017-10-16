package com.elytradev.concrete.common;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtensiveToast implements IToast {

    private ResourceLocation texture;
    private String title;
    private String subtitle;
    private int titleColor;
    private int subtitleColor;
    private int textureX, textureY;

    private static int DEFAULT_X = 0;
    private static int DEFAULT_Y = 96;

    public ExtensiveToast(String title) {
        this(title, null, TEXTURE_TOASTS.toString(), -256, -1, DEFAULT_X, DEFAULT_Y);
    }

    public ExtensiveToast(String title, String subtitle) {
        this(title, subtitle, TEXTURE_TOASTS.toString(), -256, -1, DEFAULT_X, DEFAULT_Y);
    }

    public ExtensiveToast(String title, String subtitle, String texture, int titleColor, int subtitleColor) {
        this(title, subtitle, texture, titleColor, subtitleColor, 0, 0);
    }

    public ExtensiveToast(String title,
                          String subtitle,
                          String texture,
                          int titleColor,
                          int subtitleColor,
                          int textureX,
                          int textureY) {
        this.title = title;
        this.subtitle = subtitle;
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

        return IToast.Visibility.SHOW;
    }

}
