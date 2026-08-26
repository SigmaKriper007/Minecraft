package com.opus.entity.haiku;

import com.opus.network.ModNetwork;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Серверный трекер битвы: пока в мире жив хотя бы один Хайку-Ω, все игроки
 * его измерения получают сигнал «кровавой луны». Сервер шлёт только события
 * вкл/выкл; сам переход плавно анимируется на клиенте (CrimsonMoonClient).
 *
 * Пока босс жив, сервер раз в 40 тиков шлёт «пульс» FX_MOON_START — клиент
 * держит луну включённой, а при смерти босса получает FX_MOON_END и плавно
 * гасит её. Без пульса клиент погасил бы луну через свой 5-секундный
 * таймаут, ещё до конца боя.
 */
public final class OmegaMoonTracker {

    /** Период пульса меньше клиентского таймаута (5с), чтобы луна не гасла. */
    private static final long HEARTBEAT_TICKS = 40L;

    /** Ключ = dimension, значение = была ли кровавая луна в прошлом тике. */
    private static final Map<ResourceKey<Level>, Boolean> LAST_STATE = new HashMap<>();
    private static final Map<ResourceKey<Level>, Long> LAST_SIGNAL_TICK = new HashMap<>();

    private static final EntityTypeTest<net.minecraft.world.entity.Entity, HaikuOmegaEntity> BOSS_TEST =
            EntityTypeTest.forClass(HaikuOmegaEntity.class);

    private OmegaMoonTracker() {
    }

    private static boolean hasLivingBoss(ServerLevel level) {
        List<? extends HaikuOmegaEntity> bosses = level.getEntities(BOSS_TEST, HaikuOmegaEntity::isAlive);
        return !bosses.isEmpty();
    }

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            boolean anyAlive = hasLivingBoss(level);
            ResourceKey<Level> key = level.dimension();
            boolean was = LAST_STATE.getOrDefault(key, false);
            if (anyAlive) {
                long lastSignal = LAST_SIGNAL_TICK.getOrDefault(key, Long.MIN_VALUE);
                if (!was || level.getGameTime() - lastSignal >= HEARTBEAT_TICKS) {
                    ModNetwork.broadcastOmegaFx(level, ModNetwork.FX_MOON_START);
                    LAST_SIGNAL_TICK.put(key, level.getGameTime());
                }
            } else if (was) {
                ModNetwork.broadcastOmegaFx(level, ModNetwork.FX_MOON_END);
            }
            LAST_STATE.put(key, anyAlive);
        });
        // игроку, зашедшему в разгар боя, шлём сигнал крови
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (ServerLevel level : server.getAllLevels()) {
                ResourceKey<Level> key = level.dimension();
                if (!Boolean.TRUE.equals(LAST_STATE.getOrDefault(key, false))) continue;
                if (hasLivingBoss(level)) {
                    ModNetwork.broadcastOmegaFx(level, ModNetwork.FX_MOON_START);
                }
            }
        });
    }
}
