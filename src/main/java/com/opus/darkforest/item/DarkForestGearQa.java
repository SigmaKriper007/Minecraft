package com.opus.darkforest.item;

import com.opus.OpusVsExe;
import com.opus.darkforest.registry.DarkForestItems;
import com.opus.qa.DevelopmentQa;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/** Focused development-only Task 40 contract probe. */
public final class DarkForestGearQa {
    private static final List<String> RECIPES=List.of("briarweave","briarweave_helmet","briarweave_chestplate","briarweave_leggings","briarweave_boots","dark_forest_helmet","dark_forest_chestplate","dark_forest_leggings","dark_forest_boots","dark_forest_sword","dark_forest_pickaxe","dark_forest_axe","dark_forest_shovel","dark_forest_hoe");
    private DarkForestGearQa(){ }
    public static void init(){if(DevelopmentQa.enabled(40))ServerLifecycleEvents.SERVER_STARTED.register(server->run(server.overworld()));}

    private static void run(ServerLevel level){
        for(String id:RECIPES)check(level.getRecipeManager().byKey(OpusVsExe.id(id)).orElseThrow(()->new IllegalStateException("Task 40 QA: missing recipe "+id)).getType()==RecipeType.CRAFTING,"recipe is not vanilla crafting: "+id);
        check(BriarweaveArmorMaterial.INSTANCE.getDefenseForType(ArmorItem.Type.CHESTPLATE)==6&&BriarweaveArmorMaterial.INSTANCE.getDurabilityForType(ArmorItem.Type.CHESTPLATE)==240,"Briarweave is not iron-equivalent");
        check(VestmentsArmorMaterial.INSTANCE.getDefenseForType(ArmorItem.Type.CHESTPLATE)==8&&VestmentsArmorMaterial.INSTANCE.getDurabilityForType(ArmorItem.Type.CHESTPLATE)==528&&VestmentsArmorMaterial.INSTANCE.getToughness()==2F,"Vestments are not diamond-equivalent");
        check(DarkForestToolTier.INSTANCE.getUses()==1561&&DarkForestToolTier.INSTANCE.getSpeed()==12F&&DarkForestToolTier.INSTANCE.getLevel()==0,"tool tier drifted");
        for(Item item:List.of(DarkForestItems.DARK_FOREST_SWORD,DarkForestItems.DARK_FOREST_PICKAXE,DarkForestItems.DARK_FOREST_AXE,DarkForestItems.DARK_FOREST_SHOVEL,DarkForestItems.DARK_FOREST_HOE)){ItemStack stack=new ItemStack(item);DarkForestEquipment.applyIntrinsic(stack);check(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING,stack)==1,"missing intrinsic Unbreaking I");}

        FakePlayer player=FakePlayer.get(level);BlockPos start=findSite(level);DarkForestPlayerState state=(DarkForestPlayerState)player;
        try{
            for(int z=0;z<=34;z++)level.setBlock(start.offset(0,-1,z),Blocks.OBSIDIAN.defaultBlockState(),3);
            player.setPos(start.getX()+.5,start.getY(),start.getZ()+.5);player.setXRot(0);player.setYRot(0);player.setDeltaMovement(0,0,0);
            equip(player,false);player.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.POISON,200,0));DarkForestEquipmentBonus.tick(player);check(!player.hasEffect(MobEffects.POISON),"Briarweave did not reject poison");
            equip(player,true);DarkForestEquipmentBonus.tick(player);effect(player,MobEffects.NIGHT_VISION,0);effect(player,MobEffects.DIG_SPEED,0);effect(player,MobEffects.MOVEMENT_SPEED,1);effect(player,MobEffects.DAMAGE_BOOST,0);effect(player,MobEffects.DOLPHINS_GRACE,1);
            player.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(DarkForestItems.DARK_FOREST_PICKAXE));DarkForestEquipmentBonus.tick(player);effect(player,MobEffects.DIG_SPEED,2);
            player.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);DarkForestEquipmentBonus.tick(player);effect(player,MobEffects.DIG_SPEED,0);

            state.opusvsexe$setDarkTeleportReady(0);double before=player.getZ();check(DarkForestEquipmentBonus.tryAimedTeleport(player),"safe aimed teleport failed");check(player.getZ()>before+20,"teleport did not advance toward aim");long ready=state.opusvsexe$getDarkTeleportReady();check(ready-level.getGameTime()==DarkForestEquipmentBonus.TELEPORT_COOLDOWN,"cooldown was not exactly 50 ticks");check(!DarkForestEquipmentBonus.tryAimedTeleport(player),"cooldown was bypassed");CompoundTag saved=new CompoundTag();player.saveWithoutId(saved);check(saved.getLong("OpusDarkTeleportReady")==ready,"teleport cooldown did not persist");

            player.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);DarkForestEquipmentBonus.tick(player);for(MobEffect effect:List.of(MobEffects.NIGHT_VISION,MobEffects.DIG_SPEED,MobEffects.MOVEMENT_SPEED,MobEffects.DAMAGE_BOOST,MobEffects.DOLPHINS_GRACE))check(!player.hasEffect(effect),"set effect leaked after partial-set cleanup");
            OpusVsExe.LOGGER.info("Task 40 QA PASS: 14 crafting recipes, exact armor/tool contracts, Unbreaking I, poison ward, exact full-set effects and cleanup, held-tool Haste III, safe aimed V teleport, exact persisted 50-tick cooldown");
        }finally{
            player.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);player.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);player.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);player.setItemSlot(EquipmentSlot.FEET,ItemStack.EMPTY);player.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);player.removeAllEffects();state.opusvsexe$setDarkTeleportReady(0);state.opusvsexe$setDarkSetEffects(false);state.opusvsexe$setDarkHasteAmplifier(-1);
            for(int z=0;z<=34;z++)if(level.getBlockState(start.offset(0,-1,z)).is(Blocks.OBSIDIAN))level.setBlock(start.offset(0,-1,z),Blocks.AIR.defaultBlockState(),3);
        }
    }
    private static void equip(FakePlayer player,boolean vestments){player.setItemSlot(EquipmentSlot.HEAD,new ItemStack(vestments?DarkForestItems.DARK_FOREST_HELMET:DarkForestItems.BRIARWEAVE_HELMET));player.setItemSlot(EquipmentSlot.CHEST,new ItemStack(vestments?DarkForestItems.DARK_FOREST_CHESTPLATE:DarkForestItems.BRIARWEAVE_CHESTPLATE));player.setItemSlot(EquipmentSlot.LEGS,new ItemStack(vestments?DarkForestItems.DARK_FOREST_LEGGINGS:DarkForestItems.BRIARWEAVE_LEGGINGS));player.setItemSlot(EquipmentSlot.FEET,new ItemStack(vestments?DarkForestItems.DARK_FOREST_BOOTS:DarkForestItems.BRIARWEAVE_BOOTS));}
    private static void effect(FakePlayer player,MobEffect effect,int amplifier){check(player.getEffect(effect)!=null&&player.getEffect(effect).getAmplifier()==amplifier,"wrong or missing effect "+effect.getDescriptionId());}
    private static BlockPos findSite(ServerLevel level){BlockPos spawn=level.getSharedSpawnPos();return new BlockPos(spawn.getX()-800,Math.min(level.getMaxBuildHeight()-16,Math.max(level.getMinBuildHeight()+16,spawn.getY()+120)),spawn.getZ()+800);}
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 40 QA: "+message);}
}
