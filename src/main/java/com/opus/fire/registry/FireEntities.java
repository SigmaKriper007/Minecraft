package com.opus.fire.registry;

import com.opus.fire.FireLine;
import com.opus.fire.entity.FireDemonEntity;
import com.opus.fire.entity.FireSlimeEntity;
import com.opus.fire.entity.LavaGolemEntity;
import com.opus.fire.entity.projectile.DemonicTridentEntity;
import com.opus.fire.entity.projectile.FireAuraWaveEntity;
import com.opus.fire.entity.projectile.FireballProjectile;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class FireEntities {
    public static final EntityType<FireSlimeEntity> FIRE_SLIME = register("fire_slime",
        EntityType.Builder.of(FireSlimeEntity::new, MobCategory.MONSTER).sized(0.70f, 0.65f).clientTrackingRange(10));
    public static final EntityType<LavaGolemEntity> LAVA_GOLEM = register("lava_golem",
        EntityType.Builder.of(LavaGolemEntity::new, MobCategory.MONSTER).sized(1.35f, 2.75f).clientTrackingRange(12));
    public static final EntityType<FireDemonEntity> FIRE_DEMON = register("fire_demon",
        EntityType.Builder.of(FireDemonEntity::new, MobCategory.MONSTER).sized(1.30f, 2.75f).clientTrackingRange(24));
    public static final EntityType<FireballProjectile> FIREBALL = register("fireball",
        EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC).sized(0.55f, 0.55f).clientTrackingRange(20));
    public static final EntityType<DemonicTridentEntity> DEMONIC_TRIDENT_ENTITY = register("demonic_trident",
        EntityType.Builder.<DemonicTridentEntity>of(DemonicTridentEntity::new, MobCategory.MISC).sized(0.35f, 0.35f).clientTrackingRange(20));
    public static final EntityType<FireAuraWaveEntity> FIRE_AURA_WAVE = register("fire_aura_wave",
        EntityType.Builder.<FireAuraWaveEntity>of(FireAuraWaveEntity::new, MobCategory.MISC).sized(1.0f, 0.35f).clientTrackingRange(32));

    private FireEntities() { }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, FireLine.id(id), builder.build(id));
    }

    public static void init() { }
}
