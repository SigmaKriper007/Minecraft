package com.opus.darkforest.blockentity;

import com.opus.OpusVsExe;
import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.block.RootboundPedestalBlock;
import com.opus.darkforest.entity.MossboundEndermanEntity;
import com.opus.darkforest.registry.DarkForestBlocks;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.registry.DarkForestItems;
import com.opus.qa.DevelopmentQa;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Focused development-only Task 41 resurrection and integration probe. */
public final class DarkForestResurrectionQa {
    private static final List<BlockPos> OFFSETS=List.of(new BlockPos(0,1,-7),new BlockPos(7,1,0),new BlockPos(0,1,7),new BlockPos(-7,1,0));
    private static int phase;
    private static Context context;
    private DarkForestResurrectionQa(){ }
    public static void init(){if(DevelopmentQa.enabled(41)){ServerLifecycleEvents.SERVER_STARTED.register(server->phase=1);ServerTickEvents.END_SERVER_TICK.register(server->{if(phase==1){phase=0;begin(server.overworld());}else if(phase==2){phase=0;finish();}else if(phase==3){phase=0;verifyRefund();}});}}

    private static void begin(ServerLevel level){
        BlockPos corePos=findSite(level);AABB arena=new AABB(corePos).inflate(50,24,50);FakePlayer player=FakePlayer.get(level);player.getInventory().clearContent();
        try{
            for(BlockPos floor:BlockPos.betweenClosed(corePos.offset(-12,-1,-12),corePos.offset(12,-1,12)))level.setBlock(floor,Blocks.OBSIDIAN.defaultBlockState(),3);
            level.setBlock(corePos,DarkForestBlocks.MOON_FOUNTAIN_CORE.defaultBlockState(),3);MoonFountainCoreBlockEntity core=requireCore(level,corePos);
            for(BlockPos offset:OFFSETS)level.setBlock(corePos.offset(offset),Blocks.AMETHYST_CLUSTER.defaultBlockState(),3);BlockPos protectedSocket=corePos.offset(OFFSETS.get(0));level.setBlock(protectedSocket,Blocks.DIAMOND_BLOCK.defaultBlockState(),3);core.backfillPedestals(level);check(level.getBlockState(protectedSocket).is(Blocks.DIAMOND_BLOCK),"backfill overwrote a non-placeholder block");level.setBlock(protectedSocket,Blocks.AMETHYST_CLUSTER.defaultBlockState(),3);core.backfillPedestals(level);check(allPedestals(level,corePos,false),"legacy placeholder backfill failed");
            BlockPos interactionPos=corePos.offset(OFFSETS.get(0));player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,new ItemStack(DarkForestItems.ROOTBOUND_EYE));DarkForestBlocks.ROOTBOUND_PEDESTAL.use(level.getBlockState(interactionPos),level,interactionPos,player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(interactionPos),Direction.UP,interactionPos,false));check(player.getMainHandItem().isEmpty()&&level.getBlockState(interactionPos).getValue(RootboundPedestalBlock.CHARGED),"pedestal did not consume exactly one Eye");player.setShiftKeyDown(true);DarkForestBlocks.ROOTBOUND_PEDESTAL.use(level.getBlockState(interactionPos),level,interactionPos,player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(interactionPos),Direction.UP,interactionPos,false));player.setShiftKeyDown(false);check(!level.getBlockState(interactionPos).getValue(RootboundPedestalBlock.CHARGED)&&player.getInventory().countItem(DarkForestItems.ROOTBOUND_EYE)==1,"pedestal retrieval failed");player.getInventory().clearContent();
            core.markDefeated();ItemStack missingHeart=new ItemStack(DarkForestItems.MOONFLOWER_HEART);check(core.tryStartRitual(player,missingHeart)==MoonFountainCoreBlockEntity.RitualStart.MISSING_EYES&&missingHeart.getCount()==1,"missing-eyes path consumed the Heart");chargeAll(level,corePos);
            MossboundEndermanEntity blocker=createBoss(level,corePos);bindBoss(core,blocker);ItemStack blockedHeart=new ItemStack(DarkForestItems.MOONFLOWER_HEART);check(core.tryStartRitual(player,blockedHeart)==MoonFountainCoreBlockEntity.RitualStart.BOSS_ALIVE&&blockedHeart.getCount()==1&&allPedestals(level,corePos,true),"living-boss rejection was not atomic");blocker.discard();core.markDefeated();
            ItemStack successHeart=new ItemStack(DarkForestItems.MOONFLOWER_HEART);check(core.tryStartRitual(player,successHeart)==MoonFountainCoreBlockEntity.RitualStart.STARTED&&successHeart.isEmpty()&&allPedestals(level,corePos,false),"successful ritual did not consume exactly 4+1 offerings");ItemStack secondHeart=new ItemStack(DarkForestItems.MOONFLOWER_HEART);check(core.tryStartRitual(player,secondHeart)==MoonFountainCoreBlockEntity.RitualStart.ALREADY_RUNNING&&secondHeart.getCount()==1,"active ritual accepted a duplicate activation");
            CompoundTag saved=core.saveWithFullMetadata();MoonFountainCoreBlockEntity restored=new MoonFountainCoreBlockEntity(corePos,DarkForestBlocks.MOON_FOUNTAIN_CORE.defaultBlockState());restored.load(saved);check(restored.isDefeated()&&restored.getRitualTicks()==1,"active ritual did not survive NBT round-trip");
            for(int i=0;i<MoonFountainCoreBlockEntity.RITUAL_SPAWN_TICK;i++)MoonFountainCoreBlockEntity.serverTick(level,corePos,level.getBlockState(corePos),core);check(!core.isDefeated()&&core.getRitualTicks()>MoonFountainCoreBlockEntity.RITUAL_SPAWN_TICK,"ritual did not reach its spawn state");
            context=new Context(level,corePos,arena,player,core);phase=2;
        }catch(Throwable failure){cleanup(new Context(level,corePos,arena,player,null));throw failure;}
    }

    private static void finish(){
        Context c=context;
        try{
            List<MossboundEndermanEntity> spawned=c.level.getEntitiesOfClass(MossboundEndermanEntity.class,c.arena,MossboundEndermanEntity::isAlive);check(spawned.size()==1&&!spawned.get(0).isAwakened()&&!c.core.isDefeated(),"ritual did not create exactly one dormant boss and reset defeat state");
            var advancement=c.level.getServer().getAdvancements().getAdvancement(DarkForestLine.id("chronicles/return_last_bloom"));check(advancement!=null,"resurrection advancement is missing");AdvancementProgress progress=new AdvancementProgress();progress.update(advancement.getCriteria(),advancement.getRequirements());check(progress.grantProgress("resurrected")&&progress.isDone(),"resurrection advancement state could not be awarded");
            for(int i=0;i<MoonFountainCoreBlockEntity.RITUAL_DURATION+2&&c.core.getRitualTicks()>0;i++)MoonFountainCoreBlockEntity.serverTick(c.level,c.corePos,c.level.getBlockState(c.corePos),c.core);check(c.core.getRitualTicks()==0,"completed ritual timer did not settle");
            spawned.forEach(MossboundEndermanEntity::discard);c.core.markDefeated();chargeAll(c.level,c.corePos);ItemStack refundHeart=new ItemStack(DarkForestItems.MOONFLOWER_HEART);check(c.core.tryStartRitual(c.player,refundHeart)==MoonFountainCoreBlockEntity.RitualStart.STARTED&&refundHeart.isEmpty(),"refund scenario could not start");MossboundEndermanEntity lateBlocker=createBoss(c.level,c.corePos);bindBoss(c.core,lateBlocker);for(int i=0;i<MoonFountainCoreBlockEntity.RITUAL_SPAWN_TICK;i++)MoonFountainCoreBlockEntity.serverTick(c.level,c.corePos,c.level.getBlockState(c.corePos),c.core);check(c.core.getRitualTicks()==0&&allPedestals(c.level,c.corePos,true),"late duplicate did not restore the four Eyes");phase=3;
        }catch(Throwable failure){cleanup(c);throw failure;}
    }

    private static void verifyRefund(){
        Context c=context;
        try{long refunds=c.level.getEntitiesOfClass(ItemEntity.class,c.arena,item->item.getItem().is(DarkForestItems.MOONFLOWER_HEART)).stream().mapToInt(item->item.getItem().getCount()).sum();check(refunds==1,"late duplicate did not refund one Heart exactly once");OpusVsExe.LOGGER.info("Task 41 QA PASS: safe legacy backfill, pedestal insert/retrieve, atomic failures, exact 4+1 consumption, NBT resume, one dormant boss, advancement, timer settlement and exact late-conflict refund");}
        finally{cleanup(c);context=null;}
    }

    private static void cleanup(Context c){c.level.getEntitiesOfClass(MossboundEndermanEntity.class,c.arena).forEach(MossboundEndermanEntity::discard);c.level.getEntitiesOfClass(ItemEntity.class,c.arena,item->item.getItem().is(DarkForestItems.MOONFLOWER_HEART)||item.getItem().is(DarkForestItems.ROOTBOUND_EYE)).forEach(ItemEntity::discard);c.player.getInventory().clearContent();c.player.setShiftKeyDown(false);for(BlockPos pos:BlockPos.betweenClosed(c.corePos.offset(-12,-1,-12),c.corePos.offset(12,2,12)))if(c.level.getBlockState(pos).is(Blocks.OBSIDIAN)||c.level.getBlockState(pos).is(Blocks.DIAMOND_BLOCK)||c.level.getBlockState(pos).is(Blocks.AMETHYST_CLUSTER)||c.level.getBlockState(pos).is(DarkForestBlocks.MOON_FOUNTAIN_CORE)||c.level.getBlockState(pos).is(DarkForestBlocks.ROOTBOUND_PEDESTAL))c.level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);}
    private static void chargeAll(ServerLevel level,BlockPos core){for(BlockPos offset:OFFSETS)level.setBlock(core.offset(offset),DarkForestBlocks.ROOTBOUND_PEDESTAL.defaultBlockState().setValue(RootboundPedestalBlock.CHARGED,true),3);}
    private static boolean allPedestals(ServerLevel level,BlockPos core,boolean charged){for(BlockPos offset:OFFSETS){var state=level.getBlockState(core.offset(offset));if(!state.is(DarkForestBlocks.ROOTBOUND_PEDESTAL)||state.getValue(RootboundPedestalBlock.CHARGED)!=charged)return false;}return true;}
    private static MossboundEndermanEntity createBoss(ServerLevel level,BlockPos core){MossboundEndermanEntity boss=DarkForestEntities.MOSSBOUND_ENDERMAN.create(level);check(boss!=null,"boss factory returned null");boss.setPos(core.getX()+.5,core.getY()+1,core.getZ()+6.5);boss.setArenaAnchor(core);boss.setPersistenceRequired();check(level.addFreshEntity(boss),"boss could not be added");return boss;}
    private static void bindBoss(MoonFountainCoreBlockEntity core,MossboundEndermanEntity boss){CompoundTag tag=core.saveWithFullMetadata();tag.putUUID("Boss",boss.getUUID());core.load(tag);}
    private static MoonFountainCoreBlockEntity requireCore(ServerLevel level,BlockPos pos){if(level.getBlockEntity(pos) instanceof MoonFountainCoreBlockEntity core)return core;throw new IllegalStateException("Task 41 QA: core block entity missing");}
    private static BlockPos findSite(ServerLevel level){BlockPos spawn=level.getSharedSpawnPos();return new BlockPos(spawn.getX()-960,Math.min(level.getMaxBuildHeight()-20,Math.max(level.getMinBuildHeight()+20,spawn.getY()+120)),spawn.getZ()+960);}
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 41 QA: "+message);}
    private record Context(ServerLevel level,BlockPos corePos,AABB arena,FakePlayer player,MoonFountainCoreBlockEntity core){ }
}
