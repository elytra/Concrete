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

package com.elytradev.concrete.inventory.gui.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public final class GuiDrawing {
	private GuiDrawing() {}
	
	public static void rect(ResourceLocation texture, int left, int top, int width, int height, int color) {
		rect(texture, left, top, width, height, 0, 0, 1, 1, color);
	}
	
	public static void rect(ResourceLocation texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		//float scale = 0.00390625F;
		
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		//GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, 1.0f);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0.0D).tex(u1, v2).endVertex();
		buffer.pos(left + width, top + height, 0.0D).tex(u2, v2).endVertex();
		buffer.pos(left + width, top,          0.0D).tex(u2, v1).endVertex();
		buffer.pos(left,         top,          0.0D).tex(u1, v1).endVertex();
		tessellator.draw();
		//GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	/**
	 * Draws an untextured rectangle of the specified RGB color.
	 */
	public static void rect(int left, int top, int width, int height, int color) {
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0.0D).endVertex();
		buffer.pos(left + width, top + height, 0.0D).endVertex();
		buffer.pos(left + width, top,          0.0D).endVertex();
		buffer.pos(left,         top,          0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	/**
	 * Draws a rectangle for a Fluid, because fluids are tough.
	 */
	public static void rect(Fluid fluid, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
		ResourceLocation fluidTexture = fluid.getStill();

		TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (width <= 0) width = 1;
		if (height <= 0) height = 1;

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		//GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0.0D).tex(tas.getInterpolatedU(u1), tas.getInterpolatedV(v2)).endVertex();
		buffer.pos(left + width, top + height, 0.0D).tex(tas.getInterpolatedU(u2), tas.getInterpolatedV(v2)).endVertex();
		buffer.pos(left + width, top,          0.0D).tex(tas.getInterpolatedU(u2), tas.getInterpolatedV(v1)).endVertex();
		buffer.pos(left,         top,          0.0D).tex(tas.getInterpolatedU(u1), tas.getInterpolatedV(v1)).endVertex();
		tessellator.draw();
		//GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void rect(Fluid fluid, int left, int top, int width, int height, int color) {
		rect(fluid, left, top, width, height, 0, 0, 16, 16, color);
	}
	/**
	 * Draws a beveled, round rectangle that is substantially similar to default Minecraft UI panels.
	 */
	public static void drawGuiPanel(int x, int y, int width, int height) {
		drawGuiPanel(x, y, width, height, 0x555555, 0xC6C6C6, 0xFFFFFF, 0x000000);
	}
	
	
	public static void drawGuiPanel(int x, int y, int width, int height, int shadow, int panel, int hilight, int outline) {
		rect(x + 3,         y + 3,          width - 6, height - 6, panel); //Main panel area
		
		rect(x + 2,         y + 1,          width - 4, 2,          hilight); //Top hilight
		rect(x + 2,         y + height - 3, width - 4, 2,          shadow); //Bottom shadow
		rect(x + 1,         y + 2,          2,         height - 4, hilight); //Left hilight
		rect(x + width - 3, y + 2,          2,         height - 4, shadow); //Right shadow
		rect(x + width - 3, y + 2,          1,         1,          panel); //Topright non-hilight/non-shadow transition pixel
		rect(x + 2,         y + height - 3, 1,         1,          panel); //Bottomleft non-hilight/non-shadow transition pixel
		rect(x + 3,         y + 3,          1,         1,          hilight); //Topleft round hilight pixel
		rect(x + width - 4, y + height - 4, 1,         1,          shadow); //Bottomright round shadow pixel
		
		rect(x + 2,         y,              width - 4, 1,          outline); //Top outline
		rect(x,             y + 2,          1,         height - 4, outline); //Left outline
		rect(x + width - 1, y + 2,          1,         height - 4, outline); //Right outline
		rect(x + 2,         y + height - 1, width - 4, 1,          outline); //Bottom outline
		rect(x + 1,         y + 1,          1,         1,          outline); //Topleft round pixel
		rect(x + 1,         y + height - 2, 1,         1,          outline); //Bottomleft round pixel
		rect(x + width - 2, y + 1,          1,         1,          outline); //Topright round pixel
		rect(x + width - 2, y + height - 2, 1,         1,          outline); //Bottomright round pixel
	}

	/**
	 * Draws a default-sized recessed itemslot panel
	 */
	public static void drawBeveledPanel(int x, int y) {
		drawBeveledPanel(x, y, 18, 18, 0x373737, 0x8b8b8b, 0xFFFFFF);
	}
	
	/**
	 * Draws a default-color recessed itemslot panel of variable size
	 */
	public static void drawBeveledPanel(int x, int y, int width, int height) {
		drawBeveledPanel(x, y, width, height, 0x373737, 0x8b8b8b, 0xFFFFFF);
	}
	
	/**
	 * Draws a generalized-case beveled panel. Can be inset or outset depending on arguments.
	 * @param x				x coordinate of the topleft corner
	 * @param y				y coordinate of the topleft corner
	 * @param width			width of the panel
	 * @param height		height of the panel
	 * @param topleft		color of the top/left bevel
	 * @param panel			color of the panel area
	 * @param bottomright	color of the bottom/right bevel
	 */
	/*
	public static void drawBeveledPanel(int x, int y, int width, int height, int topleft, int panel, int bottomright) {
		rect(x,             y,              width,     height,     0x8b8b8b); //Center panel
		rect(x,             y,              width - 1, 1,          0x373737); //Top shadow
		rect(x,             y + 1,          1,         height - 2, 0x373737); //Left shadow
		rect(x + width - 1, y + 1,          1,         height - 1, 0xFFFFFF); //Right hilight
		rect(x + 1,         y + height - 1, width - 1, 1,          0xFFFFFF); //Bottom hilight
	}*/
	public static void drawBeveledPanel(int x, int y, int width, int height, int topleft, int panel, int bottomright) {
		rect(x,             y,              width,     height,     panel); //Center panel
		rect(x,             y,              width - 1, 1,          topleft); //Top shadow
		rect(x,             y + 1,          1,         height - 2, topleft); //Left shadow
		rect(x + width - 1, y + 1,          1,         height - 1, bottomright); //Right hilight
		rect(x + 1,         y + height - 1, width - 1, 1,          bottomright); //Bottom hilight
	}
	
	
	public static void drawString(String s, int x, int y, int color) {
		Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, color);
	}

	public static void drawTooltip(String s, int x, int y) {

	}
	
	public static int colorAtOpacity(int opaque, float opacity) {
		if (opacity<0.0f) opacity=0.0f;
		if (opacity>1.0f) opacity=1.0f;
		
		int a = (int)(opacity * 255.0f);
		
		return (opaque & 0xFFFFFF) | (a << 24);
	}
}
