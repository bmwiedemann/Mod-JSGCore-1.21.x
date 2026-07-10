package dev.tauri.jsg.core.common.item;

import net.minecraft.world.item.CreativeModeTab;

public interface IMultiItem {
    void addAdditional(CreativeModeTab.Output output);
}
