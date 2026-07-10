package dev.tauri.jsg.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import dev.tauri.jsg.core.common.util.vectors.Vector2f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public abstract class IOBJModelRenderer<OBJ extends AbstractOBJModel> {
    public final OBJ model;
    protected final Map<TextureAtlasSprite, VertexBuffer> modelBuffers = new HashMap<>();
    protected VertexBuffer modelBufferNoAtlas;

    public IOBJModelRenderer(OBJ model) {
        this.model = model;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed) {
        if (model.isEmpty()) return;
        var buffer = initModel(poseStack, bufferSource, light, overlay, emissiveRendering, textureAtlasSprite, textureUsed);
        renderModel(buffer, poseStack, bufferSource, light, overlay, emissiveRendering, r, g, b, a, noCulling, textureAtlasSprite, textureUsed);
    }

    public static @NotNull Vector2f getCorrectUVWithSprite(float u, float v, @Nullable TextureAtlasSprite textureAtlasSprite) {
        var uv1 = new Vector2f(u, v);

        if (textureAtlasSprite != null) {
            var u0 = textureAtlasSprite.getU0();
            var u1 = textureAtlasSprite.getU1();
            var v0 = textureAtlasSprite.getV0();
            var v1 = textureAtlasSprite.getV1();
            var e = 0.001f;
            var sizeU = ((u1 - u0) - e);
            var sizeV = ((v1 - v0) - e);

            uv1 = new Vector2f(u1 - (sizeU * uv1.x) - e, v1 + (sizeV * uv1.y) - e);
        }
        return uv1;
    }

    @Nullable
    protected abstract VertexBuffer initModel(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture);

    protected abstract void renderModel(@Nullable VertexBuffer buffer, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation textureUsed);
}
