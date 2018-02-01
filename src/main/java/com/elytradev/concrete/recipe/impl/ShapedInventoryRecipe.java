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

import java.util.Arrays;

import com.elytradev.concrete.recipe.ItemIngredient;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

/**
 * Represents a shaped recipe for a custom inventory grid. Small recipes will be recognized no matter where they're
 * placed on the grid, and are optionally checked for horizontal mirroring.
 * 
 * <p>The caveat here is that this grid has a known, fixed width and height, and that the "matched" crafting area comes
 * before any other slots in the inventory. For a 3x3 grid, that means slots 0-8 are reserved for matching, and slots 9+
 * are ignored. Trying to match that 3x3 grid to an inventory smaller than 9 always reports no match, even if the recipe
 * "fits" in the new grid size. Recipes specified as being larger than the grid will be clipped to the grid, and as such
 * may match things they shouldn't because the differences are off its bottom and right edges.
 * 
 * <p>While this is meant to be a drop-in replacement for ShapedRecipes, please keep in mind that there is no common
 * ancestry.
 */
public class ShapedInventoryRecipe extends InventoryGridRecipe {
	protected ItemStack result;
	protected int gridWidth = 1;
	protected int gridHeight = 1;
	protected ItemIngredient[] recipe;
	protected ItemIngredient[] flipped = null;
	protected ResourceLocation registryName;
	
	/**
	 * Creates a new ShapedInventoryRecipe for some kind of crafting grid. The recipe ingredients can optionally be
	 * specified on a smaller grid for convenience, such as a 2x2 crafting recipe specified in 4 array elements instead
	 * of 9. Trailing nulls can also be safely omitted, even if contained within the smaller recipe area.
	 * 
	 * @param result       The ItemStack this recipe produces
	 * @param gridWidth    The width of the inventory you expect to match against.
	 * @param gridHeight   The height of the inventory you expect to match against.
	 * @param recipeWidth  The width of the recipe being passed in - can be gridWidth or smaller.
	 * @param recipeHeight The height of the recipe being passed in - can be gridHeight or smaller.
	 * @param alsoFlipped  True if the recipe should recognize the pattern if it's flipped horizontally.
	 * @param recipe       The ingredients. This can be *any* number of elements, but elements past {@code recipeWidth*recipeHeight} will be ignored.
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
				if (x>=recipeWidth || y>=recipeHeight) continue;
				if (recipeIndex>=recipe.length) continue;
				this.recipe[index] = recipe[recipeIndex];
			}
		}
		this.recipe = snug(this.recipe, gridWidth, gridHeight, (it)->false);
		if (alsoFlipped) this.flipped = snug(flip(this.recipe,gridWidth,gridHeight), gridWidth, gridHeight, (it)->false);
	}
	
	/**
	 * Creates a new ShapedInventoryRecipe to match one ingredient being placed into the grid. For performance reasons,
	 * consider using {@link ShapelessInventoryRecipe} instead.
	 * 
	 * @param result       The ItemStack this recipe produces
	 * @param width        The width of the inventory you expect to match against.
	 * @param height       The height of the inventory you expect to match against.     
	 * @param item         The ingredient that activates this recipe
	 */
	public ShapedInventoryRecipe(ItemStack result, int width, int height, ItemIngredient item) {
		this(result, width, height, 1, 1, false, item);
	}
	
	public static <T> T[] snug(T[] ts, int wid, int hit, Predicate<T> isEmpty) {
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
		
		//Now that we have the offsets, snug everything up and turn empties into nulls
		for(int y=0; y<hit; y++) {
			for(int x=0; x<wid; x++) {
				int srci = (y+minY)*wid+(x+minX);
				int dsti = y*wid+x;
				if (dsti>=result.length) continue;
				if (srci>=ts.length) {
					result[dsti] = null;
					continue;
				}
				
				T t = ts[srci];
				if (t==null || isEmpty.apply(t)) {
					result[dsti] = null;
				} else {
					result[dsti] = ts[srci];
				}
			}
		}
		
		return result;
	}
	
