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

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WFieldedBar extends WBar {
	private final ResourceLocation bar;
	private final int field;
	private final int maxField;
	private final IInventory inventory;
	
	public WFieldedBar(ResourceLocation bg, ResourceLocation bar, IInventory inv, int field, int maxField) {
		this(bg, bar, inv, field, maxField, DEFAULT_DIRECTION);
	}
	
	public WFieldedBar(ResourceLocation bg, ResourceLocation bar, IInventory inv, int field, int maxField, BarDirection dir) {
		this(bg, bar, null, inv, field, maxField, dir);
	}
	
	public WFieldedBar(ResourceLocation bg, ResourceLocation bar, @Nullable ResourceLocation fg, IInventory inv, int field, int maxField) {
		this(bg, bar, fg, inv, field, maxField, DEFAULT_DIRECTION);
	}
	
	public WFieldedBar(ResourceLocation bg, ResourceLocation bar, @Nullable ResourceLocation fg, IInventory inv, int field, int maxField, BarDirection dir) {
		super(bg, fg, dir);
		this.bar = bar;
		this.inventory = inv;
		this.field = field;
		this.maxField = maxField;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected boolean canPaintBar() {
		return true;
	}
	
	@Override
	protected float getCurrentValue() {
		return inventory.getField(field);
	}
	
	@Override
	protected float getMaxValue() {
		return inventory.getField(maxField);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void paintBar(int x, int y, float percent, int barSize) {
		switch (direction) { //anonymous blocks in this switch statement are to sandbox variables
			case UP: {
				int left = x;
				int top = y + getHeight();
				top -= barSize;
				GuiHelper.drawRectangle(bar, left, top, getWidth(), barSize, 0, 1 - percent, 1, 1);
				break;
			}
			case RIGHT: {
				GuiHelper.drawRectangle(bar, x, y, barSize, getHeight(), 0, 0, percent, 1);
				break;
			}
			case DOWN: {
				GuiHelper.drawRectangle(bar, x, y, getWidth(), barSize, 0, 0, 1, percent);
				break;
			}
			case LEFT: {
				int left = x + getWidth();
				int top = y;
				left -= barSize;
				GuiHelper.drawRectangle(bar, left, top, barSize, getHeight(), 1 - percent, 0, 1, 1);
				break;
			}
		}
	}
}
