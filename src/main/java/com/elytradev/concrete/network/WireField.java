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

package com.elytradev.concrete.network;

import java.lang.reflect.Field;

import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.field.Optional;
import com.elytradev.concrete.network.exception.BadMessageException;
import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;

class WireField<T> {
	private final Field f;
	private final Accessor<T> accessor;
	private final Marshaller<T> marshaller;
	private final Class<T> type;
	private final boolean optional;
	
	public WireField(Field f) {
		f.setAccessible(true);
		this.f = f;
		accessor = Accessors.from(f);
		try {
			type = (Class<T>) f.getType();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		MarshalledAs ma = f.getAnnotation(MarshalledAs.class);
		if (ma != null) {
			marshaller = DefaultMarshallers.getByName(ma.value());
		} else if (Marshallable.class.isAssignableFrom(type)) {
			marshaller = new MarshallableMarshaller(type);
		} else {
			marshaller = DefaultMarshallers.getByType(type);
		}
		optional = f.getAnnotation(Optional.class) != null;
		if (marshaller == null && type != Boolean.TYPE) {
			String annot = "";
			if (ma != null) {
				annot = "@MarshalledAs(\"" + ma.value().replace("\"", "\\\"") + "\") ";
			}
			if (optional) {
				annot = annot + "@Optional ";
			}
			throw new BadMessageException("Cannot find an appropriate marshaller for field " + annot + type + " " + f.getDeclaringClass().getName() + "." + f.getName());
		}
	}
	
	public T get(Object owner) {
		return accessor.get(owner);
	}
	
	public void set(Object owner, T value) {
		accessor.set(owner, value);
	}
	
	
	public void marshal(Object owner, ByteBuf out) {
		T value = accessor.get(owner);
		if (value == null) throw new BadMessageException("Wire fields cannot be null (in " + type + " " + f.getDeclaringClass().getName() + "." + f.getName() + ")");
		marshaller.marshal(out, value);
	}
	public void unmarshal(Object owner, ByteBuf in) {
		accessor.set(owner, marshaller.unmarshal(in));
	}
	
	
	public boolean isOptional() {
		return optional;
	}
	
	
	public Class<? extends T> getType() {
		return type;
	}
}
