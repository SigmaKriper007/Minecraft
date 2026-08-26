package com.opus.client.hud;

import com.opus.client.gui.BloodMoonTheme;
import com.opusvsexe.entity.custom.ExoAbility;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Pilot readout: frame, hull, energy and ability cooldowns — «кровавая луна». */
public final class ExoHud {

    private static final int BAR_WIDTH = 110;
    private static final int BAR_HEIGHT = 10;

    private ExoHud() {
    }

    public static void init() {
        HudRenderCallback.EVENT.register(ExoHud::render);
    }

    private static void render(GuiGraphics gui, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui || client.screen != null) {
            return;
        }
        if (!(client.player.getVehicle() instanceof ExosuitEntity exo)) {
            return;
        }

        int left = 8;
        int top = client.getWindow().getGuiScaledHeight() - 90;

        float time = (client.level != null ? client.level.getGameTime() : 0L) + tickDelta;
        float pulse = BloodMoonTheme.pulse(time, BloodMoonTheme.PULSE_PANEL);

        // панель с неоновой рамкой (дышит)
        gui.fill(left - 4, top - 4, left + BAR_WIDTH + 8, top + 74, BloodMoonTheme.PANEL_BG);
        int edge = BloodMoonTheme.mix(BloodMoonTheme.PANEL_EDGE, BloodMoonTheme.BLOOD_BRIGHT, pulse * 0.6F);
        gui.fill(left - 4, top - 4, left + BAR_WIDTH + 8, top - 3, edge);
        gui.fill(left - 4, top + 74, left + BAR_WIDTH + 8, top + 75, edge);
        gui.fill(left - 4, top - 4, left - 3, top + 74, edge);
        gui.fill(left + BAR_WIDTH + 7, top - 4, left + BAR_WIDTH + 8, top + 74, edge);

        gui.drawString(client.font, Component.literal(exo.getTier().displayName()), left, top, BloodMoonTheme.TEXT_PALE, true);

        int hull = Math.round(exo.getHealth());
        int maxHull = Math.round(exo.getMaxHealth());
        drawBar(gui, left, top + 13, hull / (float) Math.max(1, maxHull), BloodMoonTheme.BLOOD_BRIGHT, BloodMoonTheme.CRIMSON);
        gui.drawString(client.font, Component.translatable("hud.opusvsexe.exo.hull", hull, maxHull),
                left + 2, top + 15, BloodMoonTheme.TEXT, false);

        int energy = exo.getEnergy();
        int maxEnergy = Math.max(1, exo.getMaxEnergy());
        float energyRatio = energy / (float) maxEnergy;
        int energyColor = energyRatio < 0.2F ? BloodMoonTheme.NEON : BloodMoonTheme.AMBER;
        drawBar(gui, left, top + 26, energyRatio, energyColor, BloodMoonTheme.AMBER_BRIGHT);
        gui.drawString(client.font, Component.translatable("hud.opusvsexe.exo.energy", energy, maxEnergy),
                left + 2, top + 28, BloodMoonTheme.TEXT, false);

        int y = top + 40;
        for (int slot = 0; slot < ExosuitEntity.ABILITY_SLOTS; slot++) {
            ExoAbility ability = exo.getAbility(slot);
            if (ability == null || ability.isNone()) {
                continue;
            }
            int cooldown = exo.getCooldown(slot);
            String key = keyLabel(slot);
            Component line = cooldown > 0
                    ? Component.translatable("hud.opusvsexe.exo.ability_cooling", key,
                            Component.translatable(ability.translationKey()), (cooldown + 19) / 20)
                    : Component.translatable("hud.opusvsexe.exo.ability_ready", key,
                            Component.translatable(ability.translationKey()), ability.energyCost());
            gui.drawString(client.font, line, left, y, cooldown > 0 ? BloodMoonTheme.TEXT_MUTED : BloodMoonTheme.TEXT_PALE, true);
            y += 10;
        }
    }

    private static String keyLabel(int slot) {
        return switch (slot) {
            case 0 -> com.opus.client.ExoKeybinds.ABILITY[0].getTranslatedKeyMessage().getString();
            case 1 -> com.opus.client.ExoKeybinds.ABILITY[1].getTranslatedKeyMessage().getString();
            case 2 -> com.opus.client.ExoKeybinds.ABILITY[2].getTranslatedKeyMessage().getString();
            case 3 -> com.opus.client.ExoKeybinds.ABILITY[3].getTranslatedKeyMessage().getString();
            default -> "?";
        };
    }

    private static void drawBar(GuiGraphics gui, int x, int y, float ratio, int fill, int fillBright) {
        int clamped = Math.round(Math.max(0.0F, Math.min(1.0F, ratio)) * BAR_WIDTH);
        gui.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF140508);
        if (clamped > 0) {
            gui.fill(x, y, x + clamped, y + BAR_HEIGHT, fill);
            // неоновая кромка заполнения
            gui.fill(x, y, x + clamped, y + 1, fillBright);
        }
    }
}
