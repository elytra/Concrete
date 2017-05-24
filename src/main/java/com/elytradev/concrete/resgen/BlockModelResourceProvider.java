package com.elytradev.concrete.resgen;

import com.elytradev.concrete.common.ConcreteLog;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.minecraft.block.Block;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class BlockModelResourceProvider extends ResourceProvider {

	private static String SIMPLE_BLOCK_MODEL;

	static {
		try {
			SIMPLE_BLOCK_MODEL = Resources.toString(BlockStateResourceProvider.class.getResource("concreteblockmodel.json"), Charsets.UTF_8);
		} catch (IOException e) {
			ConcreteLog.error("Caught IOException loading concrete block model, things will definitely not work.", e);
		}
	}

	public BlockModelResourceProvider(ConcreteResourcePack resourcePack) {
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
		return name.startsWith(("assets/" + modID + "/models/block/")) && name.endsWith(".json");
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
		String textureLocation = modID + ":blocks/" + blockID;
		Integer meta = resourcePack.getMetaFromName(name);

		if (blockFromLocation instanceof IResourceHolder) {
			textureLocation = ((IResourceHolder) blockFromLocation).getResource(EnumResourceType.TEXTURE, meta).toString();
		}

		String simpleBlockJSON = SIMPLE_BLOCK_MODEL;
		simpleBlockJSON = simpleBlockJSON.replaceAll("%ALL%", textureLocation);
		return IOUtils.toInputStream(simpleBlockJSON);
	}
}
