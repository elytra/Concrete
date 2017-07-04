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

import java.util.List;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.ValidatedSlot;
import com.elytradev.concrete.inventory.gui.client.GuiHelper;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WItemSlots extends Widget {
	private final List<Slot> peers = Lists.newArrayList();
	private IInventory inventory;
	private int startIndex = 0;
	private int slotsWide = 1;
	private int slotsHigh = 1;
	private boolean big = false;
	private boolean ltr = false;
	
	public WItemSlots(IInventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big, boolean ltr) {
		this.inventory = inventory;
		this.startIndex = startIndex;
		this.slotsWide = slotsWide;
		this.slotsHigh = slotsHigh;
		this.big = big;
		this.ltr = ltr;
	}
	
	private WItemSlots() {}
	
	public static WItemSlots of(IInventory inventory, int index) {
		WItemSlots w = new WItemSlots();
		w.inventory = inventory;
		w.startIndex = index;
		return w;
	}
	
	public static WItemSlots of(IInventory inventory, int startIndex, int slotsWide, int slotsHigh) {
		WItemSlots w = new WItemSlots();
		w.inventory = inventory;
		w.startIndex = startIndex;
		w.slotsWide = slotsWide;
		w.slotsHigh = slotsHigh;
		return w;
	}
	
	public static WItemSlots outputOf(IInventory inventory, int index) {
		WItemSlots w = new WItemSlots();
		w.inventory = inventory;
		w.startIndex = index;
		w.big = true;
		return w;
	}
	
	public static WItemSlots ofPlayerStorage(IInventory inventory) {
		WItemSlots w = new WItemSlots();
		w.inventory = inventory;
		w.startIndex = 9;
		w.slotsWide = 9;
		w.slotsHigh = 3;
		return w;
	}
	
	public static WItemSlots ofPlayerHotbar(IInventory inventory) {
		WItemSlots w = new WItemSlots() {};
		w.inventory = inventory;
		w.startIndex = 0;
		w.slotsWide = 9;
		w.slotsHigh = 1;
		return w;
	}
	
	@Override
	public int getWidth() {
		return slotsWide * 18;
	}
	
	@Override
	public int getHeight() {
		return slotsHigh * 18;
	}
	
	/**
	 * Adds validated item slots to the specified container and marks this
	 * widget as valid.
	 *
	 * @param host the top-level container that will hold peers
	 * @see #invalidate
	 */
	@Override
	public void validate(ConcreteContainer host) {
		peers.clear();
		
		int index = startIndex;
		
		if (ltr) {
			for (int x = 0; x < slotsWide; x++) {
				for (int y = 0; y < slotsHigh; y++) {
					ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getX() + (x * 18), this.getY() + (y * 18));
					peers.add(slot);
					host.addSlotPeer(slot);
					index++;
				}
			}
		} else {
			for (int y = 0; y < slotsHigh; y++) {
				for (int x = 0; x < slotsWide; x++) {
					ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getX() + (x * 18), this.getY() + (y * 18));
					peers.add(slot);
					host.addSlotPeer(slot);
					index++;
				}
			}
		}
		
		super.validate(host);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		int left = -1;
		int top = -1;
		
		if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
			GuiContainer containerGui = (GuiContainer) Minecraft.getMinecraft().currentScreen;
			left = containerGui.getGuiLeft();
			top = containerGui.getGuiTop();
		}
		
		for (int xi = 0; xi < slotsWide; xi++) {
			for (int yi = 0; yi < slotsHigh; yi++) {
				if (big) {
					GuiHelper.drawBeveledPanel((xi * 18) + x - 4, (yi * 18) + y - 4, 24, 24);
				} else {
					GuiHelper.drawBeveledPanel((xi * 18) + x - 1, (yi * 18) + y - 1, 18, 18);
				}
				
				if (left != -1 && top != -1) {
					int peerIndex;
					
					if (ltr) {
						peerIndex = xi * slotsHigh + yi;
					} else {
						peerIndex = yi * slotsWide + xi;
					}
					
					Slot slot = peers.get(peerIndex);
					slot.xPos = x + (xi * 18) - left;
					slot.yPos = y + (yi * 18) - top;
				}
			}
		}
	}
}
