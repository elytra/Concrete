package com.elytradev.concrete.resgen;

import com.elytradev.concrete.common.ConcreteLog;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.minecraft.block.Block;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by darkevilmac on 5/24/17.
 */
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
		return name.startsWith(("assets/" + modID + "/blockstates/")) && name.endsWith(".json");
	}

	/**
	 * Provides an input stream for the given resource name.
	 *
	 * @param name the resource name
	 * @return
	 */
	@Override
	public InputStream provide(String name) {
		String blockID = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
		Block blockFromLocation = Block.getBlockFromName(modID + ":" + blockID);
		String modelLocation = modID + ":" + blockID;
		if (blockFromLocation instanceof IResourceHolder) {
			modelLocation = ((IResourceHolder) blockFromLocation).getResource(EnumResourceType.MODEL, 0).toString();
			modelLocation = modelLocation.substring(modelLocation.lastIndexOf("/" + 1));
			modelLocation = modID + ":" + modelLocation;
		}

		String simpleBlockState = SIMPLE_BLOCK_STATE;
		simpleBlockState = simpleBlockState.replaceAll("%MDL%", modelLocation);
		return IOUtils.toInputStream(simpleBlockState);
	}
}
