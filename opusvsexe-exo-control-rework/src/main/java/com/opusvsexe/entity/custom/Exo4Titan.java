package com.opusvsexe.entity.custom;

import com.opus.item.CombatEffects;
import com.opus.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** EXO-4: siege frame. Nothing about it is subtle. */
public class Exo4Titan extends ExosuitEntity {

    private static final ExoAbility SEISMIC_STOMP = new ExoAbility("seismic_stomp", 200, 100);
    private static final ExoAbility FORTIFY = new ExoAbility("fortify", 120, 240);
    private static final ExoAbility LAUNCH = new ExoAbility("launch", 150, 120);

    public Exo4Titan(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_4);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_4);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> SEISMIC_STOMP;
            case 1 -> FORTIFY;
            case 2 -> LAUNCH;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> CombatEffects.shockwave(this, 6.0D, this.getAttackDamage() * 0.9F, 3.0D, true);
            case 1 -> {
                this.activateShield(240);
                this.playSound(ModSounds.SHOCKWAVE, 0.8F, 1.5F);
            }
            case 2 -> {
                Vec3 motion = this.getDeltaMovement();
                this.applyImpulse(new Vec3(motion.x * 0.5D, 1.15D, motion.z * 0.5D));
                this.playSound(ModSounds.EXO_THRUST, 1.5F, 0.8F);
            }
            default -> { }
        }
    }
}
