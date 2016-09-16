package io.github.elytra.concrete;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * A set of default marshallers for common data types.
 * <p>
 * Any marshaller can be used for lists by putting "-list" after its name.
 */
public class DefaultMarshallers {
	
	/**
	 * Unsigned 8-bit (1 byte) integer.
	 * <p>
	 * Aliases: u8, uint8, ubyte
	 */
	public static final Marshaller<? extends Number> UINT8 = weld(ByteBuf::writeByte, ByteBuf::readUnsignedByte);
	/**
	 * Signed 8-bit (1 byte) integer.
	 * <p>
	 * Aliases: i8, int8, byte
	 */
	public static final Marshaller<? extends Number> INT8 = weld(ByteBuf::writeByte, ByteBuf::readByte);
	
	
	/**
	 * Unsigned 16-bit (2 byte) integer.
	 * <p>
	 * Aliases: u16, uint16, char, ushort
	 */
	public static final Marshaller<? extends Number> UINT16 = weld(ByteBuf::writeShort, ByteBuf::readUnsignedShort);
	/**
	 * Signed 16-bit (2 byte) integer.
	 * <p>
	 * Aliases: i16, int16, short
	 */
	public static final Marshaller<? extends Number> INT16 = weld(ByteBuf::writeShort, ByteBuf::readShort);
	
	
	/**
	 * Unsigned 24-bit (3 byte) integer.
	 * <p>
	 * Aliases: u24, uint24, umedium
	 */
	public static final Marshaller<? extends Number> UINT24 = weld(ByteBuf::writeMedium, ByteBuf::readUnsignedMedium);
	/**
	 * Signed 24-bit (3 byte) integer.
	 * <p>
	 * Aliases: i24, int24, medium
	 */
	public static final Marshaller<? extends Number> INT24 = weld(ByteBuf::writeMedium, ByteBuf::readMedium);
	
	
	/**
	 * Unsigned 32-bit (4 byte) integer.
	 * <p>
	 * Aliases: u32, uint32, uint, uinteger
	 */
	public static final Marshaller<? extends Number> UINT32 = weld(ByteBuf::writeInt, ByteBuf::readUnsignedInt);
	/**
	 * Signed 32-bit (4 byte) integer.
	 * <p>
	 * Aliases: i32, int32, int, integer
	 */
	public static final Marshaller<? extends Number> INT32 = weld(ByteBuf::writeInt, ByteBuf::readInt);
	
	
	/**
	 * Signed 64-bit (8 byte) integer.
	 * <p>
	 * Aliases: i64, int64, long
	 */
	public static final Marshaller<? extends Number> INT64 = weld(ByteBuf::writeLong, ByteBuf::readLong);
	
	
	/**
	 * 32-bit floating point.
	 * <p>
	 * Aliases: f32, float
	 */
	public static final Marshaller<? extends Number> FLOAT = weld(ByteBuf::writeFloat, ByteBuf::readFloat);
	
	
	/**
	 * 64-bit floating point.
	 * <p>
	 * Aliases: f64, double
	 */
	public static final Marshaller<? extends Number> DOUBLE = weld(ByteBuf::writeDouble, ByteBuf::readDouble);
	
	
	/**
	 * Protobuf variable sized integer.
	 * <p>
	 * Aliases: varint
	 */
	public static final Marshaller<? extends Number> VARINT = new VarIntMarshaller();
	
	
	/**
	 * Compound NBT tag.
	 */
	public static final Marshaller<NBTTagCompound> NBT = weld(ByteBufUtils::writeTag, ByteBufUtils::readTag);
	
	
	/**
	 * 64-bit packed BlockPos.
	 */
	public static final Marshaller<BlockPos> BLOCKPOS = new BlockPosMarshaller();
	
	
	/**
	 * UTF-8 varint-length-prefixed string.
	 */
	public static final Marshaller<String> STRING = weld(ByteBufUtils::writeUTF8String, ByteBufUtils::readUTF8String);
	
	
	
	private static final Map<String, Marshaller<?>> byName = Maps.newHashMap();
	
	
	static {
		put(UINT8, "u8", "uint8", "ubyte");
		put(INT8, "i8", "int8", "byte");
		
		put(UINT16, "u16", "uint16", "char", "ushort");
		put(INT16, "i16", "int16", "short");
		
		put(UINT24, "u24", "uint24", "umedium");
		put(INT24, "i24", "int24", "medium");
		
		put(UINT32, "u32", "uint32", "uint", "uinteger");
		put(INT32, "i32", "int32", "int", "integer");
		
		put(INT64, "i64", "int64", "long");
		
		put(FLOAT, "f32", "float");
		put(DOUBLE, "f64", "double");
		
		put(VARINT, "varint");
	}
	
	private static void put(Marshaller<?> m, String... names) {
		for (String name : names) {
			byName.put(name, m);
		}
	}
	
	
	
	public static class ListMarshaller<T> implements Marshaller<List<T>> {
		private Marshaller<T> underlying;
		
		public ListMarshaller(Marshaller<T> underlying) {
			this.underlying = underlying;
		}
		
		@Override
		public List<T> unmarshal(ByteBuf in) {
			int size = ByteBufUtils.readVarInt(in, 5);
			List<T> li = Lists.newArrayListWithExpectedSize(size);
			for (int i = 0; i < size; i++) {
				li.add(underlying.unmarshal(in));
			}
			return li;
		}

		@Override
		public void marshal(ByteBuf out, List<T> li) {
			if (li == null) {
				ByteBufUtils.writeVarInt(out, 0, 5);
			} else {
				ByteBufUtils.writeVarInt(out, li.size(), 5);
				for (T t : li) {
					underlying.marshal(out, t);
				}
			}
		}
		
	}
	
	private static class BlockPosMarshaller implements Marshaller<BlockPos> {

		@Override
		public BlockPos unmarshal(ByteBuf in) {
			return BlockPos.fromLong(in.readLong());
		}

		@Override
		public void marshal(ByteBuf out, BlockPos t) {
			out.writeLong(t.toLong());
		}

	}
	
	private static class VarIntMarshaller implements Marshaller<Number> {

		@Override
		public Number unmarshal(ByteBuf in) {
			return ByteBufUtils.readVarInt(in, 5);
		}

		@Override
		public void marshal(ByteBuf out, Number t) {
			ByteBufUtils.writeVarInt(out, t.intValue(), 5);
		}

	}
	
	
	private static <T> Marshaller<T> weld(Serializer<T> serializer, Deserializer deserializer) {
		return new Marshaller<T>() {
			@Override
			public void marshal(ByteBuf out, T t) {
				serializer.serialize(out, t);
			}
			@Override
			public T unmarshal(ByteBuf in) {
				return (T)deserializer.deserialize(in);
			}
		};
	}
	
	private interface Serializer<T> {
		void serialize(ByteBuf out, T t);
	}
	private interface Deserializer {
		Object deserialize(ByteBuf in);
	}
	
	public static <T> Marshaller<T> getByName(String name) {
		return (Marshaller<T>)byName.get(name.toLowerCase(Locale.ROOT));
	}

	public static <T> Marshaller<T> getByType(Class<T> type) {
		if (String.class.isAssignableFrom(type)) {
			return (Marshaller<T>)STRING;
		} else if (BlockPos.class.isAssignableFrom(type)) {
			return (Marshaller<T>)BLOCKPOS;
		} else if (NBTTagCompound.class.isAssignableFrom(type)) {
			return (Marshaller<T>)NBT;
		}
		return null;
	}
}
