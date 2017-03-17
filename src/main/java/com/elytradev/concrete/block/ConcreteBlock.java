package com.elytradev.concrete.block;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An extension of {@link Block} used by the Concrete block builder system, to allow for
 * the create of blocks through said system.
 *
 * This is accomplished by overriding necessary methods from {@link Block}, and replacing
 * them with the various behaviours that are defined by the user or the ones provided by
 * Concrete.
 */
public abstract class ConcreteBlock extends Block {

    public static Builder builder() {
        return new Builder();
    }

    protected final Supplier<Item> dropped;
    protected final ItemDropBehaviour itemDropBehaviour;
    protected final ExpDropBehaviour expDropBehaviour;

    private ConcreteBlock(String identifier, Material materialIn, Supplier<Item> dropped,
            ItemDropBehaviour itemDropBehaviour, ExpDropBehaviour expDropBehaviour) {
        super(materialIn);
        this.dropped = dropped;
        this.itemDropBehaviour = itemDropBehaviour;
        this.expDropBehaviour = expDropBehaviour;

        this.setRegistryName(identifier);
        this.setUnlocalizedName(Loader.instance().activeModContainer().getModId().toLowerCase() + "." + identifier);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (this.dropped != null) {
            return this.dropped.get();
        }
        return super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int quantityDropped(Random random) {
        return this.itemDropBehaviour.getQuantityDropped(random);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return this.itemDropBehaviour.getQuantityDroppedWithBonus(fortune, random);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return this.expDropBehaviour.getQuantityDropped(state, world, pos, fortune);
    }

    @Override // this is needed for its public access modifier
    public Block setSoundType(SoundType sound) {
        return super.setSoundType(sound);
    }

    public static class Builder {

        private String identifier;
        private Optional<CreativeTabs> creativeTab = Optional.empty();
        private Material material = Material.ROCK;
        private boolean translucent = false;
        private Optional<SoundType> soundType = Optional.empty();
        private Supplier<Item> drop;
        private ItemDropBehaviour itemDropBehaviour = ItemDropBehaviour.DEFAULT;
        private ExpDropBehaviour expDropBehaviour = ExpDropBehaviour.DEFAULT;
        private Optional<Float> hardness = Optional.empty();
        private Optional<Float> resistance = Optional.empty();

        private Builder() {
        }

        public Builder preset(Consumer<Builder> preset) {
            preset.accept(this);
            return this;
        }

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder creativeTab(CreativeTabs creativeTab) {
            this.creativeTab = Optional.of(creativeTab);
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder translucent() {
            this.translucent = true;
            return this;
        }

        public Builder soundType(SoundType soundType) {
            this.soundType = Optional.of(soundType);
            return this;
        }

        public Builder drop(Supplier<Item> drop) {
            this.drop = drop;
            return this;
        }

        public Builder drop(ItemDropBehaviour dropBehaviour) {
            this.itemDropBehaviour = dropBehaviour;
            return this;
        }

        public Builder drop(ExpDropBehaviour dropBehaviour) {
            this.expDropBehaviour = dropBehaviour;
            return this;
        }

        public Builder drop(Supplier<Item> drop, ItemDropBehaviour dropBehaviour) {
            this.drop = drop;
            this.itemDropBehaviour = dropBehaviour;
            return this;
        }

        public Builder hardness(float hardness) {
            this.hardness = Optional.of(hardness);
            return this;
        }

        public Builder resistance(float resistance) {
            this.resistance = Optional.of(resistance);
            return this;
        }

        public ConcreteBlock build() {
            checkNotNull(this.identifier, "An identifier is required to build a block!");

            final ConcreteBlock block;
            if (this.translucent) {
                block = new ConcreteBlock(this.identifier, this.material, this.drop, this.itemDropBehaviour, this.expDropBehaviour) {
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
            } else {
                block = new ConcreteBlock(this.identifier, this.material, this.drop, this.itemDropBehaviour, this.expDropBehaviour) {};
            }

            this.creativeTab.ifPresent(block::setCreativeTab);
            this.hardness.ifPresent(block::setHardness);
            this.resistance.ifPresent(block::setResistance);
            this.soundType.ifPresent(block::setSoundType);

            return block;
        }

    }

}
