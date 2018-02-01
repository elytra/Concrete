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

package com.elytradev.concrete.reflect.accessor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.common.base.Throwables;

class MethodHandlesAccessor<T> implements Accessor<T> {
	
	private final MethodHandle getter;
	private final MethodHandle setter;
	
	public MethodHandlesAccessor(Field f) {
		try {
			f.setAccessible(true);
			getter = MethodHandles.publicLookup().unreflectGetter(f);
			setter = MethodHandles.publicLookup().unreflectSetter(f);
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}
	
	public MethodHandlesAccessor(Method get, Method set) {
		try {
			get.setAccessible(true);
			set.setAccessible(true);
			getter = MethodHandles.publicLookup().unreflect(get);
			setter = MethodHandles.publicLookup().unreflect(set);
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}
	
	@Override
	public T get(Object owner) {
		try {
			if (owner == null) {
				return (T) getter.invoke();
			} else {
				return (T) getter.invoke(owner);
			}
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	@Override
	public void set(Object owner, T value) {
		try {
			if (owner == null) {
				setter.invoke(value);
			} else {
				setter.invoke(owner, value);
			}
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}