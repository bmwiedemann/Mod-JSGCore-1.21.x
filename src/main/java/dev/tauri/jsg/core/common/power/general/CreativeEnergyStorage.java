package dev.tauri.jsg.core.common.power.general;

public class CreativeEnergyStorage extends SmallEnergyStorage {
    public CreativeEnergyStorage() {
        super(Integer.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public long receiveLongEnergy(long maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public long extractLongEnergy(long maxExtract, boolean simulate) {
        return maxExtract;
    }

    @Override
    public long getTrueEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onEnergyChanged() {

    }
}
