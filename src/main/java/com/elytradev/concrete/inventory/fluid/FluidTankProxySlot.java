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

package com.elytradev.concrete.inventory.fluid;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;


/**
 * Manages syncing of {@code FluidStack} data between server/client, utilizing "fake" items encapsulating {@code FluidStack} data.
 *
 * <h2>Overview</h2>
 *
 * <p>FluidTankProxySlot is setup like any other {@code Slot}, with the exception that the {@code IInventory} it's supposed to reference
 * doesn't actually exist - instead, all {@code IInventory} referencing functions
 * (e.g. {@link #getStack()}, {@link #putStack(ItemStack)}, {@link #onSlotChanged()}, etc.) are all overridden to
 * serialize/deserialize information about the participating {@link ConcreteFluidTank} (namely, its {@code FluidStack}).</p>
 *
 * <p>This allows for super easy syncing of the FluidStack data between the server and the client, allowing
 * {@link com.elytradev.concrete.inventory.gui.widget.Widget}s like
 * {@link com.elytradev.concrete.inventory.gui.widget.WFluidBar} to work and sync properly.</p>
 *
 */
public class FluidTankProxySlot extends Slot {
	private final ConcreteFluidTank delegate;

	public FluidTankProxySlot(ConcreteFluidTank delegate) {
		super(null, 0, Integer.MIN_VALUE, Integer.MIN_VALUE);
		this.delegate = delegate;
	}

	@Override
	@Nonnull
	public ItemStack getStack() {
		ItemStack stack = new ItemStack(Items.STICK, 1, 0);
		NBTTagCompound stackTag = new NBTTagCompound();

		/* Having a little fun with it... */
		NBTTagCompound displayTag = new NBTTagCompound();
		NBTTagList loreTag = new NBTTagList();
		loreTag.appendTag(new NBTTagString("What? How can you see this?"));
		loreTag.appendTag(new NBTTagString("Tell @CalmBit immediately."));
		displayTag.setString("Name", "Fluid Stick");
		displayTag.setTag("Lore", loreTag);
		/* End fun */

		stackTag.setTag("fluid_tank", delegate.writeToNBT(new NBTTagCompound()));
		stackTag.setTag("display", displayTag);
		stack.setTagCompound(stackTag);
		return stack;
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		NBTTagCompound stackTag = stack.getTagCompound();
		if (stackTag != null && stackTag.hasKey("fluid_tank")) {
			delegate.readFromNBT(stackTag.getCompoundTag("fluid_tank"));
		}
	}

	@Override
	public void onSlotChanged() {
		this.delegate.markDirty();
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isHere(IInventory inv, int slot) {
		return false;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		if (other instanceof FluidTankProxySlot) {
			FluidTankProxySlot slot = (FluidTankProxySlot) other;
			if (slot.delegate == this.delegate)
				return true;
		}
		return false;
	}
}
