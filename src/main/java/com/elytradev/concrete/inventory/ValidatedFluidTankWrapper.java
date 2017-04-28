package com.elytradev.concrete.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;


public class ValidatedFluidTankWrapper implements IFluidTank {
    private ConcreteFluidTank delegate;

    public ValidatedFluidTankWrapper(ConcreteFluidTank delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        return delegate.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return delegate.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return delegate.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return delegate.getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(!delegate.getFillValidator().test(resource)) return 0;
        else return delegate.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return delegate.drain(maxDrain, doDrain);
    }
}
