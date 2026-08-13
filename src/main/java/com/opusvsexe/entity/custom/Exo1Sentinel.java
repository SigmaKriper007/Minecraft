package com.opusvsexe.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Exo1Sentinel extends ExosuitEntity {
    
    public Exo1Sentinel(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.maxEnergy = 500;
        this.exoTier = "EXO-1";
        this.health = 60.0f;
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(100)) {
            consumeEnergy(100);
            // Heavy fist slam - AoE + knockback
            var entities = getWorld().getOtherEntities(this, getBoundingBox().expand(3.0));
            for (var entity : entities) {
                if (entity instanceof LivingEntity living) {
                    living.takeKnockback(2.0, 
                        Math.sin(getYaw() * Math.PI / 180.0), 
                        -Math.cos(getYaw() * Math.PI / 180.0));
                    living.damage(getDamageSources().mobAttack(this), 8.0f);
                }
            }
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            float damage = 10.0f;
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
