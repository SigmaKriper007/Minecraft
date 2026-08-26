package com.opus.ember.item;

import com.opus.ember.sound.EmberSounds;
import com.opus.fire.registry.FireItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class EmberArmorMaterial implements ArmorMaterial {
    public static final EmberArmorMaterial INSTANCE = new EmberArmorMaterial();

    private static final int[] DURABILITY = new int[]{22, 30, 32, 22};
    private static final int[] DEFENSE = new int[]{3, 6, 7, 3};

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()] * 37;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 18;
    }

    @Override
    public SoundEvent getEquipSound() {
        return EmberSounds.EMBER_EQUIP;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(FireItems.FIRE_ESSENCE);
    }

    @Override
    public String getName() {
        return "ember";
    }

    @Override
    public float getToughness() {
        return 2.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}
