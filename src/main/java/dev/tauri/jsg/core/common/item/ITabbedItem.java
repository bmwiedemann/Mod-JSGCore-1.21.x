package dev.tauri.jsg.core.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ITabbedItem {
    @Nullable
    default RegistryObject<CreativeModeTab> getTab() {
        return null;
    }

    default List<RegistryObject<CreativeModeTab>> getTabs() {
        var tab = getTab();
        if (tab == null) return List.of();
        return List.of(tab);
    }
}
