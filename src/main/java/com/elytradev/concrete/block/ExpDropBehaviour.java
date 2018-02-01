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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * An interface used to describe the behaviour for exp drops for a {@link ConcreteBlock}.
 */
@FunctionalInterface
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
