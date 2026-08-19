package com.opusvsexe.entity.custom;

import com.opus.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** EXO-2: fast scout frame. Dash, cutting beam, short cloak. */
public class Exo2Hunter extends ExosuitEntity {

    private static final ExoAbility DASH = new ExoAbility("dash", 60, 40);
    private static final ExoAbility MINING_LASER = new ExoAbility("mining_laser", 120, 60);
    private static final ExoAbility CLOAK = new ExoAbility("cloak", 100, 300);

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
            case 1 -> MINING_LASER;
            case 2 -> CLOAK;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> {
                Vec3 look = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
                this.applyImpulse(new Vec3(look.x * 1.9D, Math.max(0.15D, look.y * 0.4D), look.z * 1.9D));
                this.playSound(ModSounds.EXO_THRUST, 1.0F, 1.5F);
            }
            case 1 -> this.beamAttack(pilot, 18.0D, this.getAttackDamage() * 0.9F);
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
