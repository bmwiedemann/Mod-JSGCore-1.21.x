package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class DoorBlockTabbed extends DoorBlock implements ITabbedItem {
    public DoorBlockTabbed(Properties pProperties, BlockSetType pType) {
        super(pProperties, pType);
    }
}
