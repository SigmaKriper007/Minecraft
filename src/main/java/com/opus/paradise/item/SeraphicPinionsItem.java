package com.opus.paradise.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public final class SeraphicPinionsItem extends Item {
    public SeraphicPinionsItem(Properties properties){super(properties);}
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> tooltip,TooltipFlag flag){super.appendHoverText(stack,level,tooltip,flag);tooltip.add(Component.translatable("item.opusvsexe.seraphic_pinions.ritual").withStyle(ChatFormatting.AQUA));}
}
