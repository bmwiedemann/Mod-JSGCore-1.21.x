package dev.tauri.jsg.core.client.loader.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class Texture extends DynamicTexture implements ITexture {

    public static Texture EMPTY_TEXTURE = null;
    private final ResourceLocation resourceLocation;

    public Texture(@NotNull NativeImage image, @NotNull ResourceLocation resourceLocation) {
        super(image);
        this.resourceLocation = resourceLocation;
        Minecraft.getInstance().getTextureManager().register(resourceLocation, this);
    }

    public static Texture getEmptyTexture() {
        if (EMPTY_TEXTURE == null)
            EMPTY_TEXTURE = new Texture(new NativeImage(16, 16, false), JSGMapping.rl(JSGCore.MOD_ID, "null"));
        return EMPTY_TEXTURE;
    }

    public void bindTexture() {
        AbstractOBJModel.setActiveTexture(resourceLocation);
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                RenderSystem.setShaderTexture(0, resourceLocation);
                GlStateManager._bindTexture(this.getId());
            });
        } else {
            RenderSystem.setShaderTexture(0, resourceLocation);
            GlStateManager._bindTexture(this.getId());
        }
    }

    public void deleteTexture() {
        super.close();
    }

    public Texture free() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                if (getPixels() != null) {
                    getPixels().close();
                }
            });
        } else {
            if (getPixels() != null) {
                getPixels().close();
            }
        }
        return this;
    }

    public static void bindTextureWithMc(ResourceLocation location) {
        RenderSystem.setShaderTexture(0, location);
    }
}
