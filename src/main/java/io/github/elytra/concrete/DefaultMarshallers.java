package io.github.elytra.concrete;

import java.util.List;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * A set of default marshallers for common data types.
 */
public class DefaultMarshallers {
	
	public static final Marshaller<? extends Number> UINT8 = weld(ByteBuf::writeByte, ByteBuf::readUnsignedByte);
	public static final Marshaller<? extends Number> INT8 = weld(ByteBuf::writeByte, ByteBuf::readByte);
	
	public static final Marshaller<? extends Number> UINT16 = weld(ByteBuf::writeShort, ByteBuf::readUnsignedShort);
	public static final Marshaller<? extends Number> INT16 = weld(ByteBuf::writeShort, ByteBuf::readShort);
	
	public static final Marshaller<? extends Number> UINT24 = weld(ByteBuf::writeMedium, ByteBuf::readUnsignedMedium);
	public static final Marshaller<? extends Number> INT24 = weld(ByteBuf::writeMedium, ByteBuf::readMedium);
	
	public static final Marshaller<? extends Number> UINT32 = weld(ByteBuf::writeInt, ByteBuf::readUnsignedInt);
	public static final Marshaller<? extends Number> INT32 = weld(ByteBuf::writeInt, ByteBuf::readInt);
	
	public static final Marshaller<? extends Number> INT64 = weld(ByteBuf::writeLong, ByteBuf::readLong);
	
	public static final Marshaller<? extends Number> FLOAT = weld(ByteBuf::writeFloat, ByteBuf::readFloat);
	
	public static final Marshaller<? extends Number> DOUBLE = weld(ByteBuf::writeDouble, ByteBuf::readDouble);
	
	public static final Marshaller<? extends Number> VARINT = new VarIntMarshaller();
	
	
	public static final Marshaller<NBTTagCompound> NBT = weld(ByteBufUtils::writeTag, ByteBufUtils::readTag);
	
	public static final Marshaller<BlockPos> BLOCKPOS = new BlockPosMarshaller();
	
	
	
	
	public static final ListMarshaller<? extends Number> UINT8_LIST = new ListMarshaller<>(UINT8);
	public static final ListMarshaller<? extends Number> INT8_LIST = new ListMarshaller<>(INT8);

	public static final ListMarshaller<? extends Number> UINT16_LIST = new ListMarshaller<>(UINT16);
	public static final ListMarshaller<? extends Number> INT16_LIST = new ListMarshaller<>(INT16);

	public static final ListMarshaller<? extends Number> UINT24_LIST = new ListMarshaller<>(UINT24);
	public static final ListMarshaller<? extends Number> INT24_LIST = new ListMarshaller<>(INT24);

	public static final ListMarshaller<? extends Number> UINT32_LIST = new ListMarshaller<>(UINT32);
	public static final ListMarshaller<? extends Number> INT32_LIST = new ListMarshaller<>(INT32);

	public static final ListMarshaller<? extends Number> INT64_LIST = new ListMarshaller<>(INT64);

	public static final ListMarshaller<? extends Number> FLOAT_LIST = new ListMarshaller<>(FLOAT);

	public static final ListMarshaller<? extends Number> DOUBLE_LIST = new ListMarshaller<>(DOUBLE);

	public static final ListMarshaller<? extends Number> VARINT_LIST = new ListMarshaller<>(VARINT);

	public static final ListMarshaller<NBTTagCompound> NBT_LIST = new ListMarshaller<>(NBT);

	public static final ListMarshaller<BlockPos> BLOCKPOS_LIST = new ListMarshaller<>(BLOCKPOS);
	
	
	
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
}
