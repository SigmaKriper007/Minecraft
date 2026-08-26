package com.opusvsexe.entity.custom;

import com.opus.entity.ExtraLaserBeamEntity;
import com.opus.entity.HeavyLaserBeamEntity;
import com.opus.entity.SkyLaserEntity;
import com.opus.item.CombatEffects;
import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * EXO-5 "Vengeance" (formerly EXO-6 "Exo+"): Kodi's unfinished last project,
 * rebuilt from the wreckage of the other frames. Dark crimson and black armor
 * with gold trim and a gold core — a hybrid of every EXO line and the Haiku
 * machines it was made to kill. Per the lore it is the final war frame.
 *
 * Abilities (4 slots):
 *  0 Extra Laser   — heavy blaster beam scaled 2.5x; blocks in its path explode
 *                    like TNT.
 *  1 Photon Lance  — fires the heavy_blaster_beam from the chest, arms spread.
 *  2 Seismic Strike— ground slam shockwave.
 *  3 Cataclysm     — calls the sky_laser down at the aim point.
 */
public class Exo5Vengeance extends ExosuitEntity {

    private static final ExoAbility EXTRA_LASER = new ExoAbility("extra_laser", 150, 200);
    private static final ExoAbility PHOTON_LANCE = new ExoAbility("photon_lance", 200, 80);
    private static final ExoAbility SEISMIC_STRIKE = new ExoAbility("seismic_strike", 250, 120);
    private static final ExoAbility CATACLYSM = new ExoAbility("cataclysm_beam", 500, 400);

    private static final double SKY_LASER_RANGE = 128.0D;

    public Exo5Vengeance(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_5);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_5);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> EXTRA_LASER;
            case 1 -> PHOTON_LANCE;
            case 2 -> SEISMIC_STRIKE;
            case 3 -> CATACLYSM;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected String abilityAnimName(int slot) {
        return switch (slot) {
            case 0 -> "ability_extra";
            case 1 -> "ability_laser";
            case 2 -> "ability_slam";
            case 3 -> "ability_ultra";
            default -> null;
        };
    }

    @Override
    protected int abilityAnimDuration(int slot) {
        return switch (slot) {
            case 0 -> 14;
            case 1 -> 12;
            case 2 -> 20;
            case 3 -> 40;
            default -> 20;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> this.spawnDirectionalBeam(
                    new ExtraLaserBeamEntity(ModEntities.EXTRA_LASER_BEAM, this.level()),
                    pilot, ModSounds.EXO_EXTRA, 1.0F, 0.8F);
            case 1 -> this.spawnDirectionalBeam(
                    new HeavyLaserBeamEntity(ModEntities.HEAVY_BLASTER_BEAM, this.level()),
                    pilot, ModSounds.EXO_LASER, 1.0F, 1.2F);
            case 2 -> CombatEffects.shockwave(this, 6.5D, this.getAttackDamage() * 0.9F, 3.2D, true);
            case 3 -> this.fireCataclysm(pilot);
            default -> { }
        }
    }

    /** Slot 3 — Cataclysm: the sky_laser comes down wherever the pilot aims. */
    private void fireCataclysm(ServerPlayer pilot) {
        Level level = this.level();
        Vec3 eye = this.getEyePosition(1.0F);
        Vec3 look = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
        BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look.scale(SKY_LASER_RANGE)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 pos;
        if (hit.getType() == HitResult.Type.MISS) {
            pos = new Vec3(Math.floor(hit.getLocation().x) + 0.5D,
                    Math.floor(hit.getLocation().y) + 0.5D,
                    Math.floor(hit.getLocation().z) + 0.5D);
        } else {
            pos = new Vec3(Math.floor(hit.getLocation().x) + 0.5D,
                    hit.getLocation().y,
                    Math.floor(hit.getLocation().z) + 0.5D);
        }

        SkyLaserEntity laser = new SkyLaserEntity(ModEntities.SKY_LASER, level);
        laser.setPos(pos);
        laser.setShooter(pilot != null ? pilot.getUUID() : this.getUUID());
        level.addFreshEntity(laser);

        level.playSound(null, pos.x, pos.y, pos.z, ModSounds.EXO_ULTRA, SoundSource.PLAYERS, 1.0F, 0.8F);
        level.playSound(null, pos.x, pos.y, pos.z, ModSounds.SUPER_LASER, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
