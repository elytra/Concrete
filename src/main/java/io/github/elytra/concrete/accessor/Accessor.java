package io.github.elytra.concrete.accessor;

public interface Accessor<T> {
	T get(Object owner);
	void set(Object owner, T value);
}