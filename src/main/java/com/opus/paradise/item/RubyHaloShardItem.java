package com.opus.paradise.item;

import net.minecraft.world.item.Item;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public final class RubyHaloShardItem extends Item {
    public RubyHaloShardItem(Properties properties){super(properties);}
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> tooltip,TooltipFlag flag){super.appendHoverText(stack,level,tooltip,flag);tooltip.add(Component.translatable("item.opusvsexe.ruby_halo_shard.ritual").withStyle(ChatFormatting.LIGHT_PURPLE));}
}
