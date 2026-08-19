package com.opus.structure;

import com.opus.OpusVsExe;
import com.opus.registry.ModBlocks;
import com.opus.registry.ModEntities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.HashMap;
import java.util.Map;

/** Hand-authored, deterministic ruins. Every marker is validated before the game loads. */
public final class ModStructures {
    public static StructurePieceType PIECE_TYPE;
    public static final net.minecraft.world.level.levelgen.structure.StructureType<OpusStructure> STRUCTURE_TYPE = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE, OpusVsExe.id("opus_structure"), () -> OpusStructure.CODEC);
    private static final Map<String, StructureLayout> LAYOUTS = new HashMap<>();

    public static void init() {
        PIECE_TYPE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE, OpusVsExe.id("opus_piece"),
                (context, tag) -> new OpusStructurePiece(PIECE_TYPE, tag));
        LAYOUTS.put("resonance_mine", build("resonance_mine", 23, 7, 23, 'o', '1', '2', 'c', 1));
        LAYOUTS.put("kimi_lab", build("kimi_lab", 25, 8, 25, 's', '1', '2', 'C', 2));
        LAYOUTS.put("kodi_forge", build("kodi_forge", 23, 8, 23, 'g', '2', 'E', 'C', 3));
        LAYOUTS.put("exo_hangar", build("exo_hangar", 29, 10, 29, 'k', 'E', 'Q', 'C', 4));
        LAYOUTS.put("haiku_citadel", build("haiku_citadel", 31, 12, 31, 't', '4', '5', 'O', 5));
        LAYOUTS.values().forEach(ModStructures::validate);
        OpusVsExe.LOGGER.info("Loaded {} lore structures with trials", LAYOUTS.size());
    }

    public static StructureLayout getLayout(String id) { return LAYOUTS.get(id); }

    private static StructureLayout build(String id, int w, int h, int d, char accent, char firstMob,
                                         char secondMob, char chestMarker, int theme) {
        char[][][] a = new char[h][d][w];
        for (int y = 0; y < h; y++) for (int z = 0; z < d; z++) for (int x = 0; x < w; x++) a[y][z][x] = '.';
        for (int z = 0; z < d; z++) for (int x = 0; x < w; x++) {
            a[0][z][x] = (x == 0 || z == 0 || x == w - 1 || z == d - 1) ? '#' : '=';
            a[h - 1][z][x] = '#';
        }
        for (int y = 1; y < h - 1; y++) for (int z = 0; z < d; z++) for (int x = 0; x < w; x++)
            if (x == 0 || z == 0 || x == w - 1 || z == d - 1) a[y][z][x] = '#';
        // A split floor turns each ruin into rooms instead of a hollow box.
        for (int z = 4; z < d - 4; z++) { a[2][z][w / 2] = '#'; if (z % 4 == 0) a[1][z][w / 2] = 'X'; }
        for (int x = 4; x < w - 4; x++) { a[3][d / 2][x] = '#'; if (x % 4 == 0) a[1][d / 2][x] = 'X'; }
        a[1][d - 1][w / 2] = 'D'; a[2][d - 1][w / 2] = 'D'; a[1][d - 2][w / 2] = 'p';
        // Trial arena and lore beacon.
        a[1][d / 2][w / 2] = accent; a[2][d / 2][w / 2] = 'k';
        a[1][2][2] = firstMob; a[1][d - 3][w - 3] = secondMob;
        a[1][2][w - 3] = chestMarker;
        a[1][d - 3][2] = (theme == 1 ? 'c' : 'C');
        for (int i = 3; i < w - 3; i += 5) { a[1][2][i] = 'T'; a[1][d - 3][i] = 'L'; }
        if (theme == 1) { for (int z = 5; z < d - 5; z += 3) a[1][z][3] = 'o'; }
        if (theme == 2) { a[1][5][5] = 's'; a[1][5][w - 6] = 's'; a[1][d - 6][5] = 's'; a[1][d - 6][w - 6] = 's'; }
        if (theme == 3) { a[1][5][5] = 'g'; a[1][5][w - 6] = 'g'; a[1][d - 6][w / 2] = chestMarker; }
        if (theme == 4) { for (int y = 3; y < h - 1; y += 2) a[y][d / 2][w / 2] = 'k'; a[1][d / 2][w / 2 - 5] = 'E'; }
        if (theme == 5) { a[4][d / 2][w / 2] = 'O'; a[5][d / 2][w / 2] = 'k'; a[1][d / 2][w / 2 - 6] = '4'; a[1][d / 2][w / 2 + 6] = '5'; }
        Map<Character, BlockState> p = palette();
        Map<Character, ResourceLocation> chests = new HashMap<>();
        chests.put('c', loot(id + "_treasure")); chests.put('C', loot(id + (id.equals("kimi_lab") ? "_memory" : "_archive")));
        return new StructureLayout(id, strings(a), p, chests, spawners());
    }

    private static String[][] strings(char[][][] a) { String[][] out = new String[a.length][]; for (int y = 0; y < a.length; y++) { out[y] = new String[a[y].length]; for (int z = 0; z < a[y].length; z++) out[y][z] = new String(a[y][z]); } return out; }
    private static Map<Character, BlockState> palette() { Map<Character, BlockState> p = new HashMap<>(); p.put('#', Blocks.DEEPSLATE_BRICKS.defaultBlockState()); p.put('=', Blocks.DEEPSLATE_TILES.defaultBlockState()); p.put('X', Blocks.CHISELED_DEEPSLATE.defaultBlockState()); p.put('T', Blocks.TORCH.defaultBlockState()); p.put('L', Blocks.WALL_TORCH.defaultBlockState()); p.put('B', Blocks.STONE_BUTTON.defaultBlockState()); p.put('p', Blocks.STONE_PRESSURE_PLATE.defaultBlockState()); p.put('D', Blocks.IRON_DOOR.defaultBlockState()); p.put('+', Blocks.DARK_OAK_FENCE.defaultBlockState()); p.put('r', ModBlocks.RAW_OPUS_BLOCK.defaultBlockState()); p.put('s', ModBlocks.STABILIZED_OPUS_BLOCK.defaultBlockState()); p.put('t', ModBlocks.RESONANT_OPUS_BLOCK.defaultBlockState()); p.put('k', ModBlocks.CORE_OPUS_BLOCK.defaultBlockState()); p.put('o', ModBlocks.OPUS_ORE.defaultBlockState()); p.put('g', ModBlocks.RESONANCE_FORGE.defaultBlockState()); return p; }
    private static Map<Character, EntityType<?>> spawners() { Map<Character, EntityType<?>> m = new HashMap<>(); m.put('1', ModEntities.HAIKU_1_5); m.put('2', ModEntities.HAIKU_2); m.put('3', ModEntities.HAIKU_3); m.put('4', ModEntities.HAIKU_4); m.put('5', ModEntities.HAIKU_5); m.put('O', ModEntities.HAIKU_OMEGA); m.put('E', ModEntities.EXO_1_SENTINEL); m.put('Q', ModEntities.EXO_2_HUNTER); m.put('V', ModEntities.EXO_3_VANGUARD); m.put('Y', ModEntities.EXO_4_TITAN); m.put('Z', ModEntities.EXO_5_VENGEANCE); return m; }
    private static ResourceLocation loot(String path) { return OpusVsExe.id("structures/" + path); }
    private static void validate(StructureLayout l) { if (l.width() < 16 || l.depth() < 16 || l.height() < 5) throw new IllegalStateException("Structure too small: " + l.id()); for (String[] layer : l.layers()) { if (layer.length != l.depth()) throw new IllegalStateException("Bad depth: " + l.id()); for (String row : layer) if (row.length() != l.width()) throw new IllegalStateException("Bad width: " + l.id()); } }
}
