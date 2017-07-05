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

/**
 * Like a JPanel with a BoxLayout
 *
 * @see Axis#X
 * @see Axis#Y
 */
public class WAxisAlignedPanel extends WPanel {
	public static final int DEFAULT_SPACING = 4;
	
	private final Axis axis;
	private int spacing = DEFAULT_SPACING;
	
	public WAxisAlignedPanel(Axis axis) {
		this.axis = axis;
	}
	
	/**
	 * Sets the spacing between widgets of this panel.
	 * <p>
	 * This method changes layout-related information, and therefore,
	 * invalidates the panel hierarchy. If the panel has already been
	 * validated, the hierarchy must be re-validated thereafter in order to
	 * reflect the changes.
	 *
	 * @see #invalidate
	 * @see #validate
	 */
	public void setSpacing(int spacing) {
		this.spacing = spacing;
		invalidate();
	}
	
	@Override
	public void pack() {
		int sizeAlongAxis = 0;
		int sizePerpendicularToAxis = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			if (i > 0) {
				sizeAlongAxis += spacing;
			}
			sizeAlongAxis += axis.getSizeAlongAxis(w);
			sizePerpendicularToAxis = Math.max(axis.getSizePerpendicularToAxis(w), sizePerpendicularToAxis);
		}
		axis.setSize(this, sizeAlongAxis, sizePerpendicularToAxis);
	}
	
	@Override
	protected void layout() {
		int unresizable = 0;
		int numResizable = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			if (w.isResizable()) {
				numResizable++;
			} else {
				unresizable += axis.getSizeAlongAxis(w);
			}
		}
		int resizeSpace = axis.getSizeAlongAxis(this) - unresizable;
		int resizeEach;
		if (numResizable > 0) {
			resizeEach = resizeSpace / numResizable;
			if (resizeEach < 18) {
				resizeEach = 18; //Don't squish things beyond a minimum reasonable size! Better for them to overlap :/
			}
		} else {
			resizeEach = 0; //None of them are resizable, so set this to an arbitrary value
		}
		
		int centerline = axis.getSizePerpendicularToAxis(this) / 2;
		int curOffset = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			if (w.isResizable()) {
				axis.setSize(w, resizeEach, axis.getSizePerpendicularToAxis(this));
			}
			axis.setLocation(w, curOffset, centerline - (axis.getSizePerpendicularToAxis(w) / 2));
			curOffset += axis.getSizeAlongAxis(w) + spacing;
		}
		
		//Layout children after parents
		super.layout();
	}
	
	public static enum Axis {
		/**
		 * Specifies that components should be laid out left to right.
		 * <ul>
		 * <li>If all children are non-resizable, they will wind up equally
		 * spaced horizontally, with their vertical position centered on the
		 * panel's centerline.</li>
		 * <li>If all children are resizable, they will wind up taking up the
		 * exact height of the panel, and each widget will split the panel's
		 * horizontal space equally.</li>
		 * <li>If some children are resizable and some children are not, each
		 * resizable child will attempt to grow equally to fill the unoccupied
		 * horizontal space (and all resizable children will still be the exact
		 * height of the panel).</li>
		 * </ul>
		 */
		X {
			@Override
			public int getSizeAlongAxis(WWidget w) {
				return w.getWidth();
			}
			
			@Override
			public int getSizePerpendicularToAxis(WWidget w) {
				return w.getHeight();
			}
			
			@Override
			public void setSize(WWidget w, int alongAxis, int perpendicularToAxis) {
				w.setSize(alongAxis, perpendicularToAxis);
			}
			
			@Override
			public void setLocation(WWidget w, int alongAxis, int perpendicularToAxis) {
				w.setLocation(alongAxis, perpendicularToAxis);
			}
		},
		
		/**
		 * Specifies that components should be laid out top to bottom.
		 * <ul>
		 * <li>If all children are non-resizable, they will wind up equally
		 * spaced vertically, with their horizontal position centered on the
		 * panel's centerline.</li>
		 * <li>If all children are resizable, they will wind up taking up the
		 * exact width of the panel, and each widget will split the panel's
		 * vertical space equally.</li>
		 * <li>If some children are resizable and some children are not, each
		 * resizable child will attempt to grow equally to fill the unoccupied
		 * vertical space (and all resizable children will still be the exact
		 * width of the panel).</li>
		 * </ul>
		 */
		Y {
			@Override
			public int getSizeAlongAxis(WWidget w) {
				return w.getHeight();
			}
			
			@Override
			public int getSizePerpendicularToAxis(WWidget w) {
				return w.getWidth();
			}
			
			@Override
			public void setSize(WWidget w, int alongAxis, int perpendicularToAxis) {
				w.setSize(perpendicularToAxis, alongAxis);
			}
			
			@Override
			public void setLocation(WWidget w, int alongAxis, int perpendicularToAxis) {
				w.setLocation(perpendicularToAxis, alongAxis);
			}
		};
		
		public abstract int getSizeAlongAxis(WWidget w);
		
		public abstract int getSizePerpendicularToAxis(WWidget w);
		
		public abstract void setSize(WWidget w, int alongAxis, int perpendicularToAxis);
		
		public abstract void setLocation(WWidget w, int alongAxis, int perpendicularToAxis);
	}
}
