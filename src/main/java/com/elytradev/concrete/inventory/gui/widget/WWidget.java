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

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class WWidget {
	private boolean valid = false;
	protected WPanel parent;
	private int x = 0;
	private int y = 0;
	private int width = 18;
	private int height = 18;
	private boolean renderTooltip;
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int x, int y) {
		this.width = x;
		this.height = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getAbsoluteX() {
		if (parent==null) {
			return getX();
		} else {
			return getX() + parent.getAbsoluteX();
		}
	}
	
	public int getAbsoluteY() {
		if (parent==null) {
			return getY();
		} else {
			return getY() + parent.getAbsoluteY();
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean canResize() {
		return false;
	}
	
	public void setParent(WPanel parent) {
		this.parent = parent;
	}

	public boolean getRenderTooltip() {
		return renderTooltip;
	}

	public void setRenderTooltip(boolean renderTooltip) {
		this.renderTooltip = renderTooltip;
	}
	
	/**
	 * Draw this Widget at the specified coordinates. The coordinates provided are the top-level device coordinates of
	 * this widget's topleft corner, so don't translate by the widget X/Y! That's already been done. Your "valid"
	 * drawing space is from (x, y) to (x + width - 1, y + height - 1) inclusive. However, no scissor or depth masking
	 * is done, so please take care to respect your boundaries.
	 * @param x The X coordinate of the leftmost pixels of this widget in device (opengl) coordinates
	 * @param y The Y coordinate of the topmost pixels of this widget in device (opengl) coordinates
	 */
	public void paint(int x, int y) {
		
	}
	
	/**
	 * Notifies this widget that the mouse has been pressed while inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public WWidget onMouseDown(int x, int y, int button) {
		return this;
	}
	
	/**
	 * Notifies this widget that the mouse has been moved while pressed and inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public void onMouseDrag(int x, int y, int button) {
	}
	
	/**
	 * Notifies this widget that the mouse has been released while inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public WWidget onMouseUp(int x, int y, int button) {
		return this;
	}
	
	/**
	 * Notifies this widget that the mouse has been pressed and released, both while inside its bounds.
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public void onClick(int x, int y, int button) {
	}
	
	/**
	 * Creates "heavyweight" component peers
	 * @param c the top-level Container that will hold the peers
	 */
	public void createPeers(ConcreteContainer c) {
	}

	@SideOnly(Side.CLIENT)
	public void paintBackground(int x, int y) {
	}

	@SideOnly(Side.CLIENT)
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		if (renderTooltip && mouseX >= x && mouseX < x+getWidth() && mouseY >= y && mouseY < y+getHeight()) {
			renderTooltip(mouseX-x+getX(),mouseY-y+getY() );
		}
	}

	/**
	 * Internal method to conditionally render tooltip data. This requires an overriden {@link #addInformation(List)
	 * addInformation} method to insert data into the tooltip - without this, the method returns early, because no work
	 * is needing to be done on an empty list.
	 * @param tX The adjusted X coordinate at which to render the tooltip.
	 * @param tY The adjusted X coordinate at which to render the tooltip.
	 */
	@SideOnly(Side.CLIENT)
	protected void renderTooltip(int tX, int tY) {
		List<String> info = new ArrayList<>();
		addInformation(info);

		if (info.size() == 0)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		int width = mc.displayWidth;
		int height = mc.displayHeight;

		GuiUtils.drawHoveringText(info, tX, tY, width, height, -1, mc.fontRenderer);
	}
	
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Creates component peers, lays out children, and initializes animation data for this Widget and all its children.
	 * The host container must clear any heavyweight peers from its records before this method is called.
	 */
	public void validate(ConcreteContainer host) {
		valid = true;
	}
	
	/**
	 * Marks this Widget as having dirty state; component peers may need to be recreated, children adapted to a new size,
	 * and animation data reset.
	 */
	public void invalidate() {
		valid = false;
	}

	/**
	 * Adds information to this widget's tooltip. This requires a call to {@link #setRenderTooltip(boolean)
	 * setRenderTooltip} (obviously passing in {@code true}), in order to enable the rendering of your tooltip.
	 * @param information List containing all previous tooltip data.
	 */
	public void addInformation(List<String> information) {
	}

}
