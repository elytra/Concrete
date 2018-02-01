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

package com.elytradev.concrete.recipe;

import com.elytradev.concrete.recipe.impl.ItemStackIngredient;
import com.elytradev.concrete.recipe.impl.OreItemIngredient;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Common superclass of all ingredients that can exist in an itemslot.
 */
public abstract class ItemIngredient implements IIngredient<ItemStack> {
	@Override
	public String getCategory() {
		return "item";
	}
	
	/**
	 * Creates a new IIngredient that matches this ItemStack. Matching is non-strict; OreDictionary.WILDCARD_VALUE will
	 * be taken into consideration if present. However, NBT will be matched strictly.
	 */
	public static ItemIngredient of(ItemStack input) {
		return new ItemStackIngredient(input);
	}
	
	/**
	 * Creates a new IIngredient that matches any metadata/damage variant of thge provided Item
	 */
	public static ItemIngredient of(Item input) {
		return new ItemStackIngredient(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE));
	}
	
	/**
	 * Creates a new IIngredient that matches any metadata variant of the provided Block
	 */
	public static ItemIngredient of(Block input) {
		return of(ItemBlock.getItemFromBlock(input));
	}
	
	/**
	 * Creates a new OreDictionary IIngredient that matches on the provided oredict key
	 */
	public static ItemIngredient of(String input) {
		return new OreItemIngredient(input, 1);
	}
	
	/**
	 * Creates a new OreDictionary IIngredient that matches at least {@code amount} of the provided oredict item.
	 */
	public static ItemIngredient of(String input, int amount) {
		return new OreItemIngredient(input, amount);
	}
}
