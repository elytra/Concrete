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
 */
public class WDirectionalPanel extends WPanel {
	public static final int DEFAULT_SPACING = 4;
	
	private final Direction direction;
	private int spacing = DEFAULT_SPACING;
	
	public WDirectionalPanel(Direction direction) {
		this.direction = direction;
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
		int directionSpace = 0;
		int centeredSpace = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			if (i > 0) {
				directionSpace += spacing;
			}
			directionSpace += direction.getDirectionSpace(w);
			centeredSpace = Math.max(direction.getCenteredSpace(w), centeredSpace);
		}
		direction.setSize(this, directionSpace, centeredSpace);
	}
	
	@Override
	protected void layout() {
		int unresizable = 0;
		int numResizable = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			if (w.canResize()) {
				numResizable++;
			} else {
				unresizable += direction.getDirectionSpace(w);
			}
		}
		int resizeSpace = direction.getDirectionSpace(this) - unresizable;
		int resizeEach;
		if (numResizable > 0) {
			resizeEach = resizeSpace / numResizable;
			if (resizeEach < 18) {
				resizeEach = 18; //Don't squish things beyond a minimum reasonable size! Better for them to overlap :/
			}
		} else {
			resizeEach = 0; //None of them are resizable, so set this to an arbitrary value
		}
		
		int centerline = direction.getCenteredSpace(this) / 2;
		int curOffset = 0;
		for (int i = 0; i < getWidgetCount(); i++) {
			WWidget w = getWidget(i);
			if (w.canResize()) {
				direction.setSize(w, resizeEach, direction.getCenteredSpace(this));
			}
			direction.setLocation(w, curOffset, centerline - (direction.getCenteredSpace(w) / 2));
			curOffset += direction.getDirectionSpace(w) + spacing;
		}
		
		//Layout children after parents
		super.layout();
	}
	
	public static enum Direction {
		/**
		 * <ul>
		 * <li>If all children are non-resizable, they will wind up equally
		 * spaced horizontally, with their vertical position centered on the
		 * panel's centerline.
		 * <li>If all children are resizable, they will wind up taking up the
		 * exact height of the panel, and each widget will split the panel's
		 * horizontal space equally.
		 * <li>If some children are resizable and some children are not, each
		 * resizable child will attempt to grow equally to fill the unoccupied
		 * horizontal space (and all resizable children will still be the exact
		 * height of the panel).
		 * </ul>
		 */
		HORIZONTAL {
			@Override
			public int getDirectionSpace(WWidget w) {
				return w.getWidth();
			}
			
			@Override
			public int getCenteredSpace(WWidget w) {
				return w.getHeight();
			}
			
			@Override
			public void setSize(WWidget w, int directionSize, int centeredSize) {
				w.setSize(directionSize, centeredSize);
			}
			
			@Override
			public void setLocation(WWidget w, int directionCoord, int centeredCoord) {
				w.setLocation(directionCoord, centeredCoord);
			}
		},
		
		/**
		 * <ul>
		 * <li>If all children are non-resizable, they will wind up equally
		 * spaced vertically, with their horizontal position centered on the
		 * panel's centerline.
		 * <li>If all children are resizable, they will wind up taking up the
		 * exact width of the panel, and each widget will split the panel's
		 * vertical space equally.
		 * <li>If some children are resizable and some children are not, each
		 * resizable child will attempt to grow equally to fill the unoccupied
		 * vertical space (and all resizable children will still be the exact
		 * width of the panel).
		 * </ul>
		 */
		VERTICAL {
			@Override
			public int getDirectionSpace(WWidget w) {
				return w.getHeight();
			}
			
			@Override
			public int getCenteredSpace(WWidget w) {
				return w.getWidth();
			}
			
			@Override
			public void setSize(WWidget w, int directionSize, int centeredSize) {
				w.setSize(centeredSize, directionSize);
			}
			
			@Override
			public void setLocation(WWidget w, int directionCoord, int centeredCoord) {
				w.setLocation(centeredCoord, directionCoord);
			}
		};
		
		public abstract int getDirectionSpace(WWidget w);
		
		public abstract int getCenteredSpace(WWidget w);
		
		public abstract void setSize(WWidget w, int directionSize, int centeredSize);
		
		public abstract void setLocation(WWidget w, int directionCoord, int centeredCoord);
	}
}
