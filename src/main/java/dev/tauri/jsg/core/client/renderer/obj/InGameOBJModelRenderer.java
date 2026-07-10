package dev.tauri.jsg.core.client.renderer.obj;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.loader.model.OBJModel;
import dev.tauri.jsg.core.client.model.IOBJModelRenderer;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import dev.tauri.jsg.core.client.renderer.shader.RenderTypes;
import dev.tauri.jsg.core.common.util.vectors.Vector2f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class InGameOBJModelRenderer extends IOBJModelRenderer<OBJModel> {
    protected final Map<Integer, Map<TextureAtlasSprite, VertexBuffer>> modelBuffersLight = new HashMap<>();
    protected final Map<Integer, VertexBuffer> modelBufferNoAtlasLight = new HashMap<>();

    public InGameOBJModelRenderer(OBJModel model) {
        super(model);
    }

    @Nullable
    protected VertexBuffer getBuffer(int light, @Nullable TextureAtlasSprite textureAtlasSprite) {
        if (JSGCore.oculusWrapper.isShaderPackActive()) {
            // shaders does not support our shader, so we will be later using vanilla one that does not support changes of light in buffers...
            if (textureAtlasSprite != null) {
                var modelBufferMap = modelBuffersLight.get(light);
                if (modelBufferMap != null)
                    return modelBufferMap.get(textureAtlasSprite);
                else
                    return null;
            }
            return modelBufferNoAtlasLight.get(light);
        }
        if (textureAtlasSprite != null)
            return modelBuffers.get(textureAtlasSprite);
        return modelBufferNoAtlas;
    }

    protected void uploadBuffer(VertexBuffer buffer, BufferBuilder bufferbuilder, int light, @Nullable TextureAtlasSprite textureAtlasSprite, boolean shouldBeginAgain) {
        BufferBuilder.RenderedBuffer rb = bufferbuilder.end();
        buffer.bind();
        buffer.upload(rb);
        VertexBuffer.unbind();

        if (shouldBeginAgain) {
            bufferbuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        }

        if (JSGCore.oculusWrapper.isShaderPackActive()) {
            // shaders does not support our shader, so we will be later using vanilla one that does not support changes of light in buffers...
            if (textureAtlasSprite != null) {
                var modelBufferMap = modelBuffersLight.getOrDefault(light, Maps.newHashMap());
                modelBufferMap.put(textureAtlasSprite, buffer);
                modelBuffersLight.put(light, modelBufferMap);
                return;
            }
            modelBufferNoAtlasLight.put(light, buffer);
            return;
        }

        if (textureAtlasSprite != null) {
            modelBuffers.put(textureAtlasSprite, buffer);
            return;
        }
        modelBufferNoAtlas = buffer;
    }

    @Override
    @Nullable
    protected VertexBuffer initModel(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture) {
        var modelBuffer = getBuffer(light, textureAtlasSprite);
        if (modelBuffer == null) {
            boolean shouldBeginAgain = false;
            BufferBuilder bufferbuilder;
            if (usedTexture != null && (bufferSource.getBuffer(RenderTypes.OBJ_TYPE.apply(usedTexture, EmissiveRenderer.getShaderInstanceSupplier(emissiveRendering, poseStack, light, null))) instanceof BufferBuilder customBuilder)) {
                bufferbuilder = customBuilder;
                shouldBeginAgain = true;
            } else {
                bufferbuilder = Tesselator.getInstance().getBuilder();
            }
            modelBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            if (!bufferbuilder.building()) {
                bufferbuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            }
            int vertexCount = model.vertices.length;

            for (int i = 0; i < vertexCount; i += 3) {
                var normal1 = new Vector3f(model.normals[i], model.normals[i + 1], model.normals[i + 2]);

                var uv1 = new Vector2f(model.textureCoords[i / 3 * 2], model.textureCoords[i / 3 * 2 + 1]);
                uv1 = getCorrectUVWithSprite(uv1.getX(), uv1.getY(), textureAtlasSprite);

                bufferbuilder
                        .vertex(model.vertices[i], model.vertices[i + 1], model.vertices[i + 2])
                        .color(1, 1, 1, 1f)
                        .uv(uv1.x, uv1.y)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(light)
                        .normal(normal1.x(), normal1.y(), normal1.z())
                        .endVertex();
            }
            uploadBuffer(modelBuffer, bufferbuilder, light, textureAtlasSprite, shouldBeginAgain);
        }
        return modelBuffer;
    }

    @Override
    protected void renderModel(@Nullable VertexBuffer modelBuffer, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture) {
        if (modelBuffer == null) return;
        EmissiveRenderer.renderWithLightOverlay(poseStack, light, emissiveRendering, () -> {
            if (noCulling) RenderSystem.disableCull();
            RenderSystem.setShaderColor(r, g, b, a);
            modelBuffer.bind();
        }, () -> {
            Matrix4f projection = RenderSystem.getProjectionMatrix();
            modelBuffer.drawWithShader(poseStack.last().pose(), projection, Objects.requireNonNull(RenderSystem.getShader()));
            VertexBuffer.unbind();
        });
    }
}
