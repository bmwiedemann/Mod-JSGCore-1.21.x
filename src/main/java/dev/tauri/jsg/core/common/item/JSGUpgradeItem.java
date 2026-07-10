package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.util.IUpgrade;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

public class JSGUpgradeItem extends JSGItem implements IUpgradeItem {
    protected final Supplier<IUpgrade> upgrade;

    public JSGUpgradeItem(Properties properties, Supplier<IUpgrade> upgrade) {
        this(properties, List.of(), upgrade);
    }

    public JSGUpgradeItem(Properties properties, RegistryObject<CreativeModeTab> tab, Supplier<IUpgrade> upgrade) {
        this(properties, (tab == null ? List.of() : List.of(tab)), upgrade);
    }

    @ParametersAreNonnullByDefault
    public JSGUpgradeItem(Properties properties, List<RegistryObject<CreativeModeTab>> tabs, Supplier<IUpgrade> upgrade) {
        super(properties, tabs);
        this.upgrade = upgrade;
    }

    @Override
    public IUpgrade getUpgrade() {
        return upgrade.get();
    }
}
