package com.opus.darkforest.world;

import com.mojang.serialization.Codec;
import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.registry.DarkForestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Converts only Dark Forest surface columns and builds clearance-checked dense Gloomwood. */
public final class DarkForestFeature extends Feature<NoneFeatureConfiguration> {
    public DarkForestFeature(Codec<NoneFeatureConfiguration> codec){super(codec);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        WorldGenLevel level=context.level();RandomSource random=context.random();BlockPos origin=context.origin();int minX=(origin.getX()>>4)<<4,minZ=(origin.getZ()>>4)<<4;int converted=0,trees=0;
        for(int x=minX;x<minX+16;x++)for(int z=minZ;z<minZ+16;z++){int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;BlockPos top=new BlockPos(x,y,z);if(!level.getBiome(top).is(DarkForestLine.DARK_FOREST))continue;BlockState state=level.getBlockState(top);if(state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.DIRT)||state.is(Blocks.PODZOL)||state.is(Blocks.COARSE_DIRT)){level.setBlock(top,DarkForestBlocks.MOONLIT_GRASS.defaultBlockState(),2);for(int depth=1;depth<=2;depth++){BlockPos below=top.below(depth);BlockState buried=level.getBlockState(below);if(buried.is(Blocks.DIRT)||buried.is(Blocks.COARSE_DIRT)||buried.is(Blocks.PODZOL))level.setBlock(below,DarkForestBlocks.MOONLIT_SOIL.defaultBlockState(),2);}converted++;}}
        for(int attempt=0;attempt<8;attempt++){int x=minX+1+random.nextInt(14),z=minZ+1+random.nextInt(14),y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;BlockPos ground=new BlockPos(x,y,z);if(level.getBlockState(ground).is(DarkForestBlocks.MOONLIT_GRASS)&&level.getBiome(ground).is(DarkForestLine.DARK_FOREST)&&placeTree(level,ground.above(),random))trees++;}
        for(int attempt=0;attempt<18;attempt++){int x=minX+random.nextInt(16),z=minZ+random.nextInt(16),y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z);BlockPos pos=new BlockPos(x,y,z);if(level.getBlockState(pos.below()).is(DarkForestBlocks.MOONLIT_GRASS)&&level.getBlockState(pos).isAir()&&level.getBiome(pos).is(DarkForestLine.DARK_FOREST))level.setBlock(pos,random.nextInt(4)==0?DarkForestBlocks.MOONFLOWER.defaultBlockState():DarkForestBlocks.THORN_FERN.defaultBlockState(),2);}
        return converted>0||trees>0;
    }
    private static boolean placeTree(WorldGenLevel level,BlockPos base,RandomSource random){int height=8+random.nextInt(5);for(int y=0;y<=height+3;y++){int radius=y<height-3?1:4;for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++){BlockPos pos=base.offset(x,y,z);BlockState state=level.getBlockState(pos);if(!state.isAir()&&!state.is(DarkForestBlocks.GLOOMWOOD_LEAVES)&&!state.canBeReplaced())return false;if(!state.getFluidState().isEmpty()||level.getBlockEntity(pos)!=null)return false;}}
        BlockState log=DarkForestBlocks.GLOOMWOOD_LOG.defaultBlockState();BlockState leaves=DarkForestBlocks.GLOOMWOOD_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT,false).setValue(LeavesBlock.DISTANCE,1);for(int y=0;y<height;y++)level.setBlock(base.above(y),log,2);BlockPos crown=base.above(height-2);
        for(int oy=-2;oy<=3;oy++){int radius=oy<=0?4:oy==1?3:2;for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++){if(x*x+z*z>radius*radius+2||(x==0&&z==0&&oy<=1))continue;BlockPos pos=crown.offset(x,oy,z);if(level.getBlockState(pos).isAir())level.setBlock(pos,leaves,2);}}
        for(int[] branch:new int[][]{{2,0},{-2,0},{0,2},{0,-2}}){BlockPos joint=base.offset(branch[0]/2,height-3,branch[1]/2);level.setBlock(joint,log,2);BlockPos tip=base.offset(branch[0],height-2,branch[1]);level.setBlock(tip,log,2);}
        return true;
    }
}
