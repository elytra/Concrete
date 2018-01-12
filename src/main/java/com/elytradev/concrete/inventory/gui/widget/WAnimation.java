package com.elytradev.concrete.inventory.gui.widget;

import com.elytradev.concrete.inventory.gui.client.GuiDrawing;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WAnimation extends WWidget {

    private Integer currentFrameCount = 0;
    private long currentSwapCount = 0;
    private String id;
    private String animFolder;
    private Integer animLength;
    private Integer animSpeed;
    private long then;

    public WAnimation(String id, String animFolder, Integer animLength, Integer animSpeed) {
        //the mod ID, necessary for namespacing. I don't know how to grab this automatically so it's gotta be manual for now.
        this.id = id;
        //the folder, inside of /textures/gui, where the frames can be found.
        //frames should named as a single number starting from zero, ex. 0.png, 1.png, 2.png, etc.
        this.animFolder = animFolder;
        //the number of frames in the animation.
        //this count should be <final frame number> + 1
        this.animLength = animLength;
        //number of milliseconds each animation frame should be. Remember, 1 tick = 50 ms.
        this.animSpeed = animSpeed;
    }

    @Override
    public boolean canResize() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void paintBackground(int x, int y) {
        //grab the system time at the very start of the frame.
        long now = Minecraft.getSystemTime();

        //assemble and draw the frame calculated last iteration.
        String frameLoc = "textures/gui/" + animFolder + "/" + currentFrameCount + ".png";
        ResourceLocation currentFrameTex = new ResourceLocation(this.id, frameLoc);
        GuiDrawing.rect(currentFrameTex, x, y, getWidth(), getHeight(), 0xFFFFFFFF);

        //calculate how much time has elapsed since the last animation change, and change the frame if necessary.
        long elapsed = now - then;
        currentSwapCount += elapsed;
        if (currentSwapCount >= animSpeed) {
            currentFrameCount++;
            currentSwapCount = 0;
        }
        //if we've hit the end of the animation, go back to the beginning
        if (currentFrameCount >= animLength - 1) {
            currentFrameCount = 0;
        }

        //frame is over; this frame is becoming the last frame so now becomes then
        this.then = now;
    }
}