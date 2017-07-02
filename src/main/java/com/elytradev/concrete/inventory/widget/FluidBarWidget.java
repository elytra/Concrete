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

package com.elytradev.concrete.inventory.widget;

import javax.annotation.Nullable;

import com.elytradev.concrete.inventory.fluid.ConcreteFluidTank;
import com.elytradev.concrete.inventory.fluid.FluidTankProxySlot;
import com.elytradev.concrete.inventory.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.GuiHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidBarWidget extends BarWidget {
	private final ConcreteFluidTank concreteFluidTank;

	public FluidBarWidget(ResourceLocation bg, ConcreteFluidTank tank) {
		this(bg, tank, DEFAULT_DIRECTION);
	}

	public FluidBarWidget(ResourceLocation bg, ConcreteFluidTank tank, BarDirection dir) {
		this(bg, null, tank, dir);
	}

	public FluidBarWidget(ResourceLocation bg, @Nullable ResourceLocation fg, ConcreteFluidTank tank) {
		this(bg, fg, tank, DEFAULT_DIRECTION);
	}

	public FluidBarWidget(ResourceLocation bg, @Nullable ResourceLocation fg, ConcreteFluidTank tank, BarDirection dir) {
		super(bg, fg, dir);
		this.concreteFluidTank = tank;
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
	protected boolean canPaintBar() {
		return concreteFluidTank.getFluid() != null;
	}
	
	@Override
	protected float getCurrentValue() {
		return concreteFluidTank.getFluidAmount();
	}
	
	@Override
	protected float getMaxValue() {
		return concreteFluidTank.getCapacity();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void paintBar(int x, int y, float percent, int barSize) {
		Fluid fluid = concreteFluidTank.getFluid().getFluid();

		switch (direction) { //anonymous blocks in this switch statement are to sandbox variables
			case UP: {
				int left = x;
				int bottom = y + getHeight();
				//top -= barSize;
				int verticalSegments = barSize / 16;
				int horizontalSegments = getWidth() / 16;
				for (int dY = 0; dY < verticalSegments; dY++) {
					for (int dX = 0; dX < horizontalSegments; dX++) {
						GuiHelper.drawRectangle(fluid, left + (dX * 16), bottom - ((dY + 1) * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f);
					}
					GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), bottom - ((dY + 1) * 16), getWidth() % 16, 16, 0.0f, 0.0f, getWidth() % 16, 16.0f);
				}

				for (int dX = 0; dX < horizontalSegments; dX++) {
					GuiHelper.drawRectangle(fluid, left + (dX * 16), bottom - ((verticalSegments) * 16) - (barSize % 16), 16, (barSize % 16), 0.0f, 16 - (barSize % 16), 16.0f, 16.0f);
				}
				GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), bottom - (verticalSegments * 16) - (barSize % 16), getWidth() % 16, (barSize % 16), 0.0f, 16 - (barSize % 16), getWidth() % 16, 16.0f);

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
						GuiHelper.drawRectangle(fluid, left + (dX * 16), top + (dY * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f);
					}
					GuiHelper.drawRectangle(fluid, left + (dX * 16), top + (verticalSegments * 16), 16, getHeight() % 16, 0.0f, 0.0f, 16, getHeight() % 16);
				}

				if (barSize % 16 != 0) {
					for (int dY = 0; dY < verticalSegments; dY++) {
						GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), top + (dY * 16), (barSize % 16), 16, 0.0f, 0.0f, (barSize % 16), 16);
					}
					GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), top + (verticalSegments * 16), (barSize % 16), getHeight() % 16, 0.0f, 0.0f, (barSize % 16), getHeight() % 16);
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
						GuiHelper.drawRectangle(fluid, left + (dX * 16), y + (dY * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f);
					}
					GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), y + (dY * 16), getWidth() % 16, 16, 0.0f, 0.0f, getWidth() % 16, 16.0f);
				}

				for (int dX = 0; dX < horizontalSegments; dX++) {
					GuiHelper.drawRectangle(fluid, left + (dX * 16), y + (verticalSegments * 16), 16, (barSize % 16), 0.0f, 0.0f, 16, (barSize % 16));
				}
				GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), y + (verticalSegments * 16), getWidth() % 16, (barSize % 16), 0.0f, 0.0f, getWidth() % 16, (barSize % 16));

				break;
			}
			case RIGHT: {
				int left = x;
				int top = y;
				int verticalSegments = getHeight() / 16;
				int horizontalSegments = barSize / 16;
				for (int dX = 0; dX < horizontalSegments; dX++) {
					for (int dY = 0; dY < verticalSegments; dY++) {
						//GuiHelper.drawRectangle(fluid, left + (dX * 16), top + (dY * 16), 16, 16, 0, 0, 1, 1);
						GuiHelper.drawRectangle(fluid, left + (dX * 16), top + (dY * 16), 16, 16, 0.0f, 0.0f, 16.0f, 16.0f);
					}
					GuiHelper.drawRectangle(fluid, left + (dX * 16), top + (verticalSegments * 16), 16, getHeight() % 16, 0.0f, 0.0f, 16, getHeight() % 16);
				}

				if (barSize % 16 != 0) {
					for (int dY = 0; dY < verticalSegments; dY++) {
						GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), top + (dY * 16), (barSize % 16), 16, 0.0f, 0.0f, (barSize % 16), 16);
					}
					GuiHelper.drawRectangle(fluid, left + (horizontalSegments * 16), top + (verticalSegments * 16), (barSize % 16), getHeight() % 16, 0.0f, 0.0f, (barSize % 16), getHeight() % 16);
				}

				break;
			}
		}
	}
}