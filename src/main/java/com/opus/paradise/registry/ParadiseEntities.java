package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.CloudGrazerEntity;
import com.opus.paradise.entity.HurricaneEntity;
import com.opus.paradise.entity.ParadiseWyvernEntity;
import com.opus.paradise.entity.SunfinchEntity;
import com.opus.paradise.entity.WindCoreEntity;
import com.opus.paradise.entity.AngelBoyEntity;
import com.opus.paradise.entity.AngelAttackEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ParadiseEntities {
    public static final EntityType<SunfinchEntity> SUNFINCH = register("sunfinch",
        EntityType.Builder.of(SunfinchEntity::new, MobCategory.CREATURE).sized(0.55F, 0.45F).clientTrackingRange(10));
    public static final EntityType<CloudGrazerEntity> CLOUD_GRAZER = register("cloud_grazer",
        EntityType.Builder.of(CloudGrazerEntity::new, MobCategory.CREATURE).sized(1.35F, 1.40F).clientTrackingRange(10));
    public static final EntityType<ParadiseWyvernEntity> PARADISE_WYVERN = register("paradise_wyvern",
        EntityType.Builder.of(ParadiseWyvernEntity::new, MobCategory.CREATURE).sized(1.85F, 1.45F).clientTrackingRange(12));
    public static final EntityType<WindCoreEntity> WIND_CORE = register("wind_core",
        EntityType.Builder.<WindCoreEntity>of(WindCoreEntity::new, MobCategory.MISC).sized(0.45F, 0.45F)
            .clientTrackingRange(12).updateInterval(1));
    public static final EntityType<HurricaneEntity> HURRICANE = register("hurricane",
        EntityType.Builder.<HurricaneEntity>of(HurricaneEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
            .clientTrackingRange(16).updateInterval(2));
    public static final EntityType<AngelBoyEntity> ANGEL_BOY = register("angel_boy",
        EntityType.Builder.of(AngelBoyEntity::new, MobCategory.MONSTER).sized(0.9F,2.45F).clientTrackingRange(14));
    public static final EntityType<AngelAttackEntity> HALO_LANCE = attack("halo_lance",.8F,1F);
    public static final EntityType<AngelAttackEntity> SERAPHIC_CROSSWIND = attack("seraphic_crosswind",1F,4F);
    public static final EntityType<AngelAttackEntity> SERAPHIC_FEATHER = attack("seraphic_feather",.35F,.35F);
    public static final EntityType<AngelAttackEntity> WINGBEAT_RING = attack("wingbeat_ring",.5F,.25F);
    public static final EntityType<AngelAttackEntity> ANGEL_ASCENSION = attack("angel_ascension",.5F,.5F);
    public static final EntityType<AngelAttackEntity> RUBY_DESCENT = attack("ruby_descent",.5F,.25F);

    private ParadiseEntities() { }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, ParadiseLine.id(id), builder.build(id));
    }

    private static EntityType<AngelAttackEntity> attack(String id,float width,float height) {
        return register(id,EntityType.Builder.<AngelAttackEntity>of(AngelAttackEntity::new,MobCategory.MISC)
            .sized(width,height).clientTrackingRange(16).updateInterval(1));
    }

    public static void init() { }
}
