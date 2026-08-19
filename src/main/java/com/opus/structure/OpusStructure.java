package com.opus.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class OpusStructure extends Structure {

    public static final Codec<OpusStructure> CODEC = RecordCodecBuilder.<OpusStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            Codec.STRING.fieldOf("layout").forGetter(OpusStructure::getLayoutId)
    ).apply(instance, OpusStructure::new)).codec();

    private final String layoutId;

    public OpusStructure(StructureSettings settings, String layoutId) {
        super(settings);
        this.layoutId = layoutId;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public static void debugLog(String message) {
        com.opus.OpusVsExe.LOGGER.info(message);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            StructureLayout layout = ModStructures.getLayout(layoutId);
            if (layout == null) return;
            Rotation rotation = Rotation.getRandom(context.random());
            int x = context.chunkPos().getMinBlockX() + 8;
            int z = context.chunkPos().getMinBlockZ() + 8;
            int y = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                    context.heightAccessor(), context.randomState());
            BlockPos start = new BlockPos(x, y, z);
            builder.addPiece(new OpusStructurePiece(
                    ModStructures.PIECE_TYPE, 0,
                    OpusStructurePiece.makeBoundingBox(start, layout),
                    layout, rotation));
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.STRUCTURE_TYPE;
    }
}