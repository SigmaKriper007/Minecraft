package com.opusvsexe.entity.custom;

import com.opus.item.CombatEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * EXO-3: thruster frame. A 13-block destructive dash along the aim, the shared
 * green Energy Shield and a ground shockwave.
 */
public class Exo3Vanguard extends ExosuitEntity {

    private static final ExoAbility DASH = new ExoAbility("dash", 80, 60);
    private static final ExoAbility ENERGY_SHIELD = new ExoAbility("energy_shield", 120, 200);
    private static final ExoAbility SHOCKWAVE = new ExoAbility("shockwave", 150, 120);

    private static final double DASH_DISTANCE = 13.0D;

    public Exo3Vanguard(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_3);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_3);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> DASH;
            case 1 -> ENERGY_SHIELD;
            case 2 -> SHOCKWAVE;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected String abilityAnimName(int slot) {
        return switch (slot) {
            case 1 -> "energy_shield";
            case 2 -> "slam";
            default -> null;
        };
    }

    @Override
    protected int abilityAnimDuration(int slot) {
        return switch (slot) {
            case 1 -> 30;
            case 2 -> 22;
            default -> 20;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> this.performDash(pilot, DASH_DISTANCE, 1.5F);
            case 1 -> this.activateEnergyShield(pilot);
            case 2 -> CombatEffects.shockwave(this, 5.0D, this.getAttackDamage() * 0.7F, 2.2D, true);
            default -> { }
        }
    }
}
