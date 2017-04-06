package com.elytradev.concrete;

import java.util.ArrayList;
import java.util.Collection;
import com.google.common.base.Supplier;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class NBTHelper {

	/**
	 * Convert the given Collection of INBTSerializable objects into an
	 * NBTTagList.
	 */
	public <T extends INBTSerializable<U>,
			U extends NBTBase> NBTTagList serialize(Collection<T> in) {
		NBTTagList out = new NBTTagList();
		for (T t : in) {
			out.appendTag(t.serializeNBT());
		}
		return out;
	}
	
	/**
	 * Convert the given NBTTagList into an ArrayList of INBTSerializable
	 * objects, using freshly-created objects from the passed constructor.
	 * <p>
	 * Example: {@code NBTHelper.deserialize(MyObject::new, myNBTList)}
	 */
	public <T extends INBTSerializable<U>,
			U extends NBTBase> ArrayList<T> deserialize(Supplier<T> constructor, NBTTagList in) {
		return deserialize(constructor, in, ArrayList::new);
	}
	
	/**
	 * Convert the given NBTTagList into a Collection of INBTSerializable
	 * objects, using freshly-created objects from the passed constructor, and
	 * a freshly-created collection from the passed collection constructor. 
	 * <p>
	 * Example: {@code NBTHelper.deserialize(MyObject::new, myNBTList, ArrayList::new)}
	 */
	public <T extends INBTSerializable<U>,
			U extends NBTBase,
			C extends Collection<T>> C deserialize(Supplier<T> constructor, NBTTagList in, Supplier<C> collectionConstructor) {
		return deserializeInto(constructor, in, collectionConstructor.get());
	}
	
	/**
	 * Add INBTSerializable objects from the given NBTTagList into a
	 * pre-existing Collection of the correct type, using freshly-created
	 * objects from the passed constructor.
	 * <p>
	 * This method does not clear the passed collection, that's up to you if you
	 * want to do it. 
	 * <p>
	 * Example: {@code NBTHelper.deserialize(MyObject::new, myNBTList, myList)}
	 */
	public <T extends INBTSerializable<U>,
			U extends NBTBase,
			C extends Collection<T>> C deserializeInto(Supplier<T> constructor, NBTTagList in, C collection) {
		for (int i = 0; i < in.tagCount(); i++) {
			T t = constructor.get();
			t.deserializeNBT((U)in.get(i));
			collection.add(t);
		}
		return collection;
	}
	
}
