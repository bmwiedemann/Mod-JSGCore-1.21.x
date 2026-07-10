package dev.tauri.jsg.core.common.block;

import dev.tauri.jsg.core.common.blockentity.ITickable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public interface ITickableBEBlock extends EntityBlock {
    @Override
    @ParametersAreNonnullByDefault
    default <T extends BlockEntity> @NotNull BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return ITickable.getTickerHelper();
    }
}
