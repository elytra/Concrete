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

package com.elytradev.concrete.resgen;

import com.elytradev.concrete.common.ConcreteLog;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class BlockStateResourceProvider extends ResourceProvider {

	private static String SIMPLE_BLOCK_STATE;

	static {
		try {
			SIMPLE_BLOCK_STATE = Resources.toString(BlockStateResourceProvider.class.getResource("concreteblockstate.json"), Charsets.UTF_8);
		} catch (IOException e) {
			ConcreteLog.error("Caught IOException loading concrete blockstate, things will definitely not work.", e);
		}
	}

	public BlockStateResourceProvider(ConcreteResourcePack resourcePack) {
		super(resourcePack);
	}

	/**
	 * Can this provider provide a resource for the given name?
	 *
	 * @param name the resource name.
	 * @return true if the required resource can be provided, false otherwise.
	 */
	@Override
	public boolean canProvide(String name) {
		return name.startsWith("assets/" + modID + "/blockstates/") && name.endsWith(".json");
	}

	/**
	 * Provides an input stream for the given resource name.
	 *
	 * @param name the resource name
	 * @return the input stream for the specified resource.
	 */
	@Override
	public InputStream provide(String name) {
		String blockID = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
		Block blockFromLocation = Block.getBlockFromName(modID + ":" + blockID);
		String modelLocation = modID + ":" + blockID;
		if (blockFromLocation instanceof IResourceHolder) {
			ResourceLocation resource = ((IResourceHolder) blockFromLocation).getResource(EnumResourceType.MODEL, 0);
			if (resource != null) {
				modelLocation = resource.toString();
				modelLocation = modelLocation.substring(modelLocation.lastIndexOf("/" + 1));
				modelLocation = modID + ":" + modelLocation;
			}
		}

		String simpleBlockState = SIMPLE_BLOCK_STATE;
		simpleBlockState = simpleBlockState.replaceAll("%MDL%", modelLocation);
		return IOUtils.toInputStream(simpleBlockState);
	}
}
