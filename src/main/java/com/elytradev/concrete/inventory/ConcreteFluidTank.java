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

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public class ConcreteFluidTank extends FluidTank implements IObservableFluidTank {
    private ArrayList<Runnable> listeners = new ArrayList<>();
    private Predicate<FluidStack> fillValidator = Validators.ANY_FLUID;
    private Predicate<FluidStack> drainValidator = Validators.ANY_FLUID;

    public ConcreteFluidTank(int capacity) {
        this(null, capacity);
    }

    public ConcreteFluidTank(Fluid fluid, int amount, int capacity) {
        this(new FluidStack(fluid, amount), capacity);
    }

    public ConcreteFluidTank(@Nullable FluidStack fluidStack, int capacity) {
        super(fluidStack, capacity);
    }

    public final ConcreteFluidTank withFillValidator(Predicate<FluidStack> fillValidator) {
        this.fillValidator = fillValidator;
        return this;
    }

    public final ConcreteFluidTank withDrainValidator(Predicate<FluidStack> drainValidator) {
        this.fillValidator = fillValidator;
        return this;
    }

    public final ConcreteFluidTank withValidators(Predicate<FluidStack> fillValidator,
                                                  Predicate<FluidStack> drainValidator) {
        return this.withFillValidator(fillValidator).withDrainValidator(drainValidator);
    }

    public void markDirty() {
        for(Runnable r : listeners) {
            r.run();
        }
    }

    @Override
    public void listen(@Nonnull Runnable r) {
        listeners.add(r);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int result = super.fill(resource, doFill);
        if (doFill) this.markDirty();
        return result;
    }

    @Override
    public int fillInternal(FluidStack resource, boolean doFill) {
        int result = super.fillInternal(resource, doFill);
        if (doFill) this.markDirty();
        return result;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack result = super.drain(resource, doDrain);
        if (doDrain) this.markDirty();
        return result;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack result = super.drain(maxDrain, doDrain);
        if (doDrain) this.markDirty();
        return result;
    }

    @Nullable
    @Override
    public FluidStack drainInternal(FluidStack resource, boolean doDrain) {
        FluidStack result = super.drainInternal(resource, doDrain);
        if (doDrain) this.markDirty();
        return result;
    }

    @Nullable
    @Override
    public FluidStack drainInternal(int maxDrain, boolean doDrain) {
        FluidStack result = super.drainInternal(maxDrain, doDrain);
        if (doDrain) this.markDirty();
        return result;
    }


}
