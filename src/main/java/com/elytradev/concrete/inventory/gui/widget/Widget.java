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

import com.elytradev.concrete.inventory.gui.ConcreteContainer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Widget {
	private boolean valid = false;
	private WPanel parent;
	private int x = 0;
	private int y = 0;
	private int width = 18;
	private int height = 18;
	
	/**
	 * Relocates this widget, relative to its parent.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy.
	 *
	 * @param x the new X coordinate of this widget
	 * @param y the new Y coordinate of this widget
	 * @see #getX
	 * @see #getY
	 * @see #invalidate
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		invalidate();
	}
	
	/**
	 * Resizes this widget.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy.
	 *
	 * @param width the new width of this widget
	 * @param height the new height of this widget
	 * @see #getWidth
	 * @see #getHeight
	 * @see #invalidate
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		invalidate();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
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
	
	public WPanel getParent() {
		return parent;
	}
	
	/**
	 * Draws the background of this Widget at the specified coordinates.
	 * <p>
	 * The coordinates provided are the top-level device coordinates of this
	 * widget's topleft corner, so don't translate by the widget X/Y! That's
	 * already been done. Your "valid" drawing space is from (x, y) to
	 * (x + width - 1, y + height - 1) inclusive. However, no scissor or depth
	 * masking is done, so please take care to respect your boundaries.
	 *
	 * @param x The X coordinate of the leftmost pixels of this widget in
	 *          device (opengl) coordinates
	 * @param y The Y coordinate of the topmost pixels of this widget in
	 *          device (opengl) coordinates
	 * @see #paintForeground
	 */
	@SideOnly(Side.CLIENT)
	public void paintBackground(int x, int y) {}
	
	/**
	 * Draws the foreground of this Widget at the specified coordinates.
	 * <p>
	 * The coordinates provided are the top-level device coordinates of this
	 * widget's topleft corner, so don't translate by the widget X/Y! That's
	 * already been done. Your "valid" drawing space is from (x, y) to
	 * (x + width - 1, y + height - 1) inclusive. However, no scissor or depth
	 * masking is done, so please take care to respect your boundaries.
	 *
	 * @param x The X coordinate of the leftmost pixels of this widget in
	 *          device (opengl) coordinates
	 * @param y The Y coordinate of the topmost pixels of this widget in
	 *          device (opengl) coordinates
	 * @see #paintBackground
	 */
	@SideOnly(Side.CLIENT)
	public void paintForeground(int x, int y) {}
	
	/**
	 * Determines whether this widget is valid.
	 * <p>
	 * A widget is valid when it is correctly sized and positioned within its
	 * parent panel and all its children are also valid.
	 *
	 * @return <code>true</code> if the widget is valid, <code>false</code> otherwise
	 * @see #validate
	 * @see #invalidate
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Validates this widget.
	 * <p>
	 * The meaning of the term <i>validating</i> is defined by the ancestors of
	 * this class.
	 *
	 * @param host the top-level container that will hold peers
	 * @see #invalidate
	 * @see WPanel#validate
	 * @see WItemSlots#validate
	 * @see WFluidBar#validate
	 */
	public void validate(ConcreteContainer host) {
		valid = true;
	}
	
	/**
	 * Invalidates this widget and its ancestors.
	 * <p>
	 * All the ancestors of the widget up to the top-most panel of the
	 * hierarchy are marked invalid. Marking a panel <i>invalid</i> indicates
	 * that the panel needs to be laid out.
	 * <p>
	 * This method is called automatically when any layout-related information
	 * changes (e.g. setting the size of the widget, or adding the widget to a
	 * panel). Widget peers may need to be recreated, children adapted to a new
	 * size, animation data reset, etc.
	 *
	 * @see #validate
	 */
	public void invalidate() {
		valid = false;
		if (parent != null) {
			parent.invalidate();
		}
	}
}
