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

import com.elytradev.concrete.inventory.ConcreteFluidTank;
import com.elytradev.concrete.inventory.FakeFluidSlot;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WFluidBar extends WWidget {
    private ResourceLocation bg;
    private int field;
    private int max = 100;
    private ConcreteFluidTank concreteFluidTank;
    private Direction direction;

    public WFluidBar(ResourceLocation bg, ConcreteFluidTank tank, int field, int maxfield) {
        this(bg, tank, field, maxfield, Direction.UP);
    }


    public WFluidBar(ResourceLocation bg, ConcreteFluidTank tank, int field, int maxfield, Direction dir) {
        this.bg = bg;
        this.concreteFluidTank = tank;
        this.field = field;
        this.max = maxfield;
        this.direction = dir;
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
        c.addSlotPeer(new FakeFluidSlot(concreteFluidTank));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void paintBackground(int x, int y) {
        GuiDrawing.rect(bg, x, y, getWidth(), getHeight(), 0xFFFFFFFF);

        // Salvaged Drawing Code From WBar
        /*float percent = inventory.getField(field)/(float)inventory.getField(max);
        if (percent<0) percent=0f;
        if (percent>1) percent=1f;

        int barMax = getWidth();
        if (direction==Direction.DOWN || direction==Direction.UP) barMax = getHeight();
        percent = ((int)(percent*barMax)) / (float)barMax; //Quantize to bar size

        int barSize = (int)(barMax*percent);
        if (barSize<=0) return;

        switch(direction) { //anonymous blocks in this switch statement are to sandbox variables
            case UP: {
                int left = x;
                int top = y + getHeight();
                top -= barSize;
                GuiDrawing.rect(bar, left, top, getWidth(), barSize, 0, 1-percent, 1, 1, 0xFFFFFFFF);
            }
            break;
            case RIGHT: {
                GuiDrawing.rect(bar, x, y, barSize, getHeight(), 0, 0, percent, 1, 0xFFFFFFFF);
            }
            break;
            case DOWN: {
                GuiDrawing.rect(bar, x, y, getWidth(), barSize, 0, 0, 1, percent, 0xFFFFFFFF);
            }
            break;
            case LEFT: {
                int left = x + getWidth();
                int top = y;
                left -= barSize;
                GuiDrawing.rect(bar, left, top, barSize, getHeight(), 1-percent, 0, 1, 1, 0xFFFFFFFF);
            }
            break;
        }


        //GuiDrawing.rect(bar, x, y+(getHeight()-barHeight), getWidth(), barHeight, 0xFFFFFFFF);

        //GuiDrawing.drawString(""+inventory.getField(field)+"/", x+18, y+9, 0xFF000000);
        //GuiDrawing.drawString(""+inventory.getField(max)+"", x+32, y+9, 0xFF000000);*/
    }

    public static enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT;
    }
}
