package com.opus.settlement.qa;

import com.opus.OpusVsExe;
import com.opus.darkforest.registry.DarkForestItems;
import com.opus.entity.haiku.Haiku15Entity;
import com.opus.registry.ModEntities;
import com.opus.registry.ModItems;
import com.opus.registry.TrophyRegistry;
import com.opus.settlement.entity.SurvivorEntity;
import com.opus.settlement.registry.SettlementEntities;
import com.opus.settlement.registry.SettlementItems;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

/** Development-only deterministic Task 42 contract probe. */
public final class SurvivorSettlementQa {
    private static boolean pending;
    private SurvivorSettlementQa() { }

    public static void init() {
        if (com.opus.qa.DevelopmentQa.enabled(42)) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> pending = true);
            ServerTickEvents.END_SERVER_TICK.register(server -> { if (pending) { pending = false; run(server.overworld()); } });
        }
    }

    private static void run(ServerLevel level) {
        BlockPos base = level.getSharedSpawnPos().offset(0, 64, 0);
        AABB area = new AABB(base).inflate(16);
        boolean oldLoot = level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        try {
            SurvivorEntity survivor = require(SettlementEntities.SURVIVOR.create(level), "survivor");
            survivor.setPos(base.getX() + .5, base.getY(), base.getZ() + .5);
            check(level.addFreshEntity(survivor), "survivor add failed");
            check(survivor.getMaxHealth() == 20F && survivor.getAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) == .10D,
                "player-scale attributes drifted");
            survivor.setVariant(17); check(survivor.getVariant() == 5, "variant normalization failed");

            UUID anger = UUID.randomUUID(); survivor.setPersistentAngerTarget(anger); survivor.startPersistentAngerTimer();
            check(survivor.getPersistentAngerTarget().equals(anger) && survivor.getRemainingPersistentAngerTime() >= 400, "persistent anger contract failed");
            FakePlayer player = FakePlayer.get(level);
            survivor.setPersistentAngerTarget(player.getUUID());
            check(survivor.canAttack(player) && survivor.isAngryAt(player), "angry player target rejected or recursed");
            survivor.setPersistentAngerTarget(anger);
            check(!survivor.canAttack(player), "unrelated player accepted as anger target");
            survivor.setHealth(5.5F); check(survivor.isRetreating(), "below-three-heart retreat did not engage");
            survivor.setHealth(6F); check(!survivor.isRetreating(), "retreat threshold drifted");

            Haiku15Entity haiku = require(ModEntities.HAIKU_1_5.create(level), "Haiku");
            haiku.setPos(base.getX() + 3.5, base.getY(), base.getZ() + .5); check(level.addFreshEntity(haiku), "Haiku add failed");
            check(!survivor.isOpusArmed() && !survivor.canAttack(haiku), "unarmed survivor targeted Haiku");
            survivor.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.OPUS_AXE));
            check(survivor.isOpusArmed() && survivor.canAttack(haiku), "Opus-armed survivor rejected Haiku");
            float before = haiku.getHealth(); check(haiku.hurt(level.damageSources().mobAttack(survivor), 2F) && haiku.getHealth() < before,
                "Haiku lattice rejected survivor-held Opus weapon");

            survivor.setItemSlot(EquipmentSlot.HEAD, new ItemStack(DarkForestItems.DARK_FOREST_HELMET));
            survivor.setItemSlot(EquipmentSlot.CHEST, new ItemStack(DarkForestItems.DARK_FOREST_CHESTPLATE));
            survivor.setItemSlot(EquipmentSlot.LEGS, new ItemStack(DarkForestItems.DARK_FOREST_LEGGINGS));
            survivor.setItemSlot(EquipmentSlot.FEET, new ItemStack(DarkForestItems.DARK_FOREST_BOOTS));
            survivor.refreshArmorAbilities();
            check(survivor.hasEffect(MobEffects.NIGHT_VISION) && survivor.hasEffect(MobEffects.DIG_SPEED)
                && survivor.hasEffect(MobEffects.MOVEMENT_SPEED) && survivor.hasEffect(MobEffects.DOLPHINS_GRACE), "Dark Forest set abilities missing");

            check(survivor.wantsToPickUp(new ItemStack(ItemsForQa.IRON_SWORD)) && survivor.wantsToPickUp(new ItemStack(DarkForestItems.DARK_FOREST_HELMET)),
                "equipment pickup rejected usable gear");
            var results = survivor.getOffers().stream().map(offer -> offer.getResult().getItem()).toList();
            check(results.containsAll(List.of(SettlementItems.OPUS_RUINS_COMPASS, SettlementItems.PARADISE_COMPASS,
                SettlementItems.DARK_FOREST_COMPASS, SettlementItems.MOON_FOUNTAIN_COMPASS)), "expedition trade set incomplete");
            check(TrophyRegistry.trophyFor(SettlementEntities.SURVIVOR) == TrophyRegistry.SURVIVOR, "survivor trophy binding missing");
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, level.getServer());
            check(!TrophyRegistry.shouldDropTrophy(survivor), "doMobLoot=false did not suppress survivor trophy");

            OpusVsExe.LOGGER.info("Task 42 QA PASS: stats, variants, persistent anger, exact retreat threshold, equipment pickup/use, conditional Haiku defense, armor abilities, four compass trades and trophy binding");
        } finally {
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(oldLoot, level.getServer());
            for (Entity entity : level.getEntities((Entity)null, area, ignored -> true)) if (!(entity instanceof net.minecraft.world.entity.player.Player)) entity.discard();
        }
    }

    private static final class ItemsForQa { private static final net.minecraft.world.item.Item IRON_SWORD = net.minecraft.world.item.Items.IRON_SWORD; }
    private static <T> T require(T value, String name) { if (value == null) throw new IllegalStateException("Task 42 QA: " + name + " factory returned null"); return value; }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException("Task 42 QA: " + message); }
}
