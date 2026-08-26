package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.recipe.ParthenonForgingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ParadiseRecipes {
    public static final RecipeType<ParthenonForgingRecipe> PARTHENON_FORGING=Registry.register(BuiltInRegistries.RECIPE_TYPE,ParadiseLine.id("parthenon_forging"),new RecipeType<>(){public String toString(){return "opusvsexe:parthenon_forging";}});
    public static final RecipeSerializer<ParthenonForgingRecipe> PARTHENON_FORGING_SERIALIZER=Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,ParadiseLine.id("parthenon_forging"),new ParthenonForgingRecipe.Serializer());
    private ParadiseRecipes(){ }
    public static void init(){ }
}
