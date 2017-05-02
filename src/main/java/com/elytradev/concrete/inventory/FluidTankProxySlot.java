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

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
 * {@link com.elytradev.concrete.inventory.gui.widget.WWidget}s like
 * {@link com.elytradev.concrete.inventory.gui.widget.WFluidBar} to work and sync properly.</p>
 *
 */
public class FluidTankProxySlot extends Slot {
    private ConcreteFluidTank delegate;

    public FluidTankProxySlot(ConcreteFluidTank delegate) {
        super(null, 0, 0, 0);
        this.delegate = delegate;
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        NBTTagCompound fluidTank = new NBTTagCompound();
        NBTTagCompound fluidStack = new NBTTagCompound();
        delegate.writeToNBT(fluidStack);
        fluidTank.setTag("fluid_tank", fluidStack);
        ItemStack result = new ItemStack(Items.STICK, 1, 0);
        result.setTagCompound(fluidTank);
        return result;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("fluid_tank"))
            delegate.readFromNBT(stack.getTagCompound().getCompoundTag("fluid_tank"));
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
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        if (other instanceof FluidTankProxySlot) {
            FluidTankProxySlot slot = (FluidTankProxySlot)other;
            if( slot.delegate == this.delegate)
                return true;
        }
        return false;
    }
}
