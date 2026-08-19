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
import com.opusvsexe.entity.custom.ExosuitEntity;
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