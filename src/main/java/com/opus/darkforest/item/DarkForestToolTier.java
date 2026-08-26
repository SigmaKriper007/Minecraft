package com.opus.darkforest.item;

import com.opus.darkforest.registry.DarkForestItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum DarkForestToolTier implements Tier {
    INSTANCE;
    @Override public int getUses(){return 1561;}
    @Override public float getSpeed(){return 12F;}
    @Override public float getAttackDamageBonus(){return 0;}
    @Override public int getLevel(){return 0;}
    @Override public int getEnchantmentValue(){return 22;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(DarkForestItems.BRIARWEAVE);}
}
