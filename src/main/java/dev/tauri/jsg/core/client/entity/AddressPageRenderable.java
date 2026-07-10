package dev.tauri.jsg.core.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.core.client.renderer.item.PageRenderer;
import dev.tauri.jsg.core.common.entity.IAddressNotebookPageData;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AddressPageRenderable<D extends IAddressNotebookPageData> implements dev.tauri.jsg.core.client.entity.NotebookPageRenderable<D> {
    @Override
    public void renderByCompound(@Nullable D data, ItemDisplayContext itemDisplayContext, CompoundTag pageCompound, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float topOffset, float bottomOffset) {
        if (data == null) return;
        var address = data.getAddress();
        var symbolType = address.getSymbolType();
        PointOfOrigin origin = data.getOrigin();
        stack.pushPose();
        stack.translate(0, -0.03f, -0.01);
        for (int symbolId : data.getSymbolsToDisplay()) {
            if (symbolId <= 3 && topOffset >= 0.25f) continue;
            if (symbolId <= 6 && topOffset >= 0.4f) continue;
            if (symbolId <= 8 && topOffset >= 0.65f) continue;
            if (symbolId <= 9 && topOffset >= 0.85f) continue;

            if (symbolId >= 9 && bottomOffset >= 0.07f) continue;
            if (symbolId >= 7 && bottomOffset >= 0.2f) continue;
            if (symbolId >= 4 && bottomOffset >= 0.4f) continue;
            if (bottomOffset >= 0.6f) continue;

            if (symbolId <= 0 || symbolId > 9) continue;
            int i = (symbolId - 1);
            if (symbolId == 9) {
                // origin
                float x = 0.21f * (10 % 3);
                float y = 0.20f * ((int) Math.floor((float) 10 / 3)) + 0.14f;
                PageRenderer.renderSymbol(stack, bufferSource, light, x, y, 0.2f, 0.2f, symbolType.getOrigin(), origin);
                continue;
            }
            if(address.getSize() <= i) continue;

            float x = 0.21f * (i % 3);
            float y = 0.20f * ((int) Math.floor((float) i / 3)) + 0.14f;
            PageRenderer.renderSymbol(stack, bufferSource, light, x, y, 0.2f, 0.2f, address.get(i), origin);
        }
        stack.popPose();
    }
}
