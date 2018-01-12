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

import com.elytradev.concrete.recipe.ItemIngredient;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackIngredient extends ItemIngredient {
	private ItemStack item;
	
	public ItemStackIngredient(ItemStack input) {
		this.item = input;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	@Override
	public boolean apply(ItemStack input) {
		if (item==null || input==null) return false;
		return OreDictionary.itemMatches(item, input, false) && ItemStack.areItemStackTagsEqual(item, input);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ItemStackIngredient)) return false;
		ItemStack a = item;
		ItemStack b = ((ItemStackIngredient)other).item;
		
		return OreDictionary.itemMatches(a, b, true) && ItemStack.areItemStackTagsEqual(a,b);
	}
	
	@Override
	public int hashCode() {
		//SHOULD produce identical numbers for equals==true. Should. But it's kind of a weak guarantee.
		int result = item.getItem().hashCode();
		//The following not represented by equals!
		//result *= 31;
		//result ^= Integer.hashCode(item.getCount());
		result *= 31;
		result ^= Integer.hashCode(item.getItemDamage());
		if (item.hasTagCompound()) {
			result *= 31;
			result ^= item.getTagCompound().hashCode();
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return item.toString();
	}
}
