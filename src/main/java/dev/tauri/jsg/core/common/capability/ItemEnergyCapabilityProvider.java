package dev.tauri.jsg.core.common.capability;

import dev.tauri.jsg.core.common.power.general.ItemEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ItemEnergyCapabilityProvider implements ICapabilityProvider {
    protected ItemEnergyStorage energyStorage;

    public ItemEnergyCapabilityProvider(final ItemStack stack, long maxEnergy, long maxReceive, long maxExtract, boolean creative) {
        energyStorage = new ItemEnergyStorage(stack, maxEnergy, maxReceive, maxExtract) {
            @Override
            public boolean isCreative() {
                return creative;
            }

            @Override
            public long setEnergy(long energy, boolean notify) {
                return super.setEnergy(creative ? Integer.MAX_VALUE : energy, notify);
            }

            @Override
            public long getTrueEnergyStored() {
                if (creative)
                    return Integer.MAX_VALUE;
                return super.getTrueEnergyStored();
            }

            @Override
            public long extractLongEnergy(long max, boolean simulate) {
                if (creative) {
                    return max;
                }
                return super.extractLongEnergy(max, simulate);
            }

            @Override
            public long receiveLongEnergy(long max, boolean simulate) {
                if (creative) {
                    return 0;
                }
                return super.receiveLongEnergy(max, simulate);
            }

            @Override
            public boolean canReceive() {
                if (creative) {
                    // Creative item should not receive any energy...
                    return false;
                }
                return super.canReceive();
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY)
            return LazyOptional.of(() -> energyStorage).cast();
        return LazyOptional.empty();
    }
}
