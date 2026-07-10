package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class PressurePlateBlockTabbed extends PressurePlateBlock implements ITabbedItem {
    public PressurePlateBlockTabbed(Sensitivity pSensitivity, Properties pProperties, BlockSetType pType) {
        super(pSensitivity, pProperties, pType);
    }
}
