package com.opusvsexe.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Exo4Titan extends ExosuitEntity {

    public Exo4Titan(EntityType<? extends ExosuitEntity> entityType, Level level) {
        super(entityType, level);
        this.maxEnergy = 1200;
        this.exoTier = "EXO-4";
        this.setHealth(220.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes()
            .add(Attributes.MAX_HEALTH, 220.0);
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(250)) {
            consumeEnergy(250);
            // Jump shockwave - breaks blocks, stuns mobs
            var entities = level().getEntities(this, getBoundingBox().inflate(5.0), entity -> entity != this);
            for (var entity : entities) {
                if (entity instanceof LivingEntity living) {
                    living.knockback(3.0,
                        Math.sin(getYRot() * Math.PI / 180.0),
                        -Math.cos(getYRot() * Math.PI / 180.0));
                    living.hurt(damageSources().mobAttack(this), 12.0f);
                }
            }
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            target.hurt(damageSources().mobAttack(this), 18.0f);
        }
    }

}
