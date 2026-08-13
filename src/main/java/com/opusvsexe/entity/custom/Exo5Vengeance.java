package com.opusvsexe.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Exo5Vengeance extends ExosuitEntity {
    private boolean overloadActive;
    private int overloadTimer;
    
    public Exo5Vengeance(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.maxEnergy = 2000;
        this.exoTier = "EXO-5";
        this.health = 350.0f;
        this.overloadActive = false;
        this.overloadTimer = 0;
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(400) && !overloadActive) {
            consumeEnergy(400);
            // Opus core overload - massive damage/speed buff with cooldown vulnerability
            overloadActive = true;
            overloadTimer = 200; // 10 seconds at 20 ticks/second
            
            // Apply speed and strength effects to rider
            if (getFirstPassenger() instanceof PlayerEntity player) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (overloadActive) {
            overloadTimer--;
            if (overloadTimer <= 0) {
                overloadActive = false;
                // Vulnerability window - could add weakness effect here
            }
        }
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            float damage = overloadActive ? 30.0f : 20.0f;
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

    public boolean isOverloadActive() {
        return overloadActive;
    }
}
