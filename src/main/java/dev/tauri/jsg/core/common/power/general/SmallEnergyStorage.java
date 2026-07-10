package dev.tauri.jsg.core.common.power.general;

import dev.tauri.jsg.core.common.power.JSGEnergyStorage;

/**
 * Contains only one energy storage
 */
public abstract class SmallEnergyStorage extends JSGEnergyStorage {
    public SmallEnergyStorage() {
        super();
    }

    public SmallEnergyStorage(long capacity) {
        super(capacity);
    }

    public SmallEnergyStorage(long capacity, long maxTransfer) {
        super(capacity, maxTransfer);
    }

    public SmallEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }
}
