package com.elytradev.concrete.inventory;


import net.minecraft.inventory.Slot;

public class FakeFluidSlot extends Slot {
    public FakeFluidSlot(ValidatedFluidTankInventoryView inventoryIn) {
        super(inventoryIn, 0, 0, 0);
    }



}
