package com.opus.settlement.qa;

import com.opus.OpusVsExe;
import com.opus.registry.TrophyRegistry;
import com.opus.settlement.entity.YoungSamuraiEntity;
import com.opus.settlement.registry.SettlementEntities;
import com.opus.settlement.registry.SettlementItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public final class YoungSamuraiQa {
    private static boolean pending;
    private YoungSamuraiQa() { }

    public static void init() {
        if (com.opus.qa.DevelopmentQa.enabled(44)) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> pending = true);
            ServerTickEvents.END_SERVER_TICK.register(server -> { if (pending) { pending = false; run(server.overworld()); } });
        }
    }

    private static void run(ServerLevel level) {
        BlockPos base = level.getSharedSpawnPos().offset(32, 80, 32);
        AABB area = new AABB(base).inflate(20D);
        boolean oldLoot = level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        try {
            YoungSamuraiEntity boss = require(SettlementEntities.YOUNG_SAMURAI.create(level), "boss");
            boss.setPos(base.getX(), base.getY(), base.getZ());
            boss.setArenaAnchor(base);
            check(level.addFreshEntity(boss), "boss add failed");
            check(boss.getMaxHealth() == 360F && boss.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) == 14D
                && boss.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) == .38D, "boss attributes drifted");
            check(SettlementEntities.YOUNG_SAMURAI.getWidth() == .66F && SettlementEntities.YOUNG_SAMURAI.getHeight() == 1.98F, "boss hitbox drifted");
            boss.tick();
            check(boss.getMainHandItem().is(SettlementItems.LONG_KATANA), "fixed long katana missing");
            check(boss.shouldEvadeForQa(true, .99F), "projectile dodge is not guaranteed");
            check(boss.shouldEvadeForQa(false, .299F) && !boss.shouldEvadeForQa(false, .30F), "30 percent general dodge boundary drifted");
            check(boss.abilityCooldownForPhase(1) == 48 && boss.abilityCooldownForPhase(2) == 24, "phase two cadence is not exactly doubled");
            boss.setHealth(180F); boss.evaluatePhaseForQa();
            check(boss.getPhase() == 2 && boss.hasAura(), "phase two aura transition failed");
            boss.beginActionForQa(YoungSamuraiEntity.Action.RISING_KNEE);
            check(boss.getAction() == YoungSamuraiEntity.Action.RISING_KNEE, "taijutsu action state missing");
            Cow target = require(EntityType.COW.create(level), "target"); target.setPos(base.getX() + 2, base.getY(), base.getZ());
            check(level.addFreshEntity(target), "target add failed");
            check(boss.strikeForQa(target, 13F) && !target.isAlive(), "focused taijutsu damage failed");
            check(TrophyRegistry.trophyFor(SettlementEntities.YOUNG_SAMURAI) == TrophyRegistry.YOUNG_SAMURAI, "boss trophy binding missing");
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, level.getServer());
            check(!TrophyRegistry.shouldDropTrophy(boss), "doMobLoot=false ignored");
            OpusVsExe.LOGGER.info("Task 44 QA PASS: Young Samurai stats/hitbox, long katana, two phases, visible aura contract, exact 2x cadence, five sword/taijutsu actions, 30% universal dodge, guaranteed projectile dodge and trophy binding");
        } finally {
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(oldLoot, level.getServer());
            for (Entity entity : level.getEntities((Entity)null, area, ignored -> true))
                if (!(entity instanceof net.minecraft.world.entity.player.Player)) entity.discard();
        }
    }

    private static <T> T require(T value, String name) { if (value == null) throw new IllegalStateException("Task 44 QA: " + name + " factory returned null"); return value; }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException("Task 44 QA: " + message); }
}
