package com.opus.paradise.item;

import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class ParadiseEquipment {
    private ParadiseEquipment() { }
    public static ItemStack applyIntrinsic(ItemStack stack) {
        if (stack.is(ParadiseItems.AERIE_BRONZE_BOOTS)
            && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FALL_PROTECTION,stack) < 2) stack.enchant(Enchantments.FALL_PROTECTION,2);
        if (stack.getItem() instanceof ParthenonArmorItem
            && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION,stack) < 3) stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION,3);
        if (stack.getItem() instanceof ParthenonTools.IntrinsicEfficiency
            && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY,stack) < 4) stack.enchant(Enchantments.BLOCK_EFFICIENCY,4);
        return stack;
    }
}
