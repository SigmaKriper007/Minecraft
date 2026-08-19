package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PulsingCoreBlock extends Block {
    public PulsingCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double x = (double) pos.getX() + 0.5 + random.nextDouble() * 0.8 - 0.4;
            double y = (double) pos.getY() + 0.5 + random.nextDouble() * 0.8 - 0.4;
            double z = (double) pos.getZ() + 0.5 + random.nextDouble() * 0.8 - 0.4;
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.05, 0.0);
        }
    }
}