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

package com.elytradev.concrete.inventory;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.Lists;


/**
 * A base class for managing fluid storage.
 *
 * <h2>Validation</h2>
 *
 * Just like {@link ConcreteItemStorage}, ConcreteFluidTank <em>does not</em> perform its own validation. Rather, this
 * is offloaded to its own wrapper for such functionality, {@link ValidatedFluidTankWrapper}.
 *
 * <h2>Serialization and Deserialization</h2>
 *
 * <p>If you're using this object to manage the fluid tank of a TileEntity, it takes three small tweaks to get no-fuss
 * serialization. In your constructor, add
 *
 * <code><pre>fluidTank.listen(this::markDirty);</pre></code>
 *
 * <p>This will mark your tile dirty any time the inventory changes, so that Minecraft won't skip serialization. Then in
 * writeToNBT:
 *
 * <code><pre>tagOut.setTag("fluid_tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(fluidTank, null));</pre></code>
 *
 * <p>and in readFromNBT:
 *
 * <code><pre>CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(fluidTank, null, tag.getTag("fluid_tank"));</pre></code>
 *
 * <p>Where <code>fluidTank</code> is your ConcreteFluidTank object. At that point the Forge Capability system will
 * do all the work for you on this, and you can focus on the interesting part of making your tile do what it's supposed to.
 *
 * <h2>Exposing as a capability</h2>
 *
 * <p>This was mentioned above in the Validation section, but although this object supplies the IFluidHandler interface,
 * generally you want to create a ValidatedFluidTankWrapper instead. These wrappers are okay to cache or memoize,
 * and merely provide a succinct delegation based on the access rules this object provides.
 *
 * <p>Simplifying hasCapability and getCapability are outside the scope of this object... but remember that "null" is a
 * valid side, and often represents the side a probe observer accesses, so plan your views accordingly.
 *
 */
public class ConcreteFluidTank extends FluidTank implements IObservableFluidTank {
	private final List<Runnable> listeners = Lists.newArrayList();
	private Predicate<FluidStack> fillValidator = Validators.ANY_FLUID;

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


	public void markDirty() {
		listeners.forEach(Runnable::run);
	}

	@Override
	public void listen(@Nonnull Runnable r) {
		listeners.add(r);
	}

	@Override
	protected void onContentsChanged() {
		this.markDirty();
		super.onContentsChanged();
	}

	@Nonnull
	public Predicate<FluidStack> getFillValidator() {
		return this.fillValidator;
	}


}
