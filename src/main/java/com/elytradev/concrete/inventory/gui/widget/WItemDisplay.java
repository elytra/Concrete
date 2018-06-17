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

package com.elytradev.concrete.inventory.gui.widget;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An item display that doesn't have a heavyweight peer. THIS MEANS IT WILL NOT SYNC from server to client, and has NO
 * TOOLTIP. It's just a cosmetic way to draw an itemstack to the GUI. Can be changed on-the-fly while the GUI is open.
 */
public class WItemDisplay extends WWidget {
	protected ItemStack stack = ItemStack.EMPTY;
	
	public WItemDisplay() {}
	
	public static WItemDisplay of(ItemStack stack) {
		WItemDisplay w = new WItemDisplay();
		w.stack = stack;
		
		return w;
	}
	
	@Override
	public int getWidth() {
		return 18;
	}
	
	@Override
	public int getHeight() {
		return 18;
	}
	
	@Override
	public void createPeers(ConcreteContainer c) {}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (stack!=null) {
			RenderHelper.enableGUIStandardItemLighting();
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
			RenderHelper.disableStandardItemLighting();
		}
	}

	public void setItemStack(ItemStack stack) {
		this.stack = stack;
	}
}