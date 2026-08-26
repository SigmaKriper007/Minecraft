package com.opus.ember.registry;

import com.opus.ember.EmberLine;
import com.opus.ember.entity.EmberSlimeEntity;
import com.opus.ember.entity.FlameDemonEntity;
import com.opus.ember.entity.ObsidianGolemEntity;
import com.opus.ember.entity.projectile.BlazingTridentEntity;
import com.opus.ember.entity.projectile.EmberAuraWaveEntity;
import com.opus.ember.entity.projectile.EmberFireballProjectile;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class EmberEntities {
    public static final EntityType<EmberSlimeEntity> EMBER_SLIME = register("ember_slime",
        EntityType.Builder.of(EmberSlimeEntity::new, MobCategory.MONSTER).sized(0.70f, 0.65f).clientTrackingRange(10));
    public static final EntityType<ObsidianGolemEntity> OBSIDIAN_GOLEM = register("obsidian_golem",
        EntityType.Builder.of(ObsidianGolemEntity::new, MobCategory.MONSTER).sized(1.35f, 2.75f).clientTrackingRange(12));
    public static final EntityType<FlameDemonEntity> FLAME_DEMON = register("flame_demon",
        EntityType.Builder.of(FlameDemonEntity::new, MobCategory.MONSTER).sized(1.30f, 3.60f).clientTrackingRange(24));
    public static final EntityType<EmberFireballProjectile> EMBER_FIREBALL = register("ember_fireball",
        EntityType.Builder.<EmberFireballProjectile>of(EmberFireballProjectile::new, MobCategory.MISC).sized(0.55f, 0.55f).clientTrackingRange(20));
    public static final EntityType<BlazingTridentEntity> BLAZING_TRIDENT = register("blazing_trident",
        EntityType.Builder.<BlazingTridentEntity>of(BlazingTridentEntity::new, MobCategory.MISC).sized(0.35f, 0.35f).clientTrackingRange(20));
    public static final EntityType<EmberAuraWaveEntity> EMBER_AURA_WAVE = register("ember_aura_wave",
        EntityType.Builder.<EmberAuraWaveEntity>of(EmberAuraWaveEntity::new, MobCategory.MISC).sized(1.0f, 0.35f).clientTrackingRange(32));

    private EmberEntities() { }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, EmberLine.id(id), builder.build(id));
    }

    public static void init() { }
}
