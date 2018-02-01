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

package com.elytradev.concrete.block;

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

import com.elytradev.concrete.common.ShadingValidator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An extension of {@link Block} used by the Concrete block builder system, to allow for
 * the creation of blocks through said system.
 *
 * This is accomplished by overriding necessary methods from {@link Block}, and replacing
 * them with the various behaviours that are defined by the user or the ones provided by
 * Concrete.
 */
public final class ConcreteBlock extends Block {

	static {
		ShadingValidator.ensureShaded();
	}

	public static Builder builder() {
		return new Builder();
	}

	private final boolean concreteTranslucent; // Minecraft has its own translucent field
	private final boolean silkHarvest;
	private final ItemDropBehaviour itemDropBehaviour;
	private final ExpDropBehaviour expDropBehaviour;

	private ConcreteBlock(String identifier, Material materialIn, boolean translucent, boolean silkHarvest,
			ItemDropBehaviour itemDropBehaviour, ExpDropBehaviour expDropBehaviour) {
		super(materialIn);
		this.concreteTranslucent = translucent;
		this.silkHarvest = silkHarvest;
		this.itemDropBehaviour = itemDropBehaviour;
		this.expDropBehaviour = expDropBehaviour;

		this.setRegistryName(identifier);
		this.setUnlocalizedName(Loader.instance().activeModContainer().getModId().toLowerCase() + "." + identifier);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if (this.itemDropBehaviour.getDrop().isPresent()) {
			return this.itemDropBehaviour.getDrop().get().get();
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
	public int damageDropped(IBlockState state) {
		return this.itemDropBehaviour.getMeta(this, state);
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		return this.expDropBehaviour.getQuantityDropped(state, world, pos, fortune);
	}

	@Override
	protected boolean canSilkHarvest() {
		return this.silkHarvest;
	}

	@Override // this is needed for its public access modifier
	public Block setSoundType(SoundType sound) {
		return super.setSoundType(sound);
	}

	// Translucent: Start
	/////////////////////

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		if (this.concreteTranslucent) {
			return BlockRenderLayer.TRANSLUCENT;
		} else {
			return super.getBlockLayer();
		}
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		if (this.concreteTranslucent) {
			return false;
		} else {
			return super.isFullCube(state);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		if (this.concreteTranslucent) {
			return false;
		} else {
			return super.isOpaqueCube(state);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (this.concreteTranslucent) {
			// adapted from BlockGlass#shouldSideBeRendered(IBlockState, IBlockAccess, BlockPos, EnumFacing)
			final IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
			final Block block = iblockstate.getBlock();

			return blockState != iblockstate || block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		} else {
			return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}
	}

	// Translucent: End
	///////////////////

	public static final class Builder {

		private String identifier;
		private Optional<CreativeTabs> creativeTab = Optional.empty();
		private Material material = Material.ROCK;
		private boolean translucent = false;
		private boolean silkHarvest = false;
		private Optional<SoundType> soundType = Optional.empty();
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

		public Builder silkHarvest() {
			this.silkHarvest = true;
			return this;
		}

		public Builder soundType(SoundType soundType) {
			this.soundType = Optional.of(soundType);
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

			final ConcreteBlock block = new ConcreteBlock(this.identifier, this.material, this.translucent, this.silkHarvest,
					this.itemDropBehaviour, this.expDropBehaviour);

			this.creativeTab.ifPresent(block::setCreativeTab);
			this.hardness.ifPresent(block::setHardness);
			this.resistance.ifPresent(block::setResistance);
			this.soundType.ifPresent(block::setSoundType);

			return block;
		}

	}

}
