package io.github.elytra.concrete.accessor;

import java.lang.reflect.Field;

public final class Accessors {
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
	
	public static <T> Accessor<T> from(Field f) {
		if (methodHandlesAvailable) {
			return new MethodHandlesAccessor<>(f);
		} else {
			return new ReflectionAccessor<>(f);
		}
	}
	
	private Accessors() {}
}
