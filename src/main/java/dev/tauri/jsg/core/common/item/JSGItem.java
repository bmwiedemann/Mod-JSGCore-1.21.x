package dev.tauri.jsg.core.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class JSGItem extends Item implements ITabbedItem {

    private final List<RegistryObject<CreativeModeTab>> tabs;

    public JSGItem(Properties properties) {
        this(properties, List.of());
    }

    public JSGItem(Properties properties, RegistryObject<CreativeModeTab> tab) {
        this(properties, (tab == null ? List.of() : List.of(tab)));
    }

    @ParametersAreNonnullByDefault
    public JSGItem(Properties properties, List<RegistryObject<CreativeModeTab>> tabs) {
        super(properties);
        this.tabs = tabs;
    }

    @Override
    @NotNull
    public List<RegistryObject<CreativeModeTab>> getTabs() {
        if (tabs == null) return List.of();
        return tabs;
    }
}
