package com.opusvsexe.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Exo3Vanguard extends ExosuitEntity {
    
    public Exo3Vanguard(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.maxEnergy = 900;
        this.exoTier = "EXO-3";
        this.health = 140.0f;
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(200)) {
            consumeEnergy(200);
            // Thrusters (brief flight/hover) + energy shield
            setVelocity(getRotationVector().x * 1.5, 1.0, getRotationVector().z * 1.5);
            // Shield activation logic would go here
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            float damage = 15.0f;
            target.damage(getDamageSources().mobAttack(this), damage);
        }
    }

    @Override
    protected ActionResult interact(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient) {
            player.startRiding(this);
        }
        return ActionResult.SUCCESS;
    }
}
