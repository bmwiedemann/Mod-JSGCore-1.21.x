package dev.tauri.jsg.core.common.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.renderer.HandPosition;
import dev.tauri.jsg.core.common.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class ItemRenderingHelper {
    public static void renderHand(PoseStack stack, MultiBufferSource bufferSource, int light, HumanoidArm hand, HandPosition handPosition) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        RenderSystem.enableDepthTest();
        PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        stack.pushPose();

        RenderSystem.setShaderTexture(0, player.getSkinTextureLocation());
        if (hand == HumanoidArm.RIGHT) {
            playerRenderer.renderRightHand(stack, bufferSource, light, player);
        } else {
            playerRenderer.renderLeftHand(stack, bufferSource, light, player);
        }

        stack.popPose();
    }

    public static void applyBobbing(PoseStack poseStack, float pPartialTicks) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.getCameraEntity() instanceof Player player) {
            float f = player.walkDist - player.walkDistO;
            float f1 = -(player.walkDist + f * pPartialTicks);
            float f2 = Mth.lerp(pPartialTicks, player.oBob, player.bob);
            poseStack.translate(Mth.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(Mth.cos(f1 * (float) Math.PI) * f2), 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f1 * (float) Math.PI) * f2 * 3.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));
        }
    }

    public static float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
        return f;
    }
}
