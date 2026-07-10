package dev.tauri.jsg.core.common.registry.helper.builder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public abstract class RegistryObjectBuilder<T extends RegistryHelper<?, ?>> {
    protected final T registryHelper;

    protected final String name;

    protected List<RegistryObject<CreativeModeTab>> tabs;

    public RegistryObjectBuilder(T registryHelper, String name) {
        this.registryHelper = registryHelper;
        this.name = name;
    }

    public RegistryObjectBuilder(String name, RegistryObjectBuilder<T> other) {
        this(other.registryHelper, name);
        if (other.tabs == null) return;
        this.tabs = new ArrayList<>();
        this.tabs.addAll(other.tabs);
    }

    public RegistryObjectBuilder<T> setInTabs(List<RegistryObject<CreativeModeTab>> tabs) {
        this.tabs = new ArrayList<>();
        this.tabs.addAll(tabs);
        return this;
    }

    public interface TooltipApplier<T> {
        @ParametersAreNonnullByDefault
        void apply(T item, ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag);
    }
}
