package com.elytradev.concrete.reflect.instanciator;

import java.lang.reflect.Constructor;

public final class Instanciators {
	private static final boolean methodHandlesAvailable;
	static {
		boolean hasMethodHandles;
		try {
			Class.forName("java.lang.invoke.MethodHandles");
			hasMethodHandles = true;
		} catch (Exception e) {
			hasMethodHandles = false;
		}
		methodHandlesAvailable = hasMethodHandles;
	}
	
	public static <T> Instanciator<T> from(Constructor<T> c) {
		if (methodHandlesAvailable) {
			return new MethodHandlesInstanciator<>(c);
		} else {
			return new ReflectionInstanciator<>(c);
		}
	}
	
	private Instanciators() {}
}
