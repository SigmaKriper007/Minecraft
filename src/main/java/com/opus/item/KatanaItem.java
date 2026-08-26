package com.opus.item;

import com.opus.entity.KatanaSlashEntity;
import com.opus.registry.ModItems;
import com.opus.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class KatanaItem extends SwordItem {
    public KatanaItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) { super(tier, attackDamage, attackSpeed, properties); }
    @Override public boolean isFoil(ItemStack stack) { return true; }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        attacker.level().playSound(null, target.blockPosition(), ModSounds.KATANA_HIT, SoundSource.PLAYERS, 1.0f, 1.0f);
        return true;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            Vec3 look = player.getLookAngle();
            if (stack.is(ModItems.KATANA_OP)) {
                Vec3 origin = player.getEyePosition().add(look.scale(1.2D)).add(0.0D, -0.55D, 0.0D);
                KatanaSlashEntity.spawn(player, KatanaSlashEntity.OPUS, origin, look, 14.0F, 24);
                player.getCooldowns().addCooldown(this, 50);
            } else if (stack.is(ModItems.KATANA_GOLD)) {
                Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
                if (horizontal.lengthSqr() < 0.01D) {
                    horizontal = new Vec3(0.0D, 0.0D, 1.0D);
                }
                Vec3 origin = player.position().add(horizontal.normalize().scale(1.25D)).add(0.0D, 0.12D, 0.0D);
                KatanaSlashEntity.spawn(player, KatanaSlashEntity.GOLD, origin, horizontal, 12.0F, 22);
                player.getCooldowns().addCooldown(this, 100);
            } else {
                Vec3 origin = player.getEyePosition().add(0.0D, -0.6D, 0.0D);
                for (int i = 0; i < 12; i++) {
                    double angle = Math.PI * 2.0D * i / 12.0D;
                    Vec3 direction = new Vec3(Math.sin(angle), 0.03D, Math.cos(angle));
                    KatanaSlashEntity.spawn(player, KatanaSlashEntity.REFINED,
                            origin.add(direction.scale(0.9D)), direction, 8.0F, 14);
                }
                player.getCooldowns().addCooldown(this, 120);
            }
            level.playSound(null, player.blockPosition(), ModSounds.KATANA_ULTIMATE,
                    SoundSource.PLAYERS, 1.0F, stack.is(ModItems.KATANA_GOLD) ? 1.3F : 0.85F);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.katana_lore").withStyle(ChatFormatting.DARK_PURPLE));
        String ability = stack.is(ModItems.KATANA_OP) ? "katana_op"
                : stack.is(ModItems.KATANA_GOLD) ? "katana_gold" : "katana_refined";
        tooltip.add(Component.translatable("item.opusvsexe." + ability + ".ability").withStyle(ChatFormatting.GOLD));
    }
}
