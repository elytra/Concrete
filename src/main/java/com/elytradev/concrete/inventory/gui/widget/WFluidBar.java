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

import com.elytradev.concrete.inventory.ConcreteFluidTank;
import com.elytradev.concrete.inventory.FluidTankProxySlot;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.google.common.base.Splitter;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class WFluidBar extends WWidget {
	private final ResourceLocation bg;
	private final ResourceLocation fg;
	private final ConcreteFluidTank concreteFluidTank;
	private final Direction direction;
	private String tooltipLabel;

	public WFluidBar(ResourceLocation bg, ConcreteFluidTank tank) {
		this(bg, tank, Direction.UP);
	}
	public WFluidBar(ResourceLocation bg, ConcreteFluidTank tank, Direction dir) {
		this(bg, null, tank, dir);
	}

	public WFluidBar(ResourceLocation bg, ResourceLocation fg,  ConcreteFluidTank tank) {
		this(bg, fg, tank, Direction.UP);
	}


	public WFluidBar(ResourceLocation bg, ResourceLocation fg, ConcreteFluidTank tank, Direction dir) {
		this.bg = bg;
		this.fg = fg;
		this.concreteFluidTank = tank;
		this.direction = dir;
	}

	/**
	 * Adds a tooltip to the WBFluidar.
	 *
	 * Formatting Guide: The tooltip label is passed into String.Format and can recieve two integers
	 * (%d) - the first is the current level of the focused tank and the second is the
	 * tank's capacity.
	 * @param label String to render on the tooltip.
	 * @return WFluidBar with tooltip enabled and set.
	 */
	public WFluidBar withTooltip(String label) {
		this.setRenderTooltip(true);
		this.tooltipLabel = label;
		return this;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	/**
	 * Creates a fake item slot to sync the fluid data between the client and the server.
	 * @param c the top-level Container that will hold the peers
	 */
	@Override
	public void createPeers(ConcreteContainer c) {
		c.addSlotPeer(new FluidTankProxySlot(concreteFluidTank));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		GuiDrawing.rect(bg, x, y, getWidth(), getHeight(), 0xFFFFFFFF);

		if(concreteFluidTank.getFluid() != null) {
		
			float percent = (float) concreteFluidTank.getFluidAmount() / (float) concreteFluidTank.getCapacity();
			if (percent < 0) percent = 0f;
			if (percent > 1) percent = 1f;
			
			int barMax = getWidth();
			if (direction == Direction.DOWN || direction == Direction.UP) barMax = getHeight();
			percent = ((int) (percent * barMax)) / (float) barMax; //Quantize to bar size
			
			int barSize = (int) (barMax * percent);
			if (barSize >= 0) {
				FluidStack stack = concreteFluidTank.getFluid();
				Fluid fluid = stack.getFluid();
				int color = GuiDrawing.colorAtOpacity(fluid.getColor(stack),1.0f);
		
				switch(direction) { //anonymous blocks in this switch statement are to sandbox variables
					case UP: {
						int left = x;
						int bottom = y + getHeight();
						//top -= barSize;
						int verticalSegments = barSize / 16;
						int horizontalSegments = getWidth() / 16;
						for (int dY = 0; dY < verticalSegments; dY++) {
							for (int dX = 0; dX < horizontalSegments; dX++) {
								GuiDrawing.rect(fluid, left + (dX * 16), bottom - ((dY + 1) * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f, color);
							}
							GuiDrawing.rect(fluid, left + (horizontalSegments * 16), bottom - ((dY + 1) * 16), getWidth() % 16, 16, 0.0f, 0.0f, getWidth() % 16, 16.0f, color);
						}
		
						for (int dX = 0; dX < horizontalSegments; dX++) {
							GuiDrawing.rect(fluid, left + (dX * 16), bottom - ((verticalSegments) * 16) - (barSize % 16), 16, (barSize % 16), 0.0f, 16 - (barSize % 16), 16.0f, 16.0f, color);
						}
						GuiDrawing.rect(fluid, left + (horizontalSegments * 16), bottom - (verticalSegments * 16) - (barSize % 16), getWidth() % 16, (barSize % 16), 0.0f, 16 - (barSize % 16), getWidth() % 16, 16.0f, color);
		
						break;
					}
					case LEFT: {
						int left = x + getWidth();
						left -= barSize;
						int top = y;
						int verticalSegments = getHeight() / 16;
						int horizontalSegments = barSize / 16;
						for (int dX = 0; dX < horizontalSegments; dX++) {
							for (int dY = 0; dY < verticalSegments; dY++) {
								GuiDrawing.rect(fluid, left + (dX * 16), y + (dY * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f, color);
							}
							GuiDrawing.rect(fluid, left + (dX * 16), y + (verticalSegments * 16), 16, getHeight() % 16, 0.0f, 0.0f, 16, getHeight() % 16, color);
						}
		
						if (barSize % 16 != 0) {
							for (int dY = 0; dY < verticalSegments; dY++) {
								GuiDrawing.rect(fluid, left + (horizontalSegments * 16), y + (dY * 16), (barSize % 16), 16, 0.0f, 0.0f, (barSize % 16), 16, color);
							}
							GuiDrawing.rect(fluid, left + (horizontalSegments * 16), y + (verticalSegments * 16), (barSize % 16), getHeight() % 16, 0.0f, 0.0f, (barSize % 16), getHeight() % 16, color);
						}
		
						break;
					}
					case DOWN: {
						int left = x;
						int top = y + getHeight();
						top -= barSize;
						int verticalSegments = barSize / 16;
						int horizontalSegments = getWidth() / 16;
						for (int dY = 0; dY < verticalSegments; dY++) {
							for (int dX = 0; dX < horizontalSegments; dX++) {
								GuiDrawing.rect(fluid, left + (dX * 16), y + (dY * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f, color);
							}
							GuiDrawing.rect(fluid, left + (horizontalSegments * 16), y + (dY * 16), getWidth() % 16, 16, 0.0f, 0.0f, getWidth() % 16, 16.0f, color);
						}
		
						for (int dX = 0; dX < horizontalSegments; dX++) {
							GuiDrawing.rect(fluid, left + (dX * 16), y + (verticalSegments * 16), 16, (barSize % 16), 0.0f, 0.0f, 16, (barSize % 16), color);
						}
						GuiDrawing.rect(fluid, left + (horizontalSegments * 16), y + (verticalSegments * 16), getWidth() % 16, (barSize % 16), 0.0f, 0.0f, getWidth() % 16, (barSize % 16), color);
		
						break;
					}
					case RIGHT: {
						int left = x;
						int top = y;
						int verticalSegments = getHeight() / 16;
						int horizontalSegments = barSize / 16;
						for (int dX = 0; dX < horizontalSegments; dX++) {
							for (int dY = 0; dY < verticalSegments; dY++) {
								GuiDrawing.rect(fluid, left + (dX * 16), y + (dY * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f, color);
							}
							GuiDrawing.rect(fluid, left + (dX * 16), y + (verticalSegments * 16), 16, getHeight() % 16, 0.0f, 0.0f, 16, getHeight() % 16, color);
						}
		
						if (barSize % 16 != 0) {
							for (int dY = 0; dY < verticalSegments; dY++) {
								GuiDrawing.rect(fluid, left + (horizontalSegments * 16), y + (dY * 16), (barSize % 16), 16, 0.0f, 0.0f, (barSize % 16), 16, color);
							}
							GuiDrawing.rect(fluid, left + (horizontalSegments * 16), y + (verticalSegments * 16), (barSize % 16), getHeight() % 16, 0.0f, 0.0f, (barSize % 16), getHeight() % 16, color);
						}
		
						break;
					}
				}
			}
		}

		if(fg != null) {
			GuiDrawing.rect(fg, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
		}
	}

	@Override
	public void addInformation(List<String> information) {
		int value = concreteFluidTank.getFluidAmount();
		int valMax = concreteFluidTank.getCapacity();
		String fluidName = "";
		FluidStack fluid = concreteFluidTank.getFluid();
		if (fluid!=null) {
			fluidName = fluid.getFluid().getLocalizedName(fluid);
		}
		try {
			for(String s : Splitter.on('\n').split(tooltipLabel)) {
				information.add(String.format(s, value, valMax, fluidName));
			}
		} catch (Throwable t) {
			//Not catching this is a hard crash if we have a bad format string. This is at least playable.
			information.add(tooltipLabel);
		}
	}

	public static enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}
}
