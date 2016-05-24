package io.github.elytra.concrete.test;

import io.github.elytra.concrete.DefaultMarshallers;
import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.Asynchronous;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.CLIENT)
@Asynchronous
public class TestMessage extends Message {

	// this doesn't work
	@MarshalledAs(DefaultMarshallers.UINT8)
	public int someByte;
	
	public TestMessage(NetworkContext ctx) {
		super(ctx);
	}

	@Override
	protected void handle(EntityPlayer sender) {
		System.out.println(sender);
	}
	
	

}
