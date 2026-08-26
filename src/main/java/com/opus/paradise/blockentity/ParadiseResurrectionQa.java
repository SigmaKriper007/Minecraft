package com.opus.paradise.blockentity;

import com.opus.OpusVsExe;
import com.opus.paradise.ParadiseLine;
import com.opus.paradise.block.SeraphicReliquaryBlock;
import com.opus.paradise.entity.AngelBoyEntity;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.advancements.AdvancementProgress;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;

/** Development-only end-to-end contract probe; never runs from a packaged production instance. */
public final class ParadiseResurrectionQa {
    private static final List<BlockPos> OFFSETS=List.of(new BlockPos(0,0,-4),new BlockPos(4,0,0),new BlockPos(0,0,4),new BlockPos(-4,0,0));
    private ParadiseResurrectionQa(){ }
    public static void init(){if(com.opus.qa.DevelopmentQa.enabled(36))ServerLifecycleEvents.SERVER_STARTED.register(server->run(server.overworld()));}

    private static void run(ServerLevel level){
        BlockPos daisPos=findEmptyCourt(level);AABB court=new AABB(daisPos).inflate(50,24,50);FakePlayer player=FakePlayer.get(level);
        try{
            level.setBlock(daisPos,ParadiseBlocks.ANGEL_DAIS.defaultBlockState(),3);AngelDaisBlockEntity dais=requireDais(level,daisPos);dais.markDefeated();
            ItemStack missingShard=new ItemStack(ParadiseItems.RUBY_HALO_SHARD);check(dais.tryStartRitual(player,missingShard)==AngelDaisBlockEntity.RitualStart.MISSING_PINIONS&&missingShard.getCount()==1,"missing-pinions path consumed the shard");
            chargeAll(level,daisPos);
            AngelBoyEntity blocker=createAngel(level,daisPos);ItemStack blockedShard=new ItemStack(ParadiseItems.RUBY_HALO_SHARD);check(dais.tryStartRitual(player,blockedShard)==AngelDaisBlockEntity.RitualStart.BOSS_ALIVE&&blockedShard.getCount()==1&&allCharged(level,daisPos),"living-boss rejection was not atomic");blocker.discard();
            ItemStack successShard=new ItemStack(ParadiseItems.RUBY_HALO_SHARD);check(dais.tryStartRitual(player,successShard)==AngelDaisBlockEntity.RitualStart.STARTED&&successShard.isEmpty()&&!allCharged(level,daisPos),"successful ritual did not consume exactly 4+1 offerings");
            CompoundTag saved=dais.saveWithFullMetadata();AngelDaisBlockEntity restored=new AngelDaisBlockEntity(daisPos,ParadiseBlocks.ANGEL_DAIS.defaultBlockState());restored.load(saved);check(restored.isDefeated()&&restored.getRitualTicks()==1,"active ritual did not survive NBT round-trip");
            for(int i=0;i<AngelDaisBlockEntity.RITUAL_SPAWN_TICK;i++)AngelDaisBlockEntity.serverTick(level,daisPos,level.getBlockState(daisPos),dais);
            List<AngelBoyEntity> spawned=level.getEntitiesOfClass(AngelBoyEntity.class,court,AngelBoyEntity::isAlive);check(spawned.size()==1&&!spawned.get(0).isAwakened()&&!dais.isDefeated(),"ritual did not create exactly one dormant boss and reset defeat state");
            var advancement=level.getServer().getAdvancements().getAdvancement(ParadiseLine.id("chronicles/reopen_heavens"));check(advancement!=null,"resurrection advancement is missing");AdvancementProgress progress=new AdvancementProgress();progress.update(advancement.getCriteria(),advancement.getRequirements());check(progress.grantProgress("resurrected")&&progress.isDone(),"resurrection advancement state could not be awarded");
            for(int i=0;i<=AngelDaisBlockEntity.RITUAL_DURATION-AngelDaisBlockEntity.RITUAL_SPAWN_TICK;i++)AngelDaisBlockEntity.serverTick(level,daisPos,level.getBlockState(daisPos),dais);check(dais.getRitualTicks()==0,"completed ritual timer did not settle");
            spawned.forEach(AngelBoyEntity::discard);dais.markDefeated();chargeAll(level,daisPos);ItemStack refundShard=new ItemStack(ParadiseItems.RUBY_HALO_SHARD);check(dais.tryStartRitual(player,refundShard)==AngelDaisBlockEntity.RitualStart.STARTED&&refundShard.isEmpty(),"refund scenario could not start");AngelBoyEntity lateBlocker=createAngel(level,daisPos);
            for(int i=0;i<AngelDaisBlockEntity.RITUAL_SPAWN_TICK;i++)AngelDaisBlockEntity.serverTick(level,daisPos,level.getBlockState(daisPos),dais);
            long refunds=level.getEntitiesOfClass(ItemEntity.class,court,item->item.getItem().is(ParadiseItems.RUBY_HALO_SHARD)).stream().mapToInt(item->item.getItem().getCount()).sum();check(dais.getRitualTicks()==0&&allCharged(level,daisPos)&&refunds==1,"late duplicate did not refund all five offerings exactly once");lateBlocker.discard();
            OpusVsExe.LOGGER.info("Task 36 QA PASS: atomic failures, 4+1 consumption, NBT resume, one dormant boss, advancement, settled timer and exact late-conflict refund");
        }finally{
            level.getEntitiesOfClass(AngelBoyEntity.class,court).forEach(AngelBoyEntity::discard);level.getEntitiesOfClass(ItemEntity.class,court,item->item.getItem().is(ParadiseItems.RUBY_HALO_SHARD)).forEach(ItemEntity::discard);level.setBlock(daisPos,Blocks.AIR.defaultBlockState(),3);for(BlockPos offset:OFFSETS){BlockPos pos=daisPos.offset(offset);level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);level.setBlock(pos.below(),Blocks.AIR.defaultBlockState(),3);}
        }
    }

    private static BlockPos findEmptyCourt(ServerLevel level){BlockPos spawn=level.getSharedSpawnPos();int y=Math.min(level.getMaxBuildHeight()-16,Math.max(level.getMinBuildHeight()+16,spawn.getY()+96));for(int step=0;step<16;step++){BlockPos candidate=new BlockPos(spawn.getX()+320+step*12,y,spawn.getZ()+320);boolean empty=level.isEmptyBlock(candidate);for(BlockPos offset:OFFSETS){BlockPos pos=candidate.offset(offset);empty&=level.isEmptyBlock(pos)&&level.isEmptyBlock(pos.below());}if(empty)return candidate;}throw new IllegalStateException("Task 36 QA: no empty high-altitude court found");}
    private static AngelDaisBlockEntity requireDais(ServerLevel level,BlockPos pos){if(level.getBlockEntity(pos) instanceof AngelDaisBlockEntity dais)return dais;throw new IllegalStateException("Task 36 QA: dais block entity was not created");}
    private static AngelBoyEntity createAngel(ServerLevel level,BlockPos daisPos){AngelBoyEntity angel=ParadiseEntities.ANGEL_BOY.create(level);check(angel!=null,"Angel entity type returned null");angel.setPos(daisPos.getX()+.5,daisPos.getY()+1,daisPos.getZ()+.5);angel.setArenaAnchor(daisPos.above());angel.setPersistenceRequired();check(level.addFreshEntity(angel),"Angel entity could not be added");return angel;}
    private static void chargeAll(ServerLevel level,BlockPos daisPos){for(BlockPos offset:OFFSETS)level.setBlock(daisPos.offset(offset),ParadiseBlocks.SERAPHIC_RELIQUARY.defaultBlockState().setValue(SeraphicReliquaryBlock.CHARGED,true),3);}
    private static boolean allCharged(ServerLevel level,BlockPos daisPos){for(BlockPos offset:OFFSETS){var state=level.getBlockState(daisPos.offset(offset));if(!state.is(ParadiseBlocks.SERAPHIC_RELIQUARY)||!state.getValue(SeraphicReliquaryBlock.CHARGED))return false;}return true;}
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 36 QA: "+message);}
}
