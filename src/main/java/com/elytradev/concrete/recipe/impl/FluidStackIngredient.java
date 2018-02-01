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

package com.elytradev.concrete.recipe.impl;

import com.elytradev.concrete.recipe.FluidIngredient;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackIngredient extends FluidIngredient {
	private FluidStack stack;
	
	public FluidStackIngredient(FluidStack stack) {
		this.stack = stack;
	}
	
	@Override
	public boolean apply(FluidStack input) {
		if (input==null || stack==null) return false;
		
		return input.containsFluid(stack);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FluidStackIngredient)) return false;
		FluidStack b = ((FluidStackIngredient)other).stack;
		
		if (b.tag==null ^ stack.tag==null) return false;
		
		return b.getFluid()==stack.getFluid() &&
				b.amount==stack.amount &&
				(b.tag==null || b.tag.equals(stack.tag));
	}
	
	@Override
	public int hashCode() {
		int result = FluidRegistry.getFluidName(stack.getFluid()).hashCode();
		result *= 31;
		result ^= Integer.hashCode(stack.amount);
		if (stack.tag!=null) {
			result *= 31;
			result ^= stack.tag.hashCode();
		}
		return result;
	}

	@Override
	public int getAmount() {
		return (stack==null) ? 0 : stack.amount;
	}
}
