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

import com.elytradev.concrete.recipe.ICustomRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public abstract class InventoryGridRecipe implements ICustomRecipe<InventoryGridRecipe, ItemStack> {
	public abstract boolean matches(IItemHandler inventory);
	public abstract boolean matches(IInventory inventory);
	
	public ItemStack getOutput(IItemHandler inventory) {
		return getOutput();
	}
	
	public ItemStack getOutput(IInventory inventory) {
		return getOutput();
	}
	
	/**
	 * Consumes all ingredients needed to make this recipe. Must only be called with {@code doConsume=true} *after* the
	 * recipe output has been obtained, so that the recipe can apply any required NBT to the output stack based on ingredients.
	 * @param inventory   The inventory holding the ingredients which this recipe is turning into an output stack
	 * @param doConsume   True if the recipe should proceed with deleting items, otherwise this method will merely check
	 *                    to make sure deleting items would be possible.
	 * @return            True if deleting items was (or would be) successful.
	 */
	public abstract boolean consumeIngredients(IItemHandler inventory, boolean doConsume);
	
	/**
	 * Consumes all ingredients needed to make this recipe. Must only be called with {@code doConsume=true} *after* the
	 * recipe output has been obtained, so that the recipe can apply any required NBT to the output stack based on ingredients.
	 * @param inventory   The inventory holding the ingredients which this recipe is turning into an output stack
	 * @param doConsume   True if the recipe should proceed with deleting items, otherwise this method will merely check
	 *                    to make sure deleting items would be possible.
	 * @return            True if deleting items was (or would be) successful.
	 */
	public abstract boolean consumeIngredients(IInventory inventory, boolean doConsume);
}
