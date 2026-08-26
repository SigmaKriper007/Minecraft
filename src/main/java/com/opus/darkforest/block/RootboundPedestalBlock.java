package com.opus.darkforest.block;

import com.opus.darkforest.registry.DarkForestItems;
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

public final class RootboundPedestalBlock extends Block {
    public static final BooleanProperty CHARGED=BooleanProperty.create("charged");
    private static final VoxelShape EMPTY_SHAPE=Shapes.or(box(1,0,1,15,3,15),box(3,3,3,13,8,13),box(5,8,5,11,12,11));
    private static final VoxelShape CHARGED_SHAPE=Shapes.or(EMPTY_SHAPE,box(6,11,6,10,15,10));
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.35F,.9F,.92F),.9F);

    public RootboundPedestalBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(CHARGED,false));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(CHARGED);}
    @Override public VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return state.getValue(CHARGED)?CHARGED_SHAPE:EMPTY_SHAPE;}

    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        ItemStack held=player.getItemInHand(hand);boolean charged=state.getValue(CHARGED);
        if(!charged&&held.is(DarkForestItems.ROOTBOUND_EYE)){
            if(!level.isClientSide){held.shrink(1);level.setBlock(pos,state.setValue(CHARGED,true),3);level.playSound(null,pos,SoundEvents.SCULK_CATALYST_BLOOM,SoundSource.BLOCKS,1F,.8F);if(level instanceof ServerLevel server)server.sendParticles(CYAN,pos.getX()+.5,pos.getY()+.85,pos.getZ()+.5,16,.24,.28,.24,.025);player.displayClientMessage(Component.translatable("message.opusvsexe.rootbound_pedestal_charged"),true);}return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(charged&&held.isEmpty()&&player.isShiftKeyDown()){
            if(!level.isClientSide){level.setBlock(pos,state.setValue(CHARGED,false),3);ItemStack returned=new ItemStack(DarkForestItems.ROOTBOUND_EYE);if(!player.getInventory().add(returned))player.drop(returned,false);level.playSound(null,pos,SoundEvents.ITEM_PICKUP,SoundSource.BLOCKS,.8F,.75F);player.displayClientMessage(Component.translatable("message.opusvsexe.rootbound_pedestal_retrieved"),true);}return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(!level.isClientSide)player.displayClientMessage(Component.translatable(charged?"message.opusvsexe.rootbound_pedestal_ready":"message.opusvsexe.rootbound_pedestal_requires_eye"),true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
