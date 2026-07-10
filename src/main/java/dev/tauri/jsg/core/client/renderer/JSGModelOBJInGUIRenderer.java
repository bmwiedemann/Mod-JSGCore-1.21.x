package dev.tauri.jsg.core.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class JSGModelOBJInGUIRenderer extends BlockEntityWithoutLevelRenderer {
    public interface RenderPartInterface {
        void render(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay);
    }

    public RenderPartInterface renderPartInterface = null;

    public JSGModelOBJInGUIRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        if (renderPartInterface == null) return;
        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        boolean renderGui = false;
        switch (itemDisplayContext) {
            case THIRD_PERSON_LEFT_HAND:
                stack.scale(0.65f, 0.65f, 0.65f);
                stack.mulPose(Axis.XP.rotationDegrees(20));
                stack.mulPose(Axis.YP.rotationDegrees(140));
                stack.mulPose(Axis.ZP.rotationDegrees(-6));
                stack.translate(0.4f, 0, 0.1f);
            case THIRD_PERSON_RIGHT_HAND:
                stack.scale(0.65f, 0.65f, 0.65f);
                stack.mulPose(Axis.XP.rotationDegrees(20));
                stack.mulPose(Axis.YP.rotationDegrees(140));
                stack.mulPose(Axis.ZP.rotationDegrees(-6));
                stack.translate(0, 0, -0.25f);
                break;
            case FIRST_PERSON_LEFT_HAND:
                stack.scale(0.5f, 0.5f, 0.5f);
                stack.mulPose(Axis.YP.rotationDegrees(-140));
                stack.mulPose(Axis.ZP.rotationDegrees(-6));
                stack.translate(0.1f, -0.2f, 0.32f);
            case FIRST_PERSON_RIGHT_HAND:
                stack.scale(0.5f, 0.5f, 0.5f);
                stack.mulPose(Axis.YN.rotationDegrees(140));
                stack.mulPose(Axis.ZP.rotationDegrees(-6));
                stack.translate(-0.2f, -0.5f, -0.05f);
                break;
            case FIXED:
                stack.scale(0.5f, 0.5f, 0.5f);
                stack.mulPose(Axis.XP.rotationDegrees(45));
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.translate(0.25f, 0.08f, 0.25f);
                break;
            case GROUND:
                stack.scale(0.25f, 0.25f, 0.25f);
                stack.translate(0.125f, 0, 0.125f);
                break;
            default:
                // gui and none
                stack.scale(0.5f, 0.5f, 0.5f);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                RenderSystem.enableDepthTest();
                stack.translate(0.0f, -0.5, 1220);
                renderGui = true;
                break;
        }
        if (renderGui) {
            AbstractOBJModel.setGUIRender();
        }
        stack.pushPose();
        renderPartInterface.render(itemStack, itemDisplayContext, stack, bufferSource, light, overlay);
        stack.popPose();
        stack.popPose();
        if (renderGui) {
            AbstractOBJModel.resetRenderType();
            RenderSystem.disableDepthTest();
        }
    }
}
