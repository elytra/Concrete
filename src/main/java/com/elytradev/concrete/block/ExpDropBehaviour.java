package com.elytradev.concrete.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * A interface used to describe the behaviour for exp drops for an {@link ConcreteBlock}.
 */
public interface ExpDropBehaviour {

    /**
     * The default drop behaviour.
     */
    ExpDropBehaviour DEFAULT = of(0);

    /**
     * Creates a drop behaviour that drops the given exp quantity.
     *
     * @param quantity The quantity of exp to drop
     * @return The drop behaviour
     */
    static ExpDropBehaviour of(int quantity) {
        return (state, world, pos, fortune) -> quantity;
    }

    /**
     * Creates a drop behaviour that drops based on given exp quantity range.
     *
     * @param minimum The minimum quantity of exp to drop
     * @param maximum The maximum quantity of exp to drop
     * @return The drop behaviour
     */
    static ExpDropBehaviour of(int minimum, int maximum) {
        return (state, world, pos, fortune) -> {
            final Random random = world instanceof World ? ((World) world).rand : new Random();
            return MathHelper.getInt(random, minimum, maximum);
        };
    }

    /**
     * Gets the quantity of exp to be dropped.
     *
     * @param state The block state
     * @param world The world
     * @param pos The position
     * @param fortune The fortune
     * @return The quantity dropped
     */
    int getQuantityDropped(IBlockState state, IBlockAccess world, BlockPos pos, int fortune);

}
