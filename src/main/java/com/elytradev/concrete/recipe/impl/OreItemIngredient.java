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

import com.elytradev.concrete.recipe.ItemIngredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class OreItemIngredient extends ItemIngredient {
	private String key;
	private int amount;

	public OreItemIngredient(String key, int amount) {
		this.key = key;
		this.amount = amount;
	}
	
	public String getKey() {
		return key;
	}
	
	public int getAmount() {
		return amount;
	}
	
	@Override
	public boolean apply(ItemStack input) {
		//Prevent our check from adding it to the list
		if (!OreDictionary.doesOreNameExist(key)) return false;
		
		if (input.getCount()<amount) return false;
		NonNullList<ItemStack> prototypes = OreDictionary.getOres(key);
		return OreDictionary.containsMatch(false, prototypes, input);
	}
	
	@Override
	public boolean equals(Object other) {
		return (other instanceof OreItemIngredient) &&
				((OreItemIngredient)other).key.equals(key) &&
				((OreItemIngredient)other).amount == amount;
	}
	
	@Override
	public int hashCode() {
		return (key.hashCode() * 31) ^ Integer.hashCode(amount);
	}
}
