package com.opus.settlement.sound;

import com.opus.settlement.SettlementLine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Japanese settlement audio — Young Samurai battle theme and defeat sting. */
public final class SettlementSounds {
    public static final SoundEvent JAPANESE_FIGHT = register("japanese_fight");
    public static final SoundEvent YOUNG_SAMURAI_DEFEATED = register("young_samurai_defeated");

    private SettlementSounds() { }

    private static SoundEvent register(String name) {
        ResourceLocation id = SettlementLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() { }
}