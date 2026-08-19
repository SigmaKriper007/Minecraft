package com.opus.client.hud;

import com.opusvsexe.entity.custom.ExoAbility;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Pilot readout: frame, hull, energy and ability cooldowns. */
public final class ExoHud {

    private static final int BAR_WIDTH = 110;
    private static final int PANEL_BG = 0xA0101014;
    private static final int ENERGY_FILL = 0xFF3FD0FF;
    private static final int ENERGY_LOW = 0xFFFF5A3C;
    private static final int HULL_FILL = 0xFF7CE07C;
    private static final int READY = 0xFFE8F4FF;
    private static final int COOLING = 0xFF7A8899;

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
        int top = client.getWindow().getGuiScaledHeight() - 78;
        gui.fill(left - 4, top - 4, left + BAR_WIDTH + 8, top + 62, PANEL_BG);

        gui.drawString(client.font, Component.literal(exo.getTier().displayName()), left, top, READY, true);

        int hull = Math.round(exo.getHealth());
        int maxHull = Math.round(exo.getMaxHealth());
        drawBar(gui, left, top + 13, hull / (float) Math.max(1, maxHull), HULL_FILL);
        gui.drawString(client.font, Component.translatable("hud.opusvsexe.exo.hull", hull, maxHull),
                left + 2, top + 15, 0xFF0A0A0A, false);

        int energy = exo.getEnergy();
        int maxEnergy = Math.max(1, exo.getMaxEnergy());
        float energyRatio = energy / (float) maxEnergy;
        drawBar(gui, left, top + 26, energyRatio, energyRatio < 0.2F ? ENERGY_LOW : ENERGY_FILL);
        gui.drawString(client.font, Component.translatable("hud.opusvsexe.exo.energy", energy, maxEnergy),
                left + 2, top + 28, 0xFF0A0A0A, false);

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
            gui.drawString(client.font, line, left, y, cooldown > 0 ? COOLING : READY, true);
            y += 10;
        }
    }

    private static String keyLabel(int slot) {
        return switch (slot) {
            case 0 -> com.opus.client.ExoKeybinds.ABILITY[0].getTranslatedKeyMessage().getString();
            case 1 -> com.opus.client.ExoKeybinds.ABILITY[1].getTranslatedKeyMessage().getString();
            case 2 -> com.opus.client.ExoKeybinds.ABILITY[2].getTranslatedKeyMessage().getString();
            default -> "?";
        };
    }

    private static void drawBar(GuiGraphics gui, int x, int y, float ratio, int colour) {
        int clamped = Math.round(Math.max(0.0F, Math.min(1.0F, ratio)) * BAR_WIDTH);
        gui.fill(x, y, x + BAR_WIDTH, y + 10, 0xFF1B1F24);
        if (clamped > 0) {
            gui.fill(x, y, x + clamped, y + 10, colour);
        }
    }
}