	public static <T> T[] flip(T[] ts, int wid, int hit) {
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
	public boolean matches(IItemHandler inventory) {
		Vec2i translation = findTranslation(inventory.getSlots(), inventory::getStackInSlot, ItemStack::isEmpty);
		if (translation.x>=gridWidth || translation.y>=gridHeight) return false;
		if (apply(recipe, inventory.getSlots(), translation.x, translation.y, inventory::getStackInSlot, (it)->inventory.extractItem(it, 1, true), false)) return true;
		if (flipped!=null) {
			if (apply(flipped, inventory.getSlots(), translation.x, translation.y, inventory::getStackInSlot, (it)->inventory.extractItem(it, 1, true), false)) return true;
		}
		
		return false;
	}
	
	@Override
	public boolean matches(IInventory inventory) {
		Vec2i translation = findTranslation(inventory.getSizeInventory(), inventory::getStackInSlot, ItemStack::isEmpty);
		if (translation.x>=gridWidth || translation.y>=gridHeight) return false;
		if (apply(recipe, inventory.getSizeInventory(), translation.x, translation.y, inventory::getStackInSlot, (it)->inventory.decrStackSize(it, 1), false)) return true;
		
		if (flipped!=null) {
			if (apply(flipped, inventory.getSizeInventory(), translation.x, translation.y, inventory::getStackInSlot, (it)->inventory.decrStackSize(it, 1), false)) return true;
		}
		
		return false;
	}

	protected boolean apply(
			ItemIngredient[] ingredients,
			int numSlots,
			int translateX,
			int translateY,
			Function<Integer,ItemStack> slotInspector,
			Function<Integer,ItemStack> slotExtractor,
			boolean doConsume) {
		
		//Always test-flight a recipe before extracting. Test-flight is extremely cheap.
		if (doConsume && !apply(ingredients, numSlots, translateX, translateY, slotInspector, slotExtractor, false)) return false;
		
		if (numSlots<ingredients.length) return false;
		
		for(int y=0; y<gridHeight; y++) {
			for(int x=0; x<gridWidth; x++) {
				int srcIndex = y*gridWidth+x;
				int destIndex = (y+translateY)*gridWidth + (x+translateX);
				if (destIndex>=numSlots || srcIndex>=ingredients.length) continue;
				if (ingredients[srcIndex]==null) {
					ItemStack stack = slotInspector.apply(destIndex);
					if (stack!=null && !stack.isEmpty()) return false;
				} else {
					ItemStack stack = slotInspector.apply(destIndex);
					if (!ingredients[srcIndex].apply(stack)) return false;
					if (doConsume) {
						ItemStack extracted = slotExtractor.apply(destIndex);
						if (extracted==null || extracted.isEmpty()) return false;
					}
				}
				
			}
		}
		return true;
	}
	
	/**
	 * Finds the translation required to snug this set of Ts to the upper-left corner.
	 * 
	 * The inventory being inspected must be gridWidth*gridHeight slots, arranged in the same way as the recipe.
	 * 
	 */
	protected <T> Vec2i findTranslation(int numSlots, Function<Integer, T> slotInspector, Predicate<T> isEmpty) {
		Vec2i result = new Vec2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		for(int y=0; y<gridHeight; y++) {
			for(int x=0; x<gridWidth; x++) {
				int index = y*gridWidth+x;
				if (index>=numSlots) continue;
				T t = slotInspector.apply(index);
				if (t==null || isEmpty.apply(t)) continue;
				
				//This is solid recipe area.
				result.x = Math.min(result.x, x);
				result.y = Math.min(result.y, y);
			}
		}
		
		return result;
	}
	
	public int getGridWidth() { return gridWidth; }
	public int getGridHeight() { return gridHeight; }
	
	/**
	 * Gets the output of this recipe. If possible, use {@link #getOutput(IInventory)} or {@link #getOutput(IItemHandler)}
	 * instead, so that appropriate NBT can be added based on the ingredients.
	 */
	@Override
	public ItemStack getOutput() {
		return result;
	}

	@Override
	public boolean consumeIngredients(IItemHandler inventory, boolean doConsume) {
		Vec2i translation = findTranslation(inventory.getSlots(), inventory::getStackInSlot, ItemStack::isEmpty);
		
		return apply(recipe, inventory.getSlots(), translation.x, translation.y, inventory::getStackInSlot, (it)->inventory.extractItem(it, 1, !doConsume), doConsume);
	}

	@Override
	public boolean consumeIngredients(IInventory inventory, boolean doConsume) {
		Vec2i translation = findTranslation(inventory.getSizeInventory(), inventory::getStackInSlot, ItemStack::isEmpty);
		
		return apply(recipe, inventory.getSizeInventory(), translation.x, translation.y, inventory::getStackInSlot, (it)->inventory.decrStackSize(it, 1), doConsume);
	}

	
	
	@Override
	public InventoryGridRecipe setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Class<InventoryGridRecipe> getRegistryType() {
		return InventoryGridRecipe.class;
	}
	
	private static class Vec2i {
		public int x;
		public int y;
		
		public Vec2i() {
			this(0,0);
		}
		
		public Vec2i(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "{x:"+x+", y:"+y+"}";
		}
	}
}
