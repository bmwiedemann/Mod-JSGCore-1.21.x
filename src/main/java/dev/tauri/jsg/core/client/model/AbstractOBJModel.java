package dev.tauri.jsg.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public abstract class AbstractOBJModel {
    public enum EnumOBJRenderMethod {
        NORMAL,
        GUI,
        LEGACY
    }

    @Nullable
    public static ResourceLocation activeTexture;

    public static void setActiveTexture(@Nullable ResourceLocation texture) {
        activeTexture = texture;
    }

    @Nullable
    public static ResourceLocation getActiveTexture() {
        return activeTexture;
    }

    protected static EnumOBJRenderMethod renderType = EnumOBJRenderMethod.NORMAL;

    public static void setGUIRender() {
        renderType = EnumOBJRenderMethod.GUI;
    }

    public static void setLegacyRender() {
        renderType = EnumOBJRenderMethod.LEGACY;
    }

    public static void resetRenderType() {
        renderType = EnumOBJRenderMethod.NORMAL;
    }

    public static EnumOBJRenderMethod getRenderMethod() {
        return renderType;
    }

    public abstract dev.tauri.jsg.core.client.model.IOBJModelRenderer<?> renderer(EnumOBJRenderMethod method);

    public abstract boolean isEmpty();

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light) {
        render(stack, bufferSource, light, (ResourceLocation) null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, null, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(stack, bufferSource, light, textureAtlasSprite, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, false, 1f, 1f, 1f, 1f, false, textureAtlasSprite, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering) {
        render(stack, bufferSource, light, emissiveRendering, (ResourceLocation) null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, emissiveRendering, null, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(stack, bufferSource, light, emissiveRendering, textureAtlasSprite, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, emissiveRendering, 1f, 1f, 1f, 1f, false, textureAtlasSprite, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering) {
        render(stack, bufferSource, light, overlay, emissiveRendering, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, overlay, emissiveRendering, 1f, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color) {
        render(stack, bufferSource, light, emissiveRendering, color, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, emissiveRendering, color, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, false, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color, boolean noCulling) {
        render(stack, bufferSource, light, emissiveRendering, color, noCulling, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, emissiveRendering, color, noCulling, textureUsed);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color, boolean noCulling) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, noCulling, null);
    }

    public void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, color, color, 1f, noCulling, textureUsed);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling) {
        render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, (ResourceLocation) null);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, null, textureUsed);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, null);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        render(getRenderMethod(), poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, textureUsed);
    }

    public void render(EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling) {
        render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, (ResourceLocation) null);
    }

    public void render(EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, null, textureUsed);
    }

    public void render(EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, null);
    }

    public void render(EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        renderer(renderType).render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, textureUsed);
    }
}
