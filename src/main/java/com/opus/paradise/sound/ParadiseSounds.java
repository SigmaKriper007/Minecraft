package com.opus.paradise.sound;

import com.opus.paradise.ParadiseLine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Paradise boss/ambience audio — Angel Boy encounter theme. */
public final class ParadiseSounds {
    public static final SoundEvent ANGEL_BOY_THEME = register("angel_boy_theme");

    private ParadiseSounds() { }

    private static SoundEvent register(String name) {
        ResourceLocation id = ParadiseLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() { }
}