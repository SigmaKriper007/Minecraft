package com.opus.sound;

import com.opus.OpusVsExe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent KATANA_SWING = register("katana_swing");
    public static final SoundEvent KATANA_HIT = register("katana_hit");
    public static final SoundEvent HAMMER_HIT = register("hammer_hit");
    public static final SoundEvent KATANA_ULTIMATE = register("katana_ultimate");
    public static final SoundEvent HAMMER_ULTIMATE = register("hammer_ultimate");
    public static final SoundEvent SHOCKWAVE = register("shockwave");
    public static final SoundEvent EXO_THRUST = register("exo_thrust");
    public static final SoundEvent SUPER_LASER = register("super_laser");
    public static final SoundEvent RADIO_EXPLOSION = register("radio_explosion");
    public static final SoundEvent DOOM_ETERNAL = registerFixed("doom_eternal", 128.0f);
    public static final SoundEvent DOOM_ETERNAL_DISC = registerFixed("doom_eternal_disc", 16.0f);

    private static SoundEvent register(String name) {
        ResourceLocation id = OpusVsExe.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static SoundEvent registerFixed(String name, float range) {
        ResourceLocation id = OpusVsExe.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, range));
    }

    public static void init() {
    }
}
