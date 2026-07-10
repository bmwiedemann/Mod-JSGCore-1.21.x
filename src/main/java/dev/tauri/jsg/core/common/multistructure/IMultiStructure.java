package dev.tauri.jsg.core.common.multistructure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IMultiStructure {
    Map<BlockPos, BlockState> getBlocks();
    boolean shouldBeMerged();
    boolean checkMergeState();
}
