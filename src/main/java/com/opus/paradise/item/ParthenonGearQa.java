package com.opus.paradise.item;

import com.opus.OpusVsExe;
import com.opus.paradise.entity.HurricaneEntity;
import com.opus.paradise.recipe.ParthenonForgingRecipe;
import com.opus.paradise.registry.ParadiseItems;
import com.opus.paradise.registry.ParadiseRecipes;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashSet;
import java.util.List;

/** Development-only contract probe; never runs from a packaged production instance. */
public final class ParthenonGearQa {
    private static final List<String> FORGING_RECIPES=List.of("aerie_bronze_ingot","aerie_bronze_helmet","aerie_bronze_chestplate","aerie_bronze_leggings","aerie_bronze_boots","parthenon_helmet","parthenon_chestplate","parthenon_leggings","parthenon_boots","parthenon_sword","parthenon_pickaxe","parthenon_axe","parthenon_shovel","parthenon_hoe");
    private ParthenonGearQa(){ }
    public static void init(){if(com.opus.qa.DevelopmentQa.enabled(35))ServerLifecycleEvents.SERVER_STARTED.register(server->run(server.overworld()));}
    private static void run(net.minecraft.server.level.ServerLevel level){
        for(String path:FORGING_RECIPES){Recipe<?> recipe=level.getRecipeManager().byKey(OpusVsExe.id(path)).orElseThrow(()->new IllegalStateException("Missing Task 35 recipe "+path));check(recipe.getType()==ParadiseRecipes.PARTHENON_FORGING,"Recipe escaped Parthenon Forge: "+path);}
        Recipe<?> forge=level.getRecipeManager().byKey(OpusVsExe.id("parthenon_forge")).orElseThrow();check(forge.getType()==net.minecraft.world.item.crafting.RecipeType.CRAFTING,"Forge block recipe must stay vanilla-craftable");
        ParthenonForgingRecipe catalystRecipe=(ParthenonForgingRecipe)level.getRecipeManager().byKey(OpusVsExe.id("parthenon_helmet")).orElseThrow();
        var dummyMenu=new net.minecraft.world.inventory.AbstractContainerMenu(null,-1){@Override public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player p,int i){return ItemStack.EMPTY;}@Override public boolean stillValid(net.minecraft.world.entity.player.Player p){return false;}};
        var grid=new net.minecraft.world.inventory.TransientCraftingContainer(dummyMenu,3,3);grid.setItem(0,new ItemStack(ParadiseItems.RUBY_HALO_SHARD));NonNullList<ItemStack> remaining=catalystRecipe.getRemainingItems(grid);check(remaining.get(0).is(ParadiseItems.RUBY_HALO_SHARD),"Ruby catalyst was consumed");
        check(AerieArmorMaterial.INSTANCE.getDefenseForType(ArmorItem.Type.CHESTPLATE)==6&&AerieArmorMaterial.INSTANCE.getDurabilityForType(ArmorItem.Type.CHESTPLATE)==240,"Aerie Bronze is not iron-equivalent");
        check(ParthenonArmorMaterial.INSTANCE.getDefenseForType(ArmorItem.Type.CHESTPLATE)==8&&ParthenonArmorMaterial.INSTANCE.getToughness()==2F,"Regalia is not diamond-equivalent");
        check(ParthenonToolTier.INSTANCE.getSpeed()==8F&&ParthenonToolTier.INSTANCE.getLevel()==3,"Parthenon tool tier drifted");
        FakePlayer player=FakePlayer.get(level);player.setPos(level.getSharedSpawnPos().getX()+.5,level.getSharedSpawnPos().getY()+80,level.getSharedSpawnPos().getZ()+.5);player.setXRot(0);player.setYRot(0);player.getAbilities().mayfly=false;player.getAbilities().flying=false;((ParadisePlayerState)player).opusvsexe$setRegaliaFlightGranted(false);((ParadisePlayerState)player).opusvsexe$setRegaliaHurricaneReady(0);
        player.setItemSlot(EquipmentSlot.HEAD,new ItemStack(ParadiseItems.PARTHENON_HELMET));player.setItemSlot(EquipmentSlot.CHEST,new ItemStack(ParadiseItems.PARTHENON_CHESTPLATE));player.setItemSlot(EquipmentSlot.LEGS,new ItemStack(ParadiseItems.PARTHENON_LEGGINGS));player.setItemSlot(EquipmentSlot.FEET,new ItemStack(ParadiseItems.PARTHENON_BOOTS));ParthenonArmorBonus.tick(player);
        check(player.getMaxHealth()==30F,"Regalia max health was "+player.getMaxHealth());check(player.getAbilities().mayfly,"Regalia chest did not grant flight");for(ItemStack armor:player.getArmorSlots())check(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION,armor)>=3,"Missing intrinsic Protection III");
        ItemStack pickaxe=new ItemStack(ParadiseItems.PARTHENON_PICKAXE);ParadiseEquipment.applyIntrinsic(pickaxe);check(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY,pickaxe)>=4,"Missing intrinsic Efficiency IV");
        var existing=new HashSet<>(level.getEntitiesOfClass(HurricaneEntity.class,player.getBoundingBox().inflate(64)));check(ParthenonArmorBonus.tryAimedHurricane(player),"First aimed hurricane was rejected");check(!ParthenonArmorBonus.tryAimedHurricane(player),"Hurricane cooldown was bypassed");var hurricanes=level.getEntitiesOfClass(HurricaneEntity.class,player.getBoundingBox().inflate(64));check(hurricanes.size()>existing.size(),"Aimed hurricane did not spawn");hurricanes.stream().filter(h->!existing.contains(h)).forEach(HurricaneEntity::discard);
        player.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);ParthenonArmorBonus.tick(player);check(player.getMaxHealth()==20F&&!player.getAbilities().mayfly&&!player.getAbilities().flying,"Regalia cleanup failed");player.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);player.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);player.setItemSlot(EquipmentSlot.FEET,ItemStack.EMPTY);
        OpusVsExe.LOGGER.info("Task 35 QA PASS: 14 forge recipes, returned catalyst, exact armor/tool tiers, Protection III, Efficiency IV, +10 health, flight cleanup and aimed Hurricane cooldown");
    }
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 35 QA: "+message);}
}
