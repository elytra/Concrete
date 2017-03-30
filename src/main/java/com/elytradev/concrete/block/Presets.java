package com.elytradev.concrete.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.function.Consumer;

/**
 * A psuedo-enum of presets for the Concrete block builder system.
 */
public final class Presets {

    /**
     * A preset for a block builder with ore properties, such as the fortune drop behaviour.
     *
     * @see ConcreteBlock.Builder#preset(Consumer)
     */
    public static Consumer<ConcreteBlock.Builder> ORE = builder -> builder
            .drop(ItemDropBehaviour.Fortune.DEFAULT);

    /**
     * A preset for a block builder with glass properties, such as the hardness, and resistance.
     *
     * @see ConcreteBlock.Builder#preset(Consumer)
     */
    public static Consumer<ConcreteBlock.Builder> GLASS = builder -> builder
            .material(Material.GLASS)
            .soundType(SoundType.GLASS)
            .drop(ItemDropBehaviour.DROP_NONE)
            .hardness(0.3f)
            .resistance(1.0f)
            .translucent()
            .silkHarvest();

    private Presets() {
    }

}
