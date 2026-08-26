package com.opus.paradise.item;

import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class AerieArmorMaterial implements ArmorMaterial {
    public static final AerieArmorMaterial INSTANCE = new AerieArmorMaterial();
    private static final int[] DURABILITY = {13, 15, 16, 11};
    private static final int[] DEFENSE = {2, 5, 6, 2};
    private AerieArmorMaterial() { }
    @Override public int getDurabilityForType(ArmorItem.Type type){return DURABILITY[type.getSlot().getIndex()]*15;}
    @Override public int getDefenseForType(ArmorItem.Type type){return DEFENSE[type.getSlot().getIndex()];}
    @Override public int getEnchantmentValue(){return 14;}
    @Override public SoundEvent getEquipSound(){return SoundEvents.ARMOR_EQUIP_GOLD;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(ParadiseItems.AERIE_BRONZE_INGOT);}
    @Override public String getName(){return "aerie_bronze";}
    @Override public float getToughness(){return 0;}
    @Override public float getKnockbackResistance(){return .035F;}
}
