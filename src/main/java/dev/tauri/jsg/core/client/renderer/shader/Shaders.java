package dev.tauri.jsg.core.client.renderer.shader;

import net.neoforged.fml.common.EventBusSubscriber;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.io.IOException;
import java.util.function.Supplier;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Shaders {
    private static OBJShaderInstance objShader;

    public static OBJShaderInstance getObjShader() {
        return objShader;
    }

    public static void setObjShader(OBJShaderInstance instance) {
        objShader = instance;
    }

    @SubscribeEvent
    public static void shaderRegister(RegisterShadersEvent event) {
        try {
            JSGCore.logger.info("Loading custom shaders.");
            event.registerShader(new OBJShaderInstance(event.getResourceProvider(), JSGMapping.rl(JSGCore.MOD_ID, "obj"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> setObjShader((OBJShaderInstance) shaderInstance));
            JSGCore.logger.info("Shaders loaded.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Supplier<ShaderInstance> getShader(PoseStack stack, int light) {
        if (JSGCore.oculusWrapper.isShaderPackActive()) return GameRenderer::getRendertypeEntityCutoutShader;
        return () -> {
            var shader = Shaders.getObjShader();
            shader.setNormMat(stack);
            shader.setLight(light);
            return shader;
        };
    }
}
