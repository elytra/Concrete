package com.elytradev.concrete.inventory.gui.widget;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import net.minecraft.inventory.IInventory;

public class WFieldedLabel extends WLabel {
    protected IInventory inventory;
    protected int field;
    protected int fieldMax;

    public WFieldedLabel(IInventory inventory, int field, int fieldMax, String format, int color) {
        super(format, color);
        this.inventory = inventory;
        this.field = field;
        this.fieldMax = fieldMax;
    }

    public WFieldedLabel(IInventory inventory, int field, int fieldMax, String format) {
        this(inventory, field, fieldMax, format, DEFAULT_TEXT_COLOR);
    }

    public WFieldedLabel(IInventory inventory, int field, String format, int color) {
        this(inventory, field, -1, format, color);
    }

    public WFieldedLabel(IInventory inventory, int field, String format) {
        this(inventory, field, -1, format);
    }

    @Override
    public void paintBackground(int x, int y) {
        String formatted = text.replaceAll("%f", Integer.toString(inventory.getField(field)));
        if(fieldMax != -1) {
            formatted = formatted.replaceAll("%m", Integer.toString(inventory.getField(fieldMax)));
        }
        GuiDrawing.drawString(formatted, x, y, color);
    }
}
