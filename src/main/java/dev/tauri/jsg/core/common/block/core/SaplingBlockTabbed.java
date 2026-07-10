package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

public class SaplingBlockTabbed extends SaplingBlock implements ITabbedItem {
    public SaplingBlockTabbed(AbstractTreeGrower pTreeGrower, Properties pProperties) {
        super(pTreeGrower, pProperties);
    }
}
