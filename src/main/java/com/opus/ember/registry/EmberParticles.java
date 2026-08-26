package com.opus.ember.registry;

import com.opus.ember.EmberLine;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Кастомные частицы Ember line: ember_spark (раскалённая искра, вверх) и
 * ember_ash (фиолетовый пепел, падает).
 */
public final class EmberParticles {

    public static final SimpleParticleType EMBER_SPARK = Registry.register(
        BuiltInRegistries.PARTICLE_TYPE, EmberLine.id("ember_spark"),
        new SimpleParticleType(false) {});

    public static final SimpleParticleType EMBER_ASH = Registry.register(
        BuiltInRegistries.PARTICLE_TYPE, EmberLine.id("ember_ash"),
        new SimpleParticleType(false) {});

    private EmberParticles() {}

    public static void init() {}
}
