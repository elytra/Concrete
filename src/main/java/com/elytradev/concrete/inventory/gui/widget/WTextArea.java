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

import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

/**
 * Multi-line word-wrapping WLabel that can optionally be scaled down to fit more text. Please be mindful that
 * odd-integer resolutions exist, so scaling down may look quite bad!
 */

public class WTextArea extends WWidget {
	protected ITextComponent text;
	
	protected final int color;

	public static final int DEFAULT_TEXT_COLOR = 0xFF404040;
	public int scale = 1;

	public WTextArea(int color) {
		this(null, color);
	}
	
	public WTextArea() {
		this(null, DEFAULT_TEXT_COLOR);
	}
	
	public WTextArea(ITextComponent text, int color) {
		this.text = text;
		this.color = color;
	}

	public WTextArea(ITextComponent text) {
		this(text, DEFAULT_TEXT_COLOR);
	}
	
	/** Scale the text to an integer multiple of the base scale. Higher numbers are smaller! */
	public void setScale(int scale) {
		this.scale = scale;
	}
	
	public void setText(ITextComponent text) {
		this.text = text;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		if (text==null) return;
		
		if (scale!=1) GlStateManager.scale(1/(float)scale, 1/(float)scale, 1);
		Minecraft.getMinecraft().fontRenderer.drawSplitString("§r"+text.getFormattedText(), (int)(x*scale), (int)(y*scale), (int)(this.getWidth()*scale), color);
		if (scale!=1) GlStateManager.scale(scale, scale, 1);
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
}