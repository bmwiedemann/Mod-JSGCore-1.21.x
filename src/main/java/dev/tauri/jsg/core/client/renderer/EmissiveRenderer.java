package dev.tauri.jsg.core.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.core.client.renderer.shader.Shaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46C;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class EmissiveRenderer {
    @ParametersAreNonnullByDefault
    public static void renderWithLightOverlay(PoseStack stack, int packedLight, boolean renderEmissive, RenderExecutor renderExecutor) {
        renderWithLightOverlay(stack, packedLight, renderEmissive, () -> {
        }, renderExecutor);
    }

    @ParametersAreNonnullByDefault
    public static void renderWithLightOverlay(PoseStack stack, int packedLight, boolean renderEmissive, RenderExecutor renderExecutorPre, RenderExecutor renderExecutorPost) {
        renderWithLightOverlay(stack, packedLight, renderEmissive, renderExecutorPre, renderExecutorPost, null);
    }

    @ParametersAreNonnullByDefault
    public static Supplier<ShaderInstance> getShaderInstanceSupplier(boolean renderEmissive, PoseStack stack, int packedLight, @Nullable Supplier<ShaderInstance> customShader) {
        return (customShader != null ? customShader : (renderEmissive ? GameRenderer::getRendertypeEyesShader : Shaders.getShader(stack, packedLight)));
    }

    @ParametersAreNonnullByDefault
    public static void renderWithLightOverlay(PoseStack stack, int packedLight, boolean renderEmissive, RenderExecutor renderExecutorPre, RenderExecutor renderExecutorPost, @Nullable Supplier<ShaderInstance> customShader) {
        var gameRenderer = Minecraft.getInstance().gameRenderer;
        stack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(getShaderInstanceSupplier(renderEmissive, stack, packedLight, customShader));
        var blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        if (!blendEnabled) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
        var cullingEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        if (!cullingEnabled)
            RenderSystem.enableCull();

        RenderSystem.setShaderColor(1, 1, 1, 1);
        renderExecutorPre.render();
        gameRenderer.lightTexture().turnOnLightLayer();
        gameRenderer.overlayTexture().setupOverlayColor();
        RenderSystem.activeTexture(GL46C.GL_TEXTURE1);
        RenderSystem.bindTexture(RenderSystem.getShaderTexture(1));
        RenderSystem.activeTexture(GL46C.GL_TEXTURE2);
        RenderSystem.bindTexture(RenderSystem.getShaderTexture(2));
        renderExecutorPost.render();

        RenderSystem.setShaderColor(1, 1, 1, 1);
        gameRenderer.lightTexture().turnOffLightLayer();
        gameRenderer.overlayTexture().teardownOverlayColor();
        if (!blendEnabled)
            RenderSystem.disableBlend();
        if (!cullingEnabled)
            RenderSystem.disableCull();
        else
            RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        stack.popPose();

    }

    public interface RenderExecutor {
        void render();
    }
}
