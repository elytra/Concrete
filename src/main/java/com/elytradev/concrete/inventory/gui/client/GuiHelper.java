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

public final class GuiHelper {
	private GuiHelper() {}
	
	public static void drawRectangle(ResourceLocation texture, int left, int top, int width, int height) {
		drawRectangle(texture, left, top, width, height, 0xFFFFFFFF);
	}
	
	public static void drawRectangle(ResourceLocation texture, int left, int top, int width, int height, int color) {
		drawRectangle(texture, left, top, width, height, 0, 0, 1, 1, color);
	}
	
	public static void drawRectangle(ResourceLocation texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2) {
		drawRectangle(texture, left, top, width, height, u1, v1, u2, v2, 0xFFFFFFFF);
	}
	
	public static void drawRectangle(ResourceLocation texture, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		//float scale = 0.00390625f;
		
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		float r = (color >> 16 & 255) / 255.0f;
		float g = (color >> 8 & 255) / 255.0f;
		float b = (color & 255) / 255.0f;
		float a = 1.0f;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		//GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0).tex(u1, v2).endVertex();
		buffer.pos(left + width, top + height, 0).tex(u2, v2).endVertex();
		buffer.pos(left + width, top,          0).tex(u2, v1).endVertex();
		buffer.pos(left,         top,          0).tex(u1, v1).endVertex();
		tessellator.draw();
		//GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	/**
	 * Draws an untextured rectangle of the specified RGB color. Alpha is always 1.0f.
	 */
	public static void drawRectangle(int left, int top, int width, int height, int color) {
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		float r = (color >> 16 & 255) / 255.0f;
		float g = (color >> 8 & 255) / 255.0f;
		float b = (color & 255) / 255.0f;
		float a = 1.0f;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0).endVertex();
		buffer.pos(left + width, top + height, 0).endVertex();
		buffer.pos(left + width, top,          0).endVertex();
		buffer.pos(left,         top,          0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	/**
	 * Draws a rectangle for a fluid, because fluids are tough.
	 */
	public static void drawRectangle(Fluid fluid, int left, int top, int width, int height, float u1, float v1, float u2, float v2) {
		drawRectangle(fluid, left, top, width, height, u1, v1, u2, v2, 0xFFFFFFFF);
	}

	/**
	 * Draws a rectangle for a fluid, because fluids are tough.
	 */
	public static void drawRectangle(Fluid fluid, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
		ResourceLocation fluidTexture = fluid.getStill();

		TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (width <= 0) width = 1;
		if (height <= 0) height = 1;

		float r = (color >> 16 & 255) / 255.0f;
		float g = (color >> 8 & 255) / 255.0f;
		float b = (color & 255) / 255.0f;
		float a = 1.0f;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		//GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0).tex(tas.getInterpolatedU(u1), tas.getInterpolatedV(v2)).endVertex();
		buffer.pos(left + width, top + height, 0).tex(tas.getInterpolatedU(u2), tas.getInterpolatedV(v2)).endVertex();
		buffer.pos(left + width, top,          0).tex(tas.getInterpolatedU(u2), tas.getInterpolatedV(v1)).endVertex();
		buffer.pos(left,         top,          0).tex(tas.getInterpolatedU(u1), tas.getInterpolatedV(v1)).endVertex();
		tessellator.draw();
		//GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	/**
	 * Draws a 16x16 rectangle for a fluid, because fluids are tough.
	 */
	public static void drawRectangle(Fluid fluid, int left, int top, int width, int height) {
		drawRectangle(fluid, left, top, width, height, 0xFFFFFFFF);
	}

	/**
	 * Draws a 16x16 rectangle for a fluid, because fluids are tough.
	 */
	public static void drawRectangle(Fluid fluid, int left, int top, int width, int height, int color) {
		drawRectangle(fluid, left, top, width, height, 0, 0, 16, 16, color);
	}

	/**
	 * Draws a beveled, round rectangle that is substantially similar to default Minecraft UI panels.
	 */
	public static void drawGuiPanel(int x, int y, int width, int height) {
		drawGuiPanel(x, y, width, height, 0x555555, 0xC6C6C6, 0xFFFFFF, 0x000000);
	}
	
	/**
	 * Draws a beveled, round rectangle that is substantially similar to default Minecraft UI panels.
	 */
	public static void drawGuiPanel(int x, int y, int width, int height, int shadow, int panel, int hilight, int outline) {
		drawRectangle(x + 3,         y + 3,          width - 6, height - 6, panel); //Main panel area
		
		drawRectangle(x + 2,         y + 1,          width - 4, 2,          hilight); //Top hilight
		drawRectangle(x + 2,         y + height - 3, width - 4, 2,          shadow); //Bottom shadow
		drawRectangle(x + 1,         y + 2,          2,         height - 4, hilight); //Left hilight
		drawRectangle(x + width - 3, y + 2,          2,         height - 4, shadow); //Right shadow
		drawRectangle(x + width - 3, y + 2,          1,         1,          panel); //Topright non-hilight/non-shadow transition pixel
		drawRectangle(x + 2,         y + height - 3, 1,         1,          panel); //Bottomleft non-hilight/non-shadow transition pixel
		drawRectangle(x + 3,         y + 3,          1,         1,          hilight); //Topleft round hilight pixel
		drawRectangle(x + width - 4, y + height - 4, 1,         1,          shadow); //Bottomright round shadow pixel
		
		drawRectangle(x + 2,         y,              width - 4, 1,          outline); //Top outline
		drawRectangle(x,             y + 2,          1,         height - 4, outline); //Left outline
		drawRectangle(x + width - 1, y + 2,          1,         height - 4, outline); //Right outline
		drawRectangle(x + 2,         y + height - 1, width - 4, 1,          outline); //Bottom outline
		drawRectangle(x + 1,         y + 1,          1,         1,          outline); //Topleft round pixel
		drawRectangle(x + 1,         y + height - 2, 1,         1,          outline); //Bottomleft round pixel
		drawRectangle(x + width - 2, y + 1,          1,         1,          outline); //Topright round pixel
		drawRectangle(x + width - 2, y + height - 2, 1,         1,          outline); //Bottomright round pixel
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
	public static void drawBeveledPanel(int x, int y, int width, int height, int topleft, int panel, int bottomright) {
		drawRectangle(x,             y,              width,     height,     panel); //Center panel
		drawRectangle(x,             y,              width - 1, 1,          topleft); //Top shadow
		drawRectangle(x,             y + 1,          1,         height - 2, topleft); //Left shadow
		drawRectangle(x + width - 1, y + 1,          1,         height - 1, bottomright); //Right hilight
		drawRectangle(x + 1,         y + height - 1, width - 1, 1,          bottomright); //Bottom hilight
	}
	
	public static void drawString(String s, int x, int y, int color) {
		Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, color);
	}
}
