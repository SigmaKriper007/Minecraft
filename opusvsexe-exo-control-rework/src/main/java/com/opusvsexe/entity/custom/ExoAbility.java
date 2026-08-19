package com.opusvsexe.entity.custom;

/**
 * One ability bound to one ability slot of a suit.
 *
 * Cost and cooldown are declared once, in one place, and are validated by the
 * server before the effect runs. The old code checked energy in three different
 * places and had no cooldowns at all, which is why abilities could be spammed.
 */
public record ExoAbility(String key, int energyCost, int cooldown) {

    public static final ExoAbility NONE = new ExoAbility("none", 0, 0);

    public boolean isNone() {
        return this == NONE;
    }

    /** Translation key used by the HUD, e.g. ability.opusvsexe.fist_slam */
    public String translationKey() {
        return "ability.opusvsexe." + this.key;
    }
}
