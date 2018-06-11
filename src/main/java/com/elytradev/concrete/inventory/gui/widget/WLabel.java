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

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.inventory.IInventory;

public class WLabel extends WWidget {
	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	protected String text;
	protected int color;
	
	protected IInventory inventory;
	protected int field1 = -1;
	protected int field2 = -1;
	
	public WLabel(String text, int color) {
		this.text = text;
		this.color = color;
	}
	
	public WLabel(String text) {
		this(text, DEFAULT_TEXT_COLOR);
	}
	
	public WLabel withFields(IInventory inv, int field1, int field2) {
		this.inventory = inv;
		this.field1 = field1;
		this.field2 = field2;
		return this;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		int field1Contents = 0;
		int field2Contents = 0;
		if (inventory!=null) {
			if (field1>=0) {
				field1Contents = inventory.getField(field1);
			}
			
			if (field2>=0) {
				field2Contents = inventory.getField(field2);
			}
		}
		
		@SuppressWarnings("deprecation")
		String formatted = net.minecraft.util.text.translation.I18n.translateToLocalFormatted(text, field1Contents, field2Contents);
		
		GuiDrawing.drawString(formatted, x, y, color);
	}

	@Override
	public boolean canResize() {
		return false;
	}
}