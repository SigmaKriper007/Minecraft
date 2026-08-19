package com.opus.structure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public record StructureLayout(
        String id,
        String[][] layers,
        Map<Character, BlockState> palette,
        Map<Character, ResourceLocation> chests,
        Map<Character, EntityType<?>> spawners
) {

    public int width() {
        return layers.length > 0 ? layers[0][0].length() : 0;
    }

    public int depth() {
        return layers.length > 0 ? layers[0].length : 0;
    }

    public int height() {
        return layers.length;
    }

    public char at(int x, int y, int z) {
        if (y < 0 || y >= layers.length) return ' ';
        String[] rows = layers[y];
        if (z < 0 || z >= rows.length) return ' ';
        String row = rows[z];
        if (x < 0 || x >= row.length()) return ' ';
        return row.charAt(x);
    }
}