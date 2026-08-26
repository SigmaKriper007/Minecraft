package com.opus.darkforest.entity;

import com.opus.OpusVsExe;
import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.block.GloomwoodSaplingBlock;
import com.opus.darkforest.registry.DarkForestBlocks;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.registry.DarkForestItems;
import com.opus.registry.TrophyRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Development-only deterministic Task 38 contract probe. */
public final class DarkForestEcologyQa {
    private static boolean pending;
    private DarkForestEcologyQa() { }
    public static void init() {
        if (com.opus.qa.DevelopmentQa.enabled(38)) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> pending = true);
            ServerTickEvents.END_SERVER_TICK.register(server -> { if (pending) { pending = false; run(server.overworld()); } });
        }
    }

    private static void run(ServerLevel level) {
        BlockPos base = findEmptySite(level);
        AABB area = new AABB(base).inflate(18, 18, 18);
        boolean oldMobLoot = level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        try {
            verifyRegistries(level);
            verifyGrowth(level, base);
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(true, level.getServer());
            Cow target = require(EntityType.COW.create(level), "damage target");
            target.setPos(base.getX() + .5, base.getY() + 2, base.getZ() + .5); check(level.addFreshEntity(target), "damage target could not be added");

            GloomBroodmotherEntity brood = require(DarkForestEntities.GLOOM_BROODMOTHER.create(level), "Broodmother");
            brood.setPos(base.getX() + .5, base.getY() + 2, base.getZ() + 3.5); check(level.addFreshEntity(brood), "Broodmother could not be added");
            check(brood.getMaxHealth() == 56F && brood.getAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) == 8,
                "Broodmother attributes drifted");

            verifyWeb(level, target, brood, base);
            verifyPulse(level, target, brood, base);
            verifyMoonwingMotion(level, base);

            brood.die(level.damageSources().generic());
            check(brood.releasedChildCount() == 6, "Broodmother successfully added " + brood.releasedChildCount() + " Spiderlings instead of 6");
            brood.die(level.damageSources().generic());
            check(brood.releasedChildCount() == 6, "Broodmother death re-entry duplicated children");
            brood.releasedChildren().forEach(Entity::discard);

            ShadeSpiderlingEntity spiderling = require(DarkForestEntities.SHADE_SPIDERLING.create(level), "Spiderling");
            spiderling.setPos(base.getX() + 2.5, base.getY() + 2, base.getZ() + .5); check(level.addFreshEntity(spiderling), "Spiderling could not be added");
            check(spiderling.getMaxHealth() == 8F && spiderling.getAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) == .42,
                "Spiderling attributes drifted");
            spiderling.discard();

            MoonwingBatEntity bat = require(DarkForestEntities.MOONWING_BAT.create(level), "Moonwing");
            bat.setPos(base.getX() - 2.5, base.getY() + 5, base.getZ() + .5); check(level.addFreshEntity(bat), "Moonwing could not be added");
            check(bat.getMaxHealth() == 24F && bat.getAttributeBaseValue(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED) == .42,
                "Moonwing attributes drifted");
            verifyLootTable(level, brood, "gloom_broodmother", DarkForestItems.SHADE_SILK, 2);
            verifyLootTable(level, bat, "moonwing_bat", DarkForestItems.MOONWING_MEMBRANE, 1);
            check(TrophyRegistry.trophyFor(DarkForestEntities.SHADE_SPIDERLING) == TrophyRegistry.SHADE_SPIDERLING, "Spiderling trophy binding missing");
            check(TrophyRegistry.trophyFor(DarkForestEntities.GLOOM_BROODMOTHER) == TrophyRegistry.GLOOM_BROODMOTHER, "Broodmother trophy binding missing");
            check(TrophyRegistry.trophyFor(DarkForestEntities.MOONWING_BAT) == TrophyRegistry.MOONWING_BAT, "Moonwing trophy binding missing");
            check(TrophyRegistry.shouldDropTrophy(bat), "doMobLoot=true rejected Moonwing trophy");
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, level.getServer());
            check(!TrophyRegistry.shouldDropTrophy(bat), "doMobLoot=false did not suppress trophy path");
            bat.discard();

            OpusVsExe.LOGGER.info("Task 38 QA PASS: sapling clearance/growth, three exact stat/hitbox contracts, biome spawn injection, Web/Pulse hit-once+expiry, Moonwing flight vector, exact six-child death, common loot, three trophies and doMobLoot suppression");
        } finally {
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(oldMobLoot, level.getServer());
            for (Entity entity : level.getEntities((Entity)null, area, entity -> true)) entity.discard();
            for (BlockPos pos : BlockPos.betweenClosed(base.offset(-8, -1, -8), base.offset(8, 16, 8)))
                if (level.getBlockState(pos).is(DarkForestBlocks.MOONLIT_SOIL) || level.getBlockState(pos).is(DarkForestBlocks.GLOOMWOOD_SAPLING)
                    || level.getBlockState(pos).is(DarkForestBlocks.GLOOMWOOD_LOG) || level.getBlockState(pos).is(DarkForestBlocks.GLOOMWOOD_LEAVES)
                    || level.getBlockState(pos).is(Blocks.OBSIDIAN)) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void verifyRegistries(ServerLevel level) {
        var biome = level.registryAccess().registryOrThrow(Registries.BIOME).get(DarkForestLine.DARK_FOREST);
        check(biome != null, "Dark Forest biome missing");
        var spawns = biome.getMobSettings().getMobs(MobCategory.MONSTER).unwrap();
        for (var type : List.of(DarkForestEntities.SHADE_SPIDERLING, DarkForestEntities.GLOOM_BROODMOTHER, DarkForestEntities.MOONWING_BAT))
            check(spawns.stream().anyMatch(data -> data.type == type), "missing biome spawn injection for " + type);
        check(DarkForestEntities.SHADE_SPIDERLING.getWidth() == .68F && DarkForestEntities.SHADE_SPIDERLING.getHeight() == .38F, "Spiderling hitbox drifted");
        check(DarkForestEntities.GLOOM_BROODMOTHER.getWidth() == 2.20F && DarkForestEntities.GLOOM_BROODMOTHER.getHeight() == 1.55F, "Broodmother hitbox drifted");
        check(DarkForestEntities.MOONWING_BAT.getWidth() == 1.60F && DarkForestEntities.MOONWING_BAT.getHeight() == .85F, "Moonwing hitbox drifted");
    }

    private static void verifyGrowth(ServerLevel level, BlockPos base) {
        level.setBlock(base, DarkForestBlocks.MOONLIT_SOIL.defaultBlockState(), 3);
        BlockPos sapling = base.above(); level.setBlock(sapling, DarkForestBlocks.GLOOMWOOD_SAPLING.defaultBlockState(), 3);
        level.setBlock(sapling.above(5), Blocks.OBSIDIAN.defaultBlockState(), 3);
        check(!GloomwoodSaplingBlock.grow(level, sapling, RandomSource.create(38)), "blocked sapling grew through obstruction");
        level.setBlock(sapling.above(5), Blocks.AIR.defaultBlockState(), 3);
        check(GloomwoodSaplingBlock.grow(level, sapling, RandomSource.create(38)), "clear sapling failed to grow");
        long logs = BlockPos.betweenClosedStream(base.offset(-5, 0, -5), base.offset(5, 15, 5))
            .filter(pos -> level.getBlockState(pos).is(DarkForestBlocks.GLOOMWOOD_LOG)).count();
        check(logs >= 8, "grown tree contained only " + logs + " logs");
    }

    private static void verifyWeb(ServerLevel level, Cow target, GloomBroodmotherEntity owner, BlockPos base) {
        target.setPos(base.getX() + 1.5, base.getY() + 2, base.getZ() + .5); target.setHealth(target.getMaxHealth()); target.removeAllEffects();
        GloomWebEntity web = require(DarkForestEntities.GLOOM_WEB.create(level), "Gloom Web");
        web.setPos(target.getX(), target.getY(), target.getZ()); web.configure(owner, 18); check(level.addFreshEntity(web), "Gloom Web could not be added");
        float health = target.getHealth(); check(web.applyTo(target), "Web rejected first valid target"); check(!web.applyTo(target), "Web UUID guard accepted a duplicate hit");
        check(target.getHealth() == health - 3F && target.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN), "Web result health=" + target.getHealth() + ", slow=" + target.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN));
        for (int i = 0; i < 20; i++) if (!web.isRemoved()) { web.tickCount++; web.tick(); }
        check(target.getHealth() == health - 3F && web.isRemoved(), "Web repeated damage or failed to expire");
    }

    private static void verifyPulse(ServerLevel level, Cow target, GloomBroodmotherEntity owner, BlockPos base) {
        target.removeAllEffects(); target.setHealth(target.getMaxHealth()); target.invulnerableTime = 0; target.setPos(base.getX() + 1.5, base.getY() + 5, base.getZ() + .5);
        MoonwingPulseEntity pulse = require(DarkForestEntities.MOONWING_PULSE.create(level), "Moonwing Pulse");
        pulse.setPos(base.getX() - 1.5, base.getY() + 5, base.getZ() + .5); pulse.configure(owner); check(level.addFreshEntity(pulse), "Moonwing Pulse could not be added");
        float health = target.getHealth(); check(pulse.applyTo(target), "Pulse rejected first valid target"); check(!pulse.applyTo(target), "Pulse UUID guard accepted a duplicate hit");
        for (int i = 0; i < 32 && !pulse.isRemoved(); i++) { pulse.tickCount++; pulse.tick(); }
        check(target.getHealth() == health - 2F && target.hasEffect(net.minecraft.world.effect.MobEffects.GLOWING) && pulse.isRemoved(), "Pulse result health=" + target.getHealth() + ", glow=" + target.hasEffect(net.minecraft.world.effect.MobEffects.GLOWING) + ", removed=" + pulse.isRemoved());
    }

    private static void verifyMoonwingMotion(ServerLevel level, BlockPos base) {
        MoonwingBatEntity bat = require(DarkForestEntities.MOONWING_BAT.create(level), "motion Moonwing");
        bat.setPos(base.getX() - 3.5, base.getY() + 5, base.getZ() + .5); check(level.addFreshEntity(bat), "motion Moonwing could not be added");
        bat.getMoveControl().setWantedPosition(bat.getX() + 8, bat.getY() + 4, bat.getZ() + 2, 1.0);
        for (int i = 0; i < 12; i++) bat.getMoveControl().tick();
        Vec3 motion = bat.getDeltaMovement();
        check(!bat.isNoGravity() || motion.lengthSqr() > .002 && motion.y > 0, "Moonwing did not produce an upward aerial movement vector");
        bat.discard();
    }

    private static void verifyLootTable(ServerLevel level, Entity entity, String id, net.minecraft.world.item.Item expected, int minimum) {
        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, entity)
            .withParameter(LootContextParams.ORIGIN, entity.position()).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
            .create(LootContextParamSets.ENTITY);
        List<ItemStack> drops = level.getServer().getLootData().getLootTable(DarkForestLine.id("entities/" + id)).getRandomItems(params, 38L);
        int count = drops.stream().filter(stack -> stack.is(expected)).mapToInt(ItemStack::getCount).sum();
        check(count >= minimum, id + " deterministic loot produced only " + count + " of " + expected);
    }
    private static BlockPos findEmptySite(ServerLevel level) {
        BlockPos spawn = level.getSharedSpawnPos(); int y = Math.min(level.getMaxBuildHeight() - 24, Math.max(level.getMinBuildHeight() + 24, spawn.getY() + 112));
        for (int step = 0; step < 24; step++) {
            BlockPos candidate = new BlockPos(spawn.getX() + 480 + step * 20, y, spawn.getZ() - 480);
            boolean empty = true;
            for (BlockPos pos : BlockPos.betweenClosed(candidate.offset(-8, 0, -8), candidate.offset(8, 16, 8))) if (!level.isEmptyBlock(pos)) { empty = false; break; }
            if (empty) return candidate;
        }
        throw new IllegalStateException("Task 38 QA: no empty high-altitude site found");
    }
    private static <T> T require(T value, String name) { if (value == null) throw new IllegalStateException("Task 38 QA: " + name + " factory returned null"); return value; }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException("Task 38 QA: " + message); }
}
