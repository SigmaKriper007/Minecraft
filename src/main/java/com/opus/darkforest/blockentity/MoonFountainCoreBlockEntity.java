package com.opus.darkforest.blockentity;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.block.RootboundPedestalBlock;
import com.opus.darkforest.entity.MossboundEndermanEntity;
import com.opus.darkforest.registry.DarkForestBlockEntities;
import com.opus.darkforest.registry.DarkForestBlocks;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.registry.DarkForestItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public final class MoonFountainCoreBlockEntity extends BlockEntity {
    public enum RitualStart { STARTED, UNDEFEATED, ALREADY_RUNNING, WRONG_ITEM, MISSING_EYES, BOSS_ALIVE }
    public static final int RITUAL_SPAWN_TICK=72;
    public static final int RITUAL_DURATION=100;
    private static final List<BlockPos> PEDESTAL_OFFSETS=List.of(new BlockPos(0,1,-7),new BlockPos(7,1,0),new BlockPos(0,1,7),new BlockPos(-7,1,0));
    private static final DustParticleOptions MOSS=new DustParticleOptions(new Vector3f(.34F,.49F,.19F),.9F);
    private static final DustParticleOptions VIOLET=new DustParticleOptions(new Vector3f(.48F,.2F,.62F),1F);
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.35F,.9F,.92F),1.1F);
    private boolean defeated;
    private UUID bossId;
    private int ritualTicks;
    private UUID ritualPlayer;

    public MoonFountainCoreBlockEntity(BlockPos pos,BlockState state){super(DarkForestBlockEntities.MOON_FOUNTAIN_CORE,pos,state);}

    public static void serverTick(net.minecraft.world.level.Level level,BlockPos pos,BlockState state,MoonFountainCoreBlockEntity core){
        if(!(level instanceof ServerLevel server))return;
        if(server.getGameTime()%20==0)core.backfillPedestals(server);
        if(core.ritualTicks>0){core.tickRitual(server);return;}
        if(core.defeated||server.getGameTime()%20!=0||core.hasLivingBoss(server))return;
        Player nearby=server.getNearestPlayer(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,32,false);if(nearby!=null)core.spawnBoss(server);
    }

    public RitualStart tryStartRitual(Player player,ItemStack held){
        if(!(level instanceof ServerLevel server))return RitualStart.WRONG_ITEM;
        RitualStart result=!defeated?RitualStart.UNDEFEATED:ritualTicks>0?RitualStart.ALREADY_RUNNING:!held.is(DarkForestItems.MOONFLOWER_HEART)?RitualStart.WRONG_ITEM:hasLivingBoss(server)?RitualStart.BOSS_ALIVE:hasAllPedestals(server)?RitualStart.STARTED:RitualStart.MISSING_EYES;
        if(result!=RitualStart.STARTED){player.displayClientMessage(Component.translatable(messageKey(result)),true);server.playSound(null,worldPosition,SoundEvents.BEACON_DEACTIVATE,SoundSource.BLOCKS,.8F,.62F);return result;}
        for(BlockPos pos:pedestalPositions())server.setBlock(pos,server.getBlockState(pos).setValue(RootboundPedestalBlock.CHARGED,false),3);
        held.shrink(1);ritualTicks=1;ritualPlayer=player.getUUID();bossId=null;setChanged();
        server.playSound(null,worldPosition,SoundEvents.SCULK_CATALYST_BLOOM,SoundSource.BLOCKS,1.6F,.55F);server.sendParticles(VIOLET,worldPosition.getX()+.5,worldPosition.getY()+1.2,worldPosition.getZ()+.5,28,.7,.65,.7,.035);player.displayClientMessage(Component.translatable("message.opusvsexe.moon_ritual_started"),true);return result;
    }

    private void tickRitual(ServerLevel server){
        int tick=ritualTicks;Vec3 center=Vec3.atCenterOf(worldPosition).add(0,.8,0);
        if(tick==24||tick==48)server.playSound(null,worldPosition,SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.BLOCKS,1.4F,tick==24?.7F:1F);
        if(tick<48&&(tick&1)==0){double progress=tick/48D;for(BlockPos pos:pedestalPositions()){Vec3 source=Vec3.atCenterOf(pos).add(0,.35,0);Vec3 point=source.lerp(center,progress);server.sendParticles((tick/2&1)==0?MOSS:VIOLET,point.x,point.y,point.z,2,.08,.08,.08,.012);}}
        else if(tick<RITUAL_SPAWN_TICK&&(tick&1)==0){double radius=3.1-(tick-48)*.105;for(int i=0;i<8;i++){double angle=i*Math.PI/4+tick*.15;server.sendParticles(i%3==0?VIOLET:CYAN,center.x+Math.cos(angle)*radius,center.y+.25+(tick-48)*.045,center.z+Math.sin(angle)*radius,1,0,0,0,0);}}
        if(tick==RITUAL_SPAWN_TICK){
            if(hasLivingBoss(server)||createBoss(server)==null){refund(server);return;}
            defeated=false;awardRitualAdvancement(server);server.playSound(null,worldPosition,SoundEvents.TOTEM_USE,SoundSource.BLOCKS,1.8F,.55F);server.sendParticles(ParticleTypes.FLASH,center.x,center.y+1,center.z,1,0,0,0,0);server.sendParticles(CYAN,center.x,center.y+.8,center.z,42,1.1,1.2,1.1,.075);server.sendParticles(ParticleTypes.REVERSE_PORTAL,center.x,center.y+.7,center.z,36,1.3,1,1.3,.08);for(Player player:server.players())if(player.distanceToSqr(center)<48*48)player.displayClientMessage(Component.translatable("message.opusvsexe.moon_ritual_complete"),true);setChanged();
        }
        if(tick>RITUAL_SPAWN_TICK&&(tick&3)==0)server.sendParticles((tick&4)==0?MOSS:CYAN,center.x,center.y+.5,center.z,4,.65,.5,.65,.02);
        ritualTicks++;if(ritualTicks>RITUAL_DURATION){ritualTicks=0;ritualPlayer=null;setChanged();}
    }

    public MossboundEndermanEntity spawnBoss(ServerLevel server){if(defeated||hasLivingBoss(server))return null;return createBoss(server);}
    private MossboundEndermanEntity createBoss(ServerLevel server){MossboundEndermanEntity boss=DarkForestEntities.MOSSBOUND_ENDERMAN.create(server);if(boss==null)return null;BlockPos spawn=findSpawn(server);boss.setPos(spawn.getX()+.5,spawn.getY(),spawn.getZ()+.5);boss.setArenaAnchor(worldPosition);boss.setPersistenceRequired();if(!server.addFreshEntity(boss))return null;bossId=boss.getUUID();setChanged();return boss;}
    private BlockPos findSpawn(ServerLevel server){BlockPos preferred=worldPosition.offset(0,1,6);for(int radius=0;radius<=6;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++)for(int dy=3;dy>=-3;dy--){BlockPos feet=preferred.offset(dx,dy,dz);boolean clear=true;for(int y=0;y<=5;y++)clear&=server.getBlockState(feet.above(y)).getCollisionShape(server,feet.above(y)).isEmpty();if(clear&&server.getBlockState(feet.below()).isFaceSturdy(server,feet.below(),net.minecraft.core.Direction.UP))return feet;}return worldPosition.above();}
    private boolean hasLivingBoss(ServerLevel server){if(bossId!=null){var tracked=server.getEntity(bossId);if(tracked==null)return true;if(tracked instanceof MossboundEndermanEntity boss&&boss.isAlive())return true;bossId=null;setChanged();}var bosses=server.getEntitiesOfClass(MossboundEndermanEntity.class,new AABB(worldPosition).inflate(48,24,48),MossboundEndermanEntity::isAlive);if(bosses.isEmpty())return false;bossId=bosses.get(0).getUUID();setChanged();return true;}
    private boolean hasAllPedestals(ServerLevel server){for(BlockPos pos:pedestalPositions()){BlockState state=server.getBlockState(pos);if(!state.is(DarkForestBlocks.ROOTBOUND_PEDESTAL)||!state.getValue(RootboundPedestalBlock.CHARGED))return false;}return true;}
    void backfillPedestals(ServerLevel server){for(BlockPos pos:pedestalPositions()){BlockState state=server.getBlockState(pos);if(state.isAir()||state.is(Blocks.AMETHYST_CLUSTER))server.setBlock(pos,DarkForestBlocks.ROOTBOUND_PEDESTAL.defaultBlockState(),3);}}
    private List<BlockPos> pedestalPositions(){return PEDESTAL_OFFSETS.stream().map(worldPosition::offset).toList();}
    private void refund(ServerLevel server){for(BlockPos pos:pedestalPositions()){BlockState state=server.getBlockState(pos);if(state.is(DarkForestBlocks.ROOTBOUND_PEDESTAL))server.setBlock(pos,state.setValue(RootboundPedestalBlock.CHARGED,true),3);else server.addFreshEntity(new ItemEntity(server,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,new ItemStack(DarkForestItems.ROOTBOUND_EYE)));}server.addFreshEntity(new ItemEntity(server,worldPosition.getX()+.5,worldPosition.getY()+1.2,worldPosition.getZ()+.5,new ItemStack(DarkForestItems.MOONFLOWER_HEART)));ritualTicks=0;ritualPlayer=null;setChanged();for(Player player:server.players())if(player.distanceToSqr(Vec3.atCenterOf(worldPosition))<48*48)player.displayClientMessage(Component.translatable("message.opusvsexe.moon_ritual_aborted"),true);}
    private void awardRitualAdvancement(ServerLevel server){if(ritualPlayer==null)return;ServerPlayer player=server.getServer().getPlayerList().getPlayer(ritualPlayer);var advancement=server.getServer().getAdvancements().getAdvancement(DarkForestLine.id("chronicles/return_last_bloom"));if(player!=null&&advancement!=null)player.getAdvancements().award(advancement,"resurrected");}
    private static String messageKey(RitualStart result){return switch(result){case UNDEFEATED->"message.opusvsexe.moon_ritual_undefeated";case ALREADY_RUNNING->"message.opusvsexe.moon_ritual_running";case WRONG_ITEM->"message.opusvsexe.moon_ritual_requires_heart";case MISSING_EYES->"message.opusvsexe.moon_ritual_missing_eyes";case BOSS_ALIVE->"message.opusvsexe.moon_ritual_boss_alive";default->"message.opusvsexe.moon_ritual_started";};}

    public void markDefeated(){defeated=true;bossId=null;setChanged();}
    public boolean isDefeated(){return defeated;}
    public int getRitualTicks(){return ritualTicks;}
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);tag.putBoolean("Defeated",defeated);if(bossId!=null)tag.putUUID("Boss",bossId);tag.putInt("RitualTicks",ritualTicks);if(ritualPlayer!=null)tag.putUUID("RitualPlayer",ritualPlayer);}
    @Override public void load(CompoundTag tag){super.load(tag);defeated=tag.getBoolean("Defeated");bossId=tag.hasUUID("Boss")?tag.getUUID("Boss"):null;ritualTicks=tag.getInt("RitualTicks");ritualPlayer=tag.hasUUID("RitualPlayer")?tag.getUUID("RitualPlayer"):null;}
}
