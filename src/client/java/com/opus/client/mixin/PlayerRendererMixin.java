package com.opus.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.GreatHelmModel;
import com.opus.client.model.OpusPlateModel;
import com.opus.client.model.ShadowAssassinHoodModel;
import com.opus.client.model.ShadowAssassinModel;
import com.opus.client.renderer.OpusHelmetLayer;
import com.opus.client.renderer.OpusPlateLayer;
import com.opus.client.renderer.ShadowAssassinHelmetLayer;
import com.opus.client.renderer.ShadowAssassinPlateLayer;
import com.opus.fire.client.layer.FireChargeLayer;
import com.opus.fire.client.layer.FireHelmetLayer;
import com.opus.fire.client.layer.FirePlateLayer;
import com.opus.fire.client.model.FireHelmetModel;
import com.opus.fire.client.model.FirePlateModel;
import com.opus.ember.client.layer.EmberChargeLayer;
import com.opus.ember.client.layer.EmberHelmetLayer;
import com.opus.ember.client.layer.EmberPlateLayer;
import com.opus.ember.client.model.EmberHelmetModel;
import com.opus.ember.client.model.EmberPlateModel;
import com.opusvsexe.entity.custom.ExosuitEntity;
import com.opus.paradise.client.model.ParthenonRegaliaModel;
import com.opus.paradise.client.renderer.ParthenonRegaliaLayer;
import com.opus.darkforest.client.model.DarkForestArmorModel;
import com.opus.darkforest.client.renderer.DarkForestArmorLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V",
            at = @At("TAIL"))
    private void opusvsexe$addOpusArmorLayers(EntityRendererProvider.Context context, boolean bl, CallbackInfo ci) {
        LivingEntityRendererAccessor<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> accessor =
                (LivingEntityRendererAccessor<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) (Object) this;
        accessor.opusvsexe$invokeAddLayer(new OpusHelmetLayer<>((PlayerRenderer) (Object) this,
                new GreatHelmModel<>(context.bakeLayer(GreatHelmModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new OpusPlateLayer<>((PlayerRenderer) (Object) this,
                new OpusPlateModel<>(context.bakeLayer(OpusPlateModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new ShadowAssassinHelmetLayer<>((PlayerRenderer) (Object) this,
                new ShadowAssassinHoodModel<>(context.bakeLayer(ShadowAssassinHoodModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new ShadowAssassinPlateLayer<>((PlayerRenderer) (Object) this,
                new ShadowAssassinModel<>(context.bakeLayer(ShadowAssassinModel.LAYER_LOCATION))));
        // Fire Biom armor layers (parallel line)
        accessor.opusvsexe$invokeAddLayer(new FireHelmetLayer<>((PlayerRenderer) (Object) this,
                new FireHelmetModel<>(context.bakeLayer(FireHelmetModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new FirePlateLayer<>((PlayerRenderer) (Object) this,
                new FirePlateModel<>(context.bakeLayer(FirePlateModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new FireChargeLayer<>((PlayerRenderer) (Object) this));
        // Ember armor layers (parallel duplicate line)
        accessor.opusvsexe$invokeAddLayer(new EmberHelmetLayer<>((PlayerRenderer) (Object) this,
                new EmberHelmetModel<>(context.bakeLayer(EmberHelmetModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new EmberPlateLayer<>((PlayerRenderer) (Object) this,
                new EmberPlateModel<>(context.bakeLayer(EmberPlateModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new EmberChargeLayer<>((PlayerRenderer) (Object) this));
        accessor.opusvsexe$invokeAddLayer(new ParthenonRegaliaLayer<>((PlayerRenderer)(Object)this,
                new ParthenonRegaliaModel<>(context.bakeLayer(ParthenonRegaliaModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new DarkForestArmorLayer<>((PlayerRenderer)(Object)this,
                new DarkForestArmorModel<>(context.bakeLayer(DarkForestArmorModel.BRIAR_LAYER)),false));
        accessor.opusvsexe$invokeAddLayer(new DarkForestArmorLayer<>((PlayerRenderer)(Object)this,
                new DarkForestArmorModel<>(context.bakeLayer(DarkForestArmorModel.VESTMENTS_LAYER)),true));
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void opusvsexe$hidePlayerWhilePiloting(AbstractClientPlayer player, float entityYaw, float partialTick,
                                                   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                                   CallbackInfo ci) {
        if (player.getVehicle() instanceof ExosuitEntity) {
            ci.cancel();
        }
    }
}
