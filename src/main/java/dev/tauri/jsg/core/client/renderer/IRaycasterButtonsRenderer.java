package dev.tauri.jsg.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.common.blockentity.PointOfOriginProvider;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface IRaycasterButtonsRenderer {
    List<RayCastedButton> getRaycasterButtons();

    Raycaster getRaycaster();

    default void renderRaycasterButtons(BlockEntity be, PoseStack poseStack, MultiBufferSource bufferSource) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        poseStack.pushPose();

        var translation = getRaycaster().getTranslation(be.getLevel(), be.getBlockPos());
        poseStack.translate(translation.getX(), translation.getY(), translation.getZ());
        var rotation = getRaycaster().getRotation(be.getLevel(), be.getBlockPos(), player);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            poseStack.pushPose();
            for (var btn : getRaycasterButtons()) {
                btn.render(poseStack);
            }
            poseStack.popPose();
        }

        if (player.isShiftKeyDown()) {
            var btn = getRaycaster().getRaycastedButton(player.level(), be.getBlockPos(), player, InteractionHand.MAIN_HAND);
            if (btn != null) {
                PointOfOrigin poo = null;
                if (be instanceof PointOfOriginProvider pooProvider)
                    poo = pooProvider.getPointOfOrigin();
                btn.renderTitle(poo, rotation, poseStack, bufferSource);
            }
        }
        poseStack.popPose();
    }
}
