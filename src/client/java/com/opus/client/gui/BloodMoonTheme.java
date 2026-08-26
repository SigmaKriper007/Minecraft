package com.opus.client.gui;

import net.minecraft.util.Mth;

/**
 * Design-токены интерфейса «Кровавой луны» (задача 17).
 *
 * Единая цветовая система и хелперы плавных неоновых анимаций для всех меню.
 * Все цвета/тайминги — здесь, а не разбросаны по экранам (skill §41, §74).
 */
public final class BloodMoonTheme {

    // ---- глубина / фон ----
    public static final int VOID        = 0xFF0A0306; // почти чёрный с красным подтоном
    public static final int DEEP        = 0xFF160508; // тёмный фон сцены
    public static final int GROUND      = 0xFF1E070B; // нижний силуэт земли
    public static final int GROUND_DARK = 0xFF0C0305;

    // ---- панели ----
    public static final int PANEL_BG    = 0xE020070D; // полупрозрачная кровавая панель
    public static final int PANEL_SOFT  = 0xE02A0A12; // кнопка normal
    public static final int PANEL_HOVER = 0xE0340C16; // кнопка hover
    public static final int PANEL_EDGE  = 0xFF4A121A; // тёмно-кровавая рамка

    // ---- кровь / неон ----
    public static final int BLOOD        = 0xFF8B1E2A;
    public static final int BLOOD_BRIGHT = 0xFFB3202E;
    public static final int CRIMSON      = 0xFFE01020;
    public static final int NEON         = 0xFFFF3B45; // неоновый красный (accent / hover)

    // ---- луна ----
    public static final int MOON       = 0xFFC42030;
    public static final int MOON_DARK  = 0xFF7E0E18;
    public static final int MOON_CORE  = 0xFFFFB0B0;

    // ---- текст ----
    public static final int TEXT        = 0xFFEDD9D0; // костяной белый
    public static final int TEXT_MUTED  = 0xFF9C6B6E; // приглушённый кровавый
    public static final int TEXT_PALE   = 0xFFFF6B70; // бледно-кровавый

    // ---- янтарь Хаику (вторичный акцент — «кровь машин») ----
    public static final int AMBER        = 0xFFE8941E;
    public static final int AMBER_BRIGHT = 0xFFFFB93E;

    // ---- тайминги анимаций (в тиках, 20/с) ----
    public static final float PULSE_TITLE  = 0.09F;  // рад/тик — период ~2.3с
    public static final float PULSE_MOON   = 0.056F; // период ~3.7с
    public static final float PULSE_PANEL  = 0.08F;
    public static final float HOVER_SPEED  = 0.16F;  // сглаживание hover (0..1 за ~6 тиков)

    private BloodMoonTheme() {
    }

    /** Синусоидальный пульс 0..1 (без резких скачков). */
    public static float pulse(float time, float speed) {
        return (Mth.sin(time * speed) + 1.0F) * 0.5F;
    }

    /** Плавный lerp для hover-переходов. */
    public static float approach(float current, float target, float speed) {
        if (current < target) {
            return Math.min(current + speed, target);
        }
        return Math.max(current - speed, target);
    }

    /** Заменить альфа-канал цвета (сохраняя RGB). */
    public static int withAlpha(int color, int alpha) {
        return ((alpha & 0xFF) << 24) | (color & 0x00FFFFFF);
    }

    /** Смешать два ARGB цвета по t (0..1), с сохранением альфы через lerp. */
    public static int mix(int from, int to, float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        int fa = (from >> 24) & 0xFF, fr = (from >> 16) & 0xFF, fg = (from >> 8) & 0xFF, fb = from & 0xFF;
        int ta = (to >> 24) & 0xFF, tr = (to >> 16) & 0xFF, tg = (to >> 8) & 0xFF, tb = to & 0xFF;
        int a = (int) Mth.lerp(t, fa, ta);
        int r = (int) Mth.lerp(t, fr, tr);
        int g = (int) Mth.lerp(t, fg, tg);
        int b = (int) Mth.lerp(t, fb, tb);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
