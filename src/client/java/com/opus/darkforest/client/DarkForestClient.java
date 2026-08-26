package com.opus.darkforest.client;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.registry.DarkForestBlocks;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.client.renderer.GloomBroodmotherRenderer;
import com.opus.darkforest.client.renderer.GloomWebRenderer;
import com.opus.darkforest.client.renderer.MoonwingBatRenderer;
import com.opus.darkforest.client.renderer.MoonwingPulseRenderer;
import com.opus.darkforest.client.renderer.ShadeSpiderlingRenderer;
import com.opus.darkforest.client.renderer.MossboundAttackRenderer;
import com.opus.darkforest.client.renderer.MossboundEndermanRenderer;
import com.opus.darkforest.client.model.DarkForestArmorModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class DarkForestClient {
    private static final Vec3 MOON_SKY=new Vec3(.035D,.028D,.105D);
    private static final Vec3 MOON_FOG=new Vec3(.055D,.075D,.105D);
    private static float previousStrength,strength;
    private DarkForestClient(){ }
    public static void init(){
        EntityModelLayerRegistry.registerModelLayer(DarkForestArmorModel.BRIAR_LAYER,DarkForestArmorModel::createBriarLayer);
        EntityModelLayerRegistry.registerModelLayer(DarkForestArmorModel.VESTMENTS_LAYER,DarkForestArmorModel::createVestmentsLayer);
        BlockRenderLayerMap.INSTANCE.putBlock(DarkForestBlocks.GLOOMWOOD_LEAVES,RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DarkForestBlocks.GLOOMWOOD_SAPLING,RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DarkForestBlocks.MOONFLOWER,RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DarkForestBlocks.THORN_FERN,RenderType.cutout());
        EntityRendererRegistry.register(DarkForestEntities.SHADE_SPIDERLING,ShadeSpiderlingRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.GLOOM_BROODMOTHER,GloomBroodmotherRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.MOONWING_BAT,MoonwingBatRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.GLOOM_WEB,GloomWebRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.MOONWING_PULSE,MoonwingPulseRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.MOSSBOUND_ENDERMAN,MossboundEndermanRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.ROOT_SNARE,MossboundAttackRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.MARKED_STEP,MossboundAttackRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.MOONWELL_ORB,MossboundAttackRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.BLOOMFALL,MossboundAttackRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.ECHO_DOUBLE,MossboundAttackRenderer::new);
        EntityRendererRegistry.register(DarkForestEntities.ECLIPSE_RUSH,MossboundAttackRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client->{previousStrength=strength;boolean inside=client.player!=null&&client.level!=null&&client.level.getBiome(client.player.blockPosition()).is(DarkForestLine.DARK_FOREST);strength=Mth.clamp(strength+(inside?1F/40F:-1F/40F),0F,1F);});
    }
    public static float strength(float partialTick){return Mth.lerp(partialTick,previousStrength,strength);}
    public static Vec3 tintSky(Vec3 original){return mix(original,MOON_SKY,strength);}
    public static Vec3 tintFog(Vec3 original){return mix(original,MOON_FOG,strength);}
    public static float moonAngle(float original,float partialTick){float s=strength(partialTick);float delta=.5F-original;if(delta>.5F)delta-=1F;if(delta<-.5F)delta+=1F;float result=original+delta*s;return result<0F?result+1F:result>=1F?result-1F:result;}
    private static Vec3 mix(Vec3 original,Vec3 target,float amount){if(amount<=0F)return original;return new Vec3(Mth.lerp(amount,original.x,target.x),Mth.lerp(amount,original.y,target.y),Mth.lerp(amount,original.z,target.z));}
}
