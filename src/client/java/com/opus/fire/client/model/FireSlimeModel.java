package com.opus.fire.client.model;

import com.opus.fire.entity.FireSlimeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class FireSlimeModel extends GeoModel<FireSlimeEntity> {

    @Override
    public void setCustomAnimations(FireSlimeEntity entity, long instanceId, AnimationState<FireSlimeEntity> state) {
        float progress = entity.getChargeProgress();
        // криперская белая вспышка: мигание раковины и ядра в конце зарядки
        float flash = entity.isFlashing() ? (entity.tickCount % 6 < 3 ? 0.14f : 0.0f) : 0.0f;
        getBone("shell").ifPresent(shell -> {
            shell.setScaleX(1.0f + progress * 0.38f + flash);
            shell.setScaleY(1.0f + progress * 0.18f + flash * 0.6f);
            shell.setScaleZ(1.0f + progress * 0.38f + flash);
        });
        getBone("core").ifPresent(core -> {
            float heat = Math.min(1.6f, 0.82f + progress * 0.78f + flash * 1.2f);
            core.setScaleX(heat); core.setScaleY(heat); core.setScaleZ(heat);
        });
        for (int i = 0; i < 4; i++) {
            getBone("crack_" + i).ifPresent(crack -> {
                crack.setHidden(!entity.isCharging());
                float spread = 0.65f + progress * 0.85f;
                crack.setScaleX(spread); crack.setScaleY(spread); crack.setScaleZ(spread);
            });
        }
    }

    @Override
    public ResourceLocation getModelResource(FireSlimeEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/fire/slime.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireSlimeEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/fire/entity/slime.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireSlimeEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/fire/slime.animation.json");
    }
}