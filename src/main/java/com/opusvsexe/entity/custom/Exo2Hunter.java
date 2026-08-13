package com.opusvsexe.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Exo2Hunter extends ExosuitEntity {
    
    public Exo2Hunter(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.maxEnergy = 700;
        this.exoTier = "EXO-2";
        this.health = 90.0f;
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(150)) {
            consumeEnergy(150);
            // Dash + mining laser
            var direction = getRotationVector();
            setVelocity(direction.x * 2.0, 0, direction.z * 2.0);
            // Mining laser logic would go here
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            float damage = 12.0f;
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
