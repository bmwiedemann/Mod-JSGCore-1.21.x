package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.StainedGlassBlock;

public class GlassBlockTabbed extends StainedGlassBlock implements ITabbedItem {
    public GlassBlockTabbed(DyeColor pDyeColor, Properties pProperties) {
        super(pDyeColor, pProperties);
    }
}
