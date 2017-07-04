/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	William Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
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

package com.elytradev.concrete.inventory.gui.widget;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WLabel extends WWidget {
	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	public static final int DEFAULT_HEIGHT = 8;
	
	protected final String text;
	protected final int color;

	public WLabel(String text, int color) {
		this.text = text;
		this.color = color;
		this.setSize(Minecraft.getMinecraft().fontRenderer.getStringWidth(text), DEFAULT_HEIGHT);//TODO: Remove reference to client-only Minecraft class
	}

	public WLabel(String text) {
		this(text, DEFAULT_TEXT_COLOR);
	}

	public static WLabel ofInventoryDisplayName(IInventory inventory, int color) {
		return new WLabel(inventory.getDisplayName().getUnformattedComponentText(), color);
	}

	public static WLabel ofInventoryDisplayName(IInventory inventory) {
		return ofInventoryDisplayName(inventory, DEFAULT_TEXT_COLOR);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void paintForeground(int x, int y) {
		GuiDrawing.drawString(text, x, y, color);
	}

	@Override
	public boolean canResize() {
		return false;
	}
}
