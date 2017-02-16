package io.github.elytra.concrete.invoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import com.google.common.base.Throwables;

public class MethodHandlesInvoker implements Invoker {

	private MethodHandle handle;

	public MethodHandlesInvoker(Method m) {
		try {
			m.setAccessible(true);
			handle = MethodHandles.lookup().unreflect(m);
		} catch (IllegalAccessException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public Object invoke(Object owner, Object... args) {
		Object[] joined = new Object[args.length+1];
		joined[0] = owner;
		System.arraycopy(args, 0, joined, 1, args.length);
		try {
			return handle.invoke(joined);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

}
