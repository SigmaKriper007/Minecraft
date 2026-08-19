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

/**
 * EXO-5: Kodi's unfinished last project. Overload trades safety for raw damage
 * and always leaves a vulnerability window behind, exactly like the lore says.
 */
public class Exo5Vengeance extends ExosuitEntity {

    private static final ExoAbility OVERLOAD = new ExoAbility("opus_overload", 400, 400);
    private static final ExoAbility VOLLEY = new ExoAbility("vengeance_volley", 200, 120);
    private static final ExoAbility THRUSTERS = new ExoAbility("thrusters", 100, 60);

    public Exo5Vengeance(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level, ExoTier.EXO_5);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ExosuitEntity.createAttributes(ExoTier.EXO_5);
    }

    @Override
    public ExoAbility getAbility(int slot) {
        return switch (slot) {
            case 0 -> OVERLOAD;
            case 1 -> VOLLEY;
            case 2 -> THRUSTERS;
            default -> ExoAbility.NONE;
        };
    }

    @Override
    protected boolean canUseAbility(int slot, ServerPlayer pilot) {
        if (slot == 0 && this.isOverloadActive()) {
            this.feedback(pilot, "message.opusvsexe.exo.already_overloaded");
            return false;
        }
        return true;
    }

    @Override
    protected void runAbility(int slot, ServerPlayer pilot) {
        switch (slot) {
            case 0 -> {
                this.activateOverload(200);
                if (pilot != null) {
                    pilot.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, true));
                    pilot.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, false, true));
                }
                this.playSound(ModSounds.KATANA_ULTIMATE, 1.6F, 0.7F);
            }
            case 1 -> {
                int hits = this.coneAttack(pilot, this.getTier().attackReach() + 2.0D, 0.2D,
                        this.getAttackDamage() * 1.2F);
                this.playSound(ModSounds.HAMMER_ULTIMATE, 1.4F, hits > 0 ? 0.9F : 1.2F);
            }
            case 2 -> {
                Vec3 look = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
                this.applyImpulse(new Vec3(look.x * 1.5D, 1.0D, look.z * 1.5D));
                this.playSound(ModSounds.EXO_THRUST, 1.6F, 0.9F);
            }
            default -> { }
        }
    }
}
