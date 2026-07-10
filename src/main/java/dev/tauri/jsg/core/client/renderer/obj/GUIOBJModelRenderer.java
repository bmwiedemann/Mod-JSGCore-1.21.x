package dev.tauri.jsg.core.client.renderer.obj;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.tauri.jsg.core.client.loader.model.OBJModel;
import dev.tauri.jsg.core.client.model.IOBJModelRenderer;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import dev.tauri.jsg.core.common.util.vectors.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46C;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

@ParametersAreNonnullByDefault
public class GUIOBJModelRenderer extends IOBJModelRenderer<OBJModel> {
    public GUIOBJModelRenderer(OBJModel model) {
        super(model);
    }

    @Override
    @Nullable
    protected VertexBuffer initModel(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture) {
        var modelBuffer = modelBufferNoAtlas;
        if (textureAtlasSprite != null)
            modelBuffer = modelBuffers.get(textureAtlasSprite);
        if (modelBuffer == null) {
            Tesselator tesselator = Tesselator.getInstance();
            modelBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            if (!bufferbuilder.building()) {
                BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, POSITION_TEX);
            }
            int vertexCount = model.vertices.length;
            for (int i = 0; i < vertexCount; i += 3) {
                var uv1 = new Vector2f(model.textureCoords[i / 3 * 2], model.textureCoords[i / 3 * 2 + 1]);
                uv1 = getCorrectUVWithSprite(uv1.getX(), uv1.getY(), textureAtlasSprite);
                bufferbuilder
                        .addVertex(model.vertices[i], model.vertices[i + 1], model.vertices[i + 2])
                        .setUv(uv1.x, uv1.y)
                        ;
            }
            var rb = bufferbuilder.end();
            modelBuffer.bind();
            modelBuffer.upload(rb);
            VertexBuffer.unbind();
            if (textureAtlasSprite != null)
                modelBuffers.put(textureAtlasSprite, modelBuffer);
            else
                modelBufferNoAtlas = modelBuffer;
        }
        return modelBuffer;
    }

    @Override
    protected void renderModel(@Nullable VertexBuffer modelBuffer, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture) {
        if (modelBuffer == null) return;
        EmissiveRenderer.renderWithLightOverlay(poseStack, light, false, () -> {
            if (noCulling) RenderSystem.disableCull();
            GL46C.glEnable(GL46C.GL_DEPTH_CLAMP);
            RenderSystem.setShaderColor(r, g, b, a);
            modelBuffer.bind();
        }, () -> {
            @SuppressWarnings("all")
            Matrix4f projectionMatrix = (new Matrix4f()).setOrtho(0.0F, (float) ((double) Minecraft.getInstance().getWindow().getWidth() / Minecraft.getInstance().getWindow().getGuiScale()), (float) ((double) Minecraft.getInstance().getWindow().getHeight() / Minecraft.getInstance().getWindow().getGuiScale()), 0.0F, -1000.0F, net.neoforged.neoforge.client.ForgeHooksClient.getGuiFarPlane());
            modelBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, Objects.requireNonNull(RenderSystem.getShader()));
            VertexBuffer.unbind();
            GL46C.glDisable(GL46C.GL_DEPTH_CLAMP);
        }, GameRenderer::getPositionTexShader);
    }
}
