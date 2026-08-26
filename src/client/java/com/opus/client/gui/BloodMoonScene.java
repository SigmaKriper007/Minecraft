package com.opus.client.gui;

import net.minecraft.client.gui.GuiGraphics;

import java.util.Random;

/**
 * Процедурный фон «кровавой луны» для меню (задача 17).
 *
 * Сцена анимирована НА ВЕСЬ ЭКРАН — снизу до верха:
 * пульсирующий кровавый градиент, всплывающие эмберы и падающие кометы
 * распределены по всей ширине/высоте (координаты нормализованы 0..1),
 * дышащая луна и неоновый горизонт внизу.
 */
public final class BloodMoonScene {

    private static final int EMBERS = 110;
    private static final int COMETS = 45;

    private static float[] EMBER;
    private static float[] COMET;
    private static float lastW = -1.0F;
    private static float lastH = -1.0F;
    private static long startNanos = -1L;

    private BloodMoonScene() {
    }

    /** Монотонное время сцены в «тиках» (20/с) для плавных анимаций. */
    public static float time() {
        if (startNanos < 0L) {
            startNanos = System.nanoTime();
        }
        return (System.nanoTime() - startNanos) / 50_000_000.0F;
    }

    /**
     * Полная сцена для главного меню (тайтл): крупная луна.
     */
    public static void render(GuiGraphics gui, int width, int height, float time) {
        renderFull(gui, width, height, time, 1.0F);
    }

    /**
     * Сцена для остальных меню (настройки, singleplayer, multiplayer): та же
     * анимация на весь экран, но луна чуть меньше, чтобы не мешать контенту.
     */
    public static void renderMenuBackground(GuiGraphics gui, int width, int height, float time) {
        renderFull(gui, width, height, time, 0.78F);
    }

    private static void renderFull(GuiGraphics gui, int width, int height, float time, float moonScale) {
        seed(width, height);
        float pulse = BloodMoonTheme.pulse(time, BloodMoonTheme.PULSE_MOON);

        // полный экран: кровавый градиент (сверху краснее, вниз — тьма)
        int top = BloodMoonTheme.mix(0xFF2E0A12, 0xFF8B1E2A, pulse * 0.5F);
        gui.fillGradient(0, 0, width, height, top, BloodMoonTheme.VOID);

        // лёгкая атмосферная красная дымка по всему экрану
        int atmosphere = BloodMoonTheme.withAlpha(BloodMoonTheme.BLOOD, (int) (12 + pulse * 12));
        gui.fill(0, 0, width, height, atmosphere);

        // падающие кометы (по всей ширине, сверху вниз)
        renderComets(gui, width, height, time);
        // всплывающие эмберы (по всей ширине, снизу вверх)
        renderEmbers(gui, width, height, time);
        // луна
        renderMoon(gui, width, height, time, moonScale);
    }

    private static void renderMoon(GuiGraphics gui, int width, int height, float time, float scale) {
        float cx = width * 0.76F;
        float cy = height * 0.27F;
        float r = height * 0.16F * scale;
        float breathe = BloodMoonTheme.pulse(time, BloodMoonTheme.PULSE_MOON);

        fillCircle(gui, cx, cy, r * (1.55F + breathe * 0.12F), withAlpha(BloodMoonTheme.MOON_CORE, (int) (40 + breathe * 28)));
        fillCircle(gui, cx, cy, r * 1.28F, withAlpha(BloodMoonTheme.NEON, (int) (26 + breathe * 22)));
        fillCircle(gui, cx, cy, r, BloodMoonTheme.MOON);
        fillCircle(gui, cx + r * 0.16F, cy + r * 0.16F, r * 0.80F, BloodMoonTheme.MOON_DARK);
        fillCircle(gui, cx - r * 0.20F, cy - r * 0.20F, r * 0.42F, withAlpha(BloodMoonTheme.MOON_CORE, (int) (70 + breathe * 40)));
    }

