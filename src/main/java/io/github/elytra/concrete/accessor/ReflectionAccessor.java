package io.github.elytra.concrete.accessor;

import java.lang.reflect.Field;

import com.google.common.base.Throwables;

class ReflectionAccessor<T> implements Accessor<T> {
	private Field f;
	public ReflectionAccessor(Field f) {
		this.f = f;
	}
	@Override
	public T get(Object owner) {
		try {
			return (T)f.get(owner);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
	@Override
	public void set(Object owner, T value) {
		try {
			f.set(owner, value);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
}