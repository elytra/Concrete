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

import org.lwjgl.opengl.GL11;

import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Simple static image display where "no image" is a valid image option, and which can be reassigned while the gui is
 * still open.
 */
public class WSwappableImage extends WWidget {
	protected ResourceLocation image;
	
	public WSwappableImage(ResourceLocation loc) {
		this.image = loc;
	}
	
	public WSwappableImage() {
		this.image = null;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	public void setImage(ResourceLocation image) {
		this.image = image;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (image!=null) {
			rect(image, x, y, getWidth(), getHeight(), 0,0,1,1);
		}
	}
	
	protected static void rect(ResourceLocation texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		//GlStateManager.translate(0, 0, -10);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		GlStateManager.color(1, 1, 1, 1.0f);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0.0D).tex(u1, v2).endVertex();
		buffer.pos(left + width, top + height, 0.0D).tex(u2, v2).endVertex();
		buffer.pos(left + width, top,          0.0D).tex(u2, v1).endVertex();
		buffer.pos(left,         top,          0.0D).tex(u1, v1).endVertex();
		tessellator.draw();
		
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
	}
}