package dev.tauri.jsg.core.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.common.helper.ItemRenderingHelper;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public abstract class AbstractItemBEWLR extends BlockEntityWithoutLevelRenderer {
    public static IClientItemExtensions create(Supplier<AbstractItemBEWLR> rendererSupplier) {
        return new IClientItemExtensions() {
            private final AbstractItemBEWLR instance = rendererSupplier.get();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return instance;
            }
        };
    }


    public AbstractItemBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        boolean renderGui = false;
        float partialTick = Minecraft.getInstance().getPartialTick();

        stack.pushPose();
        switch (itemDisplayContext) {
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                renderItemInHand(itemStack, itemDisplayContext, stack, bufferSource, light, overlay, partialTick, getHandPosition(itemDisplayContext));
                stack.popPose();
                return;
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                stack.translate(0.5, 1, 1.3);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                break;
            case FIXED:
                break;
            case GROUND:
                stack.translate(0.5, 0.5, 0.5);
                break;
            default:
                // gui and none
                //stack.scale(0.5f, 0.5f, 0.5f);
                stack.mulPose(Axis.YP.rotationDegrees(180));
                RenderSystem.enableDepthTest();
                stack.translate(-0.25, 0.26, 1220f / 2f);
                stack.mulPose(Axis.ZP.rotationDegrees(45));
                renderGui = true;
                break;
        }
        if (renderGui) {
            AbstractOBJModel.setGUIRender();
        }
        stack.pushPose();
        renderItem(itemStack, itemDisplayContext, stack, bufferSource, light, overlay, partialTick);
        stack.popPose();
        if (renderGui) {
            AbstractOBJModel.resetRenderType();
            RenderSystem.disableDepthTest();
        }
        stack.popPose();
    }

    protected void renderItemInHand(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick, HandPosition handPosition) {
        double tick = JSGMinecraftHelper.getPlayerTickClientSide() + partialTick;
        var side = (itemDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ? HumanoidArm.RIGHT : HumanoidArm.LEFT);
        float f = side == HumanoidArm.RIGHT ? 1 : -1;

        if (side == HumanoidArm.RIGHT) {
            stack.mulPose(Axis.YP.rotationDegrees(180 + 10));
            stack.mulPose(Axis.XP.rotationDegrees(30));
            stack.mulPose(Axis.ZP.rotationDegrees(-27));
            stack.translate(0.15, -0.7, -0.9);
        } else {
            stack.translate(1, 0, 0);
            stack.mulPose(Axis.YP.rotationDegrees(180 - 10));
            stack.mulPose(Axis.XP.rotationDegrees(30 - 5));
            stack.mulPose(Axis.ZP.rotationDegrees(27));
            stack.translate(-0.15, -0.62, -0.9);
        }

        stack.pushPose();
        if (handPosition == HandPosition.LOOK_AT_DISPLAY) {
            stack.mulPose(Axis.XP.rotationDegrees(-80));
            stack.mulPose(Axis.ZP.rotationDegrees(-35 * f));
            stack.mulPose(Axis.YP.rotationDegrees(-20 * f));
            stack.translate(0.15 * f, -0.7, 0.63);
            stack.scale(1, 1.8f, 1);
        }
        if (renderHands(itemDisplayContext))
            renderHands(side, itemStack, itemDisplayContext, stack, bufferSource, light, overlay, partialTick);
        stack.popPose();

        stack.pushPose();

        stack.mulPose(Axis.YP.rotationDegrees(45 * f));
        stack.mulPose(Axis.XP.rotationDegrees(-60));
        stack.mulPose(Axis.ZP.rotationDegrees(15 * f));
        stack.translate(-0.1 * f, 1, -0.4);
        renderItem(itemStack, itemDisplayContext, stack, bufferSource, light, overlay, partialTick);
        stack.popPose();
    }

    public abstract void renderItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick);

    public boolean renderHands(ItemDisplayContext itemDisplayContext) {
        return true;
    }

    public HandPosition getHandPosition(ItemDisplayContext itemDisplayContext) {
        return HandPosition.NORMAL;
    }

    public void renderHands(HumanoidArm handSide, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, float partialTick) {
        //ItemRenderingHelper.applyBobbing(stack, partialTick);
        ItemRenderingHelper.renderHand(stack, bufferSource, light, handSide, getHandPosition(itemDisplayContext));
    }
}
