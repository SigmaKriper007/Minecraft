package com.opusvsexe.entity.custom;

import com.opus.item.CombatEffects;
import com.opus.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** EXO-3: thruster frame with a real energy shield. */
public class Exo3Vanguard extends ExosuitEntity {

    private static final ExoAbility THRUSTERS = new ExoAbility("thrusters", 80, 60);
    private static final ExoAbility ENERGY_SHIELD = new ExoAbility("energy_shield", 120, 200);
    private static final ExoAbility SHOCKWAVE = new ExoAbility("shockwave", 150, 120);

    public Exo3Vanguard(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_3);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_3);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> THRUSTERS;
            case 1 -> ENERGY_SHIELD;
            case 2 -> SHOCKWAVE;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> {
                Vec3 look = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
                this.applyImpulse(new Vec3(look.x * 1.3D, 1.05D, look.z * 1.3D));
                this.playSound(ModSounds.EXO_THRUST, 1.4F, 1.1F);
            }
            case 1 -> {
                this.activateShield(200);
                this.playSound(ModSounds.SHOCKWAVE, 0.7F, 1.9F);
            }
            case 2 -> CombatEffects.shockwave(this, 5.0D, this.getAttackDamage() * 0.7F, 2.2D, true);
            default -> { }
        }
    }
}
