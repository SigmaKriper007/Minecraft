package com.opus.item;

import com.opus.registry.ModItems;
import com.opus.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
        ItemStack stack=player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            int cost; float damage; double radius; String id=stack.getItem()==ModItems.KATANA_OP?"op":stack.getItem()==ModItems.KATANA_GOLD?"gold":"refined";
            if (id.equals("op")) { cost=35; damage=14; radius=5; }
            else if (id.equals("gold")) { cost=25; damage=9; radius=4; }
            else { cost=20; damage=11; radius=3.5; }
            CombatEffects.shockwave(player, radius, damage, 1.6, false);
            if (!level.isClientSide && level instanceof ServerLevel server) {
                server.sendParticles(id.equals("op")?ParticleTypes.SOUL_FIRE_FLAME:ParticleTypes.CRIT, player.getX(), player.getEyeY(), player.getZ(), 55, .7,.4,.7,.2);
            }
            level.playSound(null, player.blockPosition(), ModSounds.KATANA_ULTIMATE, SoundSource.PLAYERS, 1.4f, id.equals("gold")?1.35f:.75f);
            player.getCooldowns().addCooldown(this, id.equals("op")?80:55);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.katana_lore").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("ПКМ: уникальная суперспособность").withStyle(ChatFormatting.GOLD));
    }
}
