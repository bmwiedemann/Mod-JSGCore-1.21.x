package dev.tauri.jsg.core.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import net.minecraft.resources.ResourceLocation;

public interface ITexture {
    void bindTexture();

    static void bindTextureWithMc(ResourceLocation location) {
        AbstractOBJModel.setActiveTexture(location);
        dev.tauri.jsg.core.client.loader.texture.TextureLoader.ensureGuiTextureLoaded(location);
        RenderSystem.setShaderTexture(0, location);
    }
}
