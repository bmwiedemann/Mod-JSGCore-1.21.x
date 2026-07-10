package dev.tauri.jsg.core.client.renderer.obj;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.tauri.jsg.core.client.loader.model.OBJModel;
import dev.tauri.jsg.core.client.model.IOBJModelRenderer;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

@ParametersAreNonnullByDefault
public class LegacyOBJModelRenderer extends IOBJModelRenderer<OBJModel> {
    public LegacyOBJModelRenderer(OBJModel model) {
        super(model);
    }

    @Override
    @Nullable
    protected VertexBuffer initModel(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture) {
        return null;
    }

    @Override
    protected void renderModel(@Nullable VertexBuffer modelBuffer, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, boolean emissiveRendering, float r, float g, float b, float a, boolean noCulling, @Nullable TextureAtlasSprite textureAtlasSprite, @Nullable ResourceLocation usedTexture) {
        Tesselator tesselator = Tesselator.getInstance();
        Matrix4f matrix = poseStack.last().pose();

        EmissiveRenderer.renderWithLightOverlay(poseStack, light, false, () -> {
            RenderSystem.setShaderColor(r, g, b, a);
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, POSITION_TEX);
            var vertices = model.vertices;
            int vertexCount = vertices.length;
            var textureCoords = model.textureCoords;
            int vertexOffset = 0;
            int texCoordOffset = 0;

            for (int i = 0; i < vertexCount / 9; i++) {
                float x1 = vertices[vertexOffset];
                float y1 = vertices[vertexOffset + 1];
                float z1 = vertices[vertexOffset + 2];

                float x2 = vertices[vertexOffset + 3];
                float y2 = vertices[vertexOffset + 4];
                float z2 = vertices[vertexOffset + 5];

                float x3 = vertices[vertexOffset + 6];
                float y3 = vertices[vertexOffset + 7];
                float z3 = vertices[vertexOffset + 8];

                float texU1 = textureCoords[texCoordOffset];
                float texV1 = textureCoords[texCoordOffset + 1];

                float texU2 = textureCoords[texCoordOffset + 2];
                float texV2 = textureCoords[texCoordOffset + 3];

                float texU3 = textureCoords[texCoordOffset + 4];
                float texV3 = textureCoords[texCoordOffset + 5];

                bufferbuilder
                        .addVertex(matrix, x1, y1, z1)
                        .setUv(texU1, texV1)
                        ;

                bufferbuilder
                        .addVertex(matrix, x2, y2, z2)
                        .setUv(texU2, texV2)
                        ;

                bufferbuilder
                        .addVertex(matrix, x3, y3, z3)
                        .setUv(texU3, texV3)
                        ;

                vertexOffset += 9;
                texCoordOffset += 6;
            }
        }, () -> {
            tesselator.end();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }, GameRenderer::getPositionTexShader);
    }
}
