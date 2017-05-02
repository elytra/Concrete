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
import com.elytradev.concrete.inventory.FluidTankProxySlot;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.client.GuiDrawing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WFluidBar extends WWidget {
    private ResourceLocation bg;
    private ConcreteFluidTank concreteFluidTank;
    private Direction direction;

    public WFluidBar(ResourceLocation bg, ConcreteFluidTank tank) {
        this(bg, tank, Direction.UP);
    }


    public WFluidBar(ResourceLocation bg, ConcreteFluidTank tank, Direction dir) {
        this.bg = bg;
        this.concreteFluidTank = tank;
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
        c.addSlotPeer(new FluidTankProxySlot(concreteFluidTank));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void paintBackground(int x, int y) {
        GuiDrawing.rect(bg, x, y, getWidth(), getHeight(), 0xFFFFFFFF);

        if(concreteFluidTank.getFluid() == null)
            return;

        float percent = concreteFluidTank.getFluidAmount()/concreteFluidTank.getCapacity();
        if (percent<0) percent=0f;
        if (percent>1) percent=1f;

        int barMax = getWidth();
        if (direction==Direction.DOWN || direction==Direction.UP) barMax = getHeight();
        percent = ((int)(percent*barMax)) / (float)barMax; //Quantize to bar size

        int barSize = (int)(barMax*percent);
        if (barSize<=0) return;

        ResourceLocation fluidTexture = concreteFluidTank.getFluid().getFluid().getStill();

        switch(direction) { //anonymous blocks in this switch statement are to sandbox variables
            case UP: {
                int left = x;
                int top = y + getHeight();
                top -= barSize;
                int verticalSegments = barSize / 16;
                int horizontalSegments = getWidth() / 16;
                for(int dY=0;dY < verticalSegments;dY++)
                {
                    for(int dX=0;dX < horizontalSegments;dX++)
                    {
                        GuiDrawing.rect(fluidTexture, left+(dX*16), y+(dY*16), 16, 16, 0, 0, 1, 1, 0xFFFFFFFF);
                    }
                    GuiDrawing.rect(fluidTexture, left+(horizontalSegments*16), y+(dY*16), 16-(getWidth()%16), 16, 0, 0, 1-percent, 1, 0xFFFFFFFF);
                }

                for(int dX=0;dX < horizontalSegments;dX++)
                {
                    GuiDrawing.rect(fluidTexture, left+(dX*16), y+(verticalSegments*16), 16, 16-(barSize%16), 0, 0, 1, 1-percent, 0xFFFFFFFF);
                }
                GuiDrawing.rect(fluidTexture, left+(horizontalSegments*16), y+(verticalSegments*16), 16-(barSize%16), 16-(getHeight()%16), 0, 0, 1-percent, 1-percent, 0xFFFFFFFF);
            }
            break;
            case RIGHT: {
                GuiDrawing.rect(fluidTexture, x, y, barSize, getHeight(), 0, 0, percent, 1, 0xFFFFFFFF);
            }
            break;
            case DOWN: {
                GuiDrawing.rect(fluidTexture, x, y, getWidth(), barSize, 0, 0, 1, percent, 0xFFFFFFFF);
            }
            break;
            case LEFT: {
                int left = x + getWidth();
                int top = y;
                left -= barSize;
                GuiDrawing.rect(fluidTexture, left, top, barSize, getHeight(), 1-percent, 0, 1, 1, 0xFFFFFFFF);
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
