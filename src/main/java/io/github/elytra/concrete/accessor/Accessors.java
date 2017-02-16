package io.github.elytra.concrete.accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
			return new ReflectionFieldAccessor<>(f);
		}
	}
	
	public static <T> Accessor<T> from(Method get, Method set) {
		if (methodHandlesAvailable) {
			return new MethodHandlesAccessor<>(get, set);
		} else {
			return new ReflectionMethodAccessor<>(get, set);
		}
	}
	
	private Accessors() {}
}
