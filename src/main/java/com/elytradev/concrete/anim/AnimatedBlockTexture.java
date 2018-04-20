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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;
import java.util.Map;

import com.elytradev.concrete.common.ConcreteLog;
import com.elytradev.concrete.common.EnumAnyRotation;
import com.elytradev.concrete.common.Rendering;
import com.elytradev.concrete.common.ShadingValidator;
import com.google.common.base.Enums;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AnimatedBlockTexture {
	private static final Gson gson = new Gson();
	private static final MapSplitter BLOCKSTATE_SPLITTER = Splitter.on(',').withKeyValueSeparator('=');

	static {
		ShadingValidator.ensureShaded();
	}
	
	private final int framesize;
	
	private final ResourceLocation texture;
	private final int textureWidth;
	private final int textureHeight;
	
	private final ImmutableMap<FaceState, AnimatedFaceTexture> faceStates;
	
	private transient final Map<IBlockState, ImmutableMap<String, String>> stateAsMapCache = Maps.newHashMap();
	
	public AnimatedBlockTexture(int framesize, ResourceLocation texture, int textureWidth, int textureHeight, ImmutableMap<FaceState, AnimatedFaceTexture> faceStates) {
		this.framesize = framesize;
		this.texture = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.faceStates = faceStates;
	}
	
	public void render(TileEntity te, double x, double y, double z, float partialTicks) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		state = state.getActualState(te.getWorld(), te.getPos());
		render(state, x, y, z, te.hashCode(), Minecraft.getMinecraft().player.ticksExisted+partialTicks);
	}
	
	public void render(IBlockState state, double x, double y, double z, int uniqifier, float ticks) {
		EnumAnyRotation rot = EnumAnyRotation.NORTH_UP;
		for (IProperty<?> prop : state.getPropertyKeys()) {
			if (prop.getName().equals("facing") && prop.getValueClass() == EnumFacing.class) {
				rot = EnumAnyRotation.fromDispenserFacing((EnumFacing)state.getValue(prop));
			} else if (prop.getValueClass() == EnumAnyRotation.class) {
				rot = (EnumAnyRotation)state.getValue(prop);
			}
		}
		
		ticks += (uniqifier & 0xFF);
		
		ImmutableMap<String, String> stateMap = stateAsMapCache.computeIfAbsent(state, this::stateToMap);
		Rendering.bindTexture(texture);
		GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			for (EnumFacing face : EnumFacing.VALUES) {
				Face absolute = Face.absoluteFaceFrom(face);
				FaceState absoluteState = new FaceState(stateMap, absolute);
				if (faceStates.containsKey(absoluteState)) {
					faceStates.get(absoluteState).render(face, textureWidth, textureHeight, framesize, ticks);
				} else {
					Face relative = Face.relativeFaceFrom(rot.getFront(), rot.getTop(), face);
					FaceState relativeState = new FaceState(stateMap, relative);
					if (faceStates.containsKey(relativeState)) {
						faceStates.get(relativeState).render(face, textureWidth, textureHeight, framesize, ticks);
					}
				}
			}
		GlStateManager.popMatrix();
	}
	
	private ImmutableMap<String, String> stateToMap(IBlockState state) {
		ImmutableMap.Builder<String, String> bldr = ImmutableMap.builder();
		for (Map.Entry<IProperty, Comparable> en : (ImmutableSet<Map.Entry<IProperty, Comparable>>)(ImmutableSet)state.getProperties().entrySet()) {
			bldr.put(en.getKey().getName(), en.getKey().getName(en.getValue()));
		}
		return bldr.build();
	}
	
	/**
	 * Reads an AnimatedBlockTexture JSON file from the given reader.
	 * @param r the reader to read from
	 * @return a newly created AnimatedBlockTexture
	 */
	public static AnimatedBlockTexture read(Reader r) throws IOException {
		JsonObject obj = gson.fromJson(r, JsonObject.class);
		ImmutableMap.Builder<FaceState, AnimatedFaceTexture> bldr = ImmutableMap.builder();
		int framesize = -1;
		ResourceLocation texture = null;
		int textureWidth = 16;
		int textureHeight = 16;
		for (Map.Entry<String, JsonElement> en : obj.entrySet()) {
			if ("_framesize".equals(en.getKey())) {
				JsonElement je = en.getValue();
				if (je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) {
					framesize = je.getAsInt();
				} else {
					throw new IOException("expected _framesize to be a number, got "+je+" instead");
				}
			} else if ("_texture".equals(en.getKey())) {
				JsonElement je = en.getValue();
				if (je.isJsonPrimitive() && je.getAsJsonPrimitive().isString()) {
					texture = new ResourceLocation(je.getAsString());
					try (IResource tex = Minecraft.getMinecraft().getResourceManager().getResource(texture)) {
						PngSizeInfo psi = new PngSizeInfo(tex.getInputStream());
						textureWidth = psi.pngWidth;
						textureHeight = psi.pngHeight;
					}
				} else {
					throw new IOException("expected _texture to be a string, got "+je+" instead");
				}
			} else if (en.getKey().startsWith("[") && en.getKey().contains("]#")) {
				if (en.getValue().isJsonObject()) {
					String blockstateStr = en.getKey().substring(1, en.getKey().lastIndexOf("]"));
					String faceStr = en.getKey().substring(en.getKey().lastIndexOf('#')+1);
					Map<String, String> blockstateMap = BLOCKSTATE_SPLITTER.split(blockstateStr);
					
					Optional<Face> optFace = Enums.getIfPresent(Face.class, faceStr.toUpperCase(Locale.ROOT));
					if (optFace.isPresent()) {
						AnimatedFaceTexture tex = AnimatedFaceTexture.fromJson(en.getValue().getAsJsonObject());
						for (Face face : optFace.get().realFaces) {
							bldr.put(new FaceState(ImmutableMap.copyOf(blockstateMap), face), tex);
						}
					} else {
						throw new IOException("unknown face "+faceStr);
					}
				} else {
					throw new IOException("expected object for blockstate spec, got "+en.getValue()+" instead");
				}
			} else {
				if (!en.getKey().startsWith("_note")) {
					ConcreteLog.warn("Unknown key {} in animated block texture JSON", en.getKey());
				}
			}
		}
		if (framesize == -1) {
			throw new IOException("animated block texture json does not specify a framesize");
		}
		if (texture == null) {
			throw new IOException("animated block texture json does not specify a texture");
		}
		return new AnimatedBlockTexture(framesize, texture, textureWidth, textureHeight, bldr.build());
	}
	
	
	/**
	 * Reads an AnimatedBlockTexture JSON file from the given resource manager
	 * at the given path.
	 * @param mgr the resource manager
	 * @param loc the path to the json file
	 * @return a newly created AnimatedBlockTexture
	 */
	public static AnimatedBlockTexture read(IResourceManager mgr, ResourceLocation loc) throws IOException {
		try (IResource res = mgr.getResource(loc)) {
			return read(res.getInputStream());
		}
	}
	
	/**
	 * Reads an AnimatedBlockTexture JSON file from the given input stream.
	 * @param in the input stream to read from
	 * @return a newly created AnimatedBlockTexture
	 */
	public static AnimatedBlockTexture read(InputStream in) throws IOException {
		Reader r = new InputStreamReader(in);
		return read(r);
	}
	
	/**
	 * Reads an AnimatedBlockTexture JSON file from the given string.
	 * @param s the string to read from
	 * @return a newly created AnimatedBlockTexture
	 */
	public static AnimatedBlockTexture read(String s) throws IOException {
		return read(new StringReader(s));
	}
	
}
