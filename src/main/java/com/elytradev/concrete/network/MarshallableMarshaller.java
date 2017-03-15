package com.elytradev.concrete.network;

import com.elytradev.concrete.exception.BadMessageException;

import io.netty.buffer.ByteBuf;

public class MarshallableMarshaller<T extends Marshallable> implements Marshaller<T> {
	private final Class<T> clazz;
	public MarshallableMarshaller(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public T unmarshal(ByteBuf in) {
		T t;
		try {
			t = clazz.newInstance();
		} catch (Exception e) {
			throw new BadMessageException("Cannot instanciate marshallable "+clazz);
		}
		t.readFromNetwork(in);
		return t;
	}

	@Override
	public void marshal(ByteBuf out, T t) {
		t.writeToNetwork(out);
	}

}
