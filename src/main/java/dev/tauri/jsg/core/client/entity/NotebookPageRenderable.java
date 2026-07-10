package dev.tauri.jsg.core.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public interface NotebookPageRenderable<D> {
    @ParametersAreNonnullByDefault
    void renderByCompound(@Nullable D data, ItemDisplayContext itemDisplayContext, CompoundTag pageCompound, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float topOffset, float bottomOffset);
}
