package dev.tauri.jsg.core.common.multistructure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.core.client.renderer.BlockRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IMultiStructureRenderer<T extends IMultiStructureBE<? extends IMultiStructure>> {

    private BlockEntity be(T tileEntity) {
        return (BlockEntity) (tileEntity);
    }

    default void renderBuildingHelper(T tileEntity, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay) {
        if (be(tileEntity).getLevel() == null) return;
        stack.pushPose();
        RenderSystem.enableBlend();
        for (Map.Entry<BlockPos, BlockState> block : tileEntity.getMergeHelper().getBlocks().entrySet()) {
            if (!be(tileEntity).getLevel().getBlockState(block.getKey()).canBeReplaced()) continue;
            var pos = block.getKey();
            stack.pushPose();
            var newPos = pos.subtract(be(tileEntity).getBlockPos());
            BlockRenderer.renderBlock(be(tileEntity).getLevel(), pos, block.getValue(), newPos, stack, source, combinedLight, combinedOverlay, 0.5f);
            stack.popPose();
        }
        stack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
