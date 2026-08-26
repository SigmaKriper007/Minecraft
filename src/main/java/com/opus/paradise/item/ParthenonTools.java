package com.opus.paradise.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import java.util.List;

public final class ParthenonTools {
    private ParthenonTools(){ }
    public interface IntrinsicEfficiency { }
    private static void tick(ItemStack stack,Level level){if(!level.isClientSide)ParadiseEquipment.applyIntrinsic(stack);}
    private static void tooltip(List<Component> lines,boolean efficiency){lines.add(Component.translatable(efficiency?"item.opusvsexe.parthenon_tools.efficiency":"item.opusvsexe.parthenon_tools.tier").withStyle(ChatFormatting.AQUA));lines.add(Component.translatable("item.opusvsexe.parthenon_tools.repair").withStyle(ChatFormatting.DARK_PURPLE));}
    public static final class Sword extends SwordItem {public Sword(Properties p){super(ParthenonToolTier.INSTANCE,3,-2.4F,p);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t,false);}}
    public static final class Pickaxe extends PickaxeItem implements IntrinsicEfficiency {public Pickaxe(Properties p){super(ParthenonToolTier.INSTANCE,1,-2.8F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t,true);}}
    public static final class Axe extends AxeItem implements IntrinsicEfficiency {public Axe(Properties p){super(ParthenonToolTier.INSTANCE,5F,-3F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t,true);}}
    public static final class Shovel extends ShovelItem implements IntrinsicEfficiency {public Shovel(Properties p){super(ParthenonToolTier.INSTANCE,1.5F,-3F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t,true);}}
    public static final class Hoe extends HoeItem implements IntrinsicEfficiency {public Hoe(Properties p){super(ParthenonToolTier.INSTANCE,-3,0F,p);}@Override public void inventoryTick(ItemStack s,Level l,Entity e,int i,boolean b){super.inventoryTick(s,l,e,i,b);tick(s,l);}@Override public void appendHoverText(ItemStack s,Level l,List<Component> t,TooltipFlag f){super.appendHoverText(s,l,t,f);tooltip(t,true);}}
}
