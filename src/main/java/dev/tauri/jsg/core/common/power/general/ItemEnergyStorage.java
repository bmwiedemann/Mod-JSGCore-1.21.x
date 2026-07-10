package dev.tauri.jsg.core.common.power.general;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemEnergyStorage extends JSGEnergyStorage {
    protected final ItemStack stack;

    public ItemEnergyStorage(ItemStack stack) {
        super();
        this.stack = stack;
        updateEnergyFromItem();
    }

    public ItemEnergyStorage(ItemStack stack, long capacity) {
        super(capacity);
        this.stack = stack;
        updateEnergyFromItem();
    }

    public ItemEnergyStorage(ItemStack stack, long capacity, long maxTransfer) {
        super(capacity, maxTransfer);
        this.stack = stack;
        updateEnergyFromItem();
    }

    public ItemEnergyStorage(ItemStack stack, long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.stack = stack;
        updateEnergyFromItem();
    }

    public boolean isCreative() {
        return false;
    }

    @Override
    public long getTrueEnergyStored() {
        updateEnergyFromItem();
        return super.getTrueEnergyStored();
    }

    public void updateEnergyFromItem() {
        var tag = ItemNBT.getOrCreateTag(stack);
        if (tag.contains("energy", Tag.TAG_INT)) {
            this.energy = tag.getInt("energy");
            return;
        }
        this.energy = tag.getLong("energy");
    }

    @Override
    public void onEnergyChanged() {
        ItemNBT.getOrCreateTag(stack).putLong("energy", energy);
    }
}
