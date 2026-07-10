package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class StairBlockTabbed extends StairBlock implements ITabbedItem {
    public StairBlockTabbed(Supplier<BlockState> state, Properties properties) {
        super(state.get(), properties);
    }
}
