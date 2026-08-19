package com.opus.mixin;

import com.opus.item.OpusArmorBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class JumpBoostMixin {

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void opusvsexe$opusJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ServerPlayer player && OpusArmorBonus.isFullOpusSuit(player)) {
            Vec3 v = player.getDeltaMovement();
            player.setDeltaMovement(v.x, v.y * 1.536f, v.z);
        }
    }
}
