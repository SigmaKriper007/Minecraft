package com.opus.mixin;

import com.opus.item.KatanaItem;
import com.opus.item.WarhammerItem;
import com.opus.sound.ModSounds;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerAttackMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$exoRideAttack(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.level().isClientSide) {
            return;
        }
        // Attacks while piloting an EXO frame never reach this method: the client
        // mixin redirects left click into the exo_attack packet, and the suit
        // validates it (cooldown, reach, pilot identity). Letting a vanilla
        // attack land here would bypass that validation, so a piloted suit
        // cannot be damaged through this path and swing sounds stay vanilla.
        if (self.getVehicle() instanceof ExosuitEntity) {
            ci.cancel();
            return;
        }
        ItemStack hand = self.getMainHandItem();
        if (hand.getItem() instanceof KatanaItem) {
            self.level().playSound(null, target.blockPosition(), ModSounds.KATANA_SWING, SoundSource.PLAYERS, 0.7f, 1.0f);
        } else if (hand.getItem() instanceof WarhammerItem) {
            self.level().playSound(null, target.blockPosition(), ModSounds.HAMMER_HIT, SoundSource.PLAYERS, 0.7f, 0.7f);
        }
    }
}
