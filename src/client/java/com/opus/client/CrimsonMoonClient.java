package com.opus.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Random;

/**
 * Клиентская часть «кровавой луны» (задача 2026-08-22).
 *
 * Сервер сообщает о начале/конце боя пакетами OMEGA_FX (FX_MOON_START /
 * FX_MOON_END). Клиент плавно набирает и так же плавно гасит силу эффекта
 * (3 сек подъём / 6 сек затухание) — переход всегда мягкий. Сам эффект:
 *  - красный цвет неба и тумана (мишени LevelRendererMixin / FogRendererMixin),
 *  - падающие красные звёзды — добавки-штрихи в слое неба вокруг камеры,
 *  - ночью звёзды на небе окрашиваются в тёмно-красный (LevelRendererMixin).
 */
@Environment(EnvType.CLIENT)
public final class CrimsonMoonClient {

    private static final int RISE_TICKS = 60;    // ~3 секунды набора
    private static final int FADE_TICKS = 120;   // ~6 секунд затухания
    /** Если сигнал от сервера пропал — свет сам гаснет. */
    private static final long SIGNAL_TIMEOUT_MS = 5000L;

    private static volatile boolean serverWantsBloodMoon = false;
    private static volatile long lastSignalMs = System.currentTimeMillis();
    private static float strength = 0.0F;

    private static final Vec3 BLOOD_SKY = new Vec3(0.45D, 0.06D, 0.08D);
    private static final Vec3 BLOOD_FOG = new Vec3(0.30D, 0.05D, 0.05D);

    private CrimsonMoonClient() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (serverWantsBloodMoon && System.currentTimeMillis() - lastSignalMs > SIGNAL_TIMEOUT_MS) {
                serverWantsBloodMoon = false; // связь с алтарём оборвалась
            }
            float step = serverWantsBloodMoon ? 1.0F / RISE_TICKS : -1.0F / FADE_TICKS;
            strength = Mth.clamp(strength + step, 0.0F, 1.0F);
        });
    }

    // ---------------- серверные сигналы ----------------

    public static void onBattleStarted() {
        serverWantsBloodMoon = true;
        lastSignalMs = System.currentTimeMillis();
    }

    public static void onBattleEnded() {
        serverWantsBloodMoon = false;
        lastSignalMs = System.currentTimeMillis();
    }

    // ---------------- сила эффекта ----------------

    /** Текущая сила кровав-эффекта: 0 — обычное небо, 1 — полная луна. */
    public static float strength() {
        return strength;
    }

    public static boolean isActive() {
        return strength > 0.002F;
    }

    /** Липнет ванильные цвета (небо/туман) к кроваво-красным. */
    public static Vec3 tintColor(Vec3 original, Vec3 target) {
        float s = strength;
        if (s <= 0.0F) return original;
        return new Vec3(
                Mth.lerp(s, original.x, target.x),
                Mth.lerp(s, original.y, target.y),
                Mth.lerp(s, original.z, target.z));
    }

    /** Века пересечения: цвет неба в кровавом спектре. */
    public static Vec3 tintSky(Vec3 original) {
        return tintColor(original, BLOOD_SKY);
    }

    public static Vec3 bloodSkyColor() {
        return BLOOD_SKY;
    }

    public static Vec3 bloodFogColor() {
        return BLOOD_FOG;
    }

    // ---------------- падающие звёзды ----------------

    private static final int COMETS = 90;
    private static final float[][] COMET_VARS = new float[COMETS][8];

    /** Красные штрихи, улетающие вниз сквозь небесный купол вокруг камеры. */
    public static void renderFallingStars(PoseStack poseStack, float partialTick) {
        if (!isActive()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        long t = mc.level.getGameTime();
        float alpha = Math.min(1.0F, strength * 1.35F);

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < COMETS; i++) {
            float[] v = cometVarsFor(i);
            float cycle = 140.0F;
            float p = ((t + partialTick) * v[3] + v[4]) % cycle;
            float prog = p / cycle;                 // 0..1 — падение
            float x = v[0] + prog * v[5];
            float y = 130.0F - prog * 190.0F + v[1];
            float z = v[2] + prog * v[6];
            if (y < -60.0F || y > 140.0F) continue;
            float len = 4.5F + v[7];
            float lx = x - v[5] * 0.02F * len;
            float ly = y + 0.55F * len * 2.0F;      // хвост вверх, к направлению полёта
            float lz = z - v[6] * 0.02F * len;
            float head = alpha * (1.0F - prog * 0.35F);
            buffer.vertex(matrix, x, y, z).color(1.0F, 0.28F, 0.24F, head).endVertex();
            buffer.vertex(matrix, lx, ly, lz).color(0.6F, 0.05F, 0.05F, 0.02F).endVertex();
        }
        Tesselator.getInstance().end();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static float[] cometVarsFor(int i) {
        if (COMET_VARS[i][0] == 0.0F && COMET_VARS[i][2] == 0.0F) {
            Random r = new Random(i * 977L + 13L);
            COMET_VARS[i][0] = (r.nextFloat() - 0.5F) * 320.0F;   // X
            COMET_VARS[i][1] = (r.nextFloat() - 0.5F) * 40.0F;    // Y
            COMET_VARS[i][2] = (r.nextFloat() - 0.5F) * 320.0F;   // Z
            COMET_VARS[i][3] = 0.35F + r.nextFloat() * 0.8F;      // скорость времени
            COMET_VARS[i][4] = r.nextFloat() * 240.0F;            // сдвиг фазы
            COMET_VARS[i][5] = (r.nextFloat() - 0.5F) * 80.0F;    // дрейф X
            COMET_VARS[i][6] = (r.nextFloat() - 0.5F) * 80.0F;    // дрейф Z
            COMET_VARS[i][7] = r.nextFloat() * 3.0F;              // длина хвоста
        }
        return COMET_VARS[i];
    }
}
