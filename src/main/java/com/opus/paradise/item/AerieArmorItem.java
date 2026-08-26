package com.opus.paradise.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public final class AerieArmorItem extends ArmorItem {
    public AerieArmorItem(Type type, Properties properties){super(AerieArmorMaterial.INSTANCE,type,properties);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){super.inventoryTick(stack,level,entity,slot,selected);if(!level.isClientSide)ParadiseEquipment.applyIntrinsic(stack);}
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> tooltip,TooltipFlag flag){super.appendHoverText(stack,level,tooltip,flag);tooltip.add(Component.translatable("item.opusvsexe.aerie_bronze_armor.desc").withStyle(ChatFormatting.AQUA));}
}
