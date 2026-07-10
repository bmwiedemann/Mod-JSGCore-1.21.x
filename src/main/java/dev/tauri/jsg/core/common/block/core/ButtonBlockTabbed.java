package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ButtonBlockTabbed extends ButtonBlock implements ITabbedItem {
    public ButtonBlockTabbed(Properties pProperties, BlockSetType pType, int pTicksToStayPressed, boolean pArrowsCanPress) {
        super(pProperties, pType, pTicksToStayPressed, pArrowsCanPress);
    }
}
