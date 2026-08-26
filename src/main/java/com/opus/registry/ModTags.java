package com.opus.registry;

import com.opus.OpusVsExe;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    public static final TagKey<Item> KATANAS = TagKey.create(Registries.ITEM, OpusVsExe.id("katanas"));
    /** Opus weapons and tools: the only player-held items that can damage Haiku chassis. */
    public static final TagKey<Item> OPUS_WEAPON = TagKey.create(Registries.ITEM, OpusVsExe.id("opus_weapon"));

    private ModTags() {
    }
}
