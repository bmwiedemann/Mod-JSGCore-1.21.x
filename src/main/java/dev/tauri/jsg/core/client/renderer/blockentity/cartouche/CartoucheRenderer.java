package dev.tauri.jsg.core.client.renderer.blockentity.cartouche;

import com.mojang.blaze3d.vertex.*;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import dev.tauri.jsg.core.client.renderer.EmissiveRenderer;
import dev.tauri.jsg.core.client.renderer.LinkableRenderer;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheBlock;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheType;
import dev.tauri.jsg.core.common.blockentity.CartoucheBE;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

public class CartoucheRenderer implements BlockEntityRenderer<CartoucheBE>, LinkableRenderer {
    public CartoucheRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldRenderOffScreen(CartoucheBE be) {
        return true;
    }

    protected CartoucheType type;

    @Override
    @ParametersAreNonnullByDefault
    public void render(CartoucheBE be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var level = be.getLevel();
        var state = be.renderStateClient;
        if (state == null) return;
        if (level == null) return;
        var pos = be.getBlockPos();

        if (level.getBlockState(pos).getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(-1) != 0) return;
        poseStack.pushPose();
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            be.getRenderBoundingBox().inset(be.getBlockPos()).render(Component.literal("RenderBox"), poseStack, buffer);
        }
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(RotationUtil.getRotation(be.getBlockState()));
        poseStack.pushPose();
        poseStack.translate(0, -0.5, -1);
        poseStack.scale(2, 2, 2);
        var block = be.getBlockState().getBlock();
        if (block instanceof CartoucheBlock cartoucheBlock) {
            var material = cartoucheBlock.material;
            var texDirection = be.getBlockState().getOptionalValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_VERTICAL_PROPERTY)
                    .filter(i -> i != 0)
                    .map(dev.tauri.jsg.core.common.blockstate.JSGProperties::getDirectionByVerticalFacing)
                    .orElse(be.getBlockState().getOptionalValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.FACING_HORIZONTAL_PROPERTY).orElse(Direction.SOUTH));
            var sprite = BlockRenderer.getSprite(material.get(), texDirection);
            if (sprite != null) {
                ITexture.bindTextureWithMc(sprite.atlasLocation());
            }
            type = cartoucheBlock.type;
            type.model.get().render(poseStack, buffer, packedLight, sprite, true);
        }
        poseStack.popPose();
        renderAddress(be, partialTick, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Nullable
    public Vector3f getSymbolCoords(SymbolInterface symbol, int symbolIndex) {
        if (type == null) return null;
        if (symbol.origin()) {
            if (!type.hasPoo) return null;
            return new Vector3f(-0.125f * 1.5f, -0.3f, 1.5f);
        }
        if (symbolIndex >= type.symbolsCount) return null;
        var x = -0.083f * 1.5f;
        var y = 0.24f;
        int scale = 1;
        return switch (type) {
            case SIX -> new Vector3f(x, 0.95f - (y * symbolIndex), scale);
            case SEVEN -> new Vector3f(x, 0.95f + y - (y * symbolIndex), scale);
            case EIGHT -> new Vector3f(x, 0.95f + (y * 2) - (y * symbolIndex), scale);
            case SEVEN_POO -> new Vector3f(x, 0.95f + 0.9f - (y * symbolIndex), scale);
            case EIGHT_POO -> new Vector3f(x, 0.95f + 0.9f + (y / 2f) - (y * symbolIndex), scale);
            case NINE_POO -> new Vector3f(x, 0.95f + 0.9f + y - (y * symbolIndex), scale);
        };
    }

    public void renderAddress(CartoucheBE be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var address = be.getAddress();
        if (address == null) return;
        address = address.addOriginIfMissingAndImmutable();
        var symbolsToDisplay = be.getSymbolsToDisplay();
        for (int symbolId : symbolsToDisplay) {
            if (symbolId <= 0 || symbolId > 9) continue;
            int i = (symbolId - 1);
            if (i >= address.getSize()) continue;
            var symbol = address.get(i);
            var coords = getSymbolCoords(symbol, i);
            if (coords == null) continue;
            poseStack.pushPose();
            poseStack.translate(coords.x, coords.y, -0.465f);
            poseStack.scale(1.3f, 1.3f, 1.3f);
            poseStack.scale(coords.z, coords.z, coords.z);
            renderSymbol(poseStack, 0, 0, symbol, be.getPointOfOrigin(), packedLight, new Color(be.color.getTextColor(), false), be.shiny);
            poseStack.popPose();
        }
    }

    public static void renderSymbol(PoseStack poseStack, float x, float y, SymbolInterface symbol, @Nullable PointOfOrigin origin, int packedLight, Color color, boolean shiny) {
        renderSymbol(poseStack, x, y, 0.2f, 0.2f, symbol, origin, color, 0.6f, packedLight, shiny);
    }

    public static void renderSymbol(PoseStack poseStack, float x, float y, float w, float h, SymbolInterface symbol, @Nullable PointOfOrigin origin, Color color, float alpha, int packedLight, boolean shiny) {
        if (shiny) packedLight = LightTexture.FULL_BRIGHT;
        var iconW = (float) symbol.getSymbolType().getIconWidth();
        var iconH = (float) symbol.getSymbolType().getIconHeight();
        var iconMax = Math.max(iconW, iconH);
        iconW /= iconMax;
        iconH /= iconMax;
        x += (w - (w * iconW)) / 2;
        y += (h - (h * iconH)) / 2;
        w *= iconW;
        h *= iconH;

        for (var i = 0; i < 2; i++) {
            float z = 0;
            float xx = x;
            float yy = y;
            if (i == 1) {
                alpha /= 2;
                z += 0.0001f;
                xx += 0.005f;
                yy -= 0.005f;
            }

            poseStack.pushPose();
            float finalXx = xx;
            float finalYy = yy;
            float finalZ = z;
            int finalPackedLight = packedLight;
            float finalAlpha = alpha;
            float finalW = w;
            float finalH = h;
            EmissiveRenderer.renderWithLightOverlay(poseStack, packedLight, shiny, () -> {
                symbol.bindIconTexture(origin);

                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();
                var matrix = poseStack.last().pose();
                var normalMat = poseStack.last().normal();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
                bufferbuilder.vertex(matrix, finalXx, finalYy, finalZ).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, finalAlpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(finalPackedLight).normal(normalMat, 0, 0, 1).endVertex();
                bufferbuilder.vertex(matrix, finalXx + finalW, finalYy, finalZ).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, finalAlpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(finalPackedLight).normal(normalMat, 0, 0, 1).endVertex();
                bufferbuilder.vertex(matrix, finalXx + finalW, finalYy + finalH, finalZ).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, finalAlpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(finalPackedLight).normal(normalMat, 0, 0, 1).endVertex();
                bufferbuilder.vertex(matrix, finalXx, finalYy + finalH, finalZ).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, finalAlpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(finalPackedLight).normal(normalMat, 0, 0, 1).endVertex();
                BufferUploader.drawWithShader(bufferbuilder.end());
            });
            poseStack.popPose();
        }
    }
}
