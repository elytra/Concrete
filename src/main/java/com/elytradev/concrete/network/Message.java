/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
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

package com.elytradev.concrete.network;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.elytradev.concrete.network.annotation.type.Asynchronous;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.concrete.network.exception.BadMessageException;
import com.elytradev.concrete.network.exception.WrongSideException;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Message {
	private static final class ClassInfo {
		public final boolean async;
		public final Side side;
		public ClassInfo(boolean async, Side side) {
			this.async = async;
			this.side = side;
		}
	}
	private static final Map<Class<?>, ClassInfo> classInfo = Maps.newHashMap();
	
	
	private transient final NetworkContext ctx;
	
	private transient final Side side;
	private transient final boolean async;
	
	public Message(NetworkContext ctx) {
		this.ctx = ctx;
		
		ClassInfo ci = classInfo.get(getClass());
		if (ci == null) {
			ReceivedOn ro = getClass().getDeclaredAnnotation(ReceivedOn.class);
			if (ro == null) {
				throw new BadMessageException("Must specify @ReceivedOn");
			} else {
				side = ro.value();
			}
			
			async = getClass().getDeclaredAnnotation(Asynchronous.class) != null;
			classInfo.put(getClass(), new ClassInfo(async, side));
		} else {
			async = ci.async;
			side = ci.side;
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	void doHandleClient() {
		if (async) {
			handle(Minecraft.getMinecraft().player);
		} else {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void run() {
					handle(Minecraft.getMinecraft().player);
				}
			});
		}
	}
	
	void doHandleServer(EntityPlayer sender) {
		if (async) {
			handle(sender);
		} else {
			((WorldServer) sender.world).addScheduledTask(() -> handle(sender));
		}
	}
	
	/**
	 * Handles this Message when received.
	 *
	 * @param player The player that sent this Message if received on the server.
	 *               The player that received this Message if received on the client.
	 */
	protected abstract void handle(EntityPlayer player);
	
	Side getSide() {
		return side;
	}
	
	/**
	 * For use on the server-side. Sends this Message to the given player.
	 */
	public final void sendTo(EntityPlayer player) {
		if (side.isServer()) wrongSide();
		if (player instanceof EntityPlayerMP) {
			for (Packet<INetHandlerPlayClient> p : toClientboundVanillaPackets()) {
				((EntityPlayerMP) player).connection.sendPacket(p);
			}
		}
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position. <i>It is almost always
	 * better to use {@link #sendToAllWatching(Entity)}, this is only useful for
	 * certain special cases.</i>
	 */
	public final void sendToAllAround(World world, Entity entity, double radius) {
		sendToAllAroundExcept(world, entity, radius, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position. <i>It is almost always
	 * better to use {@link #sendToAllWatching(World, BlockPos)}, this is only
	 * useful for certain special cases.</i>
	 */
	public final void sendToAllAround(World world, Vec3i pos, double radius) {
		sendToAllAroundExcept(world, pos, radius, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position.
	 */
	public final void sendToAllAround(World world, Vec3d pos, double radius) {
		sendToAllAroundExcept(world, pos, radius, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position.
	 */
	public final void sendToAllAround(World world, double x, double y, double z, double radius) {
		sendToAllAroundExcept(world, x, y, z, radius, null);
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position, except the given player.
	 * <i>It is almost always better to use {@link #sendToAllWatching(Entity)},
	 * this is only useful for certain special cases.</i>
	 */
	public final void sendToAllAroundExcept(World world, Entity entity, double radius, @Nullable EntityPlayer exclude) {
		sendToAllAroundExcept(world, entity.posX, entity.posY, entity.posZ, radius, exclude);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position, except the given player.
	 * <i>It is almost always better to use {@link #sendToAllWatching(World, BlockPos)},
	 * this is only useful for certain special cases.</i>
	 */
	public final void sendToAllAroundExcept(World world, Vec3i pos, double radius, @Nullable EntityPlayer exclude) {
		sendToAllAroundExcept(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, exclude);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position, except the given player.
	 */
	public final void sendToAllAroundExcept(World world, Vec3d pos, double radius, @Nullable EntityPlayer exclude) {
		sendToAllAroundExcept(world, pos.x, pos.y, pos.z, radius, exclude);
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position, except the given player.
	 */
	public final void sendToAllAroundExcept(World world, double x, double y, double z, double radius, @Nullable EntityPlayer exclude) {
		if (side.isServer()) wrongSide();
		double sq = radius * radius;
		List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
			if (ep == exclude) continue;
			if (ep.getDistanceSq(x, y, z) <= sq) {
				for (Packet<INetHandlerPlayClient> packet : packets) {
					ep.connection.sendPacket(packet);
				}
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given block.
	 */
	public final void sendToAllWatching(World world, BlockPos pos) {
		sendToAllWatchingExcept(world, pos, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given block, except the given player.
	 */
	public final void sendToAllWatchingExcept(World world, BlockPos pos, @Nullable EntityPlayer exclude) {
		if (side.isServer()) wrongSide();
		if (world instanceof WorldServer) {
			WorldServer srv = (WorldServer) world;
			Chunk c = srv.getChunkFromBlockCoords(pos);
			if (srv.getPlayerChunkMap().contains(c.x, c.z)) {
				List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
				for (EntityPlayerMP ep : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
					if (ep == exclude) continue;
					if (srv.getPlayerChunkMap().isPlayerWatchingChunk(ep, c.x, c.z)) {
						for (Packet<INetHandlerPlayClient> packet : packets) {
							ep.connection.sendPacket(packet);
						}
					}
				}
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given tile entity.
	 */
	public final void sendToAllWatching(TileEntity te) {
		sendToAllWatchingExcept(te, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given tile entity, except the given player.
	 */
	public final void sendToAllWatchingExcept(TileEntity te, @Nullable EntityPlayer exclude) {
		sendToAllWatchingExcept(te.getWorld(), te.getPos(), exclude);
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity.
	 */
	public final void sendToAllWatching(Entity e) {
		sendToAllWatchingExcept(e, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity, except the given player.
	 */
	public final void sendToAllWatchingExcept(Entity e, @Nullable EntityPlayer exclude) {
		if (side.isServer()) wrongSide();
		if (e.world instanceof WorldServer) {
			WorldServer srv = (WorldServer) e.world;
			List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
			for (EntityPlayer ep : srv.getEntityTracker().getTrackingPlayers(e)) {
				if (ep == exclude) continue;
				if (ep instanceof EntityPlayerMP) {
					for (Packet<INetHandlerPlayClient> packet : packets) {
						((EntityPlayerMP) ep).connection.sendPacket(packet);
					}
				}
			}
		}
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity, and the entity itself if it's a player.
	 */
	public final void sendToAllWatchingAndSelf(Entity e) {
		sendToAllWatchingAndSelfExcept(e, null);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity, and the entity itself if it's a player, except the
	 * given player.
	 */
	public final void sendToAllWatchingAndSelfExcept(Entity e, @Nullable EntityPlayer exclude) {
		if (side.isServer()) wrongSide();
		if (e.world instanceof WorldServer) {
			WorldServer srv = (WorldServer) e.world;
			List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
			for (EntityPlayer ep : srv.getEntityTracker().getTrackingPlayers(e)) {
				if (ep == exclude) continue;
				if (ep instanceof EntityPlayerMP) {
					for (Packet<INetHandlerPlayClient> packet : packets) {
						((EntityPlayerMP) ep).connection.sendPacket(packet);
					}
				}
			}
			if (e instanceof EntityPlayerMP) {
				for (Packet<INetHandlerPlayClient> packet : packets) {
					((EntityPlayerMP) e).connection.sendPacket(packet);
				}
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player in the
	 * given world.
	 */
	public final void sendToAllIn(World world) {
		sendToAllInExcept(world, null);
	}
	/**
	 * For use on the server-side. Sends this Message to every player in the
	 * given world, except the given player.
	 */
	public final void sendToAllInExcept(World world, @Nullable EntityPlayer exclude) {	
		if (side.isServer()) wrongSide();
		List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
			if (ep == exclude) continue;
			for (Packet<INetHandlerPlayClient> packet : packets) {
				ep.connection.sendPacket(packet);
			}
		}
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player currently
	 * connected to the server. Use sparingly, you almost never need to send
	 * a packet to everyone.
	 */
	public final void sendToEveryone() {
		if (side.isServer()) wrongSide();
		List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
			for (Packet<INetHandlerPlayClient> packet : packets) {
				ep.connection.sendPacket(packet);
			}
		}
	}
	
	/**
	 * For use on the <i>client</i>-side. This is the only valid method for use
	 * on the client side.
	 */
	@SideOnly(Side.CLIENT)
	public final void sendToServer() {
		if (side.isClient()) wrongSide();
		NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
		if (conn == null) throw new IllegalStateException("Cannot send a message while not connected");
		conn.sendPacket(toServerboundVanillaPacket());
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final Packet<INetHandlerPlayServer> toServerboundVanillaPacket() {
		return ctx.getPacketFrom(this).toC17Packet();
	}
	
	/**
	 * Mainly intended for internal use, but can be useful for more complex
	 * use cases.
	 */
	public final List<Packet<INetHandlerPlayClient>> toClientboundVanillaPackets() {
		try {
			return ctx.getPacketFrom(this).toS3FPackets();
		} catch (IOException e) {
			throw new BadMessageException(e);
		}
	}
	
	
	private void wrongSide() {
		throw new WrongSideException(getClass() + " cannot be sent from side " + side);
	}
}
