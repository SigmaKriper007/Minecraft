package com.opus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

/**
 * Тряска камеры для боя Омеги (задача 13).
 * Реализовано через ванильный механизм урона: установка hurtTime/hurtDuration
 * вызывает наклон камеры и красное мерцание — читается как ударная тряска.
 * Пакеты OMEGA_FX вызывают trigger() из клиентского потока.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class OmegaCameraShake {

    private OmegaCameraShake() {
    }

    /** intensity 0..1, длительность в тиках намека. */
    public static void trigger(float intensity, int hurtTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float i = Mth.clamp(intensity, 0.0F, 1.0F);
        if (i <= 0.0F || hurtTicks <= 0) return;
        mc.player.hurtDuration = Math.max(mc.player.hurtDuration, hurtTicks);
        mc.player.hurtTime = Math.max(mc.player.hurtTime, Math.round(hurtTicks * i));
    }
}
