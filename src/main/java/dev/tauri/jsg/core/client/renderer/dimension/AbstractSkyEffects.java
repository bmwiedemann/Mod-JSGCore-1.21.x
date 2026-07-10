package dev.tauri.jsg.core.client.renderer.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.client.renderer.dimension.util.CustomSkyObjectRenderer;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.common.integration.stellarview.StellarViewCompatibility;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSkyEffects extends DimensionSpecialEffects.OverworldEffects {
    protected final List<CustomSkyObjectRenderer> CUSTOM_SKY_OBJECTS = new ArrayList<>();

    protected VertexBuffer starBuffer;
    protected VertexBuffer skyBuffer;
    protected VertexBuffer darkBuffer;

    public AbstractSkyEffects() {
        createDarkSky();
        createLightSky();
        createStars();
    }

    public void addCustomSkyObjects(CustomSkyObjectRenderer... customSkyObjectRenderer) {
        CUSTOM_SKY_OBJECTS.addAll(List.of(customSkyObjectRenderer));
    }

    public boolean drawStars() {
        return true;
    }

    public boolean drawDefaultSun() {
        return true;
    }

    public boolean drawDefaultMoon() {
        return true;
    }


    protected void createDarkSky() {
        Tesselator tesselator = Tesselator.getInstance();
        if (this.darkBuffer != null) {
            this.darkBuffer.close();
        }

        this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        MeshData bufferbuilder$renderedbuffer = buildSkyDisc(tesselator, -16.0F);
        this.darkBuffer.bind();
        this.darkBuffer.upload(bufferbuilder$renderedbuffer);
        VertexBuffer.unbind();
    }

    protected void createLightSky() {
        Tesselator tesselator = Tesselator.getInstance();
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }

        this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        MeshData bufferbuilder$renderedbuffer = buildSkyDisc(tesselator, 16.0F);
        this.skyBuffer.bind();
        this.skyBuffer.upload(bufferbuilder$renderedbuffer);
        VertexBuffer.unbind();
    }

    public long getStarsGenerationSeed() {
        return 10842L;
    }

    public int getStarsCount() {
        return 1500;
    }

    protected void createStars() {
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }

        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        MeshData renderedBuffer = drawStars(tesselator, RandomSource.create(getStarsGenerationSeed()), getStarsCount());
        this.starBuffer.bind();
        this.starBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }

    public Color getStarColor(RandomSource random) {
        return new Color(190, 160, 0, 0xAA);
    }

    /**
     * @author Povstalec - modified by MineDragonCZ_
     * Commentary by Povstalec
     */
    protected MeshData drawStars(Tesselator tesselator, RandomSource randomsource, int numberOfStars) {
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < numberOfStars; ++i) {
            /*
             * Since I couldn't find any explanation for how this works,
             * I've taken it upon myself to explain everything in as much detail as I can
             */

            // This generates random coordinates for the Star close to the camera
            double x = (randomsource.nextFloat() * 2.0F - 1.0F);
            double y = (randomsource.nextFloat() * 2.0F - 1.0F);
            double z = (randomsource.nextFloat() * 2.0F - 1.0F);
            double starSize = (0.05F + randomsource.nextFloat() * 0.08F); // This randomizes the Star size
            double distance = x * x + y * y + z * z;

            if (distance < 1.0D && distance > 0.01D) {
                distance = 1.0D / Math.sqrt(distance);
                x *= distance;
                y *= distance;
                z *= distance;

                // This effectively pushes the Star away from the camera
                double starX = x * 100.0D;
                double starY = y * 100.0D;
                double starZ = z * 100.0D;

                /* These very obviously represent Spherical Coordinates (r, theta, phi)
                 *
                 * Spherical equations (adjusted for Minecraft, since usually +Z is up, while in Minecraft +Y is up):
                 *
                 * r = sqrt(x * x + y * y + z * z)
                 * theta = arctan2(x, z)
                 * phi = arccos(y / r)
                 *
                 * x = r * sin(phi) * sin(theta)
                 * y = r * cos(phi)
                 * z = r * sin(phi) * cos(theta)
                 *
                 * Polar equations
                 * z = r * cos(theta)
                 * x = r * sin(theta)
                 */

                double sphericalTheta = Math.atan2(x, z);
                double sinTheta = Math.sin(sphericalTheta);
                double cosTheta = Math.cos(sphericalTheta);

                double xzLength = Math.sqrt(x * x + z * z);
                double sphericalPhi = Math.atan2(xzLength, y);
                double sinPhi = Math.sin(sphericalPhi);
                double cosPhi = Math.cos(sphericalPhi);

                // sin and cos are used to effectively clamp the random number between two values without actually clamping it,
                // which would result in some awkward lines as Stars would be brought to the clamped values
                // Both affect Star size and rotation
                double random = randomsource.nextDouble() * Math.PI * 2.0D;
                double sinRandom = Math.sin(random);
                double cosRandom = Math.cos(random);

                // This loop creates the 4 corners of a Star
                var starColor = getStarColor(randomsource);
                var alpha = 1f;
                for (int k = 0; k < 3; k++) {
                    if (k >= 1) {
                        starSize *= (1f + (k * 0.3f));
                        alpha -= (0.1f + (0.2f * k));
                    }
                    for (int j = 0; j < 4; ++j) {
                        /* Bitwise AND is there to multiply the size by either 1 or -1 to reach this effect:
                         * Where a coordinate is written as (A,B)
                         * 		(-1,1)		(1,1)
                         * 		x-----------x
                         * 		|			|
                         * 		|			|
                         * 		|			|
                         * 		|			|
                         * 		x-----------x
                         * 		(-1,-1)		(1,-1)
                         * 								|	A	B
                         * 0 & 2 = 000 & 010 = 000 = 0	|	x
                         * 1 & 2 = 001 & 010 = 000 = 0	|	x	x
                         * 2 & 2 = 010 & 010 = 010 = 2	|	x	x
                         * 3 & 2 = 011 & 010 = 010 = 2	|	x	x
                         * 4 & 2 = 100 & 000 = 000 = 0	|		x
                         *
                         * After you subtract 1 one from each of them, you get this:
                         * j:	0	1	2	3
                         * --------------------
                         * A:	-1	-1	1	1
                         * B:	-1	1	1	-1
                         * Which corresponds to:
                         * UV:	00	01	11	10
                         */
                        double aLocation = (double) ((j & 2) - 1) * starSize;
                        double bLocation = (double) ((j + 1 & 2) - 1) * starSize;

                        /* These are the values for cos(random) = sin(random)
                         * (random is simply there to randomize the star rotation)
                         * j:	0	1	2	3
                         * -------------------
                         * A:	0	-2	0	2
                         * B:	-2	0	2	0
                         *
                         * A and B are there to create a diamond effect on the Y-axis and X-axis respectively
                         * (Pretend it's not as stretched as the slashes make it looked)
                         * Where a coordinate is written as (B,A)
                         *
                         * 			(0,2)
                         * 			/\
                         * 	 (-2,0)/  \(2,0)
                         * 		   \  /
                         * 			\/
                         * 			(0,-2)
                         *
                         */
                        double height = aLocation * cosRandom - bLocation * sinRandom;
                        double width = bLocation * cosRandom + aLocation * sinRandom;

                        double heightProjectionY = height * sinPhi; // Y projection of the Star's height

                        double heightProjectionXZ = -height * cosPhi; // If the Star is angled, the XZ projected height needs to be subtracted from both X and Z

                        /*
                         * projectedX:
                         * Projected height is projected onto the X-axis using sin(theta) and then gets subtracted (added because it's already negative)
                         * Width is projected onto the X-axis using cos(theta) and then gets subtracted
                         *
                         * projectedZ:
                         * Width is projected onto the Z-axis using sin(theta)
                         * Projected height is projected onto the Z-axis using cos(theta) and then gets subtracted (added because it's already negative)
                         *
                         */
                        double projectedX = heightProjectionXZ * sinTheta - width * cosTheta;
                        double projectedZ = width * sinTheta + heightProjectionXZ * cosTheta;
                        builder.addVertex((float) (starX + projectedX), (float) (starY + heightProjectionY), (float) (starZ + projectedZ)).setColor(starColor.getRed(), starColor.getGreen(), starColor.getBlue(), (int) (starColor.getAlpha() * alpha));
                    }
                }
            }
        }
        return builder.buildOrThrow();
    }

    protected static MeshData buildSkyDisc(Tesselator tesselator, float pY) {
        float f1 = 512.0F;
        float f = Math.signum(pY) * f1;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder pBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        pBuilder.addVertex(0.0F, pY, 0.0F);

        for (int i = -180; i <= 180; i += 45) {
            pBuilder.addVertex((f * Mth.cos((float) i * ((float) Math.PI / 180F))), pY, (f1 * Mth.sin((float) i * ((float) Math.PI / 180F))));
        }

        return pBuilder.buildOrThrow();
    }


    public void renderSunAndMoon(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, FogType fogType, Runnable setupFog) {
        float f11 = 1.0F - getRainLevel(level, partialTick);
        RenderSystem.setShaderColor(f11, f11, f11, 1f);
        //RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.defaultBlendFunc();
        if (drawDefaultSun())
            CustomSkyObjectRenderer.OVERWORLD_SUN.render(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog);
        if (drawDefaultMoon())
            CustomSkyObjectRenderer.OVERWORLD_MOON.render(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog);
        for (var r : CUSTOM_SKY_OBJECTS)
            r.render(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog);
    }

    public void renderSunAndMoonSunrise(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, FogType fogType, Runnable setupFog) {
        if (drawDefaultSun())
            CustomSkyObjectRenderer.OVERWORLD_SUN.renderSkySunrise(level, ticks, partialTick, poseStack, camera, projectionMatrix, setupFog);
        if (drawDefaultMoon())
            CustomSkyObjectRenderer.OVERWORLD_MOON.renderSkySunrise(level, ticks, partialTick, poseStack, camera, projectionMatrix, setupFog);
        for (var r : CUSTOM_SKY_OBJECTS)
            r.renderSkySunrise(level, ticks, partialTick, poseStack, camera, projectionMatrix, setupFog);
    }

    public void renderSkyObjects(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, FogType fogType, Runnable setupFog) {
        if (isFoggy) return;
        if (fogType == FogType.POWDER_SNOW || fogType == FogType.LAVA || doesMobEffectBlockSky(camera)) return;
        renderSkyBackground(level, ticks, partialTick, poseStack, camera, projectionMatrix, setupFog);
        renderSunAndMoonSunrise(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog);
        applyRainColor(level, ticks, partialTick, poseStack, camera, projectionMatrix, setupFog);
        if (drawStars())
            renderStars(level, ticks, partialTick, poseStack, camera, projectionMatrix, setupFog);
        renderSunAndMoon(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, fogType, setupFog);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, int pixelY, Vector3f colors) {
        if (Integrations.STELLAR_VIEW.isLoaded())
            StellarViewCompatibility.adjustLightmapColors(level, partialTicks, skyDarken, blockLightRedFlicker, skyLight, pixelX, pixelY, colors);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("all")
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelViewMatrix);
        if (Integrations.STELLAR_VIEW.isLoaded()) {
            return StellarViewCompatibility.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        }

        setupFog.run();
        poseStack.pushPose();
        renderSkyObjects(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, camera.getFluidInCamera(), setupFog);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double d0 = Minecraft.getInstance().player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level);
        if (d0 < 0.0D) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 12.0F, 0.0F);
            this.darkBuffer.bind();
            this.darkBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, RenderSystem.getShader());
            VertexBuffer.unbind();
            poseStack.popPose();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        poseStack.popPose();
        return true;
    }

    protected void renderSkyBackground(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Runnable setupFog) {
        Vec3 vec3 = level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), partialTick);
        float f = (float) vec3.x;
        float f1 = (float) vec3.y;
        float f2 = (float) vec3.z;
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, f1, f2, 1.0F);
        ShaderInstance shaderinstance = RenderSystem.getShader();
        this.skyBuffer.bind();
        this.skyBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
    }

    protected void applyRainColor(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Runnable setupFog) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        float f11 = 1.0F - getRainLevel(level, partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
    }

    protected void renderStars(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, Runnable setupFog) {
        poseStack.pushPose();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360f));
        float f11 = 1.0F - getRainLevel(level, partialTick);
        float f10 = level.getStarBrightness(partialTick) * f11;
        if (f10 > 0.0F) {
            RenderSystem.setShaderColor(1, 1, 1, f10);
            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();
            setupFog.run();
        }
        poseStack.popPose();
    }

    public float getRainLevel(ClientLevel level, float pDelta) {
        return level.getRainLevel(pDelta);
    }

    protected static boolean doesMobEffectBlockSky(Camera camera) {
        if (!(camera.getEntity() instanceof LivingEntity livingentity)) {
            return false;
        } else {
            return livingentity.hasEffect(MobEffects.BLINDNESS) || livingentity.hasEffect(MobEffects.DARKNESS);
        }
    }

    protected int prevCloudX = Integer.MIN_VALUE;
    protected int prevCloudY = Integer.MIN_VALUE;
    protected int prevCloudZ = Integer.MIN_VALUE;
    protected Vec3 prevCloudColor = Vec3.ZERO;
    @Nullable
    protected CloudStatus prevCloudsType;
    protected boolean generateClouds = true;
    @Nullable
    private VertexBuffer cloudBuffer;

    @Nullable
    public ResourceLocation getCustomCloudsTexture() {
        return null;
    }

    public float getCloudsSpeed() {
        return 1f;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        var tex = getCustomCloudsTexture();
        if (tex == null) return false;
        float f = level.effects().getCloudHeight();
        if (!Float.isNaN(f)) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            double d1 = (((float) ticks + partialTick) * 0.03F) * getCloudsSpeed();
            double d2 = (camX + d1) / 12.0D;
            double d3 = (f - (float) camY + 0.33F);
            double d4 = camZ / 12.0D + (double) 0.33F;
            d2 -= (Mth.floor(d2 / 2048.0D) * 2048);
            d4 -= (Mth.floor(d4 / 2048.0D) * 2048);
            float f3 = (float) (d2 - (double) Mth.floor(d2));
            float f4 = (float) (d3 / 4.0D - (double) Mth.floor(d3 / 4.0D)) * 4.0F;
            float f5 = (float) (d4 - (double) Mth.floor(d4));
            Vec3 vec3 = level.getCloudColor(partialTick);
            int i = (int) Math.floor(d2);
            int j = (int) Math.floor(d3 / 4.0D);
            int k = (int) Math.floor(d4);
            if (i != this.prevCloudX || j != this.prevCloudY || k != this.prevCloudZ || Minecraft.getInstance().options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(vec3) > 2.0E-4D) {
                this.prevCloudX = i;
                this.prevCloudY = j;
                this.prevCloudZ = k;
                this.prevCloudColor = vec3;
                this.prevCloudsType = Minecraft.getInstance().options.getCloudsType();
                this.generateClouds = true;
            }

            if (this.generateClouds) {
                this.generateClouds = false;
                if (this.cloudBuffer != null) {
                    this.cloudBuffer.close();
                }

                this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                MeshData bufferbuilder$renderedbuffer = this.buildClouds(Tesselator.getInstance(), d2, d3, d4, vec3);
                this.cloudBuffer.bind();
                this.cloudBuffer.upload(bufferbuilder$renderedbuffer);
                VertexBuffer.unbind();
            }

            RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getRendertypeCloudsShader);
            RenderSystem.setShaderTexture(0, tex);
            FogRenderer.levelFogColor();
            poseStack.pushPose();
            poseStack.scale(12.0F, 1.0F, 12.0F);
            poseStack.translate(-f3, f4, -f5);
            if (this.cloudBuffer != null) {
                this.cloudBuffer.bind();
                int l = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;

                for (int i1 = l; i1 < 2; ++i1) {
                    if (i1 == 0) {
                        RenderSystem.colorMask(false, false, false, false);
                    } else {
                        RenderSystem.colorMask(true, true, true, true);
                    }

                    ShaderInstance shaderinstance = RenderSystem.getShader();
                    this.cloudBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
                }

                VertexBuffer.unbind();
            }

            poseStack.popPose();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
        return true;
    }


    @SuppressWarnings("all")
    private MeshData buildClouds(Tesselator tesselator, double pX, double pY, double pZ, Vec3 pCloudColor) {
        float f = 4.0F;
        float f1 = 0.00390625F;
        int i = 8;
        int j = 4;
        float f2 = 9.765625E-4F;
        float f3 = (float) Mth.floor(pX) * 0.00390625F;
        float f4 = (float) Mth.floor(pZ) * 0.00390625F;
        float f5 = (float) pCloudColor.x;
        float f6 = (float) pCloudColor.y;
        float f7 = (float) pCloudColor.z;
        float f8 = f5 * 0.9F;
        float f9 = f6 * 0.9F;
        float f10 = f7 * 0.9F;
        float f11 = f5 * 0.7F;
        float f12 = f6 * 0.7F;
        float f13 = f7 * 0.7F;
        float f14 = f5 * 0.8F;
        float f15 = f6 * 0.8F;
        float f16 = f7 * 0.8F;
        RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getRendertypeCloudsShader);
        BufferBuilder pBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        float f17 = (float) Math.floor(pY / 4.0D) * 4.0F;
        if (this.prevCloudsType == CloudStatus.FANCY) {
            for (int k = -3; k <= 4; ++k) {
                for (int l = -3; l <= 4; ++l) {
                    float f18 = (float) (k * 8);
                    float f19 = (float) (l * 8);
                    if (f17 > -5.0F) {
                        pBuilder.addVertex(f18 + 0.0F, f17 + 0.0F, f19 + 8.0F).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f11, f12, f13, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                        pBuilder.addVertex(f18 + 8.0F, f17 + 0.0F, f19 + 8.0F).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f11, f12, f13, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                        pBuilder.addVertex(f18 + 8.0F, f17 + 0.0F, f19 + 0.0F).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f11, f12, f13, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                        pBuilder.addVertex(f18 + 0.0F, f17 + 0.0F, f19 + 0.0F).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f11, f12, f13, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                    }

                    if (f17 <= 5.0F) {
                        pBuilder.addVertex(f18 + 0.0F, f17 + 4.0F - 9.765625E-4F, f19 + 8.0F).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                        pBuilder.addVertex(f18 + 8.0F, f17 + 4.0F - 9.765625E-4F, f19 + 8.0F).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                        pBuilder.addVertex(f18 + 8.0F, f17 + 4.0F - 9.765625E-4F, f19 + 0.0F).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                        pBuilder.addVertex(f18 + 0.0F, f17 + 4.0F - 9.765625E-4F, f19 + 0.0F).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                    }

                    if (k > -1) {
                        for (int i1 = 0; i1 < 8; ++i1) {
                            pBuilder.addVertex((f18 + (float) i1 + 0.0F), (f17 + 0.0F), (f19 + 8.0F)).setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                            pBuilder.addVertex((f18 + (float) i1 + 0.0F), (f17 + 4.0F), (f19 + 8.0F)).setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                            pBuilder.addVertex((f18 + (float) i1 + 0.0F), (f17 + 4.0F), (f19 + 0.0F)).setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                            pBuilder.addVertex((f18 + (float) i1 + 0.0F), (f17 + 0.0F), (f19 + 0.0F)).setUv((f18 + (float) i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                        }
                    }

                    if (k <= 1) {
                        for (int j2 = 0; j2 < 8; ++j2) {
                            pBuilder.addVertex((f18 + (float) j2 + 1.0F - 9.765625E-4F), (f17 + 0.0F), (f19 + 8.0F)).setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                            pBuilder.addVertex((f18 + (float) j2 + 1.0F - 9.765625E-4F), (f17 + 4.0F), (f19 + 8.0F)).setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                            pBuilder.addVertex((f18 + (float) j2 + 1.0F - 9.765625E-4F), (f17 + 4.0F), (f19 + 0.0F)).setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                            pBuilder.addVertex((f18 + (float) j2 + 1.0F - 9.765625E-4F), (f17 + 0.0F), (f19 + 0.0F)).setUv((f18 + (float) j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).setColor(f8, f9, f10, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                        }
                    }

                    if (l > -1) {
                        for (int k2 = 0; k2 < 8; ++k2) {
                            pBuilder.addVertex((f18 + 0.0F), (f17 + 4.0F), (f19 + (float) k2 + 0.0F)).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                            pBuilder.addVertex((f18 + 8.0F), (f17 + 4.0F), (f19 + (float) k2 + 0.0F)).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                            pBuilder.addVertex((f18 + 8.0F), (f17 + 0.0F), (f19 + (float) k2 + 0.0F)).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                            pBuilder.addVertex((f18 + 0.0F), (f17 + 0.0F), (f19 + (float) k2 + 0.0F)).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) k2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                        }
                    }

                    if (l <= 1) {
                        for (int l2 = 0; l2 < 8; ++l2) {
                            pBuilder.addVertex((f18 + 0.0F), (f17 + 4.0F), (f19 + (float) l2 + 1.0F - 9.765625E-4F)).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                            pBuilder.addVertex((f18 + 8.0F), (f17 + 4.0F), (f19 + (float) l2 + 1.0F - 9.765625E-4F)).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                            pBuilder.addVertex((f18 + 8.0F), (f17 + 0.0F), (f19 + (float) l2 + 1.0F - 9.765625E-4F)).setUv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                            pBuilder.addVertex((f18 + 0.0F), (f17 + 0.0F), (f19 + (float) l2 + 1.0F - 9.765625E-4F)).setUv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float) l2 + 0.5F) * 0.00390625F + f4).setColor(f14, f15, f16, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                        }
                    }
                }
            }
        } else {
            int j1 = 1;
            int k1 = 32;

            for (int l1 = -32; l1 < 32; l1 += 32) {
                for (int i2 = -32; i2 < 32; i2 += 32) {
                    pBuilder.addVertex((l1 + 0), f17, (i2 + 32)).setUv((float) (l1 + 0) * 0.00390625F + f3, (float) (i2 + 32) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                    pBuilder.addVertex((l1 + 32), f17, (i2 + 32)).setUv((float) (l1 + 32) * 0.00390625F + f3, (float) (i2 + 32) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                    pBuilder.addVertex((l1 + 32), f17, (i2 + 0)).setUv((float) (l1 + 32) * 0.00390625F + f3, (float) (i2 + 0) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                    pBuilder.addVertex((l1 + 0), f17, (i2 + 0)).setUv((float) (l1 + 0) * 0.00390625F + f3, (float) (i2 + 0) * 0.00390625F + f4).setColor(f5, f6, f7, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                }
            }
        }

        return pBuilder.buildOrThrow();
    }
}
