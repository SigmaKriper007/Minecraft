package com.opus.ember.sound;

import com.opus.ember.EmberLine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class EmberSounds {
    // Armor & items
    public static final SoundEvent EMBER_EQUIP = register("ember_equip");
    public static final SoundEvent EMBER_CHARGE = register("ember_charge");
    public static final SoundEvent EMBER_FIREBALL_LAUNCH = register("ember_fireball_launch");
    public static final SoundEvent EMBER_EXPLODE = register("ember_explode");

    // Flame Demon
    public static final SoundEvent FLAME_DEMON_ROAR = registerFixed("flame_demon_roar", 64.0f);
    public static final SoundEvent FLAME_DEMON_AURA = register("flame_demon_aura");
    public static final SoundEvent FLAME_DEMON_HURT = register("flame_demon_hurt");
    public static final SoundEvent FLAME_DEMON_DEATH = register("flame_demon_death");
    public static final SoundEvent FLAME_DEMON_HIT = register("flame_demon_hit");

    // Ember Slime
    public static final SoundEvent EMBER_SLIME_CHARGE = register("ember_slime_charge");
    public static final SoundEvent EMBER_SLIME_EXPLODE = register("ember_slime_explode");

    // Obsidian Golem
    public static final SoundEvent OBSIDIAN_GOLEM_STEP = register("obsidian_golem_step");
    public static final SoundEvent OBSIDIAN_GOLEM_HURT = register("obsidian_golem_hurt");
    public static final SoundEvent OBSIDIAN_GOLEM_ATTACK = register("obsidian_golem_attack");

    private static SoundEvent register(String name) {
        ResourceLocation id = EmberLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static SoundEvent registerFixed(String name, float range) {
        ResourceLocation id = EmberLine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, range));
    }

    public static void init() {}
}
