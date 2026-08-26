package com.opus.paradise.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public final class ParthenonArmorItem extends ArmorItem {
    public ParthenonArmorItem(Type type,Properties properties){super(ParthenonArmorMaterial.INSTANCE,type,properties);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){super.inventoryTick(stack,level,entity,slot,selected);if(!level.isClientSide)ParadiseEquipment.applyIntrinsic(stack);}
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> tooltip,TooltipFlag flag){super.appendHoverText(stack,level,tooltip,flag);tooltip.add(Component.translatable("item.opusvsexe.parthenon_armor.intrinsic").withStyle(ChatFormatting.GOLD));if(getType()==Type.CHESTPLATE)tooltip.add(Component.translatable("item.opusvsexe.parthenon_chestplate.flight").withStyle(ChatFormatting.AQUA));tooltip.add(Component.translatable("item.opusvsexe.parthenon_set.ability").withStyle(ChatFormatting.LIGHT_PURPLE));}
}
