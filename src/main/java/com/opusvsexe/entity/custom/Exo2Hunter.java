package com.opusvsexe.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Exo2Hunter extends ExosuitEntity {

    public Exo2Hunter(EntityType<? extends ExosuitEntity> entityType, Level level) {
        super(entityType, level);
        this.maxEnergy = 700;
        this.exoTier = "EXO-2";
        this.setHealth(90.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes()
            .add(Attributes.MAX_HEALTH, 90.0);
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(150)) {
            consumeEnergy(150);
            // Dash + mining laser
            var direction = getViewVector(1.0f);
            setDeltaMovement(direction.x * 2.0, 0, direction.z * 2.0);
            // Mining laser logic would go here
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            target.hurt(damageSources().mobAttack(this), 12.0f);
        }
    }

}
