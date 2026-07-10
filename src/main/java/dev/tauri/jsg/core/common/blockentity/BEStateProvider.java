package dev.tauri.jsg.core.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BEStateProvider extends StateProviderInterface {
    private BlockEntity self() {
        return (BlockEntity) this;
    }

    @Override
    default BlockPos getStateHandlerBlockPos() {
        return self().getBlockPos();
    }
}
