package dev.tauri.jsg.core.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JSGBlockItem extends BlockItem implements ITabbedItem, IMultiItem {
    private final List<RegistryObject<CreativeModeTab>> tabs;
    protected final Block rawBlock;

    public JSGBlockItem(Block pBlock, Properties pProperties, @Nullable List<RegistryObject<CreativeModeTab>> tabs) {
        super(pBlock, pProperties);
        this.tabs = tabs;
        this.rawBlock = pBlock;
    }

    @Override
    public List<RegistryObject<CreativeModeTab>> getTabs() {
        return tabs;
    }

    @Override
    public void addAdditional(CreativeModeTab.Output output) {}
}
