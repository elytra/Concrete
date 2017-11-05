/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	William Thompson (unascribed),
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

import java.util.Arrays;

import com.elytradev.concrete.recipe.ItemIngredient;
import com.google.common.base.Predicate;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ShapedInventoryRecipe extends InventoryGridRecipe {
	protected ItemStack result;
	protected int gridWidth = 1;
	protected int gridHeight = 1;
	protected ItemIngredient[] recipe;
	protected ItemIngredient[] flipped = null;
	
	/**
	 * Creates a new ShapedInventoryRecipe for some kind of crafting grid.
	 * @param width        The width of the crafting area
	 * @param height       The height of the crafting area
	 * @param alsoFlipped  True if the recipe should recognize the pattern if it's flipped horizontally.
	 * @param recipe       The ingredients
	 */
	public ShapedInventoryRecipe(ItemStack result, int gridWidth, int gridHeight, int recipeWidth, int recipeHeight, boolean alsoFlipped, ItemIngredient... recipe) {
		this.result = result;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.recipe = new ItemIngredient[gridWidth*gridHeight];
		for(int y=0; y<gridHeight; y++) {
			for(int x=0; x<gridWidth; x++) {
				int index = y*gridWidth+x;
				int recipeIndex = y*recipeWidth+x;
				if (recipeIndex>=recipe.length) continue;
				this.recipe[index] = recipe[recipeIndex];
			}
		}
		this.recipe = snug(this.recipe, gridWidth, gridHeight, (it)->false);
		if (alsoFlipped) this.flipped = snug(flip(recipe,gridWidth,gridHeight), gridWidth, gridHeight, (it)->false);
	}
	
	public ShapedInventoryRecipe(ItemStack result, ItemIngredient item) {
		this(result, 1, 1, 1, 1, false, item);
	}
	
	public ShapedInventoryRecipe(ItemStack result, int width, int height, ItemIngredient item) {
		this(result, width, height, 1, 1, false, item);
	}
	
	private static <T> T[] snug(T[] ts, int wid, int hit, Predicate<T> isEmpty) {
		T[] result = Arrays.copyOf(ts, ts.length);
		//Find the origin x and y
		int minX = wid;
		int minY = hit;
		for(int y=0; y<hit; y++) {
			for(int x=0; x<wid; x++) {
				int index = y*wid+x;
				if (index>=ts.length) continue;
				T t = ts[index];
				if (t==null || isEmpty.apply(t)) continue;
				
				//This is solid recipe area. Mask it off.
				minX = Math.min(minX, x);
				minY = Math.min(minY, y);
			}
		}
		
		//Now that we have the offsets, snug everything up
		for(int y=0; y<hit; y++) {
			for(int x=0; x<wid; x++) {
				int srci = (y+minY)*wid+(x+minX);
				int dsti = y*wid+x;
				if (dsti>=result.length) continue;
				if (srci>=ts.length) result[dsti] = null;
				
				result[dsti] = ts[srci];
			}
		}
		
		return result;
	}
	
	private static <T> T[] flip(T[] ts, int wid, int hit) {
		T[] result = Arrays.copyOf(ts, ts.length);
		for(int y=0; y<hit; y++) {
			for(int x=0; x<wid; x++) {
				int dest = y*wid+x;
				int src = y*wid+ (-x + (wid-1));
				result[dest] = ts[src];
			}
		}
		return result;
	}
	
	@Override
	public boolean matches(ItemStackHandler handler) {
		ItemStack[] slots = new ItemStack[gridWidth*gridHeight]; 
		for(int i=0; i<slots.length; i++) {
			if (i>=handler.getSlots()) break;
			slots[i] = handler.getStackInSlot(i);
		}
		slots = snug(slots, gridWidth, gridHeight, ItemStack::isEmpty);
		
		return apply(recipe, slots) ||
				((flipped==null)?false:apply(flipped,slots));
	}
	
	@Override
	public boolean matches(IInventory inventory) {
		ItemStack[] slots = new ItemStack[gridWidth*gridHeight]; 
		for(int i=0; i<slots.length; i++) {
			if (i>=inventory.getSizeInventory()) break;
			slots[i] = inventory.getStackInSlot(i);
		}
		slots = snug(slots, gridWidth, gridHeight, ItemStack::isEmpty);
		
		return apply(recipe, slots) ||
				((flipped==null)?false:apply(flipped,slots));
	}
	
	protected boolean apply(ItemIngredient[] ingredients, ItemStack... slots) {
		if (slots.length<ingredients.length) return false;
		
		for(int i=0; i<ingredients.length; i++) {
			if (ingredients[i]==null) {
				if (slots[i]!=null && !slots[i].isEmpty()) return false;
			} else {
				if (!ingredients[i].apply(slots[i])) return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return result;
	}
	
}
