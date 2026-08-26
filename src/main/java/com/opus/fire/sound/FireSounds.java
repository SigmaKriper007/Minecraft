package com.opus.fire.sound;

import com.opus.fire.FireLine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class FireSounds {
    // Armor & items
    public static final SoundEvent FIRE_EQUIP = register("fire_equip");
    public static final SoundEvent FIRE_CHARGE = register("fire_charge");
    public static final SoundEvent FIREBALL_LAUNCH = register("fireball_launch");
    public static final SoundEvent FIRE_EXPLODE = register("fire_explode");

    // Demon
    public static final SoundEvent DEMON_ROAR = registerFixed("demon_roar", 64.0f);
    public static final SoundEvent DEMON_AURA = register("demon_aura");
    public static final SoundEvent DEMON_HURT = register("demon_hurt");
    public static final SoundEvent DEMON_DEATH = register("demon_death");
    public static final SoundEvent DEMON_HIT = register("demon_hit");

    // Slime
    public static final SoundEvent SLIME_CHARGE = register("slime_charge");
    public static final SoundEvent SLIME_EXPLODE = register("slime_explode");

    // Golem
    public static final SoundEvent GOLEM_STEP = register("golem_step");
    public static final SoundEvent GOLEM_HURT = register("golem_hurt");
    public static final SoundEvent GOLEM_ATTACK = register("golem_attack");

    // Portal
    public static final SoundEvent PORTAL_IGNITE = register("portal_ignite");

    // Diablo (renamed Sovereign) — ambience, phrases, death
    public static final SoundEvent DIABLO_DEATH = register("diablo_death");
    public static final SoundEvent DIABLO_THEME = register("diablo_theme");
    public static final SoundEvent FIREBIOM_SOUND = register("firebiom_sound");
    public static final SoundEvent PORTAL_SOUND = register("portal_sound");
    public static final SoundEvent LOST_LONG = register("lost_long");
    public static final SoundEvent TRY_HARDER = register("try_harder");
    public static final SoundEvent YOUR_SOUL = register("your_soul");

    private static SoundEvent register(String name) {
        ResourceLocation id = FireLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static SoundEvent registerFixed(String name, float range) {
        ResourceLocation id = FireLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, range));
    }

    public static void init() {}
}
