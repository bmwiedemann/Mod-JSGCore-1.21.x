package dev.tauri.jsg.core.common.power.general;

import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains one energy storage saved to NBT and also behaves as holder for additional storages
 */
public abstract class LargeEnergyStorage extends SmallEnergyStorage {
    private final List<IEnergyStorage> storages = new ArrayList<>();

    public LargeEnergyStorage() {
        super();
    }

    public LargeEnergyStorage(long capacity) {
        super(capacity);
    }

    public LargeEnergyStorage(long capacity, long maxTransfer) {
        super(capacity, maxTransfer);
    }

    public LargeEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public void clearStorages() {
        storages.clear();
    }

    public void addStorage(IEnergyStorage storage) {
        storages.add(storage);
    }

    public long getEnergyStoredInternal() {
        if (hasCreativeSource()) return 0;
        return energy;
    }

    public boolean hasCreativeSource() {
        return storages.stream().anyMatch(s -> s instanceof ItemEnergyStorage itemEnergyStorage && itemEnergyStorage.isCreative());
    }

    @Override
    public long getTrueEnergyStored() {
        if (hasCreativeSource()) return Long.MAX_VALUE;
        long additionalEnergy = 0;
        for (var storage : storages) {
            if (storage instanceof JSGEnergyStorage jsgStorage) {
                additionalEnergy += jsgStorage.getTrueEnergyStored();
                continue;
            }
            additionalEnergy += storage.getEnergyStored();
        }
        return super.getTrueEnergyStored() + additionalEnergy;
    }

    @Override
    public long getTrueMaxEnergyStored() {
        if (hasCreativeSource()) return Long.MAX_VALUE;
        long additionalEnergy = 0;
        for (var storage : storages) {
            if (storage instanceof JSGEnergyStorage jsgStorage) {
                additionalEnergy += jsgStorage.getTrueMaxEnergyStored();
                continue;
            }
            additionalEnergy += storage.getMaxEnergyStored();
        }
        return super.getTrueMaxEnergyStored() + additionalEnergy;
    }

    @Override
    public long receiveLongEnergy(long maxReceive, boolean simulate) {
        if (hasCreativeSource()) return 0;
        var originalMaxReceive = maxReceive;
        maxReceive -= receiveLongEnergyInternal(maxReceive, simulate);
        for (var storage : storages) {
            if (storage instanceof JSGEnergyStorage jsgStorage) {
                maxReceive -= jsgStorage.receiveLongEnergy(maxReceive, simulate);
                continue;
            }
            maxReceive -= storage.receiveEnergy(regularEnergy(maxReceive), simulate);
        }
        return originalMaxReceive - maxReceive;
    }

    public long receiveLongEnergyInternal(long maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        long energyReceived = Math.min(super.getTrueMaxEnergyStored() - energy, Math.min(maxReceive(), maxReceive));
        if (!simulate)
            energy += energyReceived;

        if (energyReceived != 0)
            onEnergyChanged();
        return energyReceived;
    }

    @Override
    public long extractLongEnergy(long maxExtract, boolean simulate) {
        if (hasCreativeSource()) return maxExtract;
        var originalMaxExtract = maxExtract;
        maxExtract -= super.extractLongEnergy(maxExtract, simulate);
        for (var storage : storages) {
            if (storage instanceof JSGEnergyStorage jsgStorage) {
                maxExtract -= jsgStorage.extractLongEnergy(maxExtract, simulate);
                continue;
            }
            maxExtract -= storage.extractEnergy(regularEnergy(maxExtract), simulate);
        }
        return originalMaxExtract - maxExtract;
    }

    /**
     * Sets ONLY internal energy
     *
     * @param energy New energy level
     * @param notify True if onEnergyChanged() should be fired
     * @return New saved energy level
     */
    @Override
    public long setEnergy(long energy, boolean notify) {
        return super.setEnergy(energy, notify);
    }
}
