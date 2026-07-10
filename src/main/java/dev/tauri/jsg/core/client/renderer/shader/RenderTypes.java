package dev.tauri.jsg.core.client.renderer.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RenderTypes {
    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {
    });

    public static final BiFunction<ResourceLocation, Supplier<ShaderInstance>, RenderType> OBJ_TYPE = Util.memoize((texture, shaderSupplier) -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(shaderSupplier))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                .createCompositeState(false);
        return RenderType.create("obj_model", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    });
}
