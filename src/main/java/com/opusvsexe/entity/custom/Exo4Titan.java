package com.opusvsexe.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Exo4Titan extends ExosuitEntity {
    
    public Exo4Titan(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.maxEnergy = 1200;
        this.exoTier = "EXO-4";
        this.health = 220.0f;
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(250)) {
            consumeEnergy(250);
            // Jump shockwave - breaks blocks, stuns mobs
            var entities = getWorld().getOtherEntities(this, getBoundingBox().expand(5.0));
            for (var entity : entities) {
                if (entity instanceof LivingEntity living) {
                    living.takeKnockback(3.0, 
                        Math.sin(getYaw() * Math.PI / 180.0), 
                        -Math.cos(getYaw() * Math.PI / 180.0));
                    living.damage(getDamageSources().mobAttack(this), 12.0f);
                }
            }
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            float damage = 18.0f;
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
