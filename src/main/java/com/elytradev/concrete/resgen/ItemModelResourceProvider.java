package com.elytradev.concrete.resgen;

import com.elytradev.concrete.common.ConcreteLog;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ItemModelResourceProvider extends ResourceProvider {

	private static String SIMPLE_ITEM_MODEL;
	private static String SIMPLE_BLOCK_MODEL;

	static {
		try {
			SIMPLE_ITEM_MODEL = Resources.toString(ItemModelResourceProvider.class.getResource("concreteitemmodel.json"), Charsets.UTF_8);
			SIMPLE_BLOCK_MODEL = Resources.toString(BlockStateResourceProvider.class.getResource("concreteblockmodel.json"), Charsets.UTF_8);
		} catch (IOException e) {
			ConcreteLog.error("Caught IOException loading concrete item and block model, things will definitely not work.", e);
		}
	}

	public ItemModelResourceProvider(ConcreteResourcePack resourcePack) {
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
		return name.startsWith(("assets/" + modID + "/models/item/")) && name.endsWith(".json");
	}

	/**
	 * Provides an input stream for the given resource name.
	 *
	 * @param name the resource name
	 * @return
	 */
	@Override
	public InputStream provide(String name) {
		String itemID = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
		Item itemFromLocation = resourcePack.getItem(name);
		String textureLocation = modID + ":items/" + itemID;
		Integer meta = resourcePack.getMetaFromName(name);

		if (itemFromLocation instanceof IResourceHolder) {
			textureLocation = ((IResourceHolder) itemFromLocation).getResource(EnumResourceType.TEXTURE, meta).toString();
		}

		// Try to return a block model file if this is an ItemBlock.
		if (Block.getBlockFromItem(itemFromLocation) != Blocks.AIR) {
			if (itemFromLocation.getRegistryName().getResourcePath().equals(itemID)) {
				try {
					String blockName = name.replace("/item/", "/block/");
					// Checks if we can use an existing block model or if we need to gen our own.
					if (resourcePack.realpackHasResourceName(blockName)) {
						return resourcePack.getRealpackInputStreamByName(blockName);
					} else {
						return resourcePack.getInputStreamByName(blockName);
					}
				} catch (IOException e) {
					ConcreteLog.error("Failed to get item model for {}", name);
				}
			} else {
				String simpleBlockModel = SIMPLE_BLOCK_MODEL;
				simpleBlockModel = simpleBlockModel.replaceAll("%ALL%", textureLocation);
				return IOUtils.toInputStream(simpleBlockModel);
			}
		}

		String simpleItemModel = SIMPLE_ITEM_MODEL;
		simpleItemModel = simpleItemModel.replaceAll("%L0%", textureLocation);
		return IOUtils.toInputStream(simpleItemModel);
	}
}
