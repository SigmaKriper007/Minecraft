package com.opus.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/** Every EXO key lives here, nothing else registers key mappings. */
public final class ExoKeybinds {

    private static final String CATEGORY = "key.categories.opusvsexe";

    public static final KeyMapping[] ABILITY = new KeyMapping[4];
    public static KeyMapping EXO_INVENTORY;
    public static KeyMapping ARMOR_SHOCKWAVE;
    public static KeyMapping EXO_PUNCH;

    private ExoKeybinds() {
    }

    public static void init() {
        ABILITY[0] = register("key.opusvsexe.ability", GLFW.GLFW_KEY_F);
        ABILITY[1] = register("key.opusvsexe.ability_2", GLFW.GLFW_KEY_G);
        ABILITY[2] = register("key.opusvsexe.ability_3", GLFW.GLFW_KEY_H);
        ABILITY[3] = register("key.opusvsexe.ability_4", GLFW.GLFW_KEY_J);
        // Dedicated key instead of hijacking the vanilla inventory key, which
        // used to fight Minecraft's own key handling and swallow clicks.
        EXO_INVENTORY = register("key.opusvsexe.exo_inventory", GLFW.GLFW_KEY_R);
        ARMOR_SHOCKWAVE = register("key.opusvsexe.armor_shockwave", GLFW.GLFW_KEY_V);
        EXO_PUNCH = register("key.opusvsexe.exo_punch", GLFW.GLFW_KEY_B);
    }

    private static KeyMapping register(String translationKey, int keyCode) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping(translationKey, InputConstants.Type.KEYSYM, keyCode, CATEGORY));
    }
}
