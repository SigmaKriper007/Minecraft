package com.opus.fire.registry;

import com.opus.fire.FireLine;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Кастомные частицы Fire Biom: ember (тлеющий уголёк, вверх) и ash (пепел, падает).
 */
public final class FireParticles {

    public static final SimpleParticleType EMBER = Registry.register(
        BuiltInRegistries.PARTICLE_TYPE, FireLine.id("ember"),
        new SimpleParticleType(false) {});

    public static final SimpleParticleType ASH = Registry.register(
        BuiltInRegistries.PARTICLE_TYPE, FireLine.id("ash"),
        new SimpleParticleType(false) {});

    private FireParticles() {}

    public static void init() {}
}
