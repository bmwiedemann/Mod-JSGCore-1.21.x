package dev.tauri.jsg.core.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import dev.tauri.jsg.core.client.renderer.HandHeldDeviceRenderer;
import dev.tauri.jsg.core.client.renderer.HandPosition;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.config.JSGCoreConfig;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;

public class PageRenderer {
    public static final ResourceLocation NOTEBOOK_PAGE_TEXTURE = JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/notebook/page_single.png");

    @Nullable // percentage, inverted(from top?)
    // TODO(Mine): maybe start using scissors instead?
    public static Pair<Float, Boolean> renderPercentages = null;

    public static void renderSymbol(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float w, float h, SymbolInterface symbol, @Nullable PointOfOrigin origin) {
        renderSymbol(poseStack, source, light, x, y, w, h, symbol, origin, Color.BLACK, JSGCoreConfig.General.visualGlyphTransparency.get().floatValue());
    }

    public static void renderSymbol(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float w, float h, SymbolInterface symbol, @Nullable PointOfOrigin origin, Color color, float alpha) {
        var iconW = (float) symbol.getSymbolType().getIconWidth();
        var iconH = (float) symbol.getSymbolType().getIconHeight();
        var iconMax = Math.max(iconW, iconH);
        iconW /= iconMax;
        iconH /= iconMax;
        x += (w - (w * iconW)) / 2;
        y += (h - (h * iconH)) / 2;
        w *= iconW;
        h *= iconH;

        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        float z = 0.011f;
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        symbol.bindIconTexture(origin);

        Matrix4f matrix = poseStack.last().pose();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        bufferbuilder.addVertex(matrix, 0.04f + x, 0.79f - y, z).setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).setUv(0, 1).setLight(light);
        bufferbuilder.addVertex(matrix, 0.04f + x + w, 0.79f - y, z).setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).setUv(1, 1).setLight(light);
        bufferbuilder.addVertex(matrix, 0.04f + x + w, 0.79f - y + h, z).setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).setUv(1, 0).setLight(light);
        bufferbuilder.addVertex(matrix, 0.04f + x, 0.79f - y + h, z).setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).setUv(0, 0).setLight(light);
        BufferUploader.drawWithShader(bufferbuilder.end());
        poseStack.popPose();
    }

    public static void renderRect(PoseStack poseStack, MultiBufferSource source, int light, float x, float y, float w, float h, Color color, float alpha) {
        if (poseStack == null) return;
        poseStack.pushPose();
        float z = 0.011f;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
        Matrix4f matrix = poseStack.last().pose();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex(matrix, 0.04f + x, 0.79f - y, z).setColor(1, 1, 1, alpha);
        bufferbuilder.addVertex(matrix, 0.04f + x + w, 0.79f - y, z).setColor(1, 1, 1, alpha);
        bufferbuilder.addVertex(matrix, 0.04f + x + w, 0.79f - y + h, z).setColor(1, 1, 1, alpha);
        bufferbuilder.addVertex(matrix, 0.04f + x, 0.79f - y + h, z).setColor(1, 1, 1, alpha);
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        poseStack.popPose();
    }

    public static void renderByCompound(PoseStack poseStack, MultiBufferSource source, int light, ItemDisplayContext lastTransform, CompoundTag compound) {
        renderByCompound(poseStack, source, light, lastTransform, compound, () -> {
        }, null);
    }

    public static void renderByCompound(PoseStack poseStack, MultiBufferSource source, int light, ItemDisplayContext lastTransform, CompoundTag compound, Runnable renderTextLayer, @Nullable List<ResourceLocation> layers) {
        if (layers == null) layers = List.of(NOTEBOOK_PAGE_TEXTURE);
        RenderSystem.enableDepthTest();
        poseStack.pushPose();

        if (lastTransform == ItemDisplayContext.FIXED) {
            poseStack.translate(0.15, 0, 0.5);
        } else if (lastTransform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.mulPose(Axis.YP.rotationDegrees(60));
            poseStack.translate(-0.55, 0.2, 0);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(28));
            poseStack.mulPose(Axis.YP.rotationDegrees(26));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-13.5f));
            poseStack.translate(-0.1, 0.2, 0);
            poseStack.scale(0.7f, 0.7f, 0.7f);

            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(270));
            poseStack.translate(0.2, 0, -1.3);

            HandHeldDeviceRenderer.renderArms(poseStack, source, light, HumanoidArm.RIGHT, Minecraft.getInstance().getPartialTick(), HandPosition.HOLD_PAGE);
            poseStack.popPose();
        } else if (lastTransform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-50));
            poseStack.translate(0.5, 0.3, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            poseStack.mulPose(Axis.YP.rotationDegrees(-36));
            poseStack.mulPose(Axis.ZP.rotationDegrees(19));
            poseStack.translate(0.3, 0.21, 0);
            poseStack.scale(0.491f, 0.491f, 0.491f);

            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(260));
            poseStack.mulPose(Axis.YP.rotationDegrees(-10));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-10));
            poseStack.translate(-0.5f, 0.2f, -1.52f);

            HandHeldDeviceRenderer.renderArms(poseStack, source, light, HumanoidArm.LEFT, Minecraft.getInstance().getPartialTick(), HandPosition.HOLD_PAGE);
            poseStack.popPose();
        } else {
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            // fail-safe... should not end up here any time
            return;
        }


        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        float z = -(layers.size() - 1) * 0.0001f;
        var topOffset = 0f;
        var bottomOffset = 0f;

        if (renderPercentages != null) {
            var percentages = renderPercentages.first();
            if (renderPercentages.second())
                // from top
                topOffset = 1f - percentages;
            else
                bottomOffset = 1f - percentages;
        }
        for (var tex : layers) {
            poseStack.pushPose();
            poseStack.scale(1.13f, 1.13f, 1.13f);
            poseStack.translate(-0.117f, -0.0577f, 0);

            float finalBottomOffset = bottomOffset;
            float finalZ = z;
            float finalTopOffset = topOffset;
            EmissiveRenderer.renderWithLightOverlay(poseStack, light, false, () -> ITexture.bindTextureWithMc(tex), () -> {
                Matrix4f matrix = poseStack.last().pose();
                var normal = poseStack.last().setNormal();
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
                bufferbuilder
                        .addVertex(matrix, 0.0f, finalBottomOffset, finalZ)
                        .setColor(1, 1, 1, 0.99f)
                        .setUv(0, 0.8046875f * (1 - finalBottomOffset))
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(normal, 0, 1, 0)
                        ;
                bufferbuilder
                        .addVertex(matrix, 0.7f + 0.117f, finalBottomOffset, finalZ)
                        .setColor(1, 1, 1, 0.99f)
                        .setUv(0.65625f, 0.8046875f * (1 - finalBottomOffset))
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(normal, 0, 1, 0)
                        ;
                bufferbuilder
                        .addVertex(matrix, 0.7f + 0.117f, 1 - finalTopOffset, finalZ)
                        .setColor(1, 1, 1, 0.99f)
                        .setUv(0.65625f, 0.8046875f * finalTopOffset)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(normal, 0, 1, 0)
                        ;
                bufferbuilder
                        .addVertex(matrix, 0.0f, 1 - finalTopOffset, finalZ)
                        .setColor(1, 1, 1, 0.99f)
                        .setUv(0, 0.8046875f * finalTopOffset)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(normal, 0, 1, 0)
                        ;
                BufferUploader.drawWithShader(bufferbuilder.end());
            });
            poseStack.popPose();
            z += 0.0001f;
        }
        if (compound != null) {
            poseStack.pushPose();
            var pageType = NotebookPageType.pageTypeFromCompound(compound);
            if (pageType != null) {
                pageType.renderWrapper(compound).renderByCompound(lastTransform, compound, poseStack, source, light, OverlayTexture.NO_OVERLAY, topOffset, bottomOffset);
            }
            poseStack.popPose();

            String name = PageNotebookItemFilled.getNameFromCompound(compound);

            float scale = 0.009f;
            float width = Minecraft.getInstance().font.width(name) * scale;

            poseStack.pushPose();
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.getBlockEntityRenderDispatcher().font;
            poseStack.translate(0.05f + (0.61f / 2), 0.935f, 0.011f);
            poseStack.scale(scale, -scale, scale);
            poseStack.translate(0, 0, -1.2);

            if (topOffset < 0.1f && bottomOffset < 0.8) {
                poseStack.pushPose();
                poseStack.scale(1 / scale, 1 / -scale, 1 / scale);
                poseStack.translate(-width / 2, 0, 0);
                poseStack.scale(scale, -scale, scale);
                font.drawInBatch(name, 0, 0, 0x383228, false, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
                poseStack.popPose();
            }

            // TODO: Refactor to use custom registered notebook page type
            if (compound.contains("customText")) {
                renderCustomTextPage(poseStack, source, light, compound.getCompound("customText"));
            }

            poseStack.pushPose();
            renderTextLayer.run();
            poseStack.popPose();
            poseStack.popPose();
        }

        poseStack.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    protected static void renderCustomTextPage(PoseStack poseStack, MultiBufferSource source, int light, @Nullable CompoundTag textTag) {
        if (textTag == null) return;
        poseStack.pushPose();
        var lines = textTag.getList("lines", CompoundTag.TAG_COMPOUND);
        int lineIndex = 0;
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(-60, -30, 0);
        for (var line : lines) {
            var lineComponent = Component.Serializer.fromJson(((CompoundTag) line).getString("component"));
            poseStack.pushPose();
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.translate(0, -8 * lineIndex, 0);
            renderText(poseStack, source, light, lineComponent, 0xffffff, false);
            poseStack.popPose();
            lineIndex++;
        }
        poseStack.popPose();
    }

    public static void renderText(PoseStack poseStack, MultiBufferSource source, int light, String text, int color, boolean shadow) {
        poseStack.pushPose();
        poseStack.scale(1, -1, 1);
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.getBlockEntityRenderDispatcher().font;
        font.drawInBatch(text, 0, 0, color, shadow, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
        poseStack.popPose();
    }

    public static void renderText(PoseStack poseStack, MultiBufferSource source, int light, Component text, int color, boolean shadow) {
        poseStack.pushPose();
        poseStack.scale(1, -1, 1);
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.getBlockEntityRenderDispatcher().font;
        font.drawInBatch(text, 0, 0, color, shadow, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, light);
        poseStack.popPose();
    }
}
