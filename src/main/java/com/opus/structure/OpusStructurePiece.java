package com.opus.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class OpusStructurePiece extends StructurePiece {

    private final StructureLayout layout;
    private final Rotation rotation;

    public OpusStructurePiece(StructurePieceType type, int depth, BoundingBox box, StructureLayout layout, Rotation rotation) {
        super(type, depth, box);
        this.layout = layout;
        this.rotation = rotation;
    }

    public OpusStructurePiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        this.layout = ModStructures.getLayout(tag.getString("Layout"));
        this.rotation = tag.contains("Rotation") ? Rotation.valueOf(tag.getString("Rotation")) : Rotation.NONE;
    }

    public static BoundingBox makeBoundingBox(BlockPos origin, StructureLayout layout) {
        return BoundingBox.fromCorners(origin, origin.offset(layout.width() - 1, layout.height() - 1, layout.depth() - 1));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Layout", layout != null ? layout.id() : "");
        tag.putString("Rotation", rotation.name());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator,
                            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos blockPos) {
        if (layout == null) return;
        BoundingBox bbox = this.boundingBox;
        OpusStructure.debugLog("Generating opus structure '" + layout.id() + "' at " + bbox);
        for (int y = 0; y < layout.height(); y++) {
            for (int z = 0; z < layout.depth(); z++) {
                for (int x = 0; x < layout.width(); x++) {
                    char c = layout.at(x, y, z);
                    if (c == ' ' || c == '.') continue;
                    BlockPos pos = transformedPos(bbox, x, y, z);
                    if (!box.isInside(pos)) continue;
                    placeElement(level, box, random, pos, x, y, z, c);
                }
            }
        }
    }

    private BlockPos transformedPos(BoundingBox bbox, int x, int y, int z) {
        int maxX = layout.width() - 1, maxZ = layout.depth() - 1;
        int tx = x, tz = z;
        switch (rotation) {
            case CLOCKWISE_90 -> { tx = maxZ - z; tz = x; }
            case CLOCKWISE_180 -> { tx = maxX - x; tz = maxZ - z; }
            case COUNTERCLOCKWISE_90 -> { tx = z; tz = maxX - x; }
            default -> { }
        }
        return new BlockPos(bbox.minX() + tx, bbox.minY() + y, bbox.minZ() + tz);
    }

    private void placeElement(WorldGenLevel level, BoundingBox box, RandomSource random, BlockPos pos,
                              int x, int y, int z, char c) {
        BlockState state = layout.palette().get(c);
        EntityType<?> spawnerType = layout.spawners().get(c);
        ResourceLocation chestLoot = layout.chests().get(c);

        if (c == 'D') {
            boolean upper = layout.at(x, y - 1, z) == 'D';
            BlockState door = Blocks.IRON_DOOR.defaultBlockState()
                    .setValue(DoorBlock.HALF, upper ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
            setBlock(level, box, pos, rotate(door));
            return;
        }
        if (state == null && spawnerType == null && chestLoot == null) return;

        if (spawnerType != null) {
            BlockState spawnerState = Blocks.SPAWNER.defaultBlockState();
            setBlock(level, box, pos, spawnerState);
            if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                CompoundTag spawnerTag = new CompoundTag();
                CompoundTag spawnDataTag = new CompoundTag();
                CompoundTag entityTag = new CompoundTag();
                entityTag.putString("id", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(spawnerType).toString());
                spawnDataTag.put("entity", entityTag);
                spawnerTag.put("SpawnData", spawnDataTag);
                spawner.getSpawner().load(null, pos, spawnerTag);
            }
            return;
        }
        if (chestLoot != null) {
            setBlock(level, box, pos, rotate(Blocks.CHEST.defaultBlockState()));
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                chest.setLootTable(chestLoot, random.nextLong());
            }
            return;
        }

        setBlock(level, box, pos, rotate(state));
    }

    private BlockState rotate(BlockState state) {
        return state.rotate(rotation);
    }

    private void setBlock(WorldGenLevel level, BoundingBox box, BlockPos pos, BlockState state) {
        if (box.isInside(pos)) {
            level.setBlock(pos, state, 2);
        }
    }
}