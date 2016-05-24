package io.github.elytra.concrete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.elytra.concrete.exception.BadMessageException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkContext {
	static final Logger log = LogManager.getLogger("Concrete");
	
	protected BiMap<Class<? extends Message>, Integer> packetIds = HashBiMap.create();
	protected String channel;
	
	private int nextPacketId = 0;
	
	private NetworkContext(String channel) {
		if (NetworkContext.class.getPackage().getName().equals("io.github.elytra.concrete")
				&& !((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))) {
			throw new RuntimeException("Elytra Concrete is designed to be shaded and must not be left in the default package!");
		}
	}
	
	public NetworkContext register(Class<? extends Message> clazz) {
		if (packetIds.containsKey(clazz)) {
			log.warn("{} was registered twice", clazz);
			return this;
		}
		packetIds.put(clazz, nextPacketId++);
		return this;
	}
	
	
	public String getChannel() {
		return channel;
	}
	
	
	
	protected FMLProxyPacket getPacketFrom(Message m) {
		if (!packetIds.containsKey(m.getClass())) throw new BadMessageException(m.getClass()+" is not registered");
		PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
		payload.writeByte(packetIds.get(m.getClass()));
		// TODO
		return new FMLProxyPacket(payload, channel);
	}


	@SubscribeEvent
	public void onServerCustomPacket(ServerCustomPacketEvent e) {
		ByteBuf payload = e.getPacket().payload();
		readPacket(e.side(), ((NetHandlerPlayServer)e.getHandler()).playerEntity, payload);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientCustomPacket(ClientCustomPacketEvent e) {
		ByteBuf payload = e.getPacket().payload();
		readPacket(e.side(), Minecraft.getMinecraft().thePlayer, payload);
	}
	
	
	private void readPacket(Side side, EntityPlayer p, ByteBuf payload) {
		int id = payload.readUnsignedByte();
		if (!packetIds.containsValue(id)) {
			throw new IllegalArgumentException("Unknown packet id "+id);
		}
		// TODO
	}
	
	
	public static NetworkContext forChannel(String channel) {
		if (channel.length() > 20)
			throw new IllegalArgumentException("Channel name too long, must be at most 20 characters");
		return new NetworkContext(channel);
	}
}
