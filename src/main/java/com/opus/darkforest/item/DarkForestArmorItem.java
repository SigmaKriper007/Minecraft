package com.opus.darkforest.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public final class DarkForestArmorItem extends ArmorItem {
    private final boolean vestments;
    public DarkForestArmorItem(Type type,boolean vestments,Properties properties){super(vestments?VestmentsArmorMaterial.INSTANCE:BriarweaveArmorMaterial.INSTANCE,type,properties);this.vestments=vestments;}
    public boolean isVestments(){return vestments;}
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,level,lines,flag);lines.add(Component.translatable(vestments?"item.opusvsexe.dark_forest_vestments.desc":"item.opusvsexe.briarweave_armor.desc").withStyle(vestments?ChatFormatting.LIGHT_PURPLE:ChatFormatting.DARK_GREEN));if(vestments)lines.add(Component.translatable("item.opusvsexe.dark_forest_vestments.teleport").withStyle(ChatFormatting.AQUA));}
}
