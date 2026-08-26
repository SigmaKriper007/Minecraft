package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.opus.client.model.OmegaSlashModel;
import com.opus.entity.omega.OmegaSlashEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OmegaSlashRenderer extends GeoEntityRenderer<OmegaSlashEntity> {

    public OmegaSlashRenderer(EntityRendererProvider.Context context) {
        super(context, new OmegaSlashModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void preRender(PoseStack poseStack, OmegaSlashEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        // Геометрия записана на дугу 138 блоков; визуальный масштаб — 1.5 ×
        // ширины хитбокса босса (12.45 блока). Зона урона (REACH) не меняется.
        float s = OmegaSlashEntity.RENDER_SCALE;
        poseStack.scale(s, s, s);
        // Поворачиваем модель по yaw сущности — иначе дуга всегда в одну
        // сторону (GeckoLib не применяет yaw для plain Entity, задача 19).
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
    }
}
