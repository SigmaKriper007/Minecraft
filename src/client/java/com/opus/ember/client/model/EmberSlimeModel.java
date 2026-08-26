package com.opus.ember.client.model;

import com.opus.ember.entity.EmberSlimeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class EmberSlimeModel extends GeoModel<EmberSlimeEntity> {

    @Override
    public void setCustomAnimations(EmberSlimeEntity entity, long instanceId, AnimationState<EmberSlimeEntity> state) {
        float progress = entity.getChargeProgress();
        getBone("shell").ifPresent(shell -> {
            shell.setScaleX(1.0f + progress * 0.30f);
            shell.setScaleY(1.0f + progress * 0.14f);
            shell.setScaleZ(1.0f + progress * 0.30f);
        });
        getBone("core").ifPresent(core -> {
            float heat = 0.82f + progress * 0.78f;
            core.setScaleX(heat); core.setScaleY(heat); core.setScaleZ(heat);
        });
        for (String name : new String[]{"spark_a", "spark_b"}) {
            getBone(name).ifPresent(spark -> {
                spark.setHidden(!entity.isCharging());
                float orb = entity.tickCount * 0.35f;
                spark.setRotX(orb);
                spark.setRotY(orb * 1.6f);
            });
        }
        for (int i = 0; i < 4; i++) {
            getBone("crack_" + i).ifPresent(crack -> {
                crack.setHidden(!entity.isCharging());
                float spread = 0.65f + progress * 0.85f;
                crack.setScaleX(spread); crack.setScaleY(spread); crack.setScaleZ(spread);
            });
        }
    }

    @Override
    public ResourceLocation getModelResource(EmberSlimeEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/ember/slime.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmberSlimeEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/ember/entity/slime.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmberSlimeEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/ember/slime.animation.json");
    }
}
