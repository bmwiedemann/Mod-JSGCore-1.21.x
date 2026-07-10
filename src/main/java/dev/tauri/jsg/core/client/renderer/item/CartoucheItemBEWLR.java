package dev.tauri.jsg.core.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.item.CartoucheItem;
import dev.tauri.jsg.core.common.util.RotationUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CartoucheItemBEWLR extends AbstractItemBEWLR {
    @Override
    public void renderItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        if (!(itemStack.getItem() instanceof CartoucheItem cartoucheItem)) return;
        stack.mulPose(RotationUtil.getRotation(null, Direction.NORTH));
        if (itemDisplayContext != ItemDisplayContext.GROUND) {
            if(itemDisplayContext == ItemDisplayContext.FIXED) {
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.translate(0.5, -0.1, 0.24);
                stack.translate(0, cartoucheItem.type.hasPoo ? 0 : (cartoucheItem.type.symbolsCount / 35.0), 0);
            }
            else if(itemDisplayContext == ItemDisplayContext.GUI)
                stack.translate(0, -0.28, -1);
            else
                stack.translate(0, -0.8, -1);
            stack.scale(0.8f, 0.8f, 0.8f);
        }
        else {
            stack.translate(0, 0, -0.3);
        }
        var material = cartoucheItem.material;
        var sprite = BlockRenderer.getSprite(material.get(), Direction.SOUTH);
        if (sprite != null) {
            ITexture.bindTextureWithMc(sprite.atlasLocation());
        }
        cartoucheItem.type.model.get().render(stack, bufferSource, light, sprite, true);

    }

    @Override
    public boolean renderHands(ItemDisplayContext itemDisplayContext) {
        return false;
    }
}
