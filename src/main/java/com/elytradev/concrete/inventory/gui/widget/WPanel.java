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
import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Comparable to Swing's JPanel, except that this is the base class for
 * Containers too - there's no way to make a WContainer such that it isn't
 * confused with Container, and we don't lose anything from the lack of abstraction.
 */
public class WPanel extends WWidget {
	public static final int DEFAULT_MINIMUM_SIZE = 18;
	
	private final List<WWidget> children = Lists.newArrayList();
	private int minWidth = DEFAULT_MINIMUM_SIZE;
	private int minHeight = DEFAULT_MINIMUM_SIZE;
	
	/**
	 * Gets the number of widgets in this panel.
	 *
	 * @return the number of widgets in this panel
	 * @see #getWidget
	 */
	public int getWidgetCount() {
		return children.size();
	}
	
	/**
	 * Gets the nth widget in this panel.
	 *
	 * @param n the index of the widget to get
	 * @return the n<sup>th</sup> widget in this panel
	 */
	public WWidget getWidget(int n) {
		return children.get(n);
	}
	
	/**
	 * Appends the specified widget to the end of this panel.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy. If the panel has already been
	 * validated, the hierarchy must be re-validated thereafter in order to
	 * properly display the added widget.
	 *
	 * @param w the widget to be added
	 * @see #invalidate
	 * @see #validate
	 */
	public void add(WWidget w) {
		add(w, -1);
	}
	
	/**
	 * Appends the specified widget to this panel at the given position.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy. If the panel has already been
	 * validated, the hierarchy must be re-validated thereafter in order to
	 * properly display the added widget.
	 *
	 * @param w     the widget to be added
	 * @param index the position at which to insert the widget, or
	 *              <code>-1</code> to append the widget to the end
	 * @see #remove
	 * @see #invalidate
	 * @see #validate
	 */
	public void add(WWidget w, int index) {
		if (index != -1) {
			children.add(index, w);
		} else {
			children.add(w);
		}
		w.setParent(this);
		invalidate();
	}
	
	/**
	 * Removes the widget, specified by <code>index</code>, from this panel.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy. If the panel has already been
	 * validated, the hierarchy must be re-validated thereafter in order to
	 * reflect the changes.
	 *
	 * @param index the index of the widget to be removed
	 * @see #add
	 * @see #invalidate
	 * @see #validate
	 * @see #getWidgetCount
	 */
	public void remove(int index) {
		children.remove(index);
		invalidate();
	}
	
	/**
	 * Removes the specified widget from this panel.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy. If the panel has already been
	 * validated, the hierarchy must be re-validated thereafter in order to
	 * reflect the changes.
	 *
	 * @param w the widget to be removed
	 * @see #add
	 * @see #invalidate
	 * @see #validate
	 */
	public void remove(WWidget w) {
		children.remove(w);
		invalidate();
	}
	
	/**
	 * Causes this panel to be sized to fit the preferred size of its widgets.
	 * The resulting width and height of the panel are automatically enlarged
	 * if either of dimensions is less than the minimum size as specified by
	 * the previous call to the {@code setMinimumSize} method.
	 *
	 * @see #setMinimumSize
	 */
	public void pack() {
		int width = 0;
		int height = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			width = Math.max(width, w.getX() + w.getWidth());
			height = Math.max(height, w.getY() + w.getHeight());
		}
		setSize(width, height);
	}
	
	/**
	 * Sets the minimum size of this panel to a constant value. If the panel's
	 * current size is less than the minimum size, the size of the panel is
	 * automatically enlarged to honor the minimum size.
	 * <p>
	 * If the {@code setSize} method is called afterwards with a width or
	 * height less than that was specified by the {@code setMinimumSize}
	 * method, the panel is automatically enlarged to meet the minimum size.
	 * The minimum size value also affects the behaviour of the {@code pack} method.
	 *
	 * @param minWidth the new minimum width of this panel
	 * @param minHeight the new minimum height of this panel
	 * @see #getMinimumWidth
	 * @see #getMinimumHeight
	 * @see #setSize
	 * @see #pack
	 */
	public void setMinimumSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		setSize(getWidth(), getHeight());
	}
	
	public int getMinimumWidth() {
		return minWidth;
	}
	
	public int getMinimumHeight() {
		return minHeight;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The width and height are automatically enlarged if either is less than
	 * the minimum size as specified by previous call to {@code setMinimumSize}.
	 *
	 * @see #getWidth
	 * @see #getHeight
	 * @see #setMinimumSize
	 */
	@Override
	public void setSize(int width, int height) {
		width = Math.max(width, getMinimumWidth());
		height = Math.max(height, getMinimumHeight());
		super.setSize(width, height);
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	/**
	 * Uses this panel's layout rules to reposition and resize widgets to fit nicely in the panel.
	 *
	 * @see #validate
	 */
	protected void layout() {}
	
	/**
	 * Validates this panel and all of its children.
	 * <p>
	 * Validating a panel means laying out its children. Layout-related
	 * changes, such as adding a widget to the panel, invalidate the panel
	 * automatically. Note that the ancestors of the panel may be invalidated
	 * also. (See {@link WWidget#invalidate} for details.) Therefore, to restore
	 * the validity of the hierarchy, the {@code validate()} method should be
	 * invoked on the top-most invalid panel of the hierarchy.
	 * <p>
	 * The host container must clear any heavyweight peers from its records
	 * before this method is called.
	 *
	 * @param host the top-level container that will hold peers
	 * @see #add
	 * @see #invalidate
	 * @see #layout
	 */
	@Override
	public void validate(ConcreteContainer host) {
		layout();
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget child = getWidget(i);
			child.validate(host);
		}
		super.validate(host);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget child = getWidget(i);
			child.paintBackground(x + child.getX(), y + child.getY());
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintForeground(int x, int y) {
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget child = getWidget(i);
			child.paintForeground(x + child.getX(), y + child.getY());
		}
	}
}
