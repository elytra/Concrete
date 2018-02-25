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

import java.util.List;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Comparable to swing's JPanel, except that this is the base class for containers too - there's no way to make a
 * WContainer such that it isn't confused with Container, and we don't lose anything from the lack of abstraction.
 */
public class WPanel extends WWidget {
	protected final List<WWidget> children = Lists.newArrayList();
	protected boolean valid;
	
	@Override
	public void createPeers(ConcreteContainer c) {
		for(WWidget child : children) {
			child.createPeers(c);
		}
		valid = true;
	}
	
	public void remove(WWidget w) {
		children.remove(w);
		valid = false;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	/**
	 * Uses this Panel's layout rules to reposition and resize components to fit nicely in the panel.
	 */
	public void layout() {
		for(WWidget child : children) {
			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child);
		}
	}
	
	protected void expandToFit(WWidget w) {
		int pushRight = w.getX()+w.getWidth();
		int pushDown =  w.getY()+w.getHeight();
		this.setSize(Math.max(this.getWidth(), pushRight), Math.max(this.getHeight(), pushDown));
	}
	
	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		if (children.isEmpty()) return super.onMouseUp(x, y, button);
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				return child.onMouseUp(x-child.getX(), y-child.getY(), button);
			}
		}
		return super.onMouseUp(x, y, button);
	}
	
	@Override
	public WWidget onMouseDown(int x, int y, int button) {
		if (children.isEmpty()) return super.onMouseDown(x, y, button);
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				return child.onMouseDown(x-child.getX(), y-child.getY(), button);
			}
		}
		return super.onMouseDown(x, y, button);
	}
	
	@Override
	public void onMouseDrag(int x, int y, int button) {
		if (children.isEmpty()) return;
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				child.onMouseDrag(x-child.getX(), y-child.getY(), button);
				return; //Only send the message to the first valid recipient
			}
		}
		super.onMouseDrag(x, y, button);
	}
	
	@Override
	public void onClick(int x, int y, int button) {
		if (children.isEmpty()) return;
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				child.onClick(x-child.getX(), y-child.getY(), button);
				return; //Only send the message to the first valid recipient
			}
		}
	}
	
	@Override
	public void validate(ConcreteContainer c) {
		layout();
		createPeers(c);
		valid = true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		for(WWidget child : children) {
			child.paintBackground(x + child.getX(), y + child.getY());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		for(WWidget child : children) {
			child.paintForeground(x + child.getX(), y + child.getY(), mouseX, mouseY);
		}
	}
}
