package com.opus.ember.item;

import com.opus.ember.entity.projectile.BlazingTridentEntity;
import com.opus.ember.registry.EmberEntities;
import com.opus.ember.sound.EmberSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class BlazingTridentItem extends Item {
    public BlazingTridentItem(Properties properties) { super(properties); }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.SPEAR; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remaining) {
        if (!(user instanceof Player player) || getUseDuration(stack) - remaining < 10) return;
        if (!level.isClientSide) {
            BlazingTridentEntity trident = new BlazingTridentEntity(EmberEntities.BLAZING_TRIDENT, level, player);
            trident.setPos(player.getEyePosition().add(player.getLookAngle().scale(0.75)));
            trident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 2.1f, 0.75f);
            level.addFreshEntity(trident);
            stack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(player.getUsedItemHand()));
            player.getCooldowns().addCooldown(this, 36);
            level.playSound(null, player, EmberSounds.EMBER_FIREBALL_LAUNCH, SoundSource.PLAYERS, 1.0f, 0.72f);
        }
    }
}
