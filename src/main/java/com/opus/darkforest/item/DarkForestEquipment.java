package com.opus.darkforest.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class DarkForestEquipment {
    private DarkForestEquipment(){ }
    public static ItemStack applyIntrinsic(ItemStack stack){if(stack.getItem() instanceof DarkForestTools.ToolMarker&&EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,stack)<1)stack.enchant(Enchantments.UNBREAKING,1);return stack;}
}
