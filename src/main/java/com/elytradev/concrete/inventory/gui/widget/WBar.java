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

import javax.annotation.Nullable;

import com.elytradev.concrete.inventory.gui.client.GuiHelper;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class WBar extends Widget {
	public static final Direction DEFAULT_DIRECTION = Direction.UP;
	
	protected final ResourceLocation bg;
	@Nullable
	protected final ResourceLocation fg;
	protected final Direction direction;
	
	public WBar(ResourceLocation bg, Direction direction) {
		this(bg, null, direction);
	}
	
	public WBar(ResourceLocation bg, @Nullable ResourceLocation fg) {
		this(bg, fg, DEFAULT_DIRECTION);
	}
	
	public WBar(ResourceLocation bg, @Nullable ResourceLocation fg, Direction direction) {
		this.bg = bg;
		this.fg = fg;
		this.direction = direction;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		GuiHelper.drawRectangle(bg, x, y, getWidth(), getHeight());
		
		if (!canPaintBar())
			return;
		
		float percent = getCurrentValue() / getMaxValue();
		percent = MathHelper.clamp(percent, 0f, 1f);
		
		int barMax;
		switch (direction) {
			case LEFT:
			case RIGHT:
				barMax = getWidth();
				break;
			case DOWN:
			case UP:
			default:
				barMax = getHeight();
				break;
		}
		
		percent = ((int) (percent * barMax)) / (float) barMax; //Quantize to bar size
		
		int barSize = (int) (barMax * percent);
		if (barSize <= 0) return;
		
		paintBar(x, y, percent, barSize);
		
		if (fg != null)
			GuiHelper.drawRectangle(fg, x, y, getWidth(), getHeight());
		
		//GuiHelper.drawRectangle(bar, x, y + (getHeight() - barHeight), getWidth(), barHeight);
		
		//GuiHelper.drawString("" + inventory.getField(field) + "/", x + 18, y + 9, 0xFF000000);
		//GuiHelper.drawString("" + inventory.getField(max) + "", x + 32, y + 9, 0xFF000000);
	}
	
	@SideOnly(Side.CLIENT)
	protected abstract boolean canPaintBar();
	
	protected abstract float getCurrentValue();
	
	protected abstract float getMaxValue();
	
	@SideOnly(Side.CLIENT)
	protected abstract void paintBar(int x, int y, float percent, int barSize);
	
	public static enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}
}
