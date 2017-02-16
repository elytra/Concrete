package com.elytradev.concrete.reflect.instanciator;

public interface Instanciator<T> {
	T newInstance(Object... args);
}