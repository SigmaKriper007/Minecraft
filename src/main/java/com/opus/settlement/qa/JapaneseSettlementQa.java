package com.opus.settlement.qa;

import com.opus.OpusVsExe;
import com.opus.registry.TrophyRegistry;
import com.opus.settlement.entity.BlackNinjaEntity;
import com.opus.settlement.entity.JapaneseWarriorEntity;
import com.opus.settlement.entity.SamuraiEntity;
import com.opus.settlement.item.JapaneseKatanaItem;
import com.opus.settlement.registry.SettlementEntities;
import com.opus.settlement.registry.SettlementItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public final class JapaneseSettlementQa {
    private static boolean pending;
    private JapaneseSettlementQa() { }
    public static void init(){
        if(com.opus.qa.DevelopmentQa.enabled(43)){
            ServerLifecycleEvents.SERVER_STARTED.register(server->pending=true);
            ServerTickEvents.END_SERVER_TICK.register(server->{if(pending){pending=false;run(server.overworld());}});
        }
    }
    private static void run(ServerLevel level){
        BlockPos base=level.getSharedSpawnPos().offset(32,80,32);AABB area=new AABB(base).inflate(20);boolean oldLoot=level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        try{
            check(level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(OpusVsExe.id("japanese_settlement"))!=null,"structure registry missing");
            BlackNinjaEntity ninja=require(SettlementEntities.BLACK_NINJA.create(level),"Black Ninja");ninja.setPos(base.getX(),base.getY(),base.getZ());check(level.addFreshEntity(ninja),"Black Ninja add failed");
            SamuraiEntity samurai=require(SettlementEntities.SAMURAI.create(level),"Samurai");samurai.setPos(base.getX()+5,base.getY(),base.getZ());check(level.addFreshEntity(samurai),"Samurai add failed");
            check(ninja.getMaxHealth()==20F&&ninja.getAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)==.34D,"Ninja stats drifted");
            check(samurai.getMaxHealth()==36F&&samurai.getAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)==9D,"Samurai stats drifted");
            check(SettlementEntities.BLACK_NINJA.getWidth()==.60F&&SettlementEntities.BLACK_NINJA.getHeight()==1.80F,"Ninja hitbox drifted");
            check(SettlementEntities.SAMURAI.getWidth()==.78F&&SettlementEntities.SAMURAI.getHeight()==2.34F,"Samurai is not exact 1.3x player scale");
            ninja.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(SettlementItems.KATANA));samurai.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(SettlementItems.LONG_KATANA));
            check(ninja.getMainHandItem().is(SettlementItems.KATANA)&&samurai.getMainHandItem().is(SettlementItems.LONG_KATANA),"warrior equipment missing");

            Cow first=require(EntityType.COW.create(level),"Ninja target");first.setPos(base.getX()+2,base.getY(),base.getZ());check(level.addFreshEntity(first),"Ninja target add failed");
            ninja.beginTechnique();check(ninja.getActionState()==JapaneseWarriorEntity.ACTION_SMOKE_STEP,"Ninja action state missing");
            check(ninja.applyTechniqueHit(first)&&first.getHealth()==3F,"Ninja technique damage drifted: "+first.getHealth());first.discard();
            Cow second=require(EntityType.COW.create(level),"Samurai target");second.setPos(base.getX()+7,base.getY(),base.getZ());check(level.addFreshEntity(second),"Samurai target add failed");
            samurai.beginTechnique();check(samurai.getActionState()==JapaneseWarriorEntity.ACTION_LONG_LUNGE,"Samurai action state missing");
            check(samurai.applyTechniqueHit(second)&&!second.isAlive(),"Samurai 12-damage technique did not defeat 10-health target");second.discard();

            check(SettlementItems.KATANA instanceof JapaneseKatanaItem katana&&!katana.isLongBlade(),"ordinary katana contract missing");
            check(SettlementItems.LONG_KATANA instanceof JapaneseKatanaItem longKatana&&longKatana.isLongBlade(),"long katana contract missing");
            check(level.getServer().getRecipeManager().byKey(OpusVsExe.id("katana")).isPresent(),"katana recipe missing");
            check(level.getServer().getRecipeManager().byKey(OpusVsExe.id("long_katana")).isPresent(),"long katana recipe missing");
            check(TrophyRegistry.trophyFor(SettlementEntities.BLACK_NINJA)==TrophyRegistry.BLACK_NINJA,"Ninja trophy binding missing");
            check(TrophyRegistry.trophyFor(SettlementEntities.SAMURAI)==TrophyRegistry.SAMURAI,"Samurai trophy binding missing");
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false,level.getServer());check(!TrophyRegistry.shouldDropTrophy(ninja),"doMobLoot=false ignored");
            OpusVsExe.LOGGER.info("Task 43 QA PASS: Japanese structure registry, exact two warrior stat/hitbox contracts, fixed weapons, smoke-step/lunge states and damage, two katana tiers/recipes and trophy bindings");
        }finally{
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(oldLoot,level.getServer());
            for(Entity entity:level.getEntities((Entity)null,area,ignored->true))if(!(entity instanceof net.minecraft.world.entity.player.Player))entity.discard();
        }
    }
    private static <T>T require(T value,String name){if(value==null)throw new IllegalStateException("Task 43 QA: "+name+" factory returned null");return value;}
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 43 QA: "+message);}
}
