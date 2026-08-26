package com.opus.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.opus.block.AltarHeartBlock;
import com.opus.blockentity.AltarHeartBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Сердце Алтаря — левитирующий фиолетовый кристалл (задача 15.2 / 16).
 *
 * Над пьедесталом блока парит ромбовидный кристалл: тёмно-фиолетовый у
 * основания → ярко-фиолетовый → бело-фиолетовое ядро. Кристалл медленно
 * вращается, покачивается по вертикали и пульсирует свечением. После
 * активации алтаря (ACTIVATED=true) он за ~1.5 сек плавно опускается на
 * пьедестал и гаснет — больше не левитирует.
 */
@Environment(EnvType.CLIENT)
public class AltarHeartBlockEntityRenderer implements BlockEntityRenderer<AltarHeartBlockEntity> {

    /** Сколько тиков кристалл «садится» после активации. */
    private static final int SINK_TICKS = 30;

    /** Старт анимации опускания: запоминаем gameTime при первом кадре активации. */
    private final Map<BlockPos, Long> sinkStart = new HashMap<>();

    public AltarHeartBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AltarHeartBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        BlockPos pos = blockEntity.getBlockPos();
        boolean activated = blockEntity.getBlockState().getValue(AltarHeartBlock.ACTIVATED);

        // старт опускания — один раз
        if (activated && !sinkStart.containsKey(pos)) {
            sinkStart.put(pos, level.getGameTime());
        } else if (!activated && sinkStart.containsKey(pos)) {
            sinkStart.remove(pos); // алтарь сброшен/переставлен — снова левитируем
        }

        long gameTime = level.getGameTime();
        float t = gameTime + partialTick;

        // прогресс активации 0..1 (1.5 сек)
        float sink = 0.0F;
        if (activated) {
            long start = sinkStart.getOrDefault(pos, gameTime);
            sink = Mth.clamp((t - start) / (float) SINK_TICKS, 0.0F, 1.0F);
        }

        // левитация: покачивание; после активации — опускание на пьедестал
        float hover = Mth.sin(t * 0.12F) * 0.12F + 0.35F;
        float yOffset = Mth.lerp(sink, hover, 0.05F);
        float rotY = Mth.lerp(sink, t * 1.2F % 360.0F, 0.0F);
        // пульс свечения затухает при активации
        float pulse = Mth.lerp(sink, 0.65F + 0.15F * Mth.sin(t * 0.2F), 0.05F);

        poseStack.pushPose();
        poseStack.translate(0.5D, yOffset, 0.5D);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotY));
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        // Верхняя пирамида: основание 0.5×0.5 на y=0.6, вершина на y=1.7
        drawPyramid(buffer, matrix, 0.5F, 0.6F, 1.7F,
                new Vector3f(0.55F, 0.20F, 0.90F), new Vector3f(0.90F, 0.50F, 1.00F), pulse);
        // Нижняя пирамида: основание 0.5×0.5 на y=0.6, вершина на y=-0.3
        drawPyramid(buffer, matrix, 0.5F, 0.6F, -0.3F,
                new Vector3f(0.55F, 0.20F, 0.90F), new Vector3f(0.70F, 0.30F, 0.95F), pulse);

        // Светящееся ядро — маленький ромб в центре (y≈0.6)
        float coreAlpha = Mth.lerp(sink, 0.95F, 0.05F);
        drawPyramid(buffer, matrix, 0.18F, 0.6F, 1.0F,
                new Vector3f(1.0F, 0.75F, 1.0F), new Vector3f(1.0F, 0.85F, 1.0F), coreAlpha);
        drawPyramid(buffer, matrix, 0.18F, 0.6F, 0.2F,
                new Vector3f(1.0F, 0.75F, 1.0F), new Vector3f(0.9F, 0.6F, 1.0F), coreAlpha);

        Tesselator.getInstance().end();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    /**
     * Рисует 4 треугольника пирамиды: квадратное основание (половина half)
     * на высоте baseY и вершина на apexY. Цвет граней интерполируется от
     * baseColor у основания к apexColor у вершины.
     */
    private static void drawPyramid(BufferBuilder buffer, Matrix4f matrix,
                                    float half, float baseY, float apexY,
                                    Vector3f baseColor, Vector3f apexColor, float alpha) {
        float[][] base = {
                {-half, -half}, {half, -half}, {half, half}, {-half, half}
        };
        for (int i = 0; i < 4; i++) {
            float[] a = base[i];
            float[] b = base[(i + 1) % 4];
            // грань: вершина, a, b (против часовой, чтобы смотреть наружу)
            buffer.vertex(matrix, 0.0F, apexY, 0.0F)
                    .color(apexColor.x, apexColor.y, apexColor.z, alpha).endVertex();
            buffer.vertex(matrix, a[0], baseY, a[1])
                    .color(baseColor.x, baseColor.y, baseColor.z, alpha).endVertex();
            buffer.vertex(matrix, b[0], baseY, b[1])
                    .color(baseColor.x, baseColor.y, baseColor.z, alpha).endVertex();
        }
    }
}
