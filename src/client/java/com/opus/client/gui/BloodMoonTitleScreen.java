package com.opus.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Главное меню мода в тематике кровавой луны (задача 17).
 *
 * Полный кастом: процедурный неоновый фон {@link BloodMoonScene}, неоновый
 * заголовок с пульсом, подзаголовок и набор кнопок (стилизуются глобально
 * через {@code AbstractButtonMixin}).
 * Музыка проигрывается через {@code MinecraftMusicMixin}.
 */
public class BloodMoonTitleScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 24;
    private static final int BUTTON_GAP = 8;

    private long startNanos = -1L;

    public BloodMoonTitleScreen() {
        super(Component.literal("Opus Vs Exe"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int startY = (int) (this.height * 0.46F);

        this.addRenderableWidget(Button.builder(
                        Component.translatable("menu.singleplayer"),
                        b -> this.minecraft.setScreen(new SelectWorldScreen(this)))
                .bounds(cx - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("menu.multiplayer"),
                        b -> this.minecraft.setScreen(new JoinMultiplayerScreen(this)))
                .bounds(cx - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_GAP), BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("menu.options"),
                        b -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options)))
                .bounds(cx - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("menu.quit"),
                        b -> this.minecraft.stop())
                .bounds(cx - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_GAP) * 3, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
    }

    @Override
    public void renderBackground(GuiGraphics gui) {
        BloodMoonScene.render(gui, this.width, this.height, animTime());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // как ванильный TitleScreen — Esc не закрывает меню
    }

    @Override
    public void added() {
        super.added();
        this.startNanos = -1L; // плавный fade-in при каждом возврате в меню (Esc и кнопки)
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);

        float fade = Mth.clamp(animTime() / 40.0F, 0.0F, 1.0F);
        drawTitle(gui, fade);
        drawFooter(gui, fade);

        // плавное проявление из тьмы
        if (fade < 1.0F) {
            int a = (int) ((1.0F - fade) * 255);
            gui.fill(0, 0, this.width, this.height, (a << 24) | (BloodMoonTheme.VOID & 0x00FFFFFF));
        }
    }

    private void drawTitle(GuiGraphics gui, float fade) {
        Minecraft mc = Minecraft.getInstance();
        String title = "OPUS VS EXE";
        float pulse = BloodMoonTheme.pulse(animTime(), BloodMoonTheme.PULSE_TITLE);

        int cx = this.width / 2;
        int titleY = (int) (this.height * 0.30F);
        float scale = 2.0F;
        float tw = mc.font.width(title) * scale;
        float tx = cx - tw / 2.0F;

        gui.pose().pushPose();
        gui.pose().translate(tx, titleY, 0);
        gui.pose().scale(scale, scale, 1.0F);

        // неоновый ореол (многослойное свечение)
        int glow = (int) (70 + pulse * 90);
        int glowColor = BloodMoonTheme.withAlpha(BloodMoonTheme.NEON, (int) (glow * fade));
        gui.drawString(mc.font, title, -2, 0, glowColor, false);
        gui.drawString(mc.font, title, 2, 0, glowColor, false);
        gui.drawString(mc.font, title, 0, -2, glowColor, false);
        gui.drawString(mc.font, title, 0, 2, glowColor, false);
        gui.drawString(mc.font, title, -1, -1, glowColor, false);
        gui.drawString(mc.font, title, 1, 1, glowColor, false);
        gui.drawString(mc.font, title, 1, -1, glowColor, false);
        gui.drawString(mc.font, title, -1, 1, glowColor, false);

        // основной текст
        int core = BloodMoonTheme.mix(BloodMoonTheme.TEXT, BloodMoonTheme.TEXT_PALE, pulse);
        gui.drawString(mc.font, title, 0, 0, BloodMoonTheme.withAlpha(core, (int) (255 * fade)), false);

        gui.pose().popPose();

        // подзаголовок
        Component subtitle = Component.translatable("menu.opusvsexe.subtitle");
        int subColor = BloodMoonTheme.mix(BloodMoonTheme.TEXT_MUTED, BloodMoonTheme.TEXT_PALE, pulse);
        gui.drawCenteredString(mc.font, subtitle, cx, titleY + (int) (mc.font.lineHeight * scale) + 10,
                BloodMoonTheme.withAlpha(subColor, (int) (255 * fade)));
    }

    private void drawFooter(GuiGraphics gui, float fade) {
        Minecraft mc = Minecraft.getInstance();
        String version = "OpusVsExe 1.0.0";
        String moon = Component.translatable("menu.opusvsexe.blood_moon").getString();

        gui.drawString(mc.font, version, 6, this.height - 20, BloodMoonTheme.withAlpha(BloodMoonTheme.TEXT_MUTED, (int) (255 * fade)), false);
        gui.drawString(mc.font, moon, this.width - mc.font.width(moon) - 6, this.height - 20,
                BloodMoonTheme.withAlpha(BloodMoonTheme.TEXT_PALE, (int) (200 * fade)), false);
    }

    private float animTime() {
        if (startNanos < 0L) {
            startNanos = System.nanoTime();
        }
        return (System.nanoTime() - startNanos) / 50_000_000.0F; // в «тиках» (20/с)
    }
}
