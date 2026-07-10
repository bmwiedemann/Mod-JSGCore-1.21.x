package dev.tauri.jsg.core.common.block.core;

import com.mojang.serialization.MapCodec;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.world.level.block.FallingBlock;

public class JSGTabbedFallingBlock extends FallingBlock implements ITabbedItem {
    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.FallingBlock> codec() {
        return simpleCodec(properties -> this);
    }

    public JSGTabbedFallingBlock(Properties pProperties) {
        super(pProperties.strength(4.5F, 3.0F));
    }
}
