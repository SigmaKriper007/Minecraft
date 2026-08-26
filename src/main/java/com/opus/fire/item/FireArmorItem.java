package com.opus.fire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Vein Crust baseline armor. Visualization remains fully 3D through the legacy-named
 * FireHelmetLayer / FirePlateLayer; the full-set resistance is server authoritative.
 */
public class FireArmorItem extends ArmorItem {

    public FireArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    private static void ensureProtection(ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, stack) >= 1) return;
        var enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(Enchantments.ALL_DAMAGE_PROTECTION, 1);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide) ensureProtection(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        ensureProtection(stack);
        super.onCraftedBy(stack, level, player);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        ensureProtection(stack);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.vein_crust_armor.ability")
            .withStyle(ChatFormatting.GOLD));
    }
}
