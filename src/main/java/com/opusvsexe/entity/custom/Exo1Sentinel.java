package com.opusvsexe.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Exo1Sentinel extends ExosuitEntity {

    public Exo1Sentinel(EntityType<? extends ExosuitEntity> entityType, Level level) {
        super(entityType, level);
        this.maxEnergy = 500;
        this.exoTier = "EXO-1";
        this.setHealth(60.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes()
            .add(Attributes.MAX_HEALTH, 60.0);
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(100)) {
            consumeEnergy(100);
            // Heavy fist slam - AoE + knockback
            var entities = level().getEntities(this, getBoundingBox().inflate(3.0), entity -> entity != this);
            for (var entity : entities) {
                if (entity instanceof LivingEntity living) {
                    living.knockback(2.0,
                        Math.sin(getYRot() * Math.PI / 180.0),
                        -Math.cos(getYRot() * Math.PI / 180.0));
                    living.hurt(damageSources().mobAttack(this), 8.0f);
                }
            }
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            target.hurt(damageSources().mobAttack(this), 10.0f);
        }
    }

}
