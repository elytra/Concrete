package io.github.elytra.concrete;

import io.netty.buffer.ByteBuf;

/**
 * Handles the serializing and deserializing of a type.
 */
public interface Marshaller<T> {
	T unmarshal(ByteBuf in);
	void marshal(ByteBuf out, T t);
}
