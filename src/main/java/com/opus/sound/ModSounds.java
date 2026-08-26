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
    public static final SoundEvent EXO_SHIELD = register("exo_shield");
    public static final SoundEvent EXO_LASER = register("exo_laser");
    public static final SoundEvent EXO_SLAM = register("exo_slam");
    public static final SoundEvent EXO_ULTRA = register("exo_ultra");
    public static final SoundEvent EXO_EXTRA = register("exo_extra");
    public static final SoundEvent DOOM_ETERNAL = registerFixed("doom_eternal", 64.0f);
    public static final SoundEvent DOOM_ETERNAL_DISC = registerFixed("doom_eternal_disc", 16.0f);
    public static final SoundEvent HAIKU_SUMMON = registerFixed("haiku_summon", 64.0f);

    // Финальный босс Омега (задача 13)
    public static final SoundEvent BOSS_ROAR = registerFixed("boss_roar", 64.0f);
    public static final SoundEvent BOSS_STEP = registerFixed("boss_step", 48.0f);
    public static final SoundEvent BOSS_PUNCH = registerFixed("boss_punch", 48.0f);
    public static final SoundEvent BOSS_TURRET_SHOT = register("boss_turret_shot");
    public static final SoundEvent BOSS_ORBITAL_WARN = registerFixed("boss_orbital_warn", 64.0f);
    public static final SoundEvent BOSS_LASER = registerFixed("boss_laser", 64.0f);
    public static final SoundEvent BOSS_RING_BURST = registerFixed("boss_ring_burst", 64.0f);
    public static final SoundEvent BOSS_TELEPORT = registerFixed("boss_teleport", 48.0f);
    public static final SoundEvent BOSS_CORE_HIT = register("boss_core_hit");
    public static final SoundEvent BOSS_DEFLECT = register("boss_deflect");
    public static final SoundEvent BOSS_PHASE_SHIFT = registerFixed("boss_phase_shift", 64.0f);
    public static final SoundEvent BOSS_SLAM = registerFixed("boss_slam", 64.0f);
    /** Взрыв смерти (как у эндер-дракона) — звучит в death-анимации Омеги. */
    public static final SoundEvent BOSS_EXPLOSION = registerFixed("boss_explosion", 64.0f);

    // Пользовательские звуки (2026-08-22)
    public static final SoundEvent HAIKU_OMEGA_DEATH = registerFixed("haiku_omega_death", 64.0f);
    public static final SoundEvent OMEGA_RING_WAVE = registerFixed("omega_ring_wave", 64.0f);
    public static final SoundEvent OMEGA_SLASH_HIT = registerFixed("omega_slash", 64.0f);
    public static final SoundEvent SKY_LASER_WARN = registerFixed("sky_laser_warn", 64.0f);
    public static final SoundEvent SKY_LASER_OMEGA = registerFixed("sky_laser_omega", 64.0f);
    public static final SoundEvent ALTAR_HEART_LOOP = register("altar_heart_loop"); // стримится пьнером
    public static final SoundEvent MENU_MUSIC = register("menu_music"); // музыка главного меню (кровавая луна)

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
