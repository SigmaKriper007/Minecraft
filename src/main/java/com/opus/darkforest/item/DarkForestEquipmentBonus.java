package com.opus.darkforest.item;

import com.opus.darkforest.registry.DarkForestItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public final class DarkForestEquipmentBonus {
    public static final int TELEPORT_COOLDOWN=50;
    private static final DustParticleOptions VIOLET=new DustParticleOptions(new Vector3f(.38F,.16F,.48F),1F);
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.35F,.9F,.92F),1F);
    private static final List<MobEffect> SET_EFFECTS=List.of(MobEffects.NIGHT_VISION,MobEffects.MOVEMENT_SPEED,MobEffects.DAMAGE_BOOST,MobEffects.DOLPHINS_GRACE);
    private DarkForestEquipmentBonus(){ }

    public static boolean isFullBriarweave(Player player){return player!=null&&player.getItemBySlot(EquipmentSlot.HEAD).is(DarkForestItems.BRIARWEAVE_HELMET)&&player.getItemBySlot(EquipmentSlot.CHEST).is(DarkForestItems.BRIARWEAVE_CHESTPLATE)&&player.getItemBySlot(EquipmentSlot.LEGS).is(DarkForestItems.BRIARWEAVE_LEGGINGS)&&player.getItemBySlot(EquipmentSlot.FEET).is(DarkForestItems.BRIARWEAVE_BOOTS);}
    public static boolean isFullVestments(Player player){return player!=null&&player.getItemBySlot(EquipmentSlot.HEAD).is(DarkForestItems.DARK_FOREST_HELMET)&&player.getItemBySlot(EquipmentSlot.CHEST).is(DarkForestItems.DARK_FOREST_CHESTPLATE)&&player.getItemBySlot(EquipmentSlot.LEGS).is(DarkForestItems.DARK_FOREST_LEGGINGS)&&player.getItemBySlot(EquipmentSlot.FEET).is(DarkForestItems.DARK_FOREST_BOOTS);}
    public static boolean isDarkTool(net.minecraft.world.item.ItemStack stack){return stack.getItem() instanceof DarkForestTools.ToolMarker;}

    public static void init(){ServerTickEvents.END_WORLD_TICK.register(world->{for(ServerPlayer player:world.players())tick(player);});}
    static void tick(ServerPlayer player){
        DarkForestPlayerState state=(DarkForestPlayerState)player;boolean alive=player.isAlive();boolean vestments=alive&&isFullVestments(player);boolean briar=alive&&isFullBriarweave(player);
        if(briar)player.removeEffect(MobEffects.POISON);
        if(vestments){apply(player,MobEffects.NIGHT_VISION,0);apply(player,MobEffects.MOVEMENT_SPEED,1);apply(player,MobEffects.DAMAGE_BOOST,0);apply(player,MobEffects.DOLPHINS_GRACE,1);state.opusvsexe$setDarkSetEffects(true);}
        else if(state.opusvsexe$hasDarkSetEffects()){for(MobEffect effect:SET_EFFECTS)player.removeEffect(effect);state.opusvsexe$setDarkSetEffects(false);}
        int desired=alive&&(isDarkTool(player.getMainHandItem())||isDarkTool(player.getOffhandItem()))?2:vestments?0:-1;updateHaste(player,state,desired);
    }
    private static void apply(ServerPlayer player,MobEffect effect,int amplifier){MobEffectInstance current=player.getEffect(effect);if(current==null||current.getAmplifier()<amplifier||current.getDuration()<210)player.addEffect(new MobEffectInstance(effect,240,amplifier,false,false,true));}
    private static void updateHaste(ServerPlayer player,DarkForestPlayerState state,int desired){int previous=state.opusvsexe$getDarkHasteAmplifier();MobEffectInstance current=player.getEffect(MobEffects.DIG_SPEED);if(previous!=desired&&current!=null&&current.getAmplifier()==previous)player.removeEffect(MobEffects.DIG_SPEED);if(desired>=0){current=player.getEffect(MobEffects.DIG_SPEED);if(current==null||current.getAmplifier()<desired||current.getDuration()<210)player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,240,desired,false,false,true));}state.opusvsexe$setDarkHasteAmplifier(desired);}

    public static boolean tryAimedTeleport(ServerPlayer player){
        if(!player.isAlive()||player.isSpectator()||!isFullVestments(player))return false;DarkForestPlayerState state=(DarkForestPlayerState)player;long now=player.serverLevel().getGameTime(),ready=state.opusvsexe$getDarkTeleportReady();
        if(now<ready){player.displayClientMessage(Component.translatable("message.opusvsexe.dark_teleport_cooldown",String.format(java.util.Locale.ROOT,"%.1f",(ready-now)/20D)),true);return false;}
        Vec3 destination=findSafeDestination(player);if(destination==null){player.displayClientMessage(Component.translatable("message.opusvsexe.dark_teleport_blocked"),true);return false;}
        ServerLevel level=player.serverLevel();Vec3 origin=player.position();level.sendParticles(VIOLET,origin.x,origin.y+1,origin.z,22,.45,.8,.45,.045);level.playSound(null,origin.x,origin.y,origin.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.PLAYERS,1F,.72F);
        player.teleportTo(destination.x,destination.y,destination.z);player.fallDistance=0;player.setDeltaMovement(Vec3.ZERO);level.sendParticles(CYAN,destination.x,destination.y+1,destination.z,28,.5,.9,.5,.05);level.playSound(null,destination.x,destination.y,destination.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.PLAYERS,1F,1.12F);
        state.opusvsexe$setDarkTeleportReady(now+TELEPORT_COOLDOWN);player.getCooldowns().addCooldown(DarkForestItems.DARK_FOREST_CHESTPLATE,TELEPORT_COOLDOWN);player.displayClientMessage(Component.translatable("message.opusvsexe.dark_teleport_cast"),true);return true;
    }
    static Vec3 findSafeDestination(ServerPlayer player){
        ServerLevel level=player.serverLevel();Vec3 start=player.getEyePosition(),look=player.getLookAngle().normalize(),end=start.add(look.scale(32));HitResult hit=level.clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));Vec3 aim=(hit.getType()==HitResult.Type.MISS?end:hit.getLocation().subtract(look.scale(.55)));
        for(int step=0;step<=64;step++){Vec3 point=aim.subtract(look.scale(step*.5));BlockPos base=BlockPos.containing(point);for(int dy=2;dy>=-3;dy--){BlockPos feet=base.offset(0,dy,0);if(!level.getWorldBorder().isWithinBounds(feet)||!level.getBlockState(feet.below()).isFaceSturdy(level,feet.below(),Direction.UP)||!level.getFluidState(feet).isEmpty()||!level.getFluidState(feet.above()).isEmpty())continue;Vec3 destination=new Vec3(feet.getX()+.5,feet.getY(),feet.getZ()+.5);AABB moved=player.getBoundingBox().move(destination.subtract(player.position()));if(level.noCollision(player,moved))return destination;}}
        return null;
    }
}
