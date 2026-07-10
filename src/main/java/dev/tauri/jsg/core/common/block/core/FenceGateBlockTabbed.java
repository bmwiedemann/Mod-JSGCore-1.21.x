package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.FenceGateBlock;

public class FenceGateBlockTabbed extends FenceGateBlock implements ITabbedItem {
    public FenceGateBlockTabbed(Properties props, SoundEvent openSound, SoundEvent closeSound) {
        super(props, openSound, closeSound);
    }
}
