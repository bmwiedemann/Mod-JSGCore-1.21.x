package dev.tauri.jsg.core.common.datafixer;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;

public class CoreItemsDataFixer {
    /**
     * MissingMappingsEvent is gone on NeoForge; renamed ids are handled with registry
     * aliases, which must be added during mod construction (before registry events).
     */
    public static void registerAliases() {
        var items = JSGCore.REGISTRY_HELPER.item().unwrap();
        items.addAlias(JSGMapping.rl(JSGCore.MOD_ID, "capacitor_block"), CoreItems.CRYSTAL_ENERGY_BASIC.getId());
        items.addAlias(JSGMapping.rl(JSGCore.MOD_ID, "capacitor_block_creative"), CoreItems.CRYSTAL_ENERGY_CREATIVE.getId());
    }
}
