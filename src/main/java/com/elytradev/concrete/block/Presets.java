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

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.function.Consumer;

/**
 * A pseudo-enum of presets for the Concrete block builder system.
 */
public final class Presets {

	/**
	 * A preset for a block builder with ore properties, such as the fortune drop behaviour.
	 *
	 * @see ConcreteBlock.Builder#preset(Consumer)
	 */
	public static final Consumer<ConcreteBlock.Builder> ORE = builder -> builder
			.drop(ItemDropBehaviour.Fortune.DEFAULT);

	/**
	 * A preset for a block builder with glass properties, such as the hardness, and resistance.
	 *
	 * @see ConcreteBlock.Builder#preset(Consumer)
	 */
	public static final Consumer<ConcreteBlock.Builder> GLASS = builder -> builder
			.material(Material.GLASS)
			.soundType(SoundType.GLASS)
			.drop(ItemDropBehaviour.DROP_NONE)
			.hardness(0.3f)
			.resistance(1.0f)
			.translucent()
			.silkHarvest();

	private Presets() {
	}

}
