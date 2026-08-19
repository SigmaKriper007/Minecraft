package com.opus.registry;

import com.opus.OpusVsExe;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    public static final TagKey<Item> KATANAS = TagKey.create(Registries.ITEM, OpusVsExe.id("katanas"));

    private ModTags() {
    }
}