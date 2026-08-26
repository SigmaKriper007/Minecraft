package com.opus.darkforest.registry;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.GloomBroodmotherEntity;
import com.opus.darkforest.entity.GloomWebEntity;
import com.opus.darkforest.entity.MoonwingBatEntity;
import com.opus.darkforest.entity.MoonwingPulseEntity;
import com.opus.darkforest.entity.MossboundAttackEntity;
import com.opus.darkforest.entity.MossboundEndermanEntity;
import com.opus.darkforest.entity.ShadeSpiderlingEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class DarkForestEntities {
    public static final EntityType<ShadeSpiderlingEntity> SHADE_SPIDERLING = register("shade_spiderling",
        EntityType.Builder.of(ShadeSpiderlingEntity::new, MobCategory.MONSTER).sized(.68F, .38F).clientTrackingRange(10));
    public static final EntityType<GloomBroodmotherEntity> GLOOM_BROODMOTHER = register("gloom_broodmother",
        EntityType.Builder.of(GloomBroodmotherEntity::new, MobCategory.MONSTER).sized(2.20F, 1.55F).clientTrackingRange(12));
    public static final EntityType<MoonwingBatEntity> MOONWING_BAT = register("moonwing_bat",
        EntityType.Builder.of(MoonwingBatEntity::new, MobCategory.MONSTER).sized(1.60F, .85F).clientTrackingRange(12).updateInterval(2));
    public static final EntityType<GloomWebEntity> GLOOM_WEB = register("gloom_web",
        EntityType.Builder.<GloomWebEntity>of(GloomWebEntity::new, MobCategory.MISC).sized(.25F, .15F).clientTrackingRange(12).updateInterval(2));
    public static final EntityType<MoonwingPulseEntity> MOONWING_PULSE = register("moonwing_pulse",
        EntityType.Builder.<MoonwingPulseEntity>of(MoonwingPulseEntity::new, MobCategory.MISC).sized(.2F, .2F).clientTrackingRange(14).updateInterval(1));
    public static final EntityType<MossboundEndermanEntity> MOSSBOUND_ENDERMAN = register("mossbound_enderman",
        EntityType.Builder.of(MossboundEndermanEntity::new, MobCategory.MONSTER).sized(2.35F,5.25F).clientTrackingRange(16).updateInterval(2).fireImmune());
    public static final EntityType<MossboundAttackEntity> ROOT_SNARE = effect("root_snare",2.5F,.2F);
    public static final EntityType<MossboundAttackEntity> MARKED_STEP = effect("marked_step",2.4F,.2F);
    public static final EntityType<MossboundAttackEntity> MOONWELL_ORB = effect("moonwell_orb",.5F,.5F);
    public static final EntityType<MossboundAttackEntity> BLOOMFALL = effect("bloomfall",2.3F,.2F);
    public static final EntityType<MossboundAttackEntity> ECHO_DOUBLE = effect("echo_double",1.2F,5.1F);
    public static final EntityType<MossboundAttackEntity> ECLIPSE_RUSH = effect("eclipse_rush",1.8F,2.5F);

    private DarkForestEntities() { }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, DarkForestLine.id(id), builder.build(id));
    }
    private static EntityType<MossboundAttackEntity> effect(String id,float width,float height){return register(id,EntityType.Builder.<MossboundAttackEntity>of(MossboundAttackEntity::new,MobCategory.MISC).sized(width,height).clientTrackingRange(16).updateInterval(1));}

    public static void init() { }
}
