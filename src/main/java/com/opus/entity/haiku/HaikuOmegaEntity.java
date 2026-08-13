package com.opus.entity.haiku;

import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.BossMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Haiku-Ω "Omega" - финальный босс
 * 20-30 блоков высотой, полностью осознавший себя ИИ
 * Уязвим только к оружию из Opus (тег opus_weapon) и урону от EXO
 */
public class HaikuOmegaEntity extends BossMob {
    
    private int currentPhase = 1;
    
    public HaikuOmegaEntity(EntityType<? extends BossMob> entityType, Level level) {
        super(entityType, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.3));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 500.0)
            .add(Attributes.MOVEMENT_SPEED, 0.12)
            .add(Attributes.ATTACK_DAMAGE, 20.0)
            .add(Attributes.FOLLOW_RANGE, 50.0)
            .add(Attributes.ARMOR, 20.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }
    
    @Override
    protected float getStandingEyeHeight() {
        return 22.0f;
    }
    
    @Override
    public BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }
    
    /**
     * Проверка фазы боя на основе здоровья
     * Phase 1: 100-50% HP
     * Phase 2: 50-25% HP (открывается слабое место)
     * Phase 3: 25-0% HP (режим ярости)
     */
    public void updatePhase() {
        float healthPercent = getHealth() / getMaxHealth();
        if (healthPercent <= 0.25f) {
            currentPhase = 3;
        } else if (healthPercent <= 0.5f) {
            currentPhase = 2;
        } else {
            currentPhase = 1;
        }
    }
    
    public int getCurrentPhase() {
        return currentPhase;
    }
}
