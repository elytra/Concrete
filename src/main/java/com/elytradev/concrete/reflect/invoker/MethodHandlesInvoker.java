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

package com.elytradev.concrete.reflect.invoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import com.google.common.base.Throwables;

public class MethodHandlesInvoker implements Invoker {

	private final MethodHandle handle;

	public MethodHandlesInvoker(Method m) {
		try {
			m.setAccessible(true);
			handle = MethodHandles.publicLookup().unreflect(m);
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Object invoke(Object owner, Object... args) {
		Object[] joined;
		if (owner != null) {
			joined = new Object[args.length + 1];
			joined[0] = owner;
			System.arraycopy(args, 0, joined, 1, args.length);
		} else {
			joined = args;
		}
		try {
			return handle.invokeWithArguments(joined);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

}
