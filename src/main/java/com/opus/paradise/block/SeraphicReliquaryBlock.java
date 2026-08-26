package com.opus.paradise.block;

import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public final class SeraphicReliquaryBlock extends Block {
    public static final BooleanProperty CHARGED=BooleanProperty.create("charged");
    private static final VoxelShape SHAPE=Shapes.or(box(2,0,2,14,3,14),box(5,3,5,11,9,11),box(3,9,3,13,12,13),box(1,11,5,15,14,11));
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.24F,.92F,1F),.9F);
    public SeraphicReliquaryBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(CHARGED,false));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(CHARGED);}
    @Override public VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        ItemStack held=player.getItemInHand(hand);boolean charged=state.getValue(CHARGED);
        if(!charged&&held.is(ParadiseItems.SERAPHIC_PINIONS)){
            if(!level.isClientSide){held.shrink(1);level.setBlock(pos,state.setValue(CHARGED,true),3);level.playSound(null,pos,SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.BLOCKS,1.2F,1.25F);if(level instanceof ServerLevel server)server.sendParticles(CYAN,pos.getX()+.5,pos.getY()+1.05,pos.getZ()+.5,14,.28,.18,.28,.025);player.displayClientMessage(Component.translatable("message.opusvsexe.reliquary_charged"),true);}return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(charged&&held.isEmpty()&&player.isShiftKeyDown()){
            if(!level.isClientSide){level.setBlock(pos,state.setValue(CHARGED,false),3);ItemStack returned=new ItemStack(ParadiseItems.SERAPHIC_PINIONS);if(!player.getInventory().add(returned))player.drop(returned,false);level.playSound(null,pos,SoundEvents.ITEM_PICKUP,SoundSource.BLOCKS,.8F,1.35F);player.displayClientMessage(Component.translatable("message.opusvsexe.reliquary_retrieved"),true);}return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(!level.isClientSide)player.displayClientMessage(Component.translatable(charged?"message.opusvsexe.reliquary_ready":"message.opusvsexe.reliquary_requires_pinion"),true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
