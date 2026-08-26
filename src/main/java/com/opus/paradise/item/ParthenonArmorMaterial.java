package com.opus.paradise.item;

import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class ParthenonArmorMaterial implements ArmorMaterial {
    public static final ParthenonArmorMaterial INSTANCE = new ParthenonArmorMaterial();
    private static final int[] DURABILITY = {13, 15, 16, 11};
    private static final int[] DEFENSE = {3, 6, 8, 3};
    private ParthenonArmorMaterial() { }
    @Override public int getDurabilityForType(ArmorItem.Type type){return DURABILITY[type.getSlot().getIndex()]*33;}
    @Override public int getDefenseForType(ArmorItem.Type type){return DEFENSE[type.getSlot().getIndex()];}
    @Override public int getEnchantmentValue(){return 10;}
    @Override public SoundEvent getEquipSound(){return SoundEvents.ARMOR_EQUIP_DIAMOND;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(ParadiseItems.RUBY_HALO_SHARD);}
    @Override public String getName(){return "parthenon_regalia";}
    @Override public float getToughness(){return 2F;}
    @Override public float getKnockbackResistance(){return 0F;}
}
