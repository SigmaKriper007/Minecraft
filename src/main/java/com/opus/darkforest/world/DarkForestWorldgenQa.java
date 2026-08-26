package com.opus.darkforest.world;

import com.opus.OpusVsExe;
import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.registry.DarkForestBlocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

/** Development-only natural-biome probe; removes its force-load before returning. */
public final class DarkForestWorldgenQa {
    private DarkForestWorldgenQa(){ }
    public static void init(){if(com.opus.qa.DevelopmentQa.enabled(37))ServerLifecycleEvents.SERVER_STARTED.register(server->run(server.overworld()));}
    private static void run(ServerLevel level){
        BlockPos spawn=level.getSharedSpawnPos();var found=level.findClosestBiome3d(holder->holder.is(DarkForestLine.DARK_FOREST),spawn,6400,32,64);check(found!=null,"no natural biome within 6400 blocks");BlockPos biomePos=findSurfaceSample(level,found.getFirst());check(biomePos!=null,"nearby climate cell had no Dark Forest surface");int chunkX=biomePos.getX()>>4,chunkZ=biomePos.getZ()>>4;long chunkKey=ChunkPos.asLong(chunkX,chunkZ);boolean alreadyForced=level.getForcedChunks().contains(chunkKey);
        try{
            if(!alreadyForced)check(level.setChunkForced(chunkX,chunkZ,true),"could not force sample chunk");level.getChunk(chunkX,chunkZ);
            int grass=0,logs=0,leaves=0;int minX=chunkX<<4,minZ=chunkZ<<4;
            for(int x=minX;x<minX+16;x++)for(int z=minZ;z<minZ+16;z++)for(int y=level.getMinBuildHeight();y<level.getMaxBuildHeight();y++){var state=level.getBlockState(new BlockPos(x,y,z));if(state.is(DarkForestBlocks.MOONLIT_GRASS))grass++;else if(state.is(DarkForestBlocks.GLOOMWOOD_LOG))logs++;else if(state.is(DarkForestBlocks.GLOOMWOOD_LEAVES))leaves++;}
            check(grass>=16,"custom surface count was "+grass+" at "+biomePos);check(logs>=4,"Gloomwood log count was "+logs+" at "+biomePos);check(leaves>=16,"Gloomwood leaf count was "+leaves+" at "+biomePos);
            check(level.registryAccess().registryOrThrow(Registries.STRUCTURE).containsKey(DarkForestLine.id("moon_fountain")),"Moon Fountain structure missing");check(level.registryAccess().registryOrThrow(Registries.STRUCTURE_SET).containsKey(DarkForestLine.id("moon_fountain")),"Moon Fountain structure set missing");check(level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).containsKey(DarkForestLine.id("dark_forest_floor")),"forest floor placed feature missing");check(level.getStructureManager().get(DarkForestLine.id("moon_fountain/main")).isPresent(),"Moon Fountain template missing");
            OpusVsExe.LOGGER.info("Task 37 QA PASS: natural Dark Forest at {} ({} blocks), sample chunk grass/log/leaves={}/{}/{}, Moon Fountain structure/set/template and floor feature loaded",biomePos,Math.round(Math.sqrt(level.getSharedSpawnPos().distSqr(biomePos))),grass,logs,leaves);
        }finally{if(!alreadyForced)level.setChunkForced(chunkX,chunkZ,false);}
    }
    private static BlockPos findSurfaceSample(ServerLevel level,BlockPos near){var source=level.getChunkSource().getGenerator().getBiomeSource();var generator=level.getChunkSource().getGenerator();var randomState=level.getChunkSource().randomState();for(int radius=0;radius<=384;radius+=16)for(int dx=-radius;dx<=radius;dx+=16)for(int dz=-radius;dz<=radius;dz+=16){if(radius>0&&Math.abs(dx)!=radius&&Math.abs(dz)!=radius)continue;int x=near.getX()+dx,z=near.getZ()+dz,y=generator.getBaseHeight(x,z,Heightmap.Types.WORLD_SURFACE_WG,level,randomState)-1;if(source.getNoiseBiome(QuartPos.fromBlock(x),QuartPos.fromBlock(y),QuartPos.fromBlock(z),randomState.sampler()).is(DarkForestLine.DARK_FOREST))return new BlockPos(x,y,z);}return null;}
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Task 37 QA: "+message);}
}
