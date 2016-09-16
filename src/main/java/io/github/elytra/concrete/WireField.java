package io.github.elytra.concrete;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.google.common.base.Throwables;

import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.exception.BadMessageException;
import io.netty.buffer.ByteBuf;

class WireField<T> {
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
	private Accessor<T> accessor;
	private Marshaller<T> marshaller;
	private Class<T> type;
	
	public WireField(Field f) {
		f.setAccessible(true);
		if (methodHandlesAvailable) {
			accessor = new MethodHandlesAccessor<>(f);
		} else {
			accessor = new ReflectionAccessor<>(f);
		}
		try {
			type = (Class<T>) f.getType();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		MarshalledAs ma = f.getAnnotation(MarshalledAs.class);
		if (ma != null) {
			marshaller = DefaultMarshallers.getByName(ma.value());
			if (marshaller == null) {
				try {
					Class<?> clazz = Class.forName(ma.value());
					if (Marshaller.class.isAssignableFrom(clazz)) {
						try {
							Field inst = clazz.getDeclaredField("INSTANCE");
							inst.setAccessible(true);
							marshaller = (Marshaller<T>) inst.get(null);
						} catch (Exception e) {
							NetworkContext.log.warn(clazz.getName()+" does not appear to define an INSTANCE field, but it should", e);
						}
						if (marshaller == null) {
							try {
								Constructor<?> cons = clazz.getConstructor();
								marshaller = (Marshaller<T>) cons.newInstance();
							} catch (Exception e) {
								throw new BadMessageException("Cannot instanciate marshaller class "+clazz.getName());
							}
						}
					}
				} catch (Exception e) {
					throw new BadMessageException("Cannot figure out what marshaller \""+ma.value()+"\" refers to! (Field "+f.getName()+" in "+f.getDeclaringClass().getName()+")", e);
				}
			}
		} else if (Marshallable.class.isAssignableFrom(type)) {
			marshaller = new MarshallableMarshaller(type);
		} else {
			marshaller = DefaultMarshallers.getByType(type);
		}
		if (marshaller == null && type != Boolean.TYPE) {
			throw new BadMessageException("Cannot find an appropriate marshaller for field "+type+" "+f.getDeclaringClass().getName()+"."+f.getName());
		}
	}
	
	public T get(Object owner) {
		return accessor.get(owner);
	}
	
	public void set(Object owner, T value) {
		accessor.set(owner, value);
	}
	
	
	public void marshal(Object owner, ByteBuf out) {
		marshaller.marshal(out, accessor.get(owner));
	}
	public void unmarshal(Object owner, ByteBuf in) {
		accessor.set(owner, marshaller.unmarshal(in));
	}
	
	
	public Class<? extends T> getType() {
		return type;
	}
	
	
	public interface Accessor<T> {
		T get(Object owner);
		void set(Object owner, T value);
	}
	public static class MethodHandlesAccessor<T> implements Accessor<T> {
		private MethodHandle getter;
		private MethodHandle setter;
		public MethodHandlesAccessor(Field f) {
			try {
				f.setAccessible(true);
				getter = MethodHandles.lookup().unreflectGetter(f);
				setter = MethodHandles.lookup().unreflectSetter(f);
			} catch (IllegalAccessException e) {
				Throwables.propagate(e);
			}
		}
		@Override
		public T get(Object owner) {
			try {
				return (T)getter.invoke(owner);
			} catch (Throwable e) {
				throw Throwables.propagate(e);
			}
		}
		@Override
		public void set(Object owner, T value) {
			try {
				setter.invoke(owner, value);
			} catch (Throwable e) {
				throw Throwables.propagate(e);
			}
		}
	}
	public static class ReflectionAccessor<T> implements Accessor<T> {
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
}
