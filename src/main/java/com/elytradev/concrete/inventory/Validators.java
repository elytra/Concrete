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

package com.elytradev.concrete.inventory;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public final class Validators {
	public static final Predicate<ItemStack> ITEM_ANY = (it) -> true;
	public static final Predicate<ItemStack> ITEM_NONE = (it) -> false;
	public static final Predicate<ItemStack> ITEM_FURNACE_FUEL = TileEntityFurnace::isItemFuel; //This is actually the most correct/accurate way to read the furnace registry!
	public static final Predicate<ItemStack> ITEM_SMELTABLE = (it) -> !FurnaceRecipes.instance().getSmeltingResult(it).isEmpty();
	public static final Predicate<ItemStack> ITEM_FLUID_CONTAINER = (it) -> it.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

	/**
	 * Default Validator - accept every fluid.
	 */
	public static final Predicate<FluidStack> FLUID_ANY = (fs) -> true;

	/**
	 * Default Validator - accept no fluid.
	 */
	public static final Predicate<FluidStack> FLUID_NONE = (fs) -> false;

	/**
	 * Example Validator - is the fluid as hot or hotter than lava?
	 */
	public static final Predicate<FluidStack> FLUID_HOT = (fs) -> fs.getFluid().getTemperature() >= 1300;

	/**
	 * Example Validator - is the fluid colder than water?
	 */
	public static final Predicate<FluidStack> FLUID_COLD = (fs) -> fs.getFluid().getTemperature() < 300;

	/**
	 * Example Validator - is the fluid a gas?
	 */
	public static final Predicate<FluidStack> FLUID_GAS = (fs) -> fs.getFluid().isGaseous();

	private Validators() {}
}
