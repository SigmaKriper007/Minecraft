package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Светящийся янтарный блок Haiku. Кроме свечения (lightLevel) испускает
 * редкие янтарные частицы-искры — «дыхание» энергии машин.
 */
public class AmberGlowBlock extends Block {
    public AmberGlowBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) == 0) {
            double x = (double) pos.getX() + 0.3 + random.nextDouble() * 0.4;
            double y = (double) pos.getY() + 0.3 + random.nextDouble() * 0.4;
            double z = (double) pos.getZ() + 0.3 + random.nextDouble() * 0.4;
            // AMBIENT_ENTITY_EFFECT окрашивается через velocity (r, g, b)
            level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, x, y, z,
                    0.91, 0.58, 0.12);
        }
        // Иногда — вспышка на поверхности
        if (random.nextInt(14) == 0) {
            double x = (double) pos.getX() + random.nextDouble();
            double y = (double) pos.getY() + 1.0 + random.nextDouble() * 0.1;
            double z = (double) pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.03, 0.0);
        }
    }
}
