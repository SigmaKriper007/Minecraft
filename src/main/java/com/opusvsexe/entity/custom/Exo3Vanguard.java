package com.opusvsexe.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Exo3Vanguard extends ExosuitEntity {

    public Exo3Vanguard(EntityType<? extends ExosuitEntity> entityType, Level level) {
        super(entityType, level);
        this.maxEnergy = 900;
        this.exoTier = "EXO-3";
        this.setHealth(140.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes()
            .add(Attributes.MAX_HEALTH, 140.0);
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(200)) {
            consumeEnergy(200);
            // Thrusters (brief flight/hover) + energy shield
            var direction = getViewVector(1.0f);
            setDeltaMovement(direction.x * 1.5, 1.0, direction.z * 1.5);
            // Shield activation logic would go here
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            target.hurt(damageSources().mobAttack(this), 15.0f);
        }
    }

}
