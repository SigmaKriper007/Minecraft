package com.opus.darkforest.entity;

import com.opus.OpusVsExe;
import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.blockentity.MoonFountainCoreBlockEntity;
import com.opus.darkforest.registry.DarkForestBlocks;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.registry.DarkForestItems;
import com.opus.qa.DevelopmentQa;
import com.opus.registry.TrophyRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Focused development-only Task 39 contract probe. */
public final class MossboundEncounterQa {
    private static boolean pending;
    private MossboundEncounterQa(){ }
    public static void init(){if(DevelopmentQa.enabled(39)){ServerLifecycleEvents.SERVER_STARTED.register(server->pending=true);ServerTickEvents.END_SERVER_TICK.register(server->{if(pending){pending=false;run(server.overworld());}});}}

    private static void run(ServerLevel level){
        BlockPos floor=findSite(level),corePos=floor.above();AABB area=new AABB(floor).inflate(20,12,20);boolean oldLoot=level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        try{
            for(BlockPos pos:BlockPos.betweenClosed(floor.offset(-10,0,-10),floor.offset(10,0,10)))level.setBlock(pos,Blocks.OBSIDIAN.defaultBlockState(),3);
            level.setBlock(corePos,DarkForestBlocks.MOON_FOUNTAIN_CORE.defaultBlockState(),3);MoonFountainCoreBlockEntity core=requireCore(level,corePos);
            MossboundEndermanEntity boss=core.spawnBoss(level);check(boss!=null&&core.spawnBoss(level)==null,"core did not enforce exactly one first spawn");
            check(DarkForestEntities.MOSSBOUND_ENDERMAN.getWidth()==2.35F&&DarkForestEntities.MOSSBOUND_ENDERMAN.getHeight()==5.25F,"boss hitbox drifted");
            check(boss.getMaxHealth()==480F&&boss.getAttributeBaseValue(Attributes.ARMOR)==12&&boss.getAttributeBaseValue(Attributes.ATTACK_DAMAGE)==16&&boss.getAttributeBaseValue(Attributes.MOVEMENT_SPEED)==.32,"boss attributes drifted");

            Cow target=require(EntityType.COW.create(level),"target");target.setPos(corePos.getX()+4.5,corePos.getY(),corePos.getZ()+.5);check(level.addFreshEntity(target),"target add failed");
            float dormantHealth=boss.getHealth();check(!boss.hurt(level.damageSources().mobAttack(target),9)&&boss.getHealth()==dormantHealth&&boss.isAwakened(),"waking hit was not zero damage");
            boss.setHealth(boss.getMaxHealth()*.64F);boss.evaluatePhaseForQa();check(boss.getPhase()==2,"65% phase threshold failed");boss.setHealth(boss.getMaxHealth()*.29F);boss.evaluatePhaseForQa();check(boss.getPhase()==3,"30% phase threshold failed");
            boss.beginActionForQa(MossboundEndermanEntity.Action.FLOWER);float before=boss.getHealth();boss.invulnerableTime=0;boss.hurt(level.damageSources().fellOutOfWorld(),10);check(boss.isFlowerOpen()&&Math.abs((before-boss.getHealth())-15F)<.01F,"flower weak point was not 1.5x");

            CompoundTag saved=core.saveWithFullMetadata();MoonFountainCoreBlockEntity restored=new MoonFountainCoreBlockEntity(corePos,DarkForestBlocks.MOON_FOUNTAIN_CORE.defaultBlockState());restored.load(saved);check(!restored.isDefeated(),"active core state failed NBT round-trip");
            target.setHealth(target.getMaxHealth());target.invulnerableTime=0;MossboundAttackEntity root=require(DarkForestEntities.ROOT_SNARE.create(level),"root snare");root.setPos(target.position());root.configure(boss);check(level.addFreshEntity(root),"root add failed");float targetHealth=target.getHealth();check(root.applyImpact(target,8)&&!root.applyImpact(target,8)&&target.getHealth()==targetHealth-8&&target.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN),"root hit-once contract failed");for(int i=0;i<36&&!root.isRemoved();i++){root.tickCount++;root.tick();}check(root.isRemoved()&&target.getHealth()==targetHealth-8,"root repeated damage or failed to expire");
            check(boss.teleportForQa(Vec3.atCenterOf(corePos.offset(5,0,0)))&&boss.position().distanceTo(Vec3.atCenterOf(corePos))<22,"safe arena teleport failed");

            verifyLoot(level,boss);check(TrophyRegistry.trophyFor(DarkForestEntities.MOSSBOUND_ENDERMAN)==TrophyRegistry.MOSSBOUND_ENDERMAN,"boss trophy binding missing");level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(true,level.getServer());check(TrophyRegistry.shouldDropTrophy(boss),"doMobLoot=true rejected boss trophy");level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false,level.getServer());check(!TrophyRegistry.shouldDropTrophy(boss),"doMobLoot=false did not suppress boss trophy");
            boss.die(level.damageSources().fellOutOfWorld());check(core.isDefeated()&&core.spawnBoss(level)==null,"victory state did not suppress automatic respawn");CompoundTag defeated=core.saveWithFullMetadata();MoonFountainCoreBlockEntity defeatedCopy=new MoonFountainCoreBlockEntity(corePos,DarkForestBlocks.MOON_FOUNTAIN_CORE.defaultBlockState());defeatedCopy.load(defeated);check(defeatedCopy.isDefeated(),"defeat state failed NBT round-trip");
            OpusVsExe.LOGGER.info("Task 39 QA PASS: single fountain spawn/defeat persistence, zero-hit awakening, exact boss contract/phases, 1.5x flower weak point, finite hit-once root, safe arena teleport, exact 1+4 progression loot, trophy and doMobLoot suppression");
        }finally{
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(oldLoot,level.getServer());for(Entity entity:level.getEntities((Entity)null,area,e->true))entity.discard();for(BlockPos pos:BlockPos.betweenClosed(floor.offset(-10,0,-10),floor.offset(10,1,10)))if(level.getBlockState(pos).is(Blocks.OBSIDIAN)||level.getBlockState(pos).is(DarkForestBlocks.MOON_FOUNTAIN_CORE))level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);
        }
    }

    private static void verifyLoot(ServerLevel level,MossboundEndermanEntity boss){LootParams params=new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY,boss).withParameter(LootContextParams.ORIGIN,boss.position()).withParameter(LootContextParams.DAMAGE_SOURCE,level.damageSources().generic()).create(LootContextParamSets.ENTITY);List<ItemStack> drops=level.getServer().getLootData().getLootTable(DarkForestLine.id("entities/mossbound_enderman")).getRandomItems(params,39L);int hearts=drops.stream().filter(s->s.is(DarkForestItems.MOONFLOWER_HEART)).mapToInt(ItemStack::getCount).sum();int eyes=drops.stream().filter(s->s.is(DarkForestItems.ROOTBOUND_EYE)).mapToInt(ItemStack::getCount).sum();check(hearts==1&&eyes==4,"progression loot was "+hearts+" heart / "+eyes+" eyes");}
    private static MoonFountainCoreBlockEntity requireCore(ServerLevel level,BlockPos pos){if(level.getBlockEntity(pos) instanceof MoonFountainCoreBlockEntity core)return core;throw new IllegalStateException("Task 39 QA: core block entity missing");}
    private static BlockPos findSite(ServerLevel level){BlockPos spawn=level.getSharedSpawnPos();int y=Math.min(level.getMaxBuildHeight()-16,Math.max(level.getMinBuildHeight()+16,spawn.getY()+120));for(int i=0;i<16;i++){BlockPos candidate=new BlockPos(spawn.getX()-640-i*24,y,spawn.getZ()+640);if(level.isEmptyBlock(candidate)&&level.isEmptyBlock(candidate.above(8)))return candidate;}throw new IllegalStateException("Task 39 QA: no empty site");}
    private static <T>T require(T value,String name){if(value==null)throw new IllegalStateException("Task 39 QA: "+name+" factory returned null");return value;}
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 39 QA: "+message);}
}
