package dev.tauri.jsg.core.common.power;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * @author Povstalec, edited by VojtechSin
 */
public abstract class JSGEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag> {
    public static final char[] SUFFIXES = {'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y', 'R', 'Q'};
    public static final String UNIT = "FE";

    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;

    public JSGEnergyStorage() {
        this(Long.MAX_VALUE);
    }

    public JSGEnergyStorage(long capacity) {
        this(capacity, Long.MAX_VALUE);
    }

    public JSGEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public JSGEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this.energy = 0;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public final int receiveEnergy(int maxReceive, boolean simulate) {
        return regularEnergy(receiveLongEnergy(maxReceive, simulate));
    }

    public long receiveLongEnergy(long maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        long energyReceived = Math.min(getTrueMaxEnergyStored() - energy, Math.min(maxReceive(), maxReceive));
        if (!simulate)
            energy += energyReceived;

        if (energyReceived != 0)
            onEnergyChanged();
        return energyReceived;
    }

    @Override
    public final int extractEnergy(int maxExtract, boolean simulate) {
        return regularEnergy(extractLongEnergy(maxExtract, simulate));
    }

    public long extractLongEnergy(long maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.min(energy, Math.min(maxExtract(), maxExtract));
        if (!simulate)
            energy -= energyExtracted;

        if (energyExtracted != 0)
            onEnergyChanged();

        return energyExtracted;
    }

    @Override
    public final int getEnergyStored() {
        return regularEnergy(getTrueEnergyStored());
    }

    public long getTrueEnergyStored() {
        return this.energy;
    }

    @Override
    public final int getMaxEnergyStored() {
        return regularEnergy(getTrueMaxEnergyStored());
    }

    public long getTrueMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract() > 0;
    }

    public boolean canFullyExtract(long maxExtract) {
        return getTrueEnergyStored() - maxExtract >= 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive() > 0 && getTrueEnergyStored() < getTrueMaxEnergyStored();
    }

    public boolean canFullyReceive(long maxReceive) {
        return getTrueEnergyStored() + maxReceive <= getTrueMaxEnergyStored();
    }

    /**
     * Sets energy level
     *
     * @param energy New energy level
     * @param notify True if onEnergyChanged() should be fired
     * @return New saved energy level
     */
    public long setEnergy(long energy, boolean notify) {
        this.energy = Math.min(energy, getTrueMaxEnergyStored());
        if (notify)
            onEnergyChanged();
        return this.energy;
    }

    public abstract void onEnergyChanged();

    public long maxReceive() {
        return this.maxReceive;
    }

    public long maxExtract() {
        return this.maxExtract;
    }

    public static int regularEnergy(long energy) {
        return (int) Math.min(Integer.MAX_VALUE, energy);
    }

    public static String energyToString(long energy) {
        if (energy < 0)
            return "NaN";

        if (energy < 1000)
            return energy + " " + UNIT;

        double total = energy;
        int prefix = -1;
        for (; total >= 1000 && prefix < SUFFIXES.length; prefix++) {
            total /= 1000;
        }

        total *= 100;
        total = Math.floor(total);
        total /= 100;

        return total + " " + SUFFIXES[prefix] + UNIT;
    }

    public static String energyToString(JSGEnergyStorage storage) {
        return energyToString(storage.getTrueEnergyStored(), storage.getTrueMaxEnergyStored());
    }

    public static String energyToString(IEnergyStorage storage) {
        if (storage instanceof JSGEnergyStorage jsgEnergyStorage)
            return energyToString(jsgEnergyStorage);
        return energyToString(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public static String energyToString(long energy, long capacity) {
        return energyToString(energy) + "/" + energyToString(capacity);
    }

    public static long energyToTarget(long energyTarget, long energyStored, long maxExtract) {
        long needed = energyTarget - energyStored;

        if (needed < 0)
            return 0;

        return Math.min(needed, maxExtract);
    }

    public static float getEnergyPercent(JSGEnergyStorage storage) {
        return getEnergyPercent(storage.getTrueEnergyStored(), storage.getTrueMaxEnergyStored());
    }

    public static float getEnergyPercent(IEnergyStorage storage) {
        if (storage instanceof JSGEnergyStorage jsgEnergyStorage)
            return getEnergyPercent(jsgEnergyStorage);
        return getEnergyPercent(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public static float getEnergyPercent(long energy, long capacity) {
        return (float) (energy / (double) capacity);
    }

    @Override
    public String toString() {
        return energyToString(getTrueEnergyStored(), getTrueMaxEnergyStored());
    }

    
    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        return serializeNBT();
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag compound) {
        deserializeNBT(compound);
    }

    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putLong("energy", energy);
        return compound;
    }

    
    public void deserializeNBT(CompoundTag compound) {
        if (compound.contains("energy", CompoundTag.TAG_INT))
            this.energy = compound.getInt("energy");
        else
            this.energy = compound.getLong("energy");
    }
}
