package com.opus.client.mixin;

import com.opus.client.model.GreatHelmModel;
import com.opus.client.model.OpusPlateModel;
import com.opus.client.renderer.OpusHelmetLayer;
import com.opus.client.renderer.OpusPlateLayer;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V",
            at = @At("TAIL"))
    private void opusvsexe$addOpusArmorLayers(EntityRendererProvider.Context context, CallbackInfo ci) {
        LivingEntityRendererAccessor<ArmorStand, ArmorStandArmorModel> accessor =
                (LivingEntityRendererAccessor<ArmorStand, ArmorStandArmorModel>) (Object) this;
        accessor.opusvsexe$invokeAddLayer(new OpusHelmetLayer<>((ArmorStandRenderer) (Object) this,
                new GreatHelmModel<>(context.bakeLayer(GreatHelmModel.LAYER_LOCATION))));
        accessor.opusvsexe$invokeAddLayer(new OpusPlateLayer<>((ArmorStandRenderer) (Object) this,
                new OpusPlateModel<>(context.bakeLayer(OpusPlateModel.LAYER_LOCATION))));
    }
}