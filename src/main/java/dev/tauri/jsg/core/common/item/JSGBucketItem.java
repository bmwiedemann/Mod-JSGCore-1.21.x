package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.Fluid;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import java.util.function.Supplier;

public class JSGBucketItem extends BucketItem implements ITabbedItem {
    public JSGBucketItem(Supplier<? extends Fluid> fluid, Properties builder) {
        super(fluid.get(), builder);
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_FLUIDS;
    }

}
