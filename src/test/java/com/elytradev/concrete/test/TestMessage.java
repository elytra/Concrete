package com.elytradev.concrete.test;

import com.elytradev.concrete.Message;
import com.elytradev.concrete.NetworkContext;
import com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.concrete.annotation.type.ReceivedOn;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

@ReceivedOn(Side.SERVER)
public class TestMessage extends Message {

	public boolean true1 = true;
	public boolean flse2 = false;
	public boolean flse3 = false;
	public boolean true4 = true;
	public boolean true5 = true;
	public boolean flse6 = false;
	public boolean true7 = true;
	public boolean true8 = true;
	public boolean flse9 = false;
	public boolean true10 = true;
	
	@MarshalledAs("u8")
	public int someByte = 255;
	@MarshalledAs("i8")
	public int someSignedByte = -128;
	public String someString = "Foo bar";
	
	public TestMessage(NetworkContext ctx) {
		super(ctx);
	}

	@Override
	protected void handle(EntityPlayer sender) {
		System.out.println(sender);
		System.out.println(this);
	}

	@Override
	public String toString() {
		return "TestMessage [true1=" + true1 + ", flse2=" + flse2 + ", flse3="
				+ flse3 + ", true4=" + true4 + ", true5=" + true5 + ", flse6="
				+ flse6 + ", true7=" + true7 + ", true8=" + true8 + ", flse9="
				+ flse9 + ", true10=" + true10 + ", someByte=" + someByte
				+ ", someSignedByte=" + someSignedByte + ", someString="
				+ someString + "]";
	}
	
	
	
	

}
