package dev.tauri.jsg.core.common.power.general;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class EnergyRequiredToOperate implements INBTSerializable<CompoundTag> {

    public long energyToOpen;
    public long keepAlive;

    public EnergyRequiredToOperate(long energyToOpen, long keepAlive) {
        this.energyToOpen = energyToOpen;
        this.keepAlive = keepAlive;
    }

    public static EnergyRequiredToOperate free() {
        return new EnergyRequiredToOperate(0, 0);
    }

    public EnergyRequiredToOperate(double energyToOpen, double keepAlive) {
        this((long) energyToOpen, (long) keepAlive);
    }

    @Override
    public String toString() {
        return "[open=" + energyToOpen + ", keepAlive=" + keepAlive + "]";
    }

    public EnergyRequiredToOperate mul(double mul) {
        return new EnergyRequiredToOperate(energyToOpen * mul, keepAlive * mul);
    }

    public EnergyRequiredToOperate add(EnergyRequiredToOperate add) {
        return new EnergyRequiredToOperate(energyToOpen + add.energyToOpen, keepAlive + add.keepAlive);
    }

    public EnergyRequiredToOperate cap(long max) {
        return new EnergyRequiredToOperate(Math.min(energyToOpen, max), keepAlive);
    }

    public EnergyRequiredToOperate update(EnergyRequiredToOperate updated) {
        this.keepAlive = updated.keepAlive;
        this.energyToOpen = updated.energyToOpen;
        return this;
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
        compound.putLong("keepAlive", keepAlive);
        compound.putLong("energyToOpen", energyToOpen);
        return compound;
    }

    
    public void deserializeNBT(CompoundTag compound) {
        if (compound.contains("keepAlive", CompoundTag.TAG_INT)) {
            keepAlive = compound.getInt("keepAlive");
            energyToOpen = compound.getInt("energyToOpen");
            return;
        }
        keepAlive = compound.getLong("keepAlive");
        energyToOpen = compound.getLong("energyToOpen");
    }
}
