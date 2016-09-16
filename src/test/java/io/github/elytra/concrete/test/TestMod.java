package io.github.elytra.concrete.test;

import io.github.elytra.concrete.NetworkContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

@Mod(modid="concrete", name="concrete", version="0.0.1")
public class TestMod {
	public NetworkContext network;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		network = NetworkContext.forChannel("ConcreteTest");
		network.register(TestMessage.class);
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
