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

package com.elytradev.concrete.anim;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.elytradev.concrete.common.Rendering;
import com.elytradev.concrete.common.ShadingValidator;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AnimatedFaceTexture {

	static {
		ShadingValidator.ensureShaded();
	}
	
	public enum Order {
		FORWARD,
		RANDOM,
	}
	
	public final Order order;
	public final int ticksPerFrame;
	public final boolean interpolate;
	public final boolean glow;
	public final int length;
	public final int u;
	public final int v;
	public final int rotation;
	
	private Random rand;
	
	public AnimatedFaceTexture(Order order, int ticksPerFrame,
			boolean interpolate, boolean glow, int length, int u, int v,
			int rotation) {
		this.order = order;
		this.ticksPerFrame = ticksPerFrame;
		this.interpolate = interpolate;
		this.glow = glow;
		this.length = length;
		this.u = u;
		this.v = v;
		this.rotation = rotation;
	}

	public void render(EnumFacing face, int textureWidth, int textureHeight, int framesize, float ticks) {
		float tW = textureWidth;
		float tH = textureHeight;
		
		float minU = u/tW;
		float maxU = (u+framesize)/tW;
		
		float absoluteFrame = ticks/ticksPerFrame;
		if (!interpolate) absoluteFrame = (int)absoluteFrame;
		int frame = getFrame((int)absoluteFrame);
		int nextFrame = getFrame((int)(absoluteFrame)+1);
		float nextFrameAlpha = absoluteFrame%1;
		
		float minVCur = (v+(framesize*frame))/tH;
		float maxVCur = (v+(framesize*(frame+1)))/tH;
		float minVNxt = (v+(framesize*nextFrame))/tH;
		float maxVNxt = (v+(framesize*(nextFrame+1)))/tH;
		
		Rendering.pushLightmap();
		if (glow) {
			GlStateManager.disableLighting();
			Rendering.lightmap(240, 240);
		}
		renderFace(face, minU, maxU, minVCur, maxVCur, 1);
		renderFace(face, minU, maxU, minVNxt, maxVNxt, nextFrameAlpha);
		if (glow) {
			GlStateManager.enableLighting();
		}
		Rendering.popLightmap();
	}


	private void renderFace(EnumFacing face, float minU, float maxU, float minV, float maxV, float alpha) {
		if (alpha <= 0) return;
		GlStateManager.color(1, 1, 1, alpha);
		if (alpha != 1) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		switch (face) {
			case DOWN:
				buf.pos(0, -0.001, 0).tex(maxU, minV).endVertex();
				buf.pos(1, -0.001, 0).tex(minU, minV).endVertex();
				buf.pos(1, -0.001, 1).tex(minU, maxV).endVertex();
				buf.pos(0, -0.001, 1).tex(maxU, maxV).endVertex();
				break;
			case UP:
				buf.pos(0, 1.001, 1).tex(minU, maxV).endVertex();
				buf.pos(1, 1.001, 1).tex(maxU, maxV).endVertex();
				buf.pos(1, 1.001, 0).tex(maxU, minV).endVertex();
				buf.pos(0, 1.001, 0).tex(minU, minV).endVertex();
				break;
			case NORTH:
				buf.pos(0, 0, -0.001).tex(maxU, maxV).endVertex();
				buf.pos(0, 1, -0.001).tex(maxU, minV).endVertex();
				buf.pos(1, 1, -0.001).tex(minU, minV).endVertex();
				buf.pos(1, 0, -0.001).tex(minU, maxV).endVertex();
				break;
			case SOUTH:
				buf.pos(0, 0, 1.001).tex(minU, maxV).endVertex();
				buf.pos(1, 0, 1.001).tex(maxU, maxV).endVertex();
				buf.pos(1, 1, 1.001).tex(maxU, minV).endVertex();
				buf.pos(0, 1, 1.001).tex(minU, minV).endVertex();
				break;
			case EAST:
				buf.pos(1.001, 0, 0).tex(maxU, maxV).endVertex();
				buf.pos(1.001, 1, 0).tex(maxU, minV).endVertex();
				buf.pos(1.001, 1, 1).tex(minU, minV).endVertex();
				buf.pos(1.001, 0, 1).tex(minU, maxV).endVertex();
				break;
			case WEST:
				buf.pos(-0.001, 0, 1).tex(maxU, maxV).endVertex();
				buf.pos(-0.001, 1, 1).tex(maxU, minV).endVertex();
				buf.pos(-0.001, 1, 0).tex(minU, minV).endVertex();
				buf.pos(-0.001, 0, 0).tex(minU, maxV).endVertex();
				break;
		}
		tess.draw();
		if (alpha != 1) {
			GlStateManager.disableBlend();
		}
	}

	private int getFrame(int absoluteFrame) {
		switch (order) {
			case FORWARD:
				return absoluteFrame%length;
			case RANDOM:
				if (rand == null) rand = new Random();
				rand.setSeed(absoluteFrame);
				return rand.nextInt(length);
			default: throw new AssertionError("missing case for "+order);
		}
	}

	public static AnimatedFaceTexture fromJson(JsonObject obj) throws IOException {
		JsonElement order = obj.get("order");
		JsonElement ticksperframe = obj.get("ticksperframe");
		JsonElement interpolate = obj.get("interpolate");
		JsonElement glow = obj.get("glow");
		JsonElement length = obj.get("length");
		JsonElement u = obj.get("u");
		JsonElement v = obj.get("v");
		JsonElement rotation = obj.get("rotation");
		
		if (order == null) order = new JsonPrimitive("forward");
		if (!order.isJsonPrimitive() || !order.getAsJsonPrimitive().isString())
			throw new IOException("expected order to be a string, got "+order);
		
		if (ticksperframe == null)
			throw new IOException("ticksperframe is required");
		if (!ticksperframe.isJsonPrimitive() || !ticksperframe.getAsJsonPrimitive().isNumber())
			throw new IOException("expected ticksperframe to be a number, got "+ticksperframe);
		
		if (interpolate == null) interpolate = new JsonPrimitive(false);
		if (!interpolate.isJsonPrimitive() || !interpolate.getAsJsonPrimitive().isBoolean())
			throw new IOException("expected interpolate to be a boolean, got "+interpolate);
		
		if (glow == null) glow = new JsonPrimitive(false);
		if (!glow.isJsonPrimitive() || !glow.getAsJsonPrimitive().isBoolean())
			throw new IOException("expected glow to be a boolean, got "+glow);
		
		if (length == null)
			throw new IOException("length is required");
		if (!length.isJsonPrimitive() || !length.getAsJsonPrimitive().isNumber())
			throw new IOException("expected length to be a number, got "+length);
		
		if (u == null)
			throw new IOException("u is required");
		if (!u.isJsonPrimitive() || !u.getAsJsonPrimitive().isNumber())
			throw new IOException("expected u to be a number, got "+u);
		
		if (v == null) v = new JsonPrimitive(0);
		if (!v.isJsonPrimitive() || !v.getAsJsonPrimitive().isNumber())
			throw new IOException("expected v to be a number, got "+v);
		
		if (rotation == null) rotation = new JsonPrimitive(0);
		if (!rotation.isJsonPrimitive() || !rotation.getAsJsonPrimitive().isNumber())
			throw new IOException("expected rotation to be a number, got "+v);
		if (rotation.getAsInt() % 90 != 0)
			throw new IOException("rotation must be a multiple of 90");
		
		Optional<Order> orderOpt = Enums.getIfPresent(Order.class, order.getAsString().toUpperCase(Locale.ROOT));
		if (!orderOpt.isPresent())
			throw new IOException("order needs to be one of forward or random");
		
		return new AnimatedFaceTexture(orderOpt.get(), ticksperframe.getAsInt(),
				interpolate.getAsBoolean(), glow.getAsBoolean(),
				length.getAsInt(), u.getAsInt(), v.getAsInt(),
				rotation.getAsInt()/90);
	}

}
