package dev.tauri.jsg.core.client.renderer.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix3f;

import java.io.IOException;

public class OBJShaderInstance extends ShaderInstance {
    private final Uniform NORMAL_MATRIX;
    private final Uniform LIGHT_U;
    private final Uniform LIGHT_V;

    public OBJShaderInstance(ResourceProvider provider, ResourceLocation shaderLocation, VertexFormat format) throws IOException {
        super(provider, shaderLocation, format);
        this.NORMAL_MATRIX = this.getUniform("NormMat");
        this.LIGHT_U = this.getUniform("LightU");
        this.LIGHT_V = this.getUniform("LightV");
    }

    public void setNormMat(PoseStack stack) {
        setNormMat(stack.last().normal());
    }

    public void setLight(int light) {
        setLight(light & '\uffff', light >> 16 & '\uffff');
    }
    public void setLight(int lightU, int lightV) {
        if (LIGHT_U != null && LIGHT_V != null) {
            LIGHT_U.set(lightU);
            LIGHT_V.set(lightV);
        }
    }

    private void setNormMat(Matrix3f normal) {
        if (NORMAL_MATRIX != null) {
            NORMAL_MATRIX.set(normal);
        }
    }
}
