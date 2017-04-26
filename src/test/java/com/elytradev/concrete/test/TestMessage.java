/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	William Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.concrete.test;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.Asynchronous;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.SERVER)
@Asynchronous
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
