package com.opus.mixin;

import com.opus.item.KatanaItem;
import com.opus.item.WarhammerItem;
import com.opus.sound.ModSounds;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
        if (self.getVehicle() instanceof ExosuitEntity exo
            && target instanceof LivingEntity living
            && living.isAlive()) {
            ItemStack main = exo.getInventory().getItem(0);
            ItemStack off = exo.getInventory().getItem(1);
            if (main.getItem() instanceof KatanaItem || off.getItem() instanceof KatanaItem) {
                self.level().playSound(null, target.blockPosition(), ModSounds.KATANA_HIT, SoundSource.PLAYERS, 1.0f, 1.0f);
            } else if (main.getItem() instanceof WarhammerItem || off.getItem() instanceof WarhammerItem) {
                self.level().playSound(null, target.blockPosition(), ModSounds.HAMMER_HIT, SoundSource.PLAYERS, 1.2f, 0.8f);
            }
            living.hurt(exo.damageSources().mobAttack(exo), exo.getAttackDamage());
            exo.swing(InteractionHand.MAIN_HAND);
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
