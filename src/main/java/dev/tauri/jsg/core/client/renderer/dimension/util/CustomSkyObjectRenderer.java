package dev.tauri.jsg.core.client.renderer.dimension.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("all")
public class CustomSkyObjectRenderer {
    public static final Function<Float, Color> DEFAULT_SUNRISE_COLOR = (coef) -> {
        float alpha = 1.0F - (1.0F - Mth.sin(coef * (float) Math.PI)) * 0.99F;
        alpha *= alpha;
        var red = coef * 0.3F + 0.7F;
        var green = coef * coef * 0.7F + 0.2F;
        var blue = coef * coef * 0.0F + 0.2F;
        return new Color(red, green, blue, alpha);
    };

    public final ResourceLocation texture;
    public final UVGetter uvGetter;
    public final Runnable finalTransformation;
    public final float celestialRotationSpeed;
    public final float size;

    public final float offsetCelestial;
    public final float offsetHorizontal;
    public final boolean fadeOutWithSun;

    public final Function<Float, Color> sunRiseColorFunction;

    public CustomSkyObjectRenderer(boolean fadeOutWithSun, boolean isSun, ResourceLocation texture, float celestialRotationSpeed, float size, float offsetHorizontal, float offsetCelestial) {
        this(fadeOutWithSun, isSun ? DEFAULT_SUNRISE_COLOR : null, texture, celestialRotationSpeed, size, offsetHorizontal, offsetCelestial);
    }

    public CustomSkyObjectRenderer(boolean fadeOutWithSun, Function<Float, Color> sunRiseColor, ResourceLocation texture, float celestialRotationSpeed, float size, float offsetHorizontal, float offsetCelestial) {
        this(fadeOutWithSun, sunRiseColor, texture, celestialRotationSpeed, size, offsetHorizontal, offsetCelestial, () -> {
        }, (o, corner, level, ticks, partialTicks, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog) -> Pair.of((corner == 1 || corner == 2) ? 1f : 0f, (corner > 1) ? 1f : 0f));
    }

    public CustomSkyObjectRenderer(boolean fadeOutWithSun, boolean isSun, ResourceLocation texture, float celestialRotationSpeed, float size, float offsetHorizontal, float offsetCelestial, Runnable finalTransformation, UVGetter uvGetter) {
        this(fadeOutWithSun, isSun ? DEFAULT_SUNRISE_COLOR : null, texture, celestialRotationSpeed, size, offsetHorizontal, offsetCelestial, finalTransformation, uvGetter);
    }

    public CustomSkyObjectRenderer(boolean fadeOutWithSun, Function<Float, Color> sunRiseColor, ResourceLocation texture, float celestialRotationSpeed, float size, float offsetHorizontal, float offsetCelestial, Runnable finalTransformation, UVGetter uvGetter) {
        this.texture = texture;
        this.sunRiseColorFunction = sunRiseColor;
        this.celestialRotationSpeed = celestialRotationSpeed;
        this.size = size * 4f;
        this.offsetCelestial = offsetCelestial;
        this.offsetHorizontal = offsetHorizontal;
        this.uvGetter = uvGetter;
        this.finalTransformation = finalTransformation;
        this.fadeOutWithSun = fadeOutWithSun;
    }

    public void render(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, FogType fogType, Runnable setupFog) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        var tessellator = Tesselator.getInstance();
        var bufferBuilder = tessellator.getBuilder();

