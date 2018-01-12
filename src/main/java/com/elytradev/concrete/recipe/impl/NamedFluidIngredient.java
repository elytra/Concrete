/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
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

package com.elytradev.concrete.recipe.impl;

import com.elytradev.concrete.recipe.FluidIngredient;

import net.minecraftforge.fluids.FluidStack;

public class NamedFluidIngredient extends FluidIngredient {
	private String key;
	private int amount;
	
	public NamedFluidIngredient(String key, int amount) {
		this.key = key;
	}
	
	@Override
	public boolean apply(FluidStack input) {
		return input.getFluid().getName().equals(key) &&
			input.amount >= amount &&
			(input.tag==null || input.tag.hasNoTags());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof NamedFluidIngredient)) return false;
		
		return this.key.equals(((NamedFluidIngredient)other).key) &&
				this.amount==((NamedFluidIngredient)other).amount;
	}
	
	@Override
	public int hashCode() {
		return (this.key.hashCode()*31) ^ Integer.hashCode(amount);
	}

	@Override
	public int getAmount() {
		return amount;
	}
}
