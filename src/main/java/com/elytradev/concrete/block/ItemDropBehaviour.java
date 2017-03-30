package com.elytradev.concrete.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/**
 * An interface used to describe the behaviour for item drops for a {@link ConcreteBlock}.
 */
public interface ItemDropBehaviour {

    /**
     * A drop behaviour for dropping no items.
     */
    ItemDropBehaviour DROP_NONE = of(0);

    /**
     * The default drop behaviour.
     */
    ItemDropBehaviour DEFAULT = of(1);

    /**
     * Creates a drop behaviour that drops the given item quantity.
     *
     * @param quantity The quantity of items to drop
     * @return The drop behaviour
     */
    static ItemDropBehaviour of(int quantity) {
        return (random) -> quantity;
    }

    /**
     * Creates a drop behaviour that drops the given item quantity.
     *
     * @param drop The item to be dropped
     * @param quantity The quantity of items to drop
     * @return The drop behaviour
     */
    static ItemDropBehaviour of(Supplier<Item> drop, int quantity) {
        return new ItemDropBehaviour() {
            @Override
            public int getQuantityDropped(Random random) {
                return quantity;
            }

            @Override
            public Optional<Supplier<Item>> getDrop() {
                return Optional.of(drop);
            }
        };
    }

    /**
     * Creates a drop behaviour that drops based on given item quantity range.
     *
     * @param minimum The minimum quantity of items to drop
     * @param maximum The maximum quantity of items to drop
     * @return The drop behaviour
     */
    static ItemDropBehaviour of(int minimum, int maximum) {
        return (random) -> MathHelper.getInt(random, minimum, maximum);
    }

    /**
     * Creates a drop behaviour that drops based on given item quantity range.
     *
     * @param drop The item to be dropped
     * @param minimum The minimum quantity of items to drop
     * @param maximum The maximum quantity of items to drop
     * @return The drop behaviour
     */
    static ItemDropBehaviour of(Supplier<Item> drop, int minimum, int maximum) {
        return new ItemDropBehaviour() {
            @Override
            public int getQuantityDropped(Random random) {
                return MathHelper.getInt(random, minimum, maximum);
            }

            @Override
            public Optional<Supplier<Item>> getDrop() {
                return Optional.of(drop);
            }
        };
    }

    /**
     * Gets the quantity of items to be dropped.
     *
     * @param random The random
     * @return The quantity dropped
     */
    int getQuantityDropped(Random random);

    /**
     * Gets the quantity of items to be dropped, with bonus applied.
     *
     * @param fortune The fortune
     * @param random The random
     * @return The quantity dropped
     */
    default int getQuantityDroppedWithBonus(int fortune, Random random) {
        return this.getQuantityDropped(random);
    }

    /**
     * Gets the {@link Item} that will be dropped.
     * If this is {@code Optional.empty()}, the block will be dropped.
     *
     * @return The item to be dropped
     */
    default Optional<Supplier<Item>> getDrop() {
        return Optional.empty();
    }

    /**
     * Gets the metadata.
     *
     * @param block The concrete block
     * @param blockState The block state
     * @return The meta
     */
    default int getMeta(ConcreteBlock block, IBlockState blockState) {
        return 0;
    }

    /**
     * A {@link ItemDropBehaviour} to be used to apply fortune. This allows for ore-like functionality.
     */
    interface Fortune extends ItemDropBehaviour {

        /**
         * A drop behaviour for dropping no items.
         */
        Fortune DROP_NONE = of(0);

        /**
         * The default drop behaviour.
         */
        Fortune DEFAULT = of(1);

        /**
         * Creates a drop behaviour that drops the given item quantity
         *
         * @param quantity The quantity of items to drop
         * @return The drop behaviour
         */
        static Fortune of(int quantity) {
            return (random) -> quantity;
        }

        /**
         * Creates a drop behaviour that drops based on given item quantity range.
         *
         * @param minimum The minimum quantity of items to drop
         * @param maximum The maximum quantity of items to drop
         * @return The drop behaviour
         */
        static Fortune of(int minimum, int maximum) {
            return (random) -> MathHelper.getInt(random, minimum, maximum);
        }

        // based on code from BlockOre#quantityDroppedWithBonus(int, Random)
        @Override
        default int getQuantityDroppedWithBonus(int fortune, Random random) {
            if (fortune > 0) {
                int i = random.nextInt(fortune + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                return this.getQuantityDropped(random) * (i + 1);
            } else {
                return this.getQuantityDropped(random);
            }
        }

    }

}
