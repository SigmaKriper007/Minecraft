package com.opus.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class HeavyLaserBeamEntity extends BlasterBeamEntity {
    public HeavyLaserBeamEntity(EntityType<? extends BlasterBeamEntity> entityType, Level level) {
        super(entityType, level);
    }
}