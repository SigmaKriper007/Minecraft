package com.opus.paradise.item;

import com.opus.paradise.entity.HurricaneEntity;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.registry.ParadiseItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.joml.Vector3f;

import java.util.UUID;

public final class ParthenonArmorBonus {
    public static final int HURRICANE_COOLDOWN=300;
    private static final UUID HEALTH_ID=UUID.fromString("b03b61b7-b153-4f08-934f-ec820a6fa035");
    private static final AttributeModifier HEALTH=new AttributeModifier(HEALTH_ID,"Parthenon Regalia vitality",10D,AttributeModifier.Operation.ADDITION);
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.22F,.94F,1F),1.1F);
    private ParthenonArmorBonus(){ }

    public static boolean isFullSet(Player player){return player!=null&&player.getItemBySlot(EquipmentSlot.HEAD).is(ParadiseItems.PARTHENON_HELMET)&&player.getItemBySlot(EquipmentSlot.CHEST).is(ParadiseItems.PARTHENON_CHESTPLATE)&&player.getItemBySlot(EquipmentSlot.LEGS).is(ParadiseItems.PARTHENON_LEGGINGS)&&player.getItemBySlot(EquipmentSlot.FEET).is(ParadiseItems.PARTHENON_BOOTS);}
    public static boolean hasChest(Player player){return player!=null&&player.getItemBySlot(EquipmentSlot.CHEST).is(ParadiseItems.PARTHENON_CHESTPLATE);}

    public static boolean tryAimedHurricane(ServerPlayer player){
        if(!player.isAlive()||player.isSpectator()||!isFullSet(player))return false;
        ParadisePlayerState state=(ParadisePlayerState)player;long now=player.server.overworld().getGameTime();long ready=state.opusvsexe$getRegaliaHurricaneReady();
        if(now<ready){player.displayClientMessage(Component.translatable("message.opusvsexe.parthenon_hurricane_cooldown",String.format(java.util.Locale.ROOT,"%.1f",(ready-now)/20D)),true);return false;}
        Vec3 start=player.getEyePosition();Vec3 end=start.add(player.getLookAngle().scale(48));HitResult block=player.level().clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
        float max=(float)(block.getType()==HitResult.Type.MISS?48*48:start.distanceToSqr(block.getLocation()));EntityHitResult entity=ProjectileUtil.getEntityHitResult(player.level(),player,start,end,new AABB(start,end).inflate(1),e->e instanceof LivingEntity&&e!=player&&!e.isSpectator(),max);
        Vec3 target=entity!=null?entity.getLocation():block.getType()!=HitResult.Type.MISS?block.getLocation():end;
        HurricaneEntity hurricane=new HurricaneEntity(ParadiseEntities.HURRICANE,player.level());hurricane.setPos(target);hurricane.setCaster(player,null);player.level().addFreshEntity(hurricane);
        state.opusvsexe$setRegaliaHurricaneReady(now+HURRICANE_COOLDOWN);player.getCooldowns().addCooldown(ParadiseItems.PARTHENON_CHESTPLATE,HURRICANE_COOLDOWN);
        player.serverLevel().sendParticles(CYAN,target.x,target.y+.2,target.z,28,1.2,.3,1.2,.05);player.level().playSound(null,target.x,target.y,target.z,SoundEvents.BEACON_ACTIVATE,SoundSource.PLAYERS,1.4F,1.25F);
        player.displayClientMessage(Component.translatable("message.opusvsexe.parthenon_hurricane_cast"),true);return true;
    }

    public static void init(){ServerTickEvents.END_WORLD_TICK.register(world->{for(ServerPlayer player:world.players())tick(player);});}
    static void tick(ServerPlayer player){for(var stack:player.getArmorSlots())ParadiseEquipment.applyIntrinsic(stack);updateHealth(player);updateFlight(player);}
    private static void updateHealth(ServerPlayer player){AttributeInstance max=player.getAttribute(Attributes.MAX_HEALTH);if(max==null)return;boolean full=player.isAlive()&&isFullSet(player);if(full&&max.getModifier(HEALTH_ID)==null)max.addTransientModifier(HEALTH);else if(!full&&max.getModifier(HEALTH_ID)!=null){max.removeModifier(HEALTH_ID);player.setHealth(Math.min(player.getHealth(),player.getMaxHealth()));}}
    private static void updateFlight(ServerPlayer player){ParadisePlayerState state=(ParadisePlayerState)player;boolean chest=player.isAlive()&&hasChest(player);if(chest&&!player.getAbilities().mayfly){player.getAbilities().mayfly=true;state.opusvsexe$setRegaliaFlightGranted(true);player.onUpdateAbilities();}else if(!chest&&state.opusvsexe$isRegaliaFlightGranted()&&!player.isCreative()&&!player.isSpectator()){state.opusvsexe$setRegaliaFlightGranted(false);player.getAbilities().mayfly=false;player.getAbilities().flying=false;player.onUpdateAbilities();}}
}
