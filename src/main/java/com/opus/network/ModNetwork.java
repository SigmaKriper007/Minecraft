package com.opus.network;
import com.opus.OpusVsExe;
import com.opus.item.CombatEffects;
import com.opus.item.OpusArmorBonus;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;
public final class ModNetwork {
 public static final ResourceLocation EXO_ABILITY=OpusVsExe.id("exo_ability");
 public static final ResourceLocation EXO_INVENTORY=OpusVsExe.id("exo_inventory");
 public static final ResourceLocation ARMOR_SHOCKWAVE=OpusVsExe.id("armor_shockwave");
 public static final ResourceLocation EXO_ATTACK=OpusVsExe.id("exo_attack");
 private static final double EXO_ATTACK_REACH=12.0;
 private ModNetwork(){}
 public static void init(){
  ServerPlayNetworking.registerGlobalReceiver(EXO_ABILITY,(server,player,handler,buf,response)->{int action=buf.readVarInt(); server.execute(()->{if(player.getVehicle() instanceof ExosuitEntity exo && exo.getControllingPassenger()==player) exo.performAbility(action);});});
  ServerPlayNetworking.registerGlobalReceiver(EXO_INVENTORY,(server,player,handler,buf,response)->server.execute(()->{if(player.getVehicle() instanceof ExosuitEntity exo) player.openMenu(exo.getMenuProvider());}));
  ServerPlayNetworking.registerGlobalReceiver(ARMOR_SHOCKWAVE,(server,player,handler,buf,response)->server.execute(()->{if(OpusArmorBonus.isFullOpusSuit(player) && OpusArmorBonus.tryTriggerShockwave(player)) CombatEffects.shockwave(player,7.0,12.0f,3.0,true);}));
  ServerPlayNetworking.registerGlobalReceiver(EXO_ATTACK,(server,player,handler,buf,response)->server.execute(()->{
   if(player.getVehicle() instanceof ExosuitEntity exo && exo.getControllingPassenger()==player){
    LivingEntity target=findRiderTarget(player,exo);
    if(target!=null) exo.attackWithRider(target);
   }
  }));
 }
 private static LivingEntity findRiderTarget(Player player, ExosuitEntity exo){
  Vec3 eye=exo.getEyePosition(1.0F);
  Vec3 view=player.getViewVector(1.0F);
  Vec3 end=eye.add(view.scale(EXO_ATTACK_REACH));
  AABB box=exo.getBoundingBox().expandTowards(view.scale(EXO_ATTACK_REACH)).inflate(1.0);
  EntityHitResult hit=ProjectileUtil.getEntityHitResult(player,eye,end,box,e->isValidAttackTarget(e,player,exo),EXO_ATTACK_REACH*EXO_ATTACK_REACH);
  return hit!=null && hit.getEntity() instanceof LivingEntity living?living:null;
 }
 private static boolean isValidAttackTarget(Entity e, Player player, ExosuitEntity exo){
  if(!(e instanceof LivingEntity living)||living==player||living==exo||living==player.getVehicle()||living.isAlliedTo(player))return false;
  if(living instanceof Player p)return !p.isSpectator();
  return true;
 }
}
