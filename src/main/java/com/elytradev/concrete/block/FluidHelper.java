package com.elytradev.concrete.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidHelper {
	/**
	 * Sets some extra properties for your fluid's block so it renders properly,
	 * making this step as painless and as repeatable as possible.
	 *
	 * Any usage of this method should be placed in the `init` section of your ClientProxy.
	 *
	 * @param fluidBlock The block your fluid is tied to.
	 * @param location The location of the model of your fluid.
	 */
	@SideOnly(Side.CLIENT)
	public static void setupFluidRenderer(BlockFluidBase fluidBlock, ModelResourceLocation location) {
		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(fluidBlock), stack -> location);
		ModelLoader.setCustomStateMapper(fluidBlock, new StateMapperBase()
		{
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return location;
			}
		});
	}

	/**
	 * Register a fluid with FluidRegistry, adding a reference for use with the universal bucket
	 * system as well.
	 *
	 * It's recommended that any usages of this method be placed in the `preInit` method of
	 * your mod.
	 *
	 * @param fluid The Fluid you're looking to register with FluidRegistry.
	 */
	public static void registerFluid(Fluid fluid) {
		FluidRegistry.registerFluid(fluid);
		FluidRegistry.addBucketForFluid(fluid);
	}
}
