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

import com.elytradev.concrete.common.GuiDrawing;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WImage extends WWidget {
	public static final int DEFAULT_COLOR = 0xFFFFFFFF;
	
	private final ResourceLocation texture;
	private final float u1;
	private final float v1;
	private final float u2;
	private final float v2;
	private final int color;
	
	public WImage(ResourceLocation texture) {
		this(texture, DEFAULT_COLOR);
	}
	
	public WImage(ResourceLocation texture, int color) {
		this(texture, 0, 0, 1, 1, color);
	}
	
	public WImage(ResourceLocation texture, float u1, float v1, float u2, float v2) {
		this(texture, u1, v1, u2, v2, DEFAULT_COLOR);
	}
	
	public WImage(ResourceLocation texture, float u1, float v1, float u2, float v2, int color) {
		this.texture = texture;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		this.color = color;
		setResizable(true);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		GuiDrawing.drawRectangle(texture, x, y, getWidth(), getHeight(), u1, v1, u2, v2, color);
	}
}
