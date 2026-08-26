package com.opus.darkforest.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public final class DarkForestTools {
    private DarkForestTools(){ }
    public interface ToolMarker { }
    private static void tick(ItemStack stack,Level level){if(!level.isClientSide)DarkForestEquipment.applyIntrinsic(stack);}
    private static void tooltip(List<Component> lines){lines.add(Component.translatable("item.opusvsexe.dark_forest_tools.tier").withStyle(ChatFormatting.DARK_PURPLE));lines.add(Component.translatable("item.opusvsexe.dark_forest_tools.haste").withStyle(ChatFormatting.GREEN));}
    public static final class Sword extends SwordItem implements ToolMarker {public Sword(Properties p){super(DarkForestToolTier.INSTANCE,3,-2.4F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t);}}
    public static final class Pickaxe extends PickaxeItem implements ToolMarker {public Pickaxe(Properties p){super(DarkForestToolTier.INSTANCE,1,-2.8F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t);}}
    public static final class Axe extends AxeItem implements ToolMarker {public Axe(Properties p){super(DarkForestToolTier.INSTANCE,6F,-3F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t);}}
    public static final class Shovel extends ShovelItem implements ToolMarker {public Shovel(Properties p){super(DarkForestToolTier.INSTANCE,1.5F,-3F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t);}}
    public static final class Hoe extends HoeItem implements ToolMarker {public Hoe(Properties p){super(DarkForestToolTier.INSTANCE,-1,0F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t);}}
}
