package dev.tauri.jsg.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IModelsHolder {
    default void loadEntry(String texturePath, boolean byOverlay) {
        for (BiomeOverlayInstance biomeOverlay : BiomeOverlayInstance.values()) {
            if (!byOverlay) {
                getBiomeTextureResourceMap().put(biomeOverlay, getLoadersHolder().texture().getTextureResource(texturePath));
            } else {
                String[] split = texturePath.split("\\.");
                if(split.length > 1)
                    getBiomeTextureResourceMap().put(biomeOverlay, getLoadersHolder().texture().getTextureResource(split[0] + biomeOverlay.suffix() + "." + split[1]));
                else
                    getBiomeTextureResourceMap().put(biomeOverlay, getLoadersHolder().texture().getTextureResource(split[0] + biomeOverlay.suffix()));
            }
        }
    }

    @NotNull
    LoadersHolder getLoadersHolder();

    @NotNull
    ResourceLocation getModelLocation();

    @NotNull
    Map<BiomeOverlayInstance, ResourceLocation> getBiomeTextureResourceMap();

    @NotNull
    List<BiomeOverlayInstance> getNonExistingTexturesReported();

    default IModelsHolder bindTexture(BiomeOverlayInstance biomeOverlay, ResourceLocation resourceLocation) {
        if (!getLoadersHolder().texture().isTextureLoaded(resourceLocation)) {
            if (!getNonExistingTexturesReported().contains(biomeOverlay)) {
                JSGCore.logger.error("{} tried to use BiomeOverlay {} but it doesn't exist. ({})", this, biomeOverlay.toString(), resourceLocation);
                getNonExistingTexturesReported().add(biomeOverlay);
            }
            resourceLocation = getBiomeTextureResourceMap().get(CoreBiomeOverlays.NORMAL.get());
        }

        getLoadersHolder().texture().getTexture(resourceLocation).bindTexture();
        //AbstractOBJModel.setActiveTexture(resourceLocation);
        return this;
    }

    default IModelsHolder bindTexture() {
        return bindTexture(CoreBiomeOverlays.NORMAL.get());
    }

    default IModelsHolder bindTexture(BiomeOverlayInstance biomeOverlay) {
        var resourceLocation = getBiomeTextureResourceMap().get(biomeOverlay);
        return bindTexture(biomeOverlay, resourceLocation);
    }

    default IModelsHolder bindTexture(RegistryObject<BiomeOverlayInstance> biomeOverlay) {
        return bindTexture(biomeOverlay.get());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light) {
        render(stack, bufferSource, light, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        render(stack, bufferSource, light, overlay, false, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(stack, bufferSource, light, overlay, false, 1f, 1f, 1f, 1f, false, textureAtlasSprite, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, null, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(stack, bufferSource, light, textureAtlasSprite, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable TextureAtlasSprite textureAtlasSprite, boolean noCulling) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, false, 1f, 1f, 1f, 1f, noCulling, textureAtlasSprite, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, false, 1f, 1f, 1f, 1f, false, textureAtlasSprite, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering) {
        render(stack, bufferSource, light, emissiveRendering, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, emissiveRendering, null, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(stack, bufferSource, light, emissiveRendering, textureAtlasSprite, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, emissiveRendering, 1f, 1f, 1f, 1f, false, textureAtlasSprite, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering) {
        render(stack, bufferSource, light, overlay, emissiveRendering, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, overlay, emissiveRendering, 1f, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color) {
        render(stack, bufferSource, light, emissiveRendering, color, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, emissiveRendering, color, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, false, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color, boolean noCulling) {
        render(stack, bufferSource, light, emissiveRendering, color, noCulling, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, boolean emissiveRendering, float color, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, OverlayTexture.NO_OVERLAY, emissiveRendering, color, noCulling, textureUsed);
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color, boolean noCulling) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, noCulling, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float color, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(stack, bufferSource, light, overlay, emissiveRendering, color, color, color, 1f, noCulling, textureUsed);
    }

    default void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling) {
        render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, null, textureUsed);
    }

    default void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, AbstractOBJModel.getActiveTexture());
    }

    default void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        render(AbstractOBJModel.getRenderMethod(), poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, textureUsed);
    }

    default void render(AbstractOBJModel.EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling) {
        render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, AbstractOBJModel.getActiveTexture());
    }

    default void render(AbstractOBJModel.EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable ResourceLocation textureUsed) {
        render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, null, textureUsed);
    }

    default void render(AbstractOBJModel.EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite) {
        render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, AbstractOBJModel.getActiveTexture());
    }

    default void render(AbstractOBJModel.EnumOBJRenderMethod renderType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        getLoadersHolder().model().getModel(getModelLocation()).render(renderType, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, textureUsed);
    }
}
