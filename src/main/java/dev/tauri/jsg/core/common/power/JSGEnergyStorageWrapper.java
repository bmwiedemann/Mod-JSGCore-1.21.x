package dev.tauri.jsg.core.common.power;

public class JSGEnergyStorageWrapper extends JSGEnergyStorage {
    protected final JSGEnergyStorage storage;

    public JSGEnergyStorageWrapper(JSGEnergyStorage storage) {
        super();
        this.storage = storage;
    }

    public JSGEnergyStorageWrapper(JSGEnergyStorage storage, long capacity) {
        super(capacity);
        this.storage = storage;
    }

    public JSGEnergyStorageWrapper(JSGEnergyStorage storage, long capacity, long maxTransfer) {
        super(capacity, maxTransfer);
        this.storage = storage;
    }

    public JSGEnergyStorageWrapper(JSGEnergyStorage storage, long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.storage = storage;
    }

    @Override
    public long receiveLongEnergy(long maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        long energyReceived = Math.min(storage.getTrueMaxEnergyStored() - storage.getTrueEnergyStored(), Math.min(maxReceive(), maxReceive));
        if (energyReceived <= 0)
            return 0;
        return storage.receiveLongEnergy(maxReceive, simulate);
    }

    @Override
    public long extractLongEnergy(long maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.min(storage.getTrueEnergyStored(), Math.min(maxExtract(), maxExtract));
        if (energyExtracted <= 0)
            return 0;
        return storage.extractLongEnergy(maxExtract, simulate);
    }

    @Override
    public long getTrueEnergyStored() {
        return storage.getTrueEnergyStored();
    }

    @Override
    public long getTrueMaxEnergyStored() {
        return storage.getTrueMaxEnergyStored();
    }

    @Override
    public boolean canFullyExtract(long maxExtract) {
        return storage.canFullyExtract(maxExtract);
    }

    @Override
    public boolean canFullyReceive(long maxReceive) {
        return storage.canFullyReceive(maxReceive);
    }

    @Override
    public long setEnergy(long energy, boolean notify) {
        return storage.setEnergy(energy, notify);
    }

    @Override
    public void onEnergyChanged() {
        storage.onEnergyChanged();
    }
}
