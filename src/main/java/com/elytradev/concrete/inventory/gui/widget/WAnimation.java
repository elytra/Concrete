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

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WAnimation extends WWidget {

    private int currentFrame= 0;
    private long currentFrameTime = 0;
    private ResourceLocation[] animLocs;
    private int frameTime;
    private long lastFrame;

    public WAnimation(int frameTime, ResourceLocation... animLocs) {
        //an array of ResourceLocations for each frame. Should be put in in order, of course.
        this.animLocs = animLocs;
        //number of milliseconds each animation frame should be. Remember, 1 tick = 50 ms.
        this.frameTime = frameTime;
    }

    @Override
    public boolean canResize() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void paintBackground(int x, int y) {
        //grab the system time at the very start of the frame.
        long now = System.nanoTime() / 1_000_000L;

        //check bounds so the ResourceLocation isn't passed a bad number
        boolean inBounds = (currentFrame >= 0) && (currentFrame < animLocs.length);
        if (!inBounds) currentFrame = 0;
        //assemble and draw the frame calculated last iteration.
        ResourceLocation currentFrameTex = animLocs[currentFrame];
        GuiDrawing.rect(currentFrameTex, x, y, getWidth(), getHeight(), 0xFFFFFFFF);

        //calculate how much time has elapsed since the last animation change, and change the frame if necessary.
        long elapsed = now - lastFrame;
        currentFrameTime += elapsed;
        if (currentFrameTime >= frameTime) {
            currentFrame++;
            //if we've hit the end of the animation, go back to the beginning
            if (currentFrame >= animLocs.length - 1) {
                currentFrame = 0;
            }
            currentFrameTime = 0;
        }

        //frame is over; this frame is becoming the last frame so write the time to lastFrame
        this.lastFrame = now;
    }
}