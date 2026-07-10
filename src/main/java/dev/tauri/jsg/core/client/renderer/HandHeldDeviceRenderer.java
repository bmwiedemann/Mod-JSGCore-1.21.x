package dev.tauri.jsg.core.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.common.helper.ItemRenderingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;


public class HandHeldDeviceRenderer {
    public static void drawStringWithShadow(PoseStack poseStack, MultiBufferSource source, float x, float y, String text, int color, boolean shadow) {
        var light = LightTexture.FULL_BRIGHT;

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.scale(0.015f, 0.015f, 0.015f);

        Minecraft.getInstance().font.drawInBatch(text, -6, 19, color, false, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);

        if (shadow) {
            poseStack.translate(-0.4, 0.6, -0.1);
            Minecraft.getInstance().font.drawInBatch(text, -6, 19, color, false, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
        }

        poseStack.popPose();
    }

    public static void drawTexturedRect(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float z, float w, float h) {
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferbuilder.addVertex(matrix, x, y, z).setUv(1, 1);
        bufferbuilder.addVertex(matrix, x + w, y, z).setUv(0, 1);
        bufferbuilder.addVertex(matrix, x + w, y + h, z).setUv(0, 0);
        bufferbuilder.addVertex(matrix, x, y + h, z).setUv(1, 0);

        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        poseStack.popPose();
    }

    public static void drawColorRect(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float z, float w, float h, float r, float g, float b, float a) {
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        bufferbuilder.addVertex(matrix, x, y, z).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x + w, y, z).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x + w, y + h, z).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x, y + h, z).setColor(r, g, b, a);

        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        poseStack.popPose();
    }

    public static void drawSemiCircle(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float z, float startAngle, float endAngle, int segments, float innerRadius, float lineWidth, float r, float g, float b, float a) {
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        var outerRadius = innerRadius + lineWidth;
        var angleDiff = (endAngle - startAngle);
        for (int i = 0; i <= segments; i++) {
            double angle = startAngle + ((angleDiff * i) / segments);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            buffer.addVertex(poseStack.last().pose(),
                            x + cos * outerRadius,
                            y + sin * outerRadius,
                            z)
                    .setColor(r, g, b, a)
                    ;

            buffer.addVertex(poseStack.last().pose(),
                            x + cos * innerRadius,
                            y + sin * innerRadius,
                            z)
                    .setColor(r, g, b, a)
                    ;
        }
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawModalRectWithCustomSizedTexture(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, x, y + height, z).setUv((u * f), ((v + height) * f1));
        bufferbuilder.addVertex(matrix, (x + width), (y + height), z).setUv(((u + width) * f), ((v + height) * f1));
        bufferbuilder.addVertex(matrix, (x + width), y, z).setUv(((u + width) * f), (v * f1));
        bufferbuilder.addVertex(matrix, x, y, z).setUv((u * f), (v * f1));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        poseStack.popPose();
    }

    public static void renderArms(PoseStack poseStack, MultiBufferSource source, int light, HumanoidArm handSide, float partialTicks, HandPosition handPosition) {
        poseStack.pushPose();
        ItemRenderingHelper.applyBobbing(poseStack, partialTicks);
        ItemRenderingHelper.renderHand(poseStack, source, light, handSide, handPosition);
        poseStack.popPose();
    }
}
