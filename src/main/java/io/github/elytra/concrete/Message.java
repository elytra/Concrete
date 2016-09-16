package io.github.elytra.concrete;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import io.github.elytra.concrete.annotation.type.Asynchronous;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import io.github.elytra.concrete.exception.BadMessageException;
import io.github.elytra.concrete.exception.WrongSideException;
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
			ci = new ClassInfo(async, side);
		} else {
			async = ci.async;
			side = ci.side;
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	void doHandleClient() {
		if (async) {
			handle(Minecraft.getMinecraft().thePlayer);
		} else {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				handle(Minecraft.getMinecraft().thePlayer);
			});
		}
	}
	
	void doHandleServer(EntityPlayer sender) {
		if (async) {
			handle(sender);
		} else {
			((WorldServer)sender.worldObj).addScheduledTask(() -> {
				handle(sender);
			});
		}
	}
	
	protected abstract void handle(EntityPlayer sender);
	
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
				((EntityPlayerMP)player).connection.sendPacket(p);
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
		sendToAllAround(world, entity.posX, entity.posY, entity.posZ, radius);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position. <i>It is almost always
	 * better to use {@link #sendToAllWatching(World, BlockPos)}, this is only
	 * useful for certain special cases.</i>
	 */
	public final void sendToAllAround(World world, Vec3i pos, double radius) {
		sendToAllAround(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, radius);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position.
	 */
	public final void sendToAllAround(World world, Vec3d pos, double radius) {
		sendToAllAround(world, pos.xCoord, pos.yCoord, pos.zCoord, radius);
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player that is
	 * within the given radius of the given position.
	 */
	public final void sendToAllAround(World world, double x, double y, double z, double radius) {
		if (side.isServer()) wrongSide();
		double sq = radius*radius;
		List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
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
		if (side.isServer()) wrongSide();
		if (world instanceof WorldServer) {
			WorldServer srv = (WorldServer)world;
			Chunk c = srv.getChunkFromBlockCoords(pos);
			if (srv.getPlayerChunkMap().contains(c.xPosition, c.zPosition)) {
				List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
				for (EntityPlayerMP ep : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
					if (srv.getPlayerChunkMap().isPlayerWatchingChunk(ep, c.xPosition, c.zPosition)) {
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
		sendToAllWatching(te.getWorld(), te.getPos());
	}
	
	
	/**
	 * For use on the server-side. Sends this Message to every player that can
	 * see the given entity.
	 */
	public final void sendToAllWatching(Entity e) {
		if (side.isServer()) wrongSide();
		if (e.worldObj instanceof WorldServer) {
			WorldServer srv = (WorldServer)e.worldObj;
			List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
			for (Packet<INetHandlerPlayClient> packet : packets) {
				srv.getEntityTracker().sendToAllTrackingEntity(e, packet);
			}
		}
	}
	
	/**
	 * For use on the server-side. Sends this Message to every player in the
	 * given world.
	 */
	public final void sendToAllIn(World world) {
		if (side.isServer()) wrongSide();
		List<Packet<INetHandlerPlayClient>> packets = toClientboundVanillaPackets();
		for (EntityPlayerMP ep : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
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
		for (EntityPlayerMP ep : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
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
		throw new WrongSideException(getClass()+" cannot be sent from side "+side);
	}
}
