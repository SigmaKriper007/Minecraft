package com.opus.fire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FirePickaxeItem extends PickaxeItem {
    public FirePickaxeItem(Properties properties) {
        super(FireToolTier.INSTANCE, 1, -2.8F, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.fire_pickaxe.ability").withStyle(ChatFormatting.GOLD));
    }
}
