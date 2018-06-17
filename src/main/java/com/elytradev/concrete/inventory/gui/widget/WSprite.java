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

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.util.ResourceLocation;

/**
 * An image display that draws multiple images from a single sprite sheet. Can be set to freeze on one frame, or animate
 * several, optionally with their own per-frame delays. Can be changed dynamically while the gui is still open.
 */
public class WSprite extends WWidget {
	protected ResourceLocation spriteSheet;
	protected int[] frames = new int[0];
	protected int[] delays = null;
	protected int curFrame;
	protected int delay;
	protected int framesWide;
	protected int framesHigh;
	protected float frameWidth;
	protected float frameHeight;
	protected long lastFrame = 0L;
	protected long elapsed = 0L;
	
	public WSprite(ResourceLocation spriteSheet, int imageWidth, int imageHeight, int spritesWide, int spritesHigh) {
		this.spriteSheet = spriteSheet;
		this.frameWidth = 1 / (float)spritesWide;
		this.frameHeight = 1 / (float)spritesHigh;
		this.curFrame = 0;
		this.framesWide = spritesWide;
		this.framesHigh = spritesHigh;
		this.delay = 50;
	}
	
	public WSprite setAnimation(int[] frameList, int frameDelay) {
		this.frames = frameList;
		this.delay = frameDelay;
		this.delays = null;
		curFrame = 0;
		return this;
	}
	
	public WSprite setDelays(int[] delayList) {
		this.delays = delayList;
		return this;
	}
	
	public WSprite setAnimationToEntireSheet(int frameDelay) {
		frames = new int[framesWide*framesHigh];
		for(int i=0; i<frames.length; i++) frames[i] = i;
		this.delay = frameDelay;
		
		return this;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		long now = System.nanoTime() / 1_000_000L;
		elapsed += now-lastFrame;
		int targetDelay = delay;
		if (delays!=null && delays.length>0) targetDelay = delays[curFrame%delays.length];
		
		if (elapsed>targetDelay) {
			elapsed = 0L; //At most, advance by one frame per frame
			curFrame++;
			if (curFrame>=frames.length) curFrame = 0;
		}
		lastFrame = now;
		
		if (curFrame<frames.length) {
			int frame = frames[curFrame];
			float u = frameWidth * (frame%framesWide);
			float v = frameHeight * (frame/framesWide);
			
			GuiDrawing.rect(spriteSheet, x, y, getWidth(), getHeight(), u, v, u+frameWidth, v+frameHeight, 0xFFFFFFFF);
		}
	}
}
