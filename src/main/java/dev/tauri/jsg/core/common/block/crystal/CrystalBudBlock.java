package dev.tauri.jsg.core.common.block.crystal;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import dev.tauri.jsg.core.common.registry.RegistryObject;

public class CrystalBudBlock extends AmethystClusterBlock implements ITabbedItem {
    public final CrystalBudType type;
    public final ICrystalColor color;

    public CrystalBudBlock(CrystalBudType type, ICrystalColor color) {
        super(type.size, type.offset, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(color.getColor()).lightLevel((state) -> ((type.ordinal() * 2) + 1)));
        this.type = type;
        this.color = color;
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_BUILDING_BLOCKS;
    }
}
