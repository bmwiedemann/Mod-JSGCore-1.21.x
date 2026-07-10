package dev.tauri.jsg.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.renderer.LevelRenderer.DIRECTIONS;

public class BlockRenderer {

    @SuppressWarnings("deprecation")
    public static void renderBlockShaded(Level level, BlockPos absoluteWorldPos, BlockState blockState, BlockPos pos, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        var rendershape = blockState.getRenderShape();
        stack.pushPose();
        stack.translate(pos.getX(), pos.getY(), pos.getZ());
        if (rendershape == RenderShape.MODEL) {
            var modelData = ModelData.EMPTY;
            var renderer = Minecraft.getInstance().getBlockRenderer();
            var bakedModel = renderer.getBlockModel(blockState);
            var vertexConsumer = source.getBuffer(RenderType.translucent());
            var seed = blockState.getSeed(absoluteWorldPos);
            for (net.minecraft.client.renderer.RenderType rt : bakedModel.getRenderTypes(blockState, RandomSource.create(42), modelData)) {
                renderer.getModelRenderer().tesselateBlock(level, bakedModel, blockState, absoluteWorldPos, stack, vertexConsumer,
                        true, level.random, seed, overlay, modelData, rt);
            }
        } else
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, stack, source, light, overlay);
        stack.popPose();
    }


    public static void renderBlock(Level level, BlockPos absoluteWorldPos, BlockState blockState, BlockPos pos, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        renderBlock(level, absoluteWorldPos, blockState, pos, stack, source, light, overlay, 1);
    }

    @SuppressWarnings("deprecation")
    public static void renderBlock(Level level, BlockPos absoluteWorldPos, BlockState blockState, BlockPos pos, PoseStack stack, MultiBufferSource source, int light, int overlay, float alpha) {
        var rendershape = blockState.getRenderShape();
        stack.pushPose();
        stack.translate(pos.getX(), pos.getY(), pos.getZ());
        if (rendershape == RenderShape.MODEL) {
            var renderer = Minecraft.getInstance().getBlockRenderer();
            BakedModel bakedmodel = renderer.getBlockModel(blockState);
            var modelData = ModelData.EMPTY;
            for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(blockState, RandomSource.create(42), modelData))
                renderModel(stack.last(), source.getBuffer(RenderType.translucent()), blockState, bakedmodel, (tintIndex) -> Minecraft.getInstance().getBlockColors().getColor(blockState, level, absoluteWorldPos, tintIndex), alpha, light, overlay, modelData, rt);
        } else
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, stack, source, light, overlay);
        stack.popPose();
    }

    public static void renderModel(PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, Function<Integer, Integer> tintColorGetter, float alpha, int pPackedLight, int pPackedOverlay, net.neoforged.neoforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        for (Direction direction : DIRECTIONS) {
            randomsource.setSeed(42L);
            renderQuadList(pPose, pConsumer, tintColorGetter, alpha, pModel.getQuads(pState, direction, randomsource, modelData, renderType), pPackedLight, pPackedOverlay);
        }

        randomsource.setSeed(42L);
        renderQuadList(pPose, pConsumer, tintColorGetter, alpha, pModel.getQuads(pState, null, randomsource, modelData, renderType), pPackedLight, pPackedOverlay);
    }

    private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, Function<Integer, Integer> tintColorGetter, float alpha, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
        for (BakedQuad bakedquad : pQuads) {
            float r = 1;
            float g = 1;
            float b = 1;
            if (bakedquad.isTinted()) {
                var color = tintColorGetter.apply(bakedquad.getTintIndex());
                if (color != -1) {
                    float red = (float) (color >> 16 & 255) / 255.0F;
                    float green = (float) (color >> 8 & 255) / 255.0F;
                    float blue = (float) (color & 255) / 255.0F;

                    r = Mth.clamp(red, 0.0F, 1.0F);
                    g = Mth.clamp(green, 0.0F, 1.0F);
                    b = Mth.clamp(blue, 0.0F, 1.0F);
                }
            }
            pConsumer.putBulkData(pPose, bakedquad, new float[]{1, 1, 1, 1}, r, g, b, alpha, new int[]{pPackedLight, pPackedLight, pPackedLight, pPackedLight}, pPackedOverlay, true);
        }

    }


    public enum FluidTextureType {
        STILL,
        FLOWING
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack fluid, FluidTextureType type) {
        try {
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
            if (type == FluidTextureType.STILL)
                return getSprite(props.getStillTexture(fluid));
            return getSprite(props.getFlowingTexture(fluid));
        } catch (Exception ignored) {

        }
        return null;
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    @SuppressWarnings("deprecation")
    public static TextureAtlasSprite getSprite(BlockState blockState, Direction direction) {
        var modelManager = Minecraft.getInstance().getModelManager();
        var model = modelManager.getBlockModelShaper().getBlockModel(blockState);
        List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create(42L));
        TextureAtlasSprite bestSprite = model.getParticleIcon(); // Fallback

        if (quads.isEmpty()) {
            quads = model.getQuads(blockState, null, RandomSource.create());
        }

        for (BakedQuad quad : quads) {
            if (quad.getTintIndex() == -1 || quads.size() == 1) {
                bestSprite = quad.getSprite();
                break;
            }
        }
        return bestSprite;
    }
}
