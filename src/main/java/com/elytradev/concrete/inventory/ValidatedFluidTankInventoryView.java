package com.elytradev.concrete.inventory;

import com.elytradev.concrete.inventory.ConcreteFluidTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ValidatedFluidTankInventoryView implements IInventory{
    private ConcreteFluidTank delegate;

    public ValidatedFluidTankInventoryView(ConcreteFluidTank delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        NBTTagCompound fluidTank = new NBTTagCompound();
        fluidTank.setTag("fluid_tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(delegate, null));
        return new ItemStack(Items.STICK, 1,0, fluidTank);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(stack.getTagCompound().hasKey("fluid_tank")) {
            NBTTagCompound fluidTank = stack.getTagCompound().getCompoundTag("fluid_tank");
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(delegate, null, fluidTank);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {
        delegate.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getTagCompound().hasKey("fluid_tank");
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getName() {
        return delegate.getFluid().getUnlocalizedName();
    }

    @Override
    public boolean hasCustomName() {
        return delegate.getFluid().getUnlocalizedName()!=null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(delegate.getFluid().getUnlocalizedName());
    }
}