    private static void renderEmbers(GuiGraphics gui, int width, int height, float time) {
        for (int i = 0; i < EMBERS; i++) {
            float nx = EMBER[i * 4];
            float drift = EMBER[i * 4 + 1];
            float speed = EMBER[i * 4 + 2];
            float phase = EMBER[i * 4 + 3];

            float prog = ((time * speed) + phase) % 1.0F;
            float x = nx * width + drift * width * prog;
            float y = height + 10 - prog * (height + 20);
            float twinkle = BloodMoonTheme.pulse(time * speed * 2.0F + phase * 6.28F, 2.0F);

            int alpha = (int) (60 + twinkle * 110);
            int s = 1 + (i % 3);
            gui.fill((int) x, (int) y, (int) x + s, (int) y + s, withAlpha(BloodMoonTheme.CRIMSON, alpha));
        }
    }

    private static void renderComets(GuiGraphics gui, int width, int height, float time) {
        for (int i = 0; i < COMETS; i++) {
            float nx = COMET[i * 6];
            float ny0 = COMET[i * 6 + 1];
            float driftX = COMET[i * 6 + 2];
            float speed = COMET[i * 6 + 3];
            float phase = COMET[i * 6 + 4];
            float len = COMET[i * 6 + 5];

            float prog = ((time * speed) + phase) % 1.0F;
            float x = nx * width + prog * driftX * width;
            float y = ny0 * height + prog * (height + 40);
            float alpha = (1.0F - prog) * 130;
            if (alpha <= 0.0F) {
                continue;
            }

            int color = withAlpha(BloodMoonTheme.NEON, (int) alpha);
            gui.fill((int) x, (int) y, (int) (x + 2), (int) (y + len), color);
        }
    }

    /** Залитый круг через горизонтальные полосы {@link GuiGraphics#fill} (без смены шейдера). */
    public static void fillCircle(GuiGraphics gui, float cx, float cy, float radius, int color) {
        int r = (int) Math.ceil(radius);
        for (int dy = -r; dy <= r; dy++) {
            int half = (int) Math.sqrt((long) r * r - (long) dy * dy);
            int y = (int) (cy + dy);
            gui.fill((int) (cx - half), y, (int) (cx + half) + 1, y + 1, color);
        }
    }

    private static int withAlpha(int color, int alpha) {
        return BloodMoonTheme.withAlpha(color, alpha);
    }

    /** Нормализованные координаты частиц: пересоздаём при смене размера окна. */
    private static void seed(float width, float height) {
        if (lastW == width && lastH == height && EMBER != null) {
            return;
        }
        lastW = width;
        lastH = height;
        Random r = new Random(0x0B1000D5EEDL);

        EMBER = new float[EMBERS * 4];
        for (int i = 0; i < EMBERS; i++) {
            EMBER[i * 4] = r.nextFloat();                         // nx (0..1)
            EMBER[i * 4 + 1] = (r.nextFloat() - 0.5F) * 0.08F;    // drift nx
            EMBER[i * 4 + 2] = 0.008F + r.nextFloat() * 0.03F;    // speed
            EMBER[i * 4 + 3] = r.nextFloat();                     // phase
        }

        COMET = new float[COMETS * 6];
        for (int i = 0; i < COMETS; i++) {
            COMET[i * 6] = r.nextFloat();                         // nx
            COMET[i * 6 + 1] = -r.nextFloat() * 0.35F;            // ny0 (над экраном)
            COMET[i * 6 + 2] = (r.nextFloat() - 0.5F) * 0.28F;    // driftX
            COMET[i * 6 + 3] = 0.012F + r.nextFloat() * 0.02F;    // speed
            COMET[i * 6 + 4] = r.nextFloat();                     // phase
            COMET[i * 6 + 5] = 7.0F + r.nextFloat() * 11.0F;      // длина хвоста
        }
    }
}