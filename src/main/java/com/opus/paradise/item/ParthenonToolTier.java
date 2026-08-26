package com.opus.paradise.item;

import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum ParthenonToolTier implements Tier {
    INSTANCE;
    @Override public int getUses(){return 1561;}
    @Override public float getSpeed(){return 8F;}
    @Override public float getAttackDamageBonus(){return 3F;}
    @Override public int getLevel(){return 3;}
    @Override public int getEnchantmentValue(){return 10;}
    @Override public Ingredient getRepairIngredient(){return Ingredient.of(ParadiseItems.RUBY_HALO_SHARD);}
}
