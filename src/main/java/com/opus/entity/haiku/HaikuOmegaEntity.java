package com.opus.entity.haiku;

import com.opus.OpusVsExe;
import com.opus.sound.ModSounds;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Haiku-Ω "Omega" - финальный босс
 * 20-30 блоков высотой, полностью осознавший себя ИИ
 * Уязвим только к оружию из Opus (тег opus_weapon) и урону от EXO
 * Агррится на игрока, но не атакует напрямую - только преследует,
 * пока играет его музыкальная тема
 */
public class HaikuOmegaEntity extends PathfinderMob {

    private int currentPhase = 1;
    private boolean musicStarted = false;
    private long musicRestartTick = 0;
    private static final int MUSIC_LENGTH_TICKS = 8270;

    public HaikuOmegaEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.3));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 500.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.FOLLOW_RANGE, 50.0)
            .add(Attributes.ARMOR, 20.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        boolean hasTarget = getTarget() != null && isAlive();
        if (hasTarget && !musicStarted) {
            musicStarted = true;
            musicRestartTick = level().getGameTime() + MUSIC_LENGTH_TICKS;
            startBossMusic();
        } else if (!hasTarget && musicStarted) {
            musicStarted = false;
            stopBossMusic();
        } else if (hasTarget && musicStarted && level().getGameTime() >= musicRestartTick) {
            musicRestartTick = level().getGameTime() + MUSIC_LENGTH_TICKS;
            startBossMusic();
        }
    }

    private void startBossMusic() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(this) <= 16384.0) {
                player.playNotifySound(ModSounds.DOOM_ETERNAL, SoundSource.RECORDS, 1.0f, 1.0f);
            }
        }
    }

    private void stopBossMusic() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (ServerPlayer player : serverLevel.players()) {
            player.connection.send(new ClientboundStopSoundPacket(OpusVsExe.id("doom_eternal"), SoundSource.RECORDS));
        }
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) {
        stopBossMusic();
        musicStarted = false;
        musicRestartTick = 0;
        super.die(source);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 22.0f;
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