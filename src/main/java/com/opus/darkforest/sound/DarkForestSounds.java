package com.opus.darkforest.sound;

import com.opus.darkforest.DarkForestLine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Dark Forest boss audio — Mossbound Enderman theme and slowed death cry. */
public final class DarkForestSounds {
    public static final SoundEvent ENDERMAN_THEME = register("enderman_theme");
    public static final SoundEvent MOSSBOUND_DEATH = register("mossbound_enderman_death");

    private DarkForestSounds() { }

    private static SoundEvent register(String name) {
        ResourceLocation id = DarkForestLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() { }
}