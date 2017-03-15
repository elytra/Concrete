package com.elytradev.concrete.network;

import io.netty.buffer.ByteBuf;

public interface Marshallable {
	void writeToNetwork(ByteBuf buf);
	void readFromNetwork(ByteBuf buf);
}
