package com.elytradev.concrete.inventory.gui.widget;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import net.minecraft.client.Minecraft;

public class WLabel extends WWidget {
    protected String text;
    protected int color;

    public static final int DEFAULT_TEXT_COLOR = 0x404040;

    public WLabel(String text, int color) {
        this.text = text;
        this.color = color;
        this.setSize(Minecraft.getMinecraft().fontRenderer.getStringWidth(text), 8);
    }

    public WLabel(String text) {
        this(text, DEFAULT_TEXT_COLOR);
    }

    @Override
    public void paintBackground(int x, int y) {
        GuiDrawing.drawString(text, x, y, color);
    }

    @Override
    public boolean canResize() {
        return false;
    }
}
