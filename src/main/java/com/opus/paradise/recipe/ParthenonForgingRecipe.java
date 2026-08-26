package com.opus.paradise.recipe;

import com.google.gson.JsonObject;
import com.opus.paradise.item.ParadiseEquipment;
import com.opus.paradise.registry.ParadiseRecipes;
import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public final class ParthenonForgingRecipe implements Recipe<CraftingContainer> {
    private final ShapedRecipe delegate;
    public ParthenonForgingRecipe(ShapedRecipe delegate){this.delegate=delegate;}
    @Override public boolean matches(CraftingContainer container,Level level){return delegate.matches(container,level);}
    @Override public ItemStack assemble(CraftingContainer container,RegistryAccess access){return ParadiseEquipment.applyIntrinsic(delegate.assemble(container,access));}
    @Override public boolean canCraftInDimensions(int width,int height){return delegate.canCraftInDimensions(width,height);}
    @Override public ItemStack getResultItem(RegistryAccess access){return ParadiseEquipment.applyIntrinsic(delegate.getResultItem(access).copy());}
    @Override public NonNullList<ItemStack> getRemainingItems(CraftingContainer container){
        NonNullList<ItemStack> remaining=delegate.getRemainingItems(container);
        for(int index=0;index<container.getContainerSize();index++)if(container.getItem(index).is(ParadiseItems.RUBY_HALO_SHARD))remaining.set(index,new ItemStack(ParadiseItems.RUBY_HALO_SHARD));
        return remaining;
    }
    @Override public NonNullList<Ingredient> getIngredients(){return delegate.getIngredients();}
    @Override public String getGroup(){return delegate.getGroup();}
    @Override public ResourceLocation getId(){return delegate.getId();}
    @Override public RecipeSerializer<?> getSerializer(){return ParadiseRecipes.PARTHENON_FORGING_SERIALIZER;}
    @Override public RecipeType<?> getType(){return ParadiseRecipes.PARTHENON_FORGING;}

    public static final class Serializer implements RecipeSerializer<ParthenonForgingRecipe> {
        @Override public ParthenonForgingRecipe fromJson(ResourceLocation id,JsonObject json){return new ParthenonForgingRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(id,json));}
        @Override public ParthenonForgingRecipe fromNetwork(ResourceLocation id,FriendlyByteBuf buffer){return new ParthenonForgingRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(id,buffer));}
        @Override public void toNetwork(FriendlyByteBuf buffer,ParthenonForgingRecipe recipe){RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer,recipe.delegate);}
    }
}
