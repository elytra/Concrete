package com.elytradev.concrete.test;

import com.elytradev.concrete.NetworkContext;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

@Mod(modid="concrete", name="concrete", version="0.0.1")
public class TestMod {
	public NetworkContext network;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		network = NetworkContext.forChannel("ConcreteTest");
		network.register(TestMessage.class);
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onJoin(ClientConnectedToServerEvent e) {
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Minecraft.getMinecraft().addScheduledTask(() -> {
				try {
					new TestMessage(network).sendToServer();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			});
		}).start();
	}
}
