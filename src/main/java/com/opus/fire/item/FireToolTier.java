package com.opus.fire.item;

import com.opus.fire.registry.FireItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum FireToolTier implements Tier {
    INSTANCE;

    @Override public int getUses() { return 1800; }
    @Override public float getSpeed() { return 9.0F; }
    @Override public float getAttackDamageBonus() { return 3.0F; }
    @Override public int getLevel() { return 3; }
    @Override public int getEnchantmentValue() { return 18; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(FireItems.FIRE_ESSENCE); }
}
