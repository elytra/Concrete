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

import org.junit.Assert;
import org.junit.Test;

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.recipe.impl.ShapedInventoryRecipe;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ShapedRecipeTest {
	@Test
	public void snugDifferentSizes() {
		String[] test1 = { "A" };
		String[] test2 = {  "", "",
				          null, "A" };
		String[] test3 = { null,   "", null,
				           null,  "A", null,
				           null, null, null };
		
		String[] test4 = { null, null, null, null,
				           null,   "", null, null,
				           null, null,  "A", null,
				           null, null, null, null };
		
		String[] result1 = ShapedInventoryRecipe.snug(test1, 1, 1, String::isEmpty);
		String[] result2 = ShapedInventoryRecipe.snug(test2, 2, 2, String::isEmpty);
		String[] result3 = ShapedInventoryRecipe.snug(test3, 3, 3, String::isEmpty);
		String[] result4 = ShapedInventoryRecipe.snug(test4, 4, 4, String::isEmpty);
		
		Assert.assertArrayEquals(new String[]{"A"}, result1);
		Assert.assertArrayEquals(new String[]{"A", null, null, null}, result2);
		Assert.assertArrayEquals(new String[]{"A", null, null, null, null, null, null, null, null}, result3);
		Assert.assertArrayEquals(new String[]{
				"A", null, null, null,
				null, null, null, null,
				null, null, null, null,
				null, null, null, null
		}, result4);
	}
	
	@Test
	public void snugShapesInRectangle() {
		String[] input = {
				null, null, "A",
				null,  "B", "C"
				};
		String[] result = ShapedInventoryRecipe.snug(input, 3, 2, String::isEmpty);
		Assert.assertArrayEquals(new String[]{
				null, "A", null,
				 "B", "C", null
		}, result);
	}
	
	@Test
	public void flipAndSnugRectangle() {
		String[] input = {
				null, "A", null,
				 "B", "C", null
				};
		String[] flipped = ShapedInventoryRecipe.flip(input, 3, 2);
		Assert.assertArrayEquals(new String[]{
				null, "A", null,
				null, "C",  "B"
		}, flipped);
		
		String[] snugged = ShapedInventoryRecipe.snug(flipped, 3, 2, String::isEmpty);
		Assert.assertArrayEquals(new String[]{
				"A", null, null,
				"C",  "B", null
		}, snugged);
	}
	
	@Test
	public void recognizeRecipes() {
		Bootstrap.register();
		Item stone = new Item().setUnlocalizedName("stone");
		Item stick = new Item().setUnlocalizedName("stick");
		Item axe = new Item();
		ItemIngredient s = ItemIngredient.of(stone);
		ItemIngredient l = ItemIngredient.of(stick);
		ShapedInventoryRecipe stoneAxe = new ShapedInventoryRecipe(new ItemStack(axe), 3, 3, 2, 3, true,
				s,    s,
				s,    l,
				null, l
				);
		ConcreteItemStorage storage = new ConcreteItemStorage(9);
		//Place axe in storage backwards
		storage.setStackInSlot(1, new ItemStack(stone));
		storage.setStackInSlot(2, new ItemStack(stone));
		storage.setStackInSlot(4, new ItemStack(stick));
		storage.setStackInSlot(5, new ItemStack(stone));
		storage.setStackInSlot(7, new ItemStack(stick));
		Assert.assertTrue(stoneAxe.matches(storage));
		
		Item chisel = new Item();
		
		ShapedInventoryRecipe stoneChisel = new ShapedInventoryRecipe(new ItemStack(chisel), 3, 3, 2, 2, true,
				null,    s,
				l,    null
				);
		
		Assert.assertFalse(stoneChisel.matches(storage));
		ConcreteItemStorage chiselStorage = new ConcreteItemStorage(9);
		chiselStorage.setStackInSlot(4, new ItemStack(stone));
		chiselStorage.setStackInSlot(8, new ItemStack(stick));
		
		Assert.assertTrue(stoneChisel.matches(chiselStorage));
	}
	

}
