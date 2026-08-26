package com.opusvsexe.entity.custom;

import com.opus.item.CombatEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/** EXO-1: Kodi's first war frame. Slow, blunt, reliable. */
public class Exo1Sentinel extends ExosuitEntity {

    private static final ExoAbility FIST_SLAM = new ExoAbility("fist_slam", 100, 60);
    private static final ExoAbility ENERGY_SHIELD = new ExoAbility("energy_shield", 120, 200);

    public Exo1Sentinel(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_1);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> FIST_SLAM;
            case 1 -> ENERGY_SHIELD;
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
            case 0 -> CombatEffects.shockwave(this, 3.5D, this.getAttackDamage() * 0.8F, 1.8D, true);
            case 1 -> this.activateEnergyShield(pilot);
            default -> { }
        }
    }
}
