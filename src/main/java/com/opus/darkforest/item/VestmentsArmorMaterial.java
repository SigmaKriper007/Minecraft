package com.opus.darkforest.item;

import com.opus.darkforest.registry.DarkForestItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class VestmentsArmorMaterial implements ArmorMaterial {
    public static final VestmentsArmorMaterial INSTANCE=new VestmentsArmorMaterial();
    private static final int[] DURABILITY={13,15,16,11},DEFENSE={3,6,8,3};
    private VestmentsArmorMaterial(){ }
    @Override public int getDurabilityForType(ArmorItem.Type type){return DURABILITY[type.getSlot().getIndex()]*33;}
    @Override public int getDefenseForType(ArmorItem.Type type){return DEFENSE[type.getSlot().getIndex()];}
    @Override public int getEnchantmentValue(){return 18;}
    @Override public SoundEvent getEquipSound(){return SoundEvents.ARMOR_EQUIP_NETHERITE;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(DarkForestItems.ROOTBOUND_EYE);}
    @Override public String getName(){return "dark_forest_vestments";}
    @Override public float getToughness(){return 2;}
    @Override public float getKnockbackResistance(){return .05F;}
}