        var celestialAngle = getObjectCelestialAngle(level.getTimeOfDay(partialTick), false);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(getObjectHorizontalAngle(level.getTimeOfDay(partialTick), false)));
        poseStack.mulPose(Axis.XP.rotationDegrees(celestialAngle));
        finalTransformation.run();

        if (fadeOutWithSun) {
            RenderSystem.setShaderColor(1, 1, 1, Math.min(0.8f, level.getStarBrightness(partialTick) * 4f) + 0.2f);
        } else
            RenderSystem.setShaderColor(1, 1, 1, 1);

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(poseStack.last().pose(), -size, 100.0F, -size).setUv(
                uvGetter.getUV(this, 0, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).first(),
                uvGetter.getUV(this, 0, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).second()
        );
        bufferBuilder.addVertex(poseStack.last().pose(), size, 100.0F, -size).setUv(
                uvGetter.getUV(this, 1, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).first(),
                uvGetter.getUV(this, 1, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).second()
        );
        bufferBuilder.addVertex(poseStack.last().pose(), size, 100.0F, size).setUv(
                uvGetter.getUV(this, 2, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).first(),
                uvGetter.getUV(this, 2, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).second()
        );
        bufferBuilder.addVertex(poseStack.last().pose(), -size, 100.0F, size).setUv(
                uvGetter.getUV(this, 3, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).first(),
                uvGetter.getUV(this, 3, level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog).second()
        );
        tessellator.end();

        poseStack.popPose();
    }

    public float getObjectCelestialAngle(float dayTime, boolean radians) {
        return dayTime * (radians ? (float) (Math.PI * 2f) : 360f) * celestialRotationSpeed + offsetCelestial;
    }

    public float getObjectHorizontalAngle(float dayTime, boolean radians) {
        return -(radians ? (float) Math.PI : 90.0F) + offsetHorizontal;
    }

    public void renderSkySunrise(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Runnable setupFog) {
        var timeOfDay = level.getTimeOfDay(partialTick);
        var angle = getObjectCelestialAngle(timeOfDay, true);
        Color sunRiseColor = null;
        float radius = 0.4F;
        float cosAngle = Mth.cos(angle);
        if (this.sunRiseColorFunction != null && cosAngle >= -radius && cosAngle <= radius) {
            float coef = cosAngle / radius * 0.5F + 0.5F;
            sunRiseColor = this.sunRiseColorFunction.apply(coef);
        }

        if (sunRiseColor != null) {
            poseStack.pushPose();
            float f3 = Mth.sin(angle) < 0.0F ? 0 : 180;
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(f3 + getObjectHorizontalAngle(timeOfDay, false)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            finalTransformation.run();
            float r = sunRiseColor.getRed() / 255f;
            float g = sunRiseColor.getGreen() / 255f;
            float b = sunRiseColor.getBlue() / 255f;
            float a = sunRiseColor.getAlpha() / 255f;
            Matrix4f matrix4f = poseStack.last().pose();
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.addVertex(matrix4f, 0.0F, 100.0F, 0.0F).setColor(r, g, b, a);
            for (int j = 0; j <= 16; ++j) {
                float f7 = (float) j * ((float) Math.PI * 2F) / 16.0F;
                float f8 = Mth.sin(f7);
                float f9 = Mth.cos(f7);
                bufferbuilder.addVertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * a).setColor(r, g, b, 0.0F);
            }

            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            poseStack.popPose();
        }
    }

    public static final ResourceLocation OVERWORLD_MOON_LOCATION = JSGMapping.rl("textures/environment/moon_phases.png");
    public static final ResourceLocation OVERWORLD_SUN_LOCATION = JSGMapping.rl("textures/environment/sun.png");

    public static final CustomSkyObjectRenderer OVERWORLD_SUN = new CustomSkyObjectRenderer(false, true, OVERWORLD_SUN_LOCATION, 1f, 30f, 0, 0);
    public static final CustomSkyObjectRenderer OVERWORLD_MOON = new CustomSkyObjectRenderer(true, false, OVERWORLD_MOON_LOCATION, 1f, 20f, 0, 180f, () -> {
    }, (o, corner, level, ticks, partialTicks, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog) -> {
        int k = level.getMoonPhase();
        int l = k % 4;
        int i1 = k / 4 % 2;
        float minX = (float) (l) / 4.0F;
        float minY = (float) (i1) / 2.0F;
        float maxX = (float) (l + 1) / 4.0F;
        float maxY = (float) (i1 + 1) / 2.0F;

        return switch (corner) {
            case 0 -> Pair.of(maxX, minY);
            case 1 -> Pair.of(minX, minY);
            case 2 -> Pair.of(minX, maxY);
            default -> Pair.of(maxX, maxY);
        };
    });

    public interface UVGetter {
        Pair<Float, Float> getUV(CustomSkyObjectRenderer skyObject, int corner, ClientLevel level, int ticks, float partialTicks, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, FogType fogType, Runnable setupFog);
    }
}
