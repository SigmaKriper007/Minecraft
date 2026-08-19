package com.opusvsexe.entity.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Exo5Vengeance extends ExosuitEntity {
    private boolean overloadActive;
    private int overloadTimer;

    public Exo5Vengeance(EntityType<? extends ExosuitEntity> entityType, Level level) {
        super(entityType, level);
        this.maxEnergy = 2000;
        this.exoTier = "EXO-5";
        this.setHealth(350.0f);
        this.overloadActive = false;
        this.overloadTimer = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes()
            .add(Attributes.MAX_HEALTH, 350.0);
    }

    @Override
    public void performAbility() {
        if (hasEnoughEnergy(400) && !overloadActive) {
            consumeEnergy(400);
            // Opus core overload - massive damage/speed buff with cooldown vulnerability
            overloadActive = true;
            overloadTimer = 200; // 10 seconds at 20 ticks/second

            // Apply speed and strength effects to rider
            if (getFirstPassenger() instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
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
    public float getAttackDamage() {
        return this.overloadActive ? 30.0f : 20.0f;
    }

    @Override
    public void performAttack() {
        var target = getTarget();
        if (target != null && target.isAlive()) {
            target.hurt(damageSources().mobAttack(this), overloadActive ? 30.0f : 20.0f);
        }
    }


    public boolean isOverloadActive() {
        return overloadActive;
    }
}
