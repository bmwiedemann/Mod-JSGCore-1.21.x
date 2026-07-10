package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;

public class SaplingBlockTabbed extends SaplingBlock implements ITabbedItem {
    public SaplingBlockTabbed(TreeGrower pTreeGrower, Properties pProperties) {
        super(pTreeGrower, pProperties);
    }
}
