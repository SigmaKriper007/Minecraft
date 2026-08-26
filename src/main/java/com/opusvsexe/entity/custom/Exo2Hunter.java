package com.opusvsexe.entity.custom;

import com.opus.entity.HeavyLaserBeamEntity;
import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * EXO-2: fast scout frame. A 13-block destructive dash along the aim, a heavy
 * cutting laser (same beam entity as the Heavy Laser Gun) and a short cloak.
 */
public class Exo2Hunter extends ExosuitEntity {

    private static final ExoAbility DASH = new ExoAbility("dash", 80, 60);
    private static final ExoAbility CUTTING_LASER = new ExoAbility("cutting_laser", 120, 60);
    private static final ExoAbility CLOAK = new ExoAbility("cloak", 100, 300);

    private static final double DASH_DISTANCE = 13.0D;

    public Exo2Hunter(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_2);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_2);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> DASH;
            case 1 -> CUTTING_LASER;
            case 2 -> CLOAK;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected String abilityAnimName(int slot) {
        return switch (slot) {
            case 1 -> "chest_shot";
            default -> null;
        };
    }

    @Override
    protected int abilityAnimDuration(int slot) {
        return switch (slot) {
            case 1 -> 16;
            case 2 -> 20;
            default -> 20;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> this.performDash(pilot, DASH_DISTANCE, 1.5F);
            case 1 -> this.spawnDirectionalBeam(
                    new HeavyLaserBeamEntity(ModEntities.HEAVY_BLASTER_BEAM, this.level()),
                    pilot, ModSounds.EXO_LASER, 1.0F, 1.2F);
            case 2 -> {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 160, 0, false, false));
                if (pilot != null) {
                    pilot.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 1, false, true));
                }
                this.playSound(ModSounds.EXO_THRUST, 0.6F, 2.0F);
            }
            default -> { }
        }
    }
}
