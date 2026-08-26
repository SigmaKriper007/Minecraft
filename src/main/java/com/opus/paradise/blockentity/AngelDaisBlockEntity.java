package com.opus.paradise.blockentity;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.block.SeraphicReliquaryBlock;
import com.opus.paradise.entity.AngelBoyEntity;
import com.opus.paradise.registry.ParadiseBlockEntities;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.registry.ParadiseItems;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public final class AngelDaisBlockEntity extends BlockEntity {
    public enum RitualStart { STARTED, UNDEFEATED, ALREADY_RUNNING, WRONG_ITEM, MISSING_PINIONS, BOSS_ALIVE }
    public static final int RITUAL_SPAWN_TICK=60;
    public static final int RITUAL_DURATION=80;
    private static final List<BlockPos> RELIQUARY_OFFSETS=List.of(new BlockPos(0,0,-4),new BlockPos(4,0,0),new BlockPos(0,0,4),new BlockPos(-4,0,0));
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.22F,.94F,1F),.85F);
    private static final DustParticleOptions GOLD=new DustParticleOptions(new Vector3f(1F,.76F,.22F),1F);
    private static final DustParticleOptions RUBY=new DustParticleOptions(new Vector3f(.9F,.08F,.16F),1.25F);
    private boolean defeated;
    private int ritualTicks;
    private UUID ritualPlayer;

    public AngelDaisBlockEntity(BlockPos pos,BlockState state){super(ParadiseBlockEntities.ANGEL_DAIS,pos,state);}

    public static void serverTick(net.minecraft.world.level.Level level,BlockPos pos,BlockState state,AngelDaisBlockEntity dais){
        if(!(level instanceof ServerLevel server))return;
        if(server.getGameTime()%20==0)dais.backfillReliquaries(server);
        if(dais.ritualTicks>0){dais.tickRitual(server);return;}
        if(dais.defeated||server.getGameTime()%20!=0||dais.hasLivingAngel(server))return;
        AABB court=new AABB(pos).inflate(16,8,16);Player player=server.getNearestPlayer(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,13,false);
        if(player!=null&&court.contains(player.position()))dais.spawnAngel(server);
    }

    public RitualStart tryStartRitual(Player player,ItemStack held){
        if(!(level instanceof ServerLevel server))return RitualStart.WRONG_ITEM;
        RitualStart result=!defeated?RitualStart.UNDEFEATED:ritualTicks>0?RitualStart.ALREADY_RUNNING:!held.is(ParadiseItems.RUBY_HALO_SHARD)?RitualStart.WRONG_ITEM:hasLivingAngel(server)?RitualStart.BOSS_ALIVE:hasAllReliquaries(server)?RitualStart.STARTED:RitualStart.MISSING_PINIONS;
        if(result!=RitualStart.STARTED){player.displayClientMessage(Component.translatable(messageKey(result)),true);server.playSound(null,worldPosition,SoundEvents.BEACON_DEACTIVATE,SoundSource.BLOCKS,.75F,.7F);return result;}
        for(BlockPos rel:reliquaryPositions())server.setBlock(rel,server.getBlockState(rel).setValue(SeraphicReliquaryBlock.CHARGED,false),3);
        held.shrink(1);ritualTicks=1;ritualPlayer=player.getUUID();setChanged();
        server.playSound(null,worldPosition,SoundEvents.BEACON_ACTIVATE,SoundSource.BLOCKS,1.5F,.72F);server.sendParticles(CYAN,worldPosition.getX()+.5,worldPosition.getY()+1.1,worldPosition.getZ()+.5,28,.8,.35,.8,.035);player.displayClientMessage(Component.translatable("message.opusvsexe.angel_ritual_started"),true);
        return result;
    }

    private void tickRitual(ServerLevel server){
        int tick=ritualTicks;Vec3 center=Vec3.atCenterOf(worldPosition).add(0,.8,0);
        if(tick==20||tick==40)server.playSound(null,worldPosition,SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.BLOCKS,1.4F,tick==20?.85F:1.15F);
        if(tick<40&&(tick&1)==0){double progress=tick/40D;for(BlockPos pos:reliquaryPositions()){Vec3 source=Vec3.atCenterOf(pos).add(0,.65,0);Vec3 point=source.lerp(center,progress);server.sendParticles((tick/2&1)==0?CYAN:GOLD,point.x,point.y,point.z,2,.08,.08,.08,.015);}}
        else if(tick<RITUAL_SPAWN_TICK&&(tick&1)==0){double radius=3.4-(tick-40)*.13;for(int i=0;i<8;i++){double angle=i*Math.PI/4+tick*.12;server.sendParticles(i%3==0?GOLD:CYAN,center.x+Math.cos(angle)*radius,center.y+.3+(tick-40)*.035,center.z+Math.sin(angle)*radius,1,0,0,0,0);}}
        if(tick==RITUAL_SPAWN_TICK){
            if(hasLivingAngel(server)||spawnAngel(server)==null){refund(server);return;}
            defeated=false;awardRitualAdvancement(server);server.playSound(null,worldPosition,SoundEvents.TOTEM_USE,SoundSource.BLOCKS,2F,.82F);server.sendParticles(ParticleTypes.FLASH,center.x,center.y+.8,center.z,1,0,0,0,0);server.sendParticles(RUBY,center.x,center.y+.5,center.z,36,1.1,.7,1.1,.08);server.sendParticles(ParticleTypes.END_ROD,center.x,center.y+.5,center.z,48,1.4,1,1.4,.11);for(Player player:server.players())if(player.distanceToSqr(center)<48*48)player.displayClientMessage(Component.translatable("message.opusvsexe.angel_ritual_complete"),true);setChanged();
        }
        if(tick>RITUAL_SPAWN_TICK&&(tick&3)==0)server.sendParticles(GOLD,center.x,center.y+.5,center.z,4,.7,.5,.7,.02);
        ritualTicks++;
        if(ritualTicks>RITUAL_DURATION){ritualTicks=0;ritualPlayer=null;setChanged();}
    }

    private AngelBoyEntity spawnAngel(ServerLevel server){
        AngelBoyEntity angel=ParadiseEntities.ANGEL_BOY.create(server);if(angel==null)return null;
        angel.setPos(worldPosition.getX()+.5,worldPosition.getY()+1,worldPosition.getZ()+.5);angel.setArenaAnchor(worldPosition.above());angel.setPersistenceRequired();return server.addFreshEntity(angel)?angel:null;
    }
    private boolean hasLivingAngel(ServerLevel server){return !server.getEntitiesOfClass(AngelBoyEntity.class,new AABB(worldPosition).inflate(48,24,48),AngelBoyEntity::isAlive).isEmpty();}
    private boolean hasAllReliquaries(ServerLevel server){for(BlockPos pos:reliquaryPositions()){BlockState state=server.getBlockState(pos);if(!state.is(ParadiseBlocks.SERAPHIC_RELIQUARY)||!state.getValue(SeraphicReliquaryBlock.CHARGED))return false;}return true;}
    private void backfillReliquaries(ServerLevel server){for(BlockPos pos:reliquaryPositions())if(server.getBlockState(pos).isAir()){if(server.getBlockState(pos.below()).isAir())server.setBlock(pos.below(),Blocks.GOLD_BLOCK.defaultBlockState(),3);server.setBlock(pos,ParadiseBlocks.SERAPHIC_RELIQUARY.defaultBlockState(),3);}}
    private List<BlockPos> reliquaryPositions(){return RELIQUARY_OFFSETS.stream().map(worldPosition::offset).toList();}
    private void refund(ServerLevel server){for(BlockPos pos:reliquaryPositions())if(server.getBlockState(pos).is(ParadiseBlocks.SERAPHIC_RELIQUARY))server.setBlock(pos,server.getBlockState(pos).setValue(SeraphicReliquaryBlock.CHARGED,true),3);server.addFreshEntity(new ItemEntity(server,worldPosition.getX()+.5,worldPosition.getY()+1.2,worldPosition.getZ()+.5,new ItemStack(ParadiseItems.RUBY_HALO_SHARD)));ritualTicks=0;ritualPlayer=null;setChanged();for(Player player:server.players())if(player.distanceToSqr(Vec3.atCenterOf(worldPosition))<48*48)player.displayClientMessage(Component.translatable("message.opusvsexe.angel_ritual_aborted"),true);}
    private void awardRitualAdvancement(ServerLevel server){if(ritualPlayer==null)return;ServerPlayer player=server.getServer().getPlayerList().getPlayer(ritualPlayer);var advancement=server.getServer().getAdvancements().getAdvancement(ParadiseLine.id("chronicles/reopen_heavens"));if(player!=null&&advancement!=null)player.getAdvancements().award(advancement,"resurrected");}
    private static String messageKey(RitualStart result){return switch(result){case UNDEFEATED->"message.opusvsexe.angel_ritual_undefeated";case ALREADY_RUNNING->"message.opusvsexe.angel_ritual_running";case WRONG_ITEM->"message.opusvsexe.angel_ritual_requires_shard";case MISSING_PINIONS->"message.opusvsexe.angel_ritual_missing_pinions";case BOSS_ALIVE->"message.opusvsexe.angel_ritual_boss_alive";default->"message.opusvsexe.angel_ritual_started";};}
    public boolean isDefeated(){return defeated;}public int getRitualTicks(){return ritualTicks;}public void markDefeated(){defeated=true;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);tag.putBoolean("Defeated",defeated);tag.putInt("RitualTicks",ritualTicks);if(ritualPlayer!=null)tag.putUUID("RitualPlayer",ritualPlayer);}
    @Override public void load(CompoundTag tag){super.load(tag);defeated=tag.getBoolean("Defeated");ritualTicks=tag.getInt("RitualTicks");ritualPlayer=tag.hasUUID("RitualPlayer")?tag.getUUID("RitualPlayer"):null;}
}
