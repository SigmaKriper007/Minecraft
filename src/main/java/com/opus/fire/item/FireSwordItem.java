package com.opus.fire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public final class FireSwordItem extends SwordItem {
    public FireSwordItem(Properties properties) {
        super(FireToolTier.INSTANCE, 3, -2.4F, properties);
    }

    private static void ensureFireAspect(ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) >= 2) return;
        var enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(Enchantments.FIRE_ASPECT, 2);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide) ensureFireAspect(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        ensureFireAspect(stack);
        super.onCraftedBy(stack, level, player);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        ensureFireAspect(stack);
        return stack;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setSecondsOnFire(8);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.fire_sword.ability").withStyle(ChatFormatting.GOLD));
    }
}
