package dev.tauri.jsg.core.common.registry.helper;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.registry.helper.builder.block.BlockRegistryHelperGeneric;
import dev.tauri.jsg.core.common.registry.helper.builder.block.BlockRegistryHelperOre;
import dev.tauri.jsg.core.common.registry.helper.builder.item.ItemRegistryHelperGeneric;

public class CoreRegistryHelpers {

    public static final BlockRegistryHelperGeneric BLOCK_HELPER = new BlockRegistryHelperGeneric(JSGCore.REGISTRY_HELPER::block);
    public static final BlockRegistryHelperOre ORE_HELPER = new BlockRegistryHelperOre(JSGCore.REGISTRY_HELPER::block);

    public static final ItemRegistryHelperGeneric ITEM_HELPER = new ItemRegistryHelperGeneric(JSGCore.REGISTRY_HELPER::item);

    public static final FluidHelper FLUID_HELPER = new FluidHelper(JSGCore.REGISTRY_HELPER::fluid, JSGCore.REGISTRY_HELPER::fluidType, JSGCore.REGISTRY_HELPER::item, JSGCore.REGISTRY_HELPER::block);

}
