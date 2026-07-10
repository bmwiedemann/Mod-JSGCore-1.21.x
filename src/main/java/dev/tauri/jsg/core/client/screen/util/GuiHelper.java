package dev.tauri.jsg.core.client.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.JSGColorUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class GuiHelper {

    public static void translateFor3D() {
        currentStack.scale(1, -1, 1);
    }

    public static PoseStack currentStack = null;

    public static void drawTexturedRectScaled(int xLeftCoord, int yBottomCoord, TextureAtlasSprite textureSprite, int maxWidth, int maxHeight, float scaleHeight) {
        maxHeight = (int) (maxHeight * scaleHeight);
        yBottomCoord -= maxHeight;

        drawTexturedRect(xLeftCoord, yBottomCoord, textureSprite, maxWidth, maxHeight, scaleHeight);
    }

    public static void drawTexturedRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int maxWidth, int maxHeight, float scaleHeight) {
        double v = textureSprite.getV1() - textureSprite.getV0();
        v *= (1 - scaleHeight);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureSprite.atlasLocation());
        Matrix4f matrix = currentStack.last().pose();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, (xCoord), (yCoord + maxHeight), 0).setUv(textureSprite.getU0(), textureSprite.getV1());
        bufferbuilder.addVertex(matrix, (xCoord + maxWidth), (yCoord + maxHeight), 0).setUv(textureSprite.getU1(), textureSprite.getV1());
        bufferbuilder.addVertex(matrix, (xCoord + maxWidth), (yCoord), 0).setUv(textureSprite.getU1(), (float) (textureSprite.getV0() + v));
        bufferbuilder.addVertex(matrix, (xCoord), (yCoord), 0).setUv(textureSprite.getU0(), (float) (textureSprite.getV0() + v));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawTexturedRect(float x, float y, float textureX, float textureY, float width, float height) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, (x), (y + height), 0).setUv(((float) (textureX) * 0.00390625F), ((float) (textureY + height) * 0.00390625F));
        bufferbuilder.addVertex(matrix, (x + width), (y + height), 0).setUv(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F));
        bufferbuilder.addVertex(matrix, (x + width), (y), 0).setUv(((float) (textureX + width) * 0.00390625F), ((float) (textureY) * 0.00390625F));
        bufferbuilder.addVertex(matrix, (x), (y), 0).setUv(((float) (textureX) * 0.00390625F), ((float) (textureY) * 0.00390625F));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(x, y, u, v, width, height, 0);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel) {
        float uScale = 0.00390625F;
        float vScale = 0.00390625F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, (float) x, (float) (y + height), zLevel).setUv((float) u * 0.00390625F, (float) (v + height) * 0.00390625F);
        bufferbuilder.addVertex(matrix, (float) (x + width), (float) (y + height), zLevel).setUv((float) (u + width) * 0.00390625F, (float) (v + height) * 0.00390625F);
        bufferbuilder.addVertex(matrix, (float) (x + width), (float) y, zLevel).setUv((float) (u + width) * 0.00390625F, (float) v * 0.00390625F);
        bufferbuilder.addVertex(matrix, (float) x, (float) y, zLevel).setUv((float) u * 0.00390625F, (float) v * 0.00390625F);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static boolean isPointInRegion(double rectX, double rectY, double rectWidth, double rectHeight, double pointX, double pointY) {
        return pointX >= rectX && pointY >= rectY && pointX < (rectX + rectWidth) && pointY < (rectY + rectHeight);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, x, y + height, 0.0f).setUv((u * f), ((v + (float) height) * f1));
        bufferbuilder.addVertex(matrix, (x + width), (y + height), 0.0f).setUv(((u + (float) width) * f), ((v + (float) height) * f1));
        bufferbuilder.addVertex(matrix, (x + width), y, 0.0f).setUv(((u + (float) width) * f), (v * f1));
        bufferbuilder.addVertex(matrix, x, y, 0.0f).setUv((u * f), (v * f1));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Matrix4f matrix = currentStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, x, (y + height), 0.0f).setUv((u * f), ((v + (float) vHeight) * f1));
        bufferbuilder.addVertex(matrix, (x + width), (y + height), 0.0f).setUv(((u + (float) uWidth) * f), ((v + (float) vHeight) * f1));
        bufferbuilder.addVertex(matrix, (x + width), y, 0.0f).setUv(((u + (float) uWidth) * f), (v * f1));
        bufferbuilder.addVertex(matrix, x, y, 0.0f).setUv((u * f), (v * f1));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawScaledCustomSizeModalRectColor(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, float r, float g, float b, float a) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Matrix4f matrix = currentStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix, x, (y + height), 0.0f).setUv((u * f), ((v + (float) vHeight) * f1)).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, (x + width), (y + height), 0.0f).setUv(((u + (float) uWidth) * f), ((v + (float) vHeight) * f1)).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, (x + width), y, 0.0f).setUv(((u + (float) uWidth) * f), (v * f1)).setColor(r, g, b, a);
        bufferbuilder.addVertex(matrix, x, y, 0.0f).setUv((u * f), (v * f1)).setColor(r, g, b, a);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Matrix4f matrix = currentStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        //RenderSystem.setShaderColor(f, f1, f2, f3);
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex(matrix, left, bottom, 0.0f).setColor(f, f1, f2, f3);
        bufferbuilder.addVertex(matrix, right, bottom, 0.0f).setColor(f, f1, f2, f3);
        bufferbuilder.addVertex(matrix, right, top, 0.0f).setColor(f, f1, f2, f3);
        bufferbuilder.addVertex(matrix, left, top, 0.0f).setColor(f, f1, f2, f3);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawGradientRect(currentStack, left, top, right, bottom, startColor, endColor);
    }

    public static void drawGradientRect(PoseStack stack, int left, int top, int right, int bottom, int startColor, int endColor) {
        drawGradientRect(stack.last().pose(), 0, left, top, right, bottom, startColor, endColor);
    }

    public static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex(mat, right, top, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        bufferbuilder.addVertex(mat, left, top, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        bufferbuilder.addVertex(mat, left, bottom, zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        bufferbuilder.addVertex(mat, right, bottom, zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        RenderSystem.disableBlend();
    }

    public static void drawTexturedRectWithShadow(int x, int y, int xOffset, int yOffset, int xSize, int ySize, float color) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(color, color, color, 1);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, xSize, ySize);

        RenderSystem.setShaderColor(color, color, color, 0.2f);
        drawModalRectWithCustomSizedTexture(x + xOffset, y + yOffset, 0, 0, xSize, ySize, xSize, ySize);
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void drawHoveringText(GuiGraphics graphics, Font font, List<String> textLines, int mouseX, int mouseY) {
        List<Component> components = new ArrayList<>();
        for (String s : textLines)
            components.add(Component.literal(s));
        graphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
    }


    public static void renderTexturedRect(GuiGraphics graphics, ResourceLocation texture, int x, int y, int z, int u, int v, int width, int height, int texWidth, int texHeight) {
        graphics.blit(texture, x, y, z, u, v, texWidth, texHeight, width, height);
    }


    public static void blit(GuiGraphics graphics, ResourceLocation tex, int x, int y, int sizeX, int sizeY, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_) {
        graphics.blit(tex, x, y, sizeX, sizeY, p_282285_, p_283199_, p_282186_, p_282322_, p_282481_, p_281887_);
    }

    public static void renderTransparentBackground(GuiGraphics graphics, Screen screen) {
        graphics.fillGradient(0, 0, screen.width, screen.height, -1072689136, -804253680);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite) {
        drawTiledSprite(xPosition, yPosition, yOffset, desiredWidth, desiredHeight, sprite, 16, 16, 0);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel) {
        drawTiledSprite(xPosition, yPosition, yOffset, desiredWidth, desiredHeight, sprite, textureWidth, textureHeight, zLevel, true);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel, boolean blend) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        if (blend) {
            RenderSystem.enableBlend();
        }
        //Note: We still use the tesselator as that is what GuiGraphics#innerBlit does
        BufferBuilder vertexBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = currentStack.last().pose();
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uLocalDif = uDif * maskRight / textureWidth;
            float uLocalMin;
            float uLocalMax;
            uLocalMin = uMin + uLocalDif;
            uLocalMax = uMax;
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    //Note: We don't want to fully break out because our height will be zero if we are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * textureHeight);
                int maskTop = textureHeight - height;
                float vLocalDif = vDif * maskTop / textureHeight;
                float vLocalMin;
                float vLocalMax;
                vLocalMin = vMin;
                vLocalMax = vMax - vLocalDif;
                vertexBuffer.addVertex(matrix4f, x, y + textureHeight, zLevel).setUv(uLocalMin, vLocalMax);
                vertexBuffer.addVertex(matrix4f, shiftedX, y + textureHeight, zLevel).setUv(uLocalMax, vLocalMax);
                vertexBuffer.addVertex(matrix4f, shiftedX, y + maskTop, zLevel).setUv(uLocalMax, vLocalMin);
                vertexBuffer.addVertex(matrix4f, x, y + maskTop, zLevel).setUv(uLocalMin, vLocalMin);
            }
        }
        BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());
        if (blend) {
            RenderSystem.disableBlend();
        }
    }

    public static void renderScrollingStringLeftAligned(GuiGraphics graphics, Font font, Component component, int minX, int maxX, int y, int color, boolean shadow) {
        int i = font.width(component);
        int k = maxX - minX;
        if (i > k) {
            int l = i - k;
            double d0 = (double) Util.getMillis() / 1000.0D;
            double d1 = Math.max((double) l * 0.5D, 3.0D);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D;
            double d3 = Mth.lerp(d2, 0.0D, l);
            graphics.enableScissor(minX, y, maxX, y + 12);
            graphics.drawString(font, component, minX - (int) d3, y, color, shadow);
            graphics.disableScissor();
        } else {
            graphics.drawString(font, component, minX, y, color, shadow);
        }
    }

    public static void renderScrollingStringLeftAligned(GuiGraphics graphics, Font font, Component component, int minX, int maxX, int minY, int maxY, int color, boolean shadow) {
        int i = font.width(component);
        int j = (minY + maxY - 9) / 2 + 1;
        int k = maxX - minX;
        if (i > k) {
            int l = i - k;
            double d0 = (double) Util.getMillis() / 1000.0D;
            double d1 = Math.max((double) l * 0.5D, 3.0D);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D;
            double d3 = Mth.lerp(d2, 0.0D, l);
            graphics.enableScissor(minX, minY, maxX, maxY);
            graphics.drawString(font, component, minX - (int) d3, j, color, shadow);
            graphics.disableScissor();
        } else {
            graphics.drawString(font, component, minX, j, color, shadow);
        }
    }

    public static void renderPath(GuiGraphics graphics, List<Vector2i> path, int width, int color, boolean shade) {
        Vector2i lastPoint = null;
        if (shade) {
            for (var point : path) {
                if (lastPoint == null) {
                    lastPoint = point;
                    continue;
                }
                lineShade(graphics, point.x, point.y, lastPoint.x, lastPoint.y, width, color, true);
                lastPoint = point;
            }
        }
        lastPoint = null;
        for (var point : path) {
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }
            line(graphics, point.x, point.y, lastPoint.x, lastPoint.y, width, color, false, true);
            lastPoint = point;
        }
    }

    public static void renderOutline(GuiGraphics graphics, int pX, int pY, int pWidth, int pHeight, int pColor, int lineWidth, boolean shade) {
        if (shade) {
            renderOutline(graphics, pX + 1, pY + 1, pWidth, pHeight, JSGColorUtil.blendColors(pColor, 0xff000000, 0.3f), lineWidth, false);
        }
        graphics.fill(pX, pY, pX + pWidth, pY + lineWidth, pColor);
        graphics.fill(pX, pY + pHeight - lineWidth, pX + pWidth, pY + pHeight, pColor);
        graphics.fill(pX, pY + lineWidth, pX + lineWidth, pY + pHeight - lineWidth, pColor);
        graphics.fill(pX + pWidth - lineWidth, pY + lineWidth, pX + pWidth, pY + pHeight - lineWidth, pColor);
    }

    public static void line(GuiGraphics graphics, float x1, float y1, float x2, float y2, int width, int color) {
        line(graphics, x1, y1, x2, y2, width, color, false, false);
    }

    public static void line(GuiGraphics graphics, float x1, float y1, float x2, float y2, int width, int color, boolean shade, boolean overlap) {
        var angle = 1.5 * Math.PI + Math.atan2(y2 - y1, x2 - x1);
        graphics.pose().pushPose();
        graphics.pose().translate(x1, y1, 0);
        graphics.pose().mulPose(Axis.ZP.rotation((float) angle));
        var length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (shade)
            lineShade(graphics, x1, y1, x2, y2, width, color, overlap);
        graphics.pose().translate(0, 0.5, 0);
        graphics.fill(-(int) Math.floor(width / 2f), (overlap ? -1 : 0), (int) Math.ceil(width / 2f), (int) length, color);
        graphics.pose().popPose();
    }

    public static void lineShade(GuiGraphics graphics, float x1, float y1, float x2, float y2, int width, int color, boolean overlap) {
        var angle = 1.5 * Math.PI + Math.atan2(y2 - y1, x2 - x1);
        graphics.pose().pushPose();
        graphics.pose().translate(x1, y1, 0);
        graphics.pose().mulPose(Axis.ZP.rotation((float) angle));
        var length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        graphics.pose().translate(0, 0.5, 0);
        graphics.renderOutline(-(int) Math.floor(width / 2f) - 1, -(overlap ? 1 : 0), (int) Math.ceil(width / 2f), (int) length + 1, JSGColorUtil.blendColors(color, 0xff000000, 0.2f));
        graphics.pose().popPose();
    }

    public static void blitNineSliced(GuiGraphics graphics, ResourceLocation pAtlasLocation, int pX, int pY, int pWidth, int pHeight, int pLeftSliceWidth, int pTopSliceHeight, int pRightSliceWidth, int pBottomSliceHeight, int pUWidth, int pVHeight, int pTextureX, int pTextureY, int texWidth, int texHeight) {
        pLeftSliceWidth = Math.min(pLeftSliceWidth, pWidth / 2);
        pRightSliceWidth = Math.min(pRightSliceWidth, pWidth / 2);
        pTopSliceHeight = Math.min(pTopSliceHeight, pHeight / 2);
        pBottomSliceHeight = Math.min(pBottomSliceHeight, pHeight / 2);
        if (pWidth == pUWidth && pHeight == pVHeight) {
            graphics.blit(pAtlasLocation, pX, pY, pTextureX, pTextureY, pWidth, pHeight, texWidth, texHeight);
        } else if (pHeight == pVHeight) {
            graphics.blit(pAtlasLocation, pX, pY, pTextureX, pTextureY, pLeftSliceWidth, pHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX + pLeftSliceWidth, pY, pWidth - pRightSliceWidth - pLeftSliceWidth, pHeight, pTextureX + pLeftSliceWidth, pTextureY, pUWidth - pRightSliceWidth - pLeftSliceWidth, pVHeight, texWidth, texHeight);
            graphics.blit(pAtlasLocation, pX + pWidth - pRightSliceWidth, pY, pTextureX + pUWidth - pRightSliceWidth, pTextureY, pRightSliceWidth, pHeight, texWidth, texHeight);
        } else if (pWidth == pUWidth) {
            graphics.blit(pAtlasLocation, pX, pY, pTextureX, pTextureY, pWidth, pTopSliceHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX, pY + pTopSliceHeight, pWidth, pHeight - pBottomSliceHeight - pTopSliceHeight, pTextureX, pTextureY + pTopSliceHeight, pUWidth, pVHeight - pBottomSliceHeight - pTopSliceHeight, texWidth, texHeight);
            graphics.blit(pAtlasLocation, pX, pY + pHeight - pBottomSliceHeight, pTextureX, pTextureY + pVHeight - pBottomSliceHeight, pWidth, pBottomSliceHeight, texWidth, texHeight);
        } else {
            graphics.blit(pAtlasLocation, pX, pY, pTextureX, pTextureY, pLeftSliceWidth, pTopSliceHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX + pLeftSliceWidth, pY, pWidth - pRightSliceWidth - pLeftSliceWidth, pTopSliceHeight, pTextureX + pLeftSliceWidth, pTextureY, pUWidth - pRightSliceWidth - pLeftSliceWidth, pTopSliceHeight, texWidth, texHeight);
            graphics.blit(pAtlasLocation, pX + pWidth - pRightSliceWidth, pY, pTextureX + pUWidth - pRightSliceWidth, pTextureY, pRightSliceWidth, pTopSliceHeight, texWidth, texHeight);
            graphics.blit(pAtlasLocation, pX, pY + pHeight - pBottomSliceHeight, pTextureX, pTextureY + pVHeight - pBottomSliceHeight, pLeftSliceWidth, pBottomSliceHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX + pLeftSliceWidth, pY + pHeight - pBottomSliceHeight, pWidth - pRightSliceWidth - pLeftSliceWidth, pBottomSliceHeight, pTextureX + pLeftSliceWidth, pTextureY + pVHeight - pBottomSliceHeight, pUWidth - pRightSliceWidth - pLeftSliceWidth, pBottomSliceHeight, texWidth, texHeight);
            graphics.blit(pAtlasLocation, pX + pWidth - pRightSliceWidth, pY + pHeight - pBottomSliceHeight, pTextureX + pUWidth - pRightSliceWidth, pTextureY + pVHeight - pBottomSliceHeight, pRightSliceWidth, pBottomSliceHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX, pY + pTopSliceHeight, pLeftSliceWidth, pHeight - pBottomSliceHeight - pTopSliceHeight, pTextureX, pTextureY + pTopSliceHeight, pLeftSliceWidth, pVHeight - pBottomSliceHeight - pTopSliceHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX + pLeftSliceWidth, pY + pTopSliceHeight, pWidth - pRightSliceWidth - pLeftSliceWidth, pHeight - pBottomSliceHeight - pTopSliceHeight, pTextureX + pLeftSliceWidth, pTextureY + pTopSliceHeight, pUWidth - pRightSliceWidth - pLeftSliceWidth, pVHeight - pBottomSliceHeight - pTopSliceHeight, texWidth, texHeight);
            graphics.blitRepeating(pAtlasLocation, pX + pWidth - pRightSliceWidth, pY + pTopSliceHeight, pLeftSliceWidth, pHeight - pBottomSliceHeight - pTopSliceHeight, pTextureX + pUWidth - pRightSliceWidth, pTextureY + pTopSliceHeight, pRightSliceWidth, pVHeight - pBottomSliceHeight - pTopSliceHeight, texWidth, texHeight);
        }
    }

    public static int[] getCenterPos(int rectWidth, int rectHeight, int winWidth, int winHeight) {
        return new int[]{((winWidth - rectWidth) / 2), ((winHeight - rectHeight) / 2)};
    }

    public static void renderSymbolCentered(GuiGraphics graphics, float x, float y, int size, SymbolInterface symbol, @Nullable PointOfOrigin origin) {
        var scale = ((float) size / 32f);
        var coef = Math.max(symbol.getSymbolType().getIconWidth(), symbol.getSymbolType().getIconHeight()) / 32f;
        float w = symbol.getSymbolType().getIconWidth() / coef * scale;
        float h = symbol.getSymbolType().getIconHeight() / coef * scale;
        x -= (w / 2f);
        y -= (h / 2f);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        symbol.bindIconTexture(origin);
        Matrix4f matrix = graphics.pose().last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, x, y, 0).setUv(0, 0);
        bufferbuilder.addVertex(matrix, x, y + h, 0).setUv(0, 1);
        bufferbuilder.addVertex(matrix, x + w, y + h, 0).setUv(1, 1);
        bufferbuilder.addVertex(matrix, x + w, y, 0).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
}
