package com.opus.fire.item;

import com.opus.fire.registry.FireItems;
import com.opus.fire.sound.FireSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class FireArmorMaterial implements ArmorMaterial {
    public static final FireArmorMaterial INSTANCE = new FireArmorMaterial();

    // Vein Crust is the Four Veins baseline: iron durability/defense plus intrinsic Protection I.
    private static final int[] DURABILITY = new int[]{13, 15, 16, 11};
    private static final int[] DEFENSE = new int[]{2, 5, 6, 2};

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()] * 15;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 9;
    }

    @Override
    public SoundEvent getEquipSound() {
        return FireSounds.FIRE_EQUIP;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(FireItems.MAGMA_CRUST);
    }

    @Override
    public String getName() {
        return "vein_crust";
    }

    @Override
    public float getToughness() {
        return 0.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0f;
    }
}
