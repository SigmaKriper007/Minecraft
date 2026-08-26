package com.opusvsexe.entity.custom;

import com.opus.item.CombatEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * EXO-4: siege frame. Nothing about it is subtle. Seismic stomp, the shared
 * green Energy Shield and a 13-block destructive dash along the aim.
 */
public class Exo4Titan extends ExosuitEntity {

    private static final ExoAbility SEISMIC_STOMP = new ExoAbility("seismic_stomp", 200, 100);
    private static final ExoAbility ENERGY_SHIELD = new ExoAbility("energy_shield", 120, 200);
    private static final ExoAbility DASH = new ExoAbility("dash", 120, 80);

    private static final double DASH_DISTANCE = 13.0D;

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
            case 1 -> ENERGY_SHIELD;
            case 2 -> DASH;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected String abilityAnimName(int slot) {
        return switch (slot) {
            case 0 -> "slam";
            case 1 -> "energy_shield";
            default -> null;
        };
    }

    @Override
    protected int abilityAnimDuration(int slot) {
        return switch (slot) {
            case 0 -> 22;
            case 1 -> 30;
            default -> 20;
        };
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> CombatEffects.shockwave(this, 6.0D, this.getAttackDamage() * 0.9F, 3.0D, true);
            case 1 -> this.activateEnergyShield(pilot);
            case 2 -> this.performDash(pilot, DASH_DISTANCE, 1.5F);
            default -> { }
        }
    }
}
