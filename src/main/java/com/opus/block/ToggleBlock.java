package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ToggleBlock extends Block {
    public ToggleBlock(Properties properties) {
        super(properties);
    }

    public abstract BooleanProperty getToggleProperty();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getToggleProperty());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        BooleanProperty property = getToggleProperty();
        boolean current = state.getValue(property);
        level.setBlock(pos, state.setValue(property, !current), 3);
        boolean openLike = property.getName().equals("open");
        level.playSound(null, pos,
                openLike
                        ? (current ? SoundEvents.IRON_DOOR_CLOSE : SoundEvents.IRON_DOOR_OPEN)
                        : SoundEvents.LEVER_CLICK,
                SoundSource.BLOCKS, 1.0f, 1.0f);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public boolean isToggledOn(BlockState state) {
        return state.getValue(getToggleProperty());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (isToggledOn(state) && random.nextInt(3) == 0) {
            double x = (double) pos.getX() + 0.5 + random.nextDouble() * 0.6 - 0.3;
            double y = (double) pos.getY() + 1.02;
            double z = (double) pos.getZ() + 0.5 + random.nextDouble() * 0.6 - 0.3;
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.15, 0.0);
        }
    }

    public static class OpenToggleBlock extends ToggleBlock {
        public static final BooleanProperty OPEN = BooleanProperty.create("open");

        public OpenToggleBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BooleanProperty getToggleProperty() {
            return OPEN;
        }
    }

    public static class ActiveToggleBlock extends ToggleBlock {
        public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

        public ActiveToggleBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BooleanProperty getToggleProperty() {
            return ACTIVE;
        }
    }
}