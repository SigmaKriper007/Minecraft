package com.opus.darkforest.entity;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.registry.DarkForestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class ShadeSpiderlingEntity extends Spider implements GeoEntity {
    private static final EntityDataAccessor<Integer> BITE_TICKS = SynchedEntityData.defineId(ShadeSpiderlingEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SCUTTLE = RawAnimation.begin().thenLoop("scuttle");
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("bite");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ShadeSpiderlingEntity(EntityType<? extends Spider> type, Level level) { super(type, level); xpReward = 4; }

    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(BITE_TICKS, 0); }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8).add(Attributes.ATTACK_DAMAGE, 3)
            .add(Attributes.MOVEMENT_SPEED, .42).add(Attributes.FOLLOW_RANGE, 20);
    }

    public static boolean canSpawn(EntityType<ShadeSpiderlingEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return level.getBiome(pos).is(DarkForestLine.DARK_FOREST) && nativeFloor(level, pos.below())
            && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    static boolean nativeFloor(ServerLevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos).is(DarkForestBlocks.MOONLIT_GRASS)
            || level.getBlockState(pos).is(DarkForestBlocks.MOONLIT_SOIL);
    }

    @Override public boolean doHurtTarget(net.minecraft.world.entity.Entity target) { entityData.set(BITE_TICKS, 10); return super.doHurtTarget(target); }
    @Override public void tick() { super.tick(); if (!level().isClientSide && entityData.get(BITE_TICKS) > 0) entityData.set(BITE_TICKS, entityData.get(BITE_TICKS) - 1); }
    @Override protected ResourceLocation getDefaultLootTable() { return DarkForestLine.id("entities/shade_spiderling"); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spiderling", 1, state -> {
            state.getController().setAnimation(entityData.get(BITE_TICKS) > 0 ? BITE : state.isMoving() ? SCUTTLE : IDLE);
            return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
