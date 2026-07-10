package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

public class GlassPaneBlockTabbed extends StainedGlassPaneBlock implements ITabbedItem {
    public GlassPaneBlockTabbed(DyeColor pDyeColor, Properties pProperties) {
        super(pDyeColor, pProperties);
    }
}
