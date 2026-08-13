package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ResonanceForgeBlock extends Block {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    
    public ResonanceForgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = (double)pos.getX() + 0.5 + random.nextDouble() * 0.4 - 0.2;
            double y = (double)pos.getY() + 0.8 + random.nextDouble() * 0.4 - 0.2;
            double z = (double)pos.getZ() + 0.5 + random.nextDouble() * 0.4 - 0.2;
            level.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0, 0.1, 0.0);
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, 
                    SoundSource.BLOCKS, 0.5f + random.nextFloat() * 0.3f, 
                    0.8f + random.nextFloat() * 0.4f, false);
            }
        }
    }
}
