package dev.tauri.jsg.core.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.ParametersAreNonnullByDefault;

public class NotebookPageRenderableWrapper<D, R extends NotebookPageRenderable<D>> {
    protected final R renderable;
    protected final D data;

    public NotebookPageRenderableWrapper(R renderable, D data) {
        this.renderable = renderable;
        this.data = data;
    }

    @ParametersAreNonnullByDefault
    public void renderByCompound(ItemDisplayContext itemDisplayContext, CompoundTag pageCompound, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float topOffset, float bottomOffset) {
        renderable.renderByCompound(data, itemDisplayContext, pageCompound, stack, bufferSource, light, overlay, topOffset, bottomOffset);
    }
}
