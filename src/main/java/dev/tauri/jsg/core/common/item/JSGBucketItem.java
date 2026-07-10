package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class JSGBucketItem extends BucketItem implements ITabbedItem {
    public JSGBucketItem(Supplier<? extends Fluid> fluid, Properties builder) {
        super(fluid, builder);
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_FLUIDS;
    }

    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }
}
