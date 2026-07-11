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
        var rb = bufferbuilder.build();
        if (rb != null) {
            buffer.bind();
            buffer.upload(rb);
            VertexBuffer.unbind();
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
            // 1.21: buffers are single-use; always build our own from the Tesselator
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
            modelBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            int vertexCount = model.vertices.length;

            for (int i = 0; i < vertexCount; i += 3) {
                var normal1 = new Vector3f(model.normals[i], model.normals[i + 1], model.normals[i + 2]);

                var uv1 = new Vector2f(model.textureCoords[i / 3 * 2], model.textureCoords[i / 3 * 2 + 1]);
                uv1 = getCorrectUVWithSprite(uv1.getX(), uv1.getY(), textureAtlasSprite);

                bufferbuilder
                        .addVertex(model.vertices[i], model.vertices[i + 1], model.vertices[i + 2])
                        .setColor(1, 1, 1, 1f)
                        .setUv(uv1.x, uv1.y)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(normal1.x(), normal1.y(), normal1.z())
                        ;
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
            // 1.21: the camera rotation lives in the global model-view (the BER pose only holds the
            // camera-relative translation), so compose both - passing the pose alone renders the
            // model in view space, gluing it to the camera
            Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix()).mul(poseStack.last().pose());
            modelBuffer.drawWithShader(modelView, projection, Objects.requireNonNull(RenderSystem.getShader()));
            VertexBuffer.unbind();
        });
    }
}
