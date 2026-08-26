package com.opus.settlement.registry;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.entity.SurvivorEntity;
import com.opus.settlement.entity.BlackNinjaEntity;
import com.opus.settlement.entity.SamuraiEntity;
import com.opus.settlement.entity.YoungSamuraiEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class SettlementEntities {
    public static final EntityType<SurvivorEntity> SURVIVOR = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, SettlementLine.id("survivor"),
        EntityType.Builder.of(SurvivorEntity::new, MobCategory.CREATURE)
            .sized(.60F, 1.80F).clientTrackingRange(10).build("survivor"));
    public static final EntityType<BlackNinjaEntity> BLACK_NINJA = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, SettlementLine.id("black_ninja"),
        EntityType.Builder.of(BlackNinjaEntity::new, MobCategory.MONSTER)
            .sized(.60F, 1.80F).clientTrackingRange(10).build("black_ninja"));
    public static final EntityType<SamuraiEntity> SAMURAI = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, SettlementLine.id("samurai"),
        EntityType.Builder.of(SamuraiEntity::new, MobCategory.MONSTER)
            .sized(.78F, 2.34F).clientTrackingRange(12).build("samurai"));
    public static final EntityType<YoungSamuraiEntity> YOUNG_SAMURAI = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, SettlementLine.id("young_samurai"),
        EntityType.Builder.of(YoungSamuraiEntity::new, MobCategory.MONSTER)
            .sized(.66F, 1.98F).clientTrackingRange(14).build("young_samurai"));

    private SettlementEntities() { }
    public static void init() { }
}
