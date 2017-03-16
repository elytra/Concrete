package com.elytradev.concrete.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

/**
 * An interface used to define the behaviour that will be used to construct a {@link ConcreteBlock},
 * from the Concrete block builder system.
 *
 * This allows for an extended {@link ConcreteBlock} in special cases where such functionality may
 * be desired, such as glass.
 */
public interface ConstructionBehaviour {

    /**
     * The default construction behaviour.
     */
    ConstructionBehaviour DEFAULT = ConcreteBlock::new;

    /**
     * The construction behaviour for glass blocks.
     */
    ConstructionBehaviour GLASS = new ConstructionBehaviour() {
        @Override
        public ConcreteBlock construct(String identifier, Material materialIn, Supplier<Item> dropped,
                ItemDropBehaviour itemDropBehaviour, ExpDropBehaviour expDropBehaviour) {
            return new ConcreteBlock(identifier, materialIn, dropped, itemDropBehaviour, expDropBehaviour) {
                @SideOnly(Side.CLIENT)
                public BlockRenderLayer getBlockLayer() {
                    return BlockRenderLayer.TRANSLUCENT;
                }

                @Override
                public boolean isFullCube(IBlockState state) {
                    return false;
                }

                @Override
                protected boolean canSilkHarvest() {
                    return true;
                }

                @Override
                public boolean isOpaqueCube(IBlockState state) {
                    return false;
                }

                // adapted from BlockGlass#shouldSideBeRendered(IBlockState, IBlockAccess, BlockPos, EnumFacing)
                @SideOnly(Side.CLIENT)
                public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
                    final IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
                    final Block block = iblockstate.getBlock();

                    return blockState != iblockstate || block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
                }
            };
        }
    };

    /**
     * Gets a constructed {@link ConcreteBlock} with the given parameters.
     *
     * @param identifier The block identifier
     * @param materialIn The block material
     * @param dropped The item dropped by the block
     * @param itemDropBehaviour The item drop behaviour
     * @param expDropBehaviour The exp drop behaviour
     * @return The constructed block
     */
    ConcreteBlock construct(String identifier, Material materialIn, Supplier<Item> dropped,
            ItemDropBehaviour itemDropBehaviour, ExpDropBehaviour expDropBehaviour);

}
