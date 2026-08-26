package com.opus.fire.world;

import com.opus.fire.block.EmberBeanBlock;
import com.opus.fire.entity.FireDemonEntity;
import com.opus.fire.registry.FireBlocks;
import com.opus.fire.registry.FireEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class FireRealmBuilder {
    private static final int INNER_RING = 29;
    private static final int OUTER_RING = 37;
    private static final int RIVER_END = 120;

    private FireRealmBuilder() { }

    public static Vec3 ensureBuilt(ServerLevel level) {
        int ground = findBuiltGround(level);
        if (ground < 0) {
            ground = Math.max(1, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 0, 0) - 1);
            build(level, ground, RandomSource.create(level.getSeed() ^ 0x4F55525645494E4CL));
            spawnBoss(level, ground);
        }
        enforceWorldBorder(level);
        return new Vec3(0.5, ground + 1.1, -13.5);
    }

    private static int findBuiltGround(ServerLevel level) {
        for (int y = level.getMinBuildHeight(); y < Math.min(96, level.getMaxBuildHeight()); y++) {
            if (level.getBlockState(new BlockPos(0, y, 0)).is(FireBlocks.CRIMSON_ICE)) return y;
        }
        return -1;
    }

    private static void build(ServerLevel level, int y, RandomSource random) {
        shapeCrown(level, y);
        carveFourVeins(level, y, random);
        placeForest(level, y, random);
        placeSeal(level, y);
        buildBoundaryWall(level, y);
        level.setBlock(new BlockPos(0, y + 1, -12), FireBlocks.FIRE_PORTAL.defaultBlockState(), 3);
    }

    private static void shapeCrown(ServerLevel level, int y) {
        for (int x = -43; x <= 43; x++) {
            for (int z = -43; z <= 43; z++) {
                double distance = Math.sqrt(x * x + z * z);
                double angle = Math.atan2(z, x);
                double inner = INNER_RING + Math.sin(angle * 5.0) * 1.35 + Math.sin(angle * 11.0) * 0.55;
                double outer = OUTER_RING + Math.sin(angle * 7.0 + 0.8) * 1.6 + Math.sin(angle * 13.0) * 0.45;
                BlockPos pos = new BlockPos(x, y, z);
                if (distance >= inner && distance <= outer) {
                    lava(level, pos);
                } else if ((distance >= inner - 3 && distance < inner) || (distance > outer && distance <= outer + 3)) {
                    bank(level, pos);
                } else if (distance < inner - 3) {
                    level.setBlock(pos, FireBlocks.FIRE_SOIL.defaultBlockState(), 2);
                    level.setBlock(pos.below(), FireBlocks.MAGMA_CRUST.defaultBlockState(), 2);
                }
            }
        }
    }

    private static void carveFourVeins(ServerLevel level, int y, RandomSource random) {
        carveVein(level, y, 1, 0, random.fork());
        carveVein(level, y, -1, 0, random.fork());
        carveVein(level, y, 0, 1, random.fork());
        carveVein(level, y, 0, -1, random.fork());
    }

    private static void carveVein(ServerLevel level, int y, int dx, int dz, RandomSource random) {
        double drift = random.nextDouble() * 2.0 - 1.0;
        for (int step = OUTER_RING - 2; step <= RIVER_END; step++) {
            if (step % (7 + random.nextInt(4)) == 0) drift += random.nextDouble() * 2.0 - 1.0;
            drift = Mth.clamp(drift, -7.0, 7.0);
            double meander = drift + Math.sin(step * 0.115 + dx * 1.7 + dz * 2.9) * 2.35
                + Math.sin(step * 0.041 + dx - dz) * 1.6;
            int centerX = dx * step + (int) Math.round(dz * meander);
            int centerZ = dz * step + (int) Math.round(dx * meander);
            int halfWidth = 1 + ((step / 11 + Math.abs(dx + dz)) % 2);
            carveCrossSection(level, y, centerX, centerZ, dx, dz, halfWidth);
            if (step > 58 && step < 108 && step % 31 == 0) {
                int side = random.nextBoolean() ? 1 : -1;
                for (int branch = 1; branch <= 7; branch++) {
                    int bx = centerX + dz * side * branch + dx * branch / 2;
                    int bz = centerZ + dx * side * branch + dz * branch / 2;
                    carveCrossSection(level, y, bx, bz, dx, dz, 1);
                }
            }
        }
    }

    private static void carveCrossSection(ServerLevel level, int y, int cx, int cz, int dx, int dz, int halfWidth) {
        for (int lateral = -halfWidth - 2; lateral <= halfWidth + 2; lateral++) {
            int x = cx + dz * lateral;
            int z = cz + dx * lateral;
            BlockPos pos = new BlockPos(x, y, z);
            if (Math.abs(lateral) <= halfWidth) lava(level, pos); else bank(level, pos);
        }
    }

    private static void lava(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2);
        level.setBlock(pos.below(), FireBlocks.MAGMA_CRUST.defaultBlockState(), 2);
        level.setBlock(pos.below(2), FireBlocks.MAGMA_CRUST.defaultBlockState(), 2);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 2);
    }

    private static void bank(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.LAVA)) {
            level.setBlock(pos, FireBlocks.MAGMA_CRUST.defaultBlockState(), 2);
            level.setBlock(pos.below(), FireBlocks.MAGMA_CRUST.defaultBlockState(), 2);
        }
    }

    private static void placeForest(ServerLevel level, int y, RandomSource random) {
        int placed = 0;
        int attempts = 0;
        while (placed < 24 && attempts++ < 180) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = placed < 4 ? 18.0 + random.nextDouble() * 5.0 : 46.0 + random.nextDouble() * 68.0;
            int x = Mth.floor(Math.cos(angle) * radius);
            int z = Mth.floor(Math.sin(angle) * radius);
            BlockPos ground = new BlockPos(x, y, z);
            if (!level.getBlockState(ground).is(FireBlocks.FIRE_SOIL) || !level.getBlockState(ground.above()).isAir()) continue;
            if (nearLava(level, ground, 4)) continue;
            placeKapok(level, ground.above(), random);
            placed++;
        }
    }

    private static boolean nearLava(ServerLevel level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
            if (level.getBlockState(center.offset(x, 0, z)).is(Blocks.LAVA)) return true;
        }
        return false;
    }

    private static void placeKapok(ServerLevel level, BlockPos base, RandomSource random) {
        int height = 14 + random.nextInt(10);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int length = 3 + random.nextInt(4);
            for (int i = 0; i <= length; i++) {
                BlockPos root = base.relative(direction, i).above(Math.max(0, 2 - i));
                level.setBlock(root, FireBlocks.EMBER_LOG.defaultBlockState().setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                    direction.getAxis()), 2);
            }
        }
        for (int h = 0; h < height; h++) {
            level.setBlock(base.above(h), FireBlocks.EMBER_LOG.defaultBlockState(), 2);
            if (h < height / 2) {
                level.setBlock(base.offset(1, h, 0), FireBlocks.EMBER_LOG.defaultBlockState(), 2);
                level.setBlock(base.offset(0, h, 1), FireBlocks.EMBER_LOG.defaultBlockState(), 2);
                level.setBlock(base.offset(1, h, 1), FireBlocks.EMBER_LOG.defaultBlockState(), 2);
            }
        }
        int branches = 3 + random.nextInt(3);
        for (int branch = 0; branch < branches; branch++) {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int startY = height - 5 + random.nextInt(4);
            int length = 3 + random.nextInt(4);
            BlockPos tip = base.above(startY);
            for (int i = 1; i <= length; i++) {
                tip = base.above(startY + i / 3).relative(direction, i);
                level.setBlock(tip, FireBlocks.EMBER_LOG.defaultBlockState().setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                    direction.getAxis()), 2);
            }
            leafCrown(level, tip, 3 + random.nextInt(2), random);
        }
        leafCrown(level, base.above(height), 5, random);
        placeBeans(level, base, height, random);
    }

    public static boolean growPlayerKapok(ServerLevel level, BlockPos base, RandomSource random) {
        int height = 10 + random.nextInt(5);
        for (int y = 0; y <= height; y++) {
            BlockState state = level.getBlockState(base.above(y));
            if (!canTreeReplace(state)) return false;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int length = 1; length <= 4; length++) {
                BlockPos branch = base.above(height - 2 + length / 3).relative(direction, length);
                if (!canTreeReplace(level.getBlockState(branch))) return false;
            }
        }

        convertKapokTerrain(level, base.below(), random);
        for (int y = 0; y < height; y++) {
            level.setBlock(base.above(y), FireBlocks.EMBER_LOG.defaultBlockState(), 3);
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos tip = base.above(height - 2);
            for (int length = 1; length <= 4; length++) {
                tip = base.above(height - 2 + length / 3).relative(direction, length);
                level.setBlock(tip, FireBlocks.EMBER_LOG.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, direction.getAxis()), 3);
            }
            leafCrown(level, tip, 3, random);
        }
        leafCrown(level, base.above(height), 4, random);
        placeBeans(level, base, height, random);
        return true;
    }

    private static boolean canTreeReplace(net.minecraft.world.level.block.state.BlockState state) {
        return state.isAir() || state.is(FireBlocks.EMBER_SAPLING) || state.is(FireBlocks.EMBER_LEAVES)
            || state.is(FireBlocks.FIRE_VINE) || state.is(FireBlocks.FIRE_BEAN);
    }

    private static void convertKapokTerrain(ServerLevel level, BlockPos ground, RandomSource random) {
        for (int x = -5; x <= 5; x++) for (int z = -5; z <= 5; z++) {
            if (x * x + z * z > 25) continue;
            BlockPos surface = ground.offset(x, 0, z);
            BlockState existing = level.getBlockState(surface);
            if (existing.isAir() || !existing.getFluidState().isEmpty() || existing.is(Blocks.BEDROCK)
                    || level.getBlockEntity(surface) != null) continue;
            level.setBlock(surface, random.nextInt(7) == 0 ? FireBlocks.ASH_BLOCK.defaultBlockState()
                : FireBlocks.FIRE_SOIL.defaultBlockState(), 3);
            BlockPos foundation = surface.below();
            BlockState below = level.getBlockState(foundation);
            if (!below.isAir() && below.getFluidState().isEmpty() && !below.is(Blocks.BEDROCK)
                    && level.getBlockEntity(foundation) == null) {
                level.setBlock(foundation, FireBlocks.MAGMA_CRUST.defaultBlockState(), 3);
            }
        }
    }

    private static void leafCrown(ServerLevel level, BlockPos center, int radius, RandomSource random) {
        for (int x = -radius; x <= radius; x++) for (int y = -2; y <= 2; y++) for (int z = -radius; z <= radius; z++) {
            double shape = x * x + z * z + y * y * 3.0;
            if (shape <= radius * radius + random.nextInt(5) && random.nextInt(9) != 0) {
                BlockPos pos = center.offset(x, y, z);
                if (level.getBlockState(pos).isAir()) level.setBlock(pos, FireBlocks.EMBER_LEAVES.defaultBlockState(), 2);
            }
        }
        int vines = 2 + random.nextInt(4);
        for (int i = 0; i < vines; i++) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int z = random.nextInt(radius * 2 + 1) - radius;
            BlockPos start = center.offset(x, -2, z);
            int length = 2 + random.nextInt(8);
            for (int v = 0; v < length; v++) {
                BlockPos pos = start.below(v);
                if (!level.getBlockState(pos).isAir()) break;
                level.setBlock(pos, FireBlocks.FIRE_VINE.defaultBlockState(), 2);
            }
        }
    }

    private static void placeBeans(ServerLevel level, BlockPos base, int height, RandomSource random) {
        int count = 1 + random.nextInt(4);
        for (int i = 0; i < count; i++) {
            Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos pos = base.above(3 + random.nextInt(Math.max(2, height - 7))).relative(facing);
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, FireBlocks.FIRE_BEAN.defaultBlockState().setValue(EmberBeanBlock.FACING, facing)
                    .setValue(EmberBeanBlock.AGE, random.nextInt(3)), 2);
            }
        }
    }

    private static void placeSeal(ServerLevel level, int y) {
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
            level.setBlock(new BlockPos(x, y, z), FireBlocks.CRIMSON_ICE.defaultBlockState(), 2);
        }
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) for (int h = 1; h <= 5; h++) {
            boolean shell = (Math.abs(x) == 2 && Math.abs(z) == 2) || h == 5;
            level.setBlock(new BlockPos(x, y + h, z), shell ? FireBlocks.CRIMSON_ICE.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static boolean spawnBoss(ServerLevel level, int y) {
        AABB area = new AABB(-48, y - 8, -48, 48, y + 32, 48);
        if (!level.getEntitiesOfClass(FireDemonEntity.class, area, entity -> entity.isAlive()).isEmpty()) return false;
        FireDemonEntity demon = new FireDemonEntity(FireEntities.FIRE_DEMON, level);
        demon.setPos(0.5, y + 1.0, 0.5);
        return level.addFreshEntity(demon);
    }

    public static boolean restoreBossSeal(ServerLevel level) {
        int ground = findBuiltGround(level);
        if (ground < 0) return false;
        AABB area = new AABB(-48, ground - 8, -48, 48, ground + 32, 48);
        if (!level.getEntitiesOfClass(FireDemonEntity.class, area, Entity::isAlive).isEmpty()) return false;
        placeSeal(level, ground);
        return spawnBoss(level, ground);
    }

    /** Конец мира: глухая базальтовая стена по периметру 115×115. */
    private static final int BOUNDARY_HALF = 57; // [-57..57] = 115 блоков

    private static void buildBoundaryWall(ServerLevel level, int y) {
        for (int x = -BOUNDARY_HALF - 1; x <= BOUNDARY_HALF + 1; x++) {
            for (int z = -BOUNDARY_HALF - 1; z <= BOUNDARY_HALF + 1; z++) {
                if (Math.abs(x) <= BOUNDARY_HALF && Math.abs(z) <= BOUNDARY_HALF) continue;
                for (int yy = y - 40; yy <= y + 70; yy++) {
                    level.setBlock(new BlockPos(x, yy, z), Blocks.BEDROCK.defaultBlockState(), 2);
                }
            }
        }
    }

    private static void enforceWorldBorder(ServerLevel level) {
        var border = level.getWorldBorder();
        border.setCenter(0.0, 0.0);
        border.setSize(115.0);
        border.setWarningBlocks(6);
        border.setWarningTime(10);
    }
}
