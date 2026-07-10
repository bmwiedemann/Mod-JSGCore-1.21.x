package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TrapdoorBlockTabbed extends TrapDoorBlock implements ITabbedItem {
    public TrapdoorBlockTabbed(Properties pProperties, BlockSetType pType) {
        super(pProperties, pType);
    }
}
