package dev.tauri.jsg.core.common.util;

import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified version of {@link ItemStackHandler}.
 * Respects resizing of the item handlers.
 */
public class JSGItemStackHandler extends ItemStackHandler {

    private int size;

    public JSGItemStackHandler(int size) {
        super(size);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        List<ItemStack> copy = Util.make(new ArrayList<>(), (list) -> list.addAll(this.stacks));
        super.setSize(size);
        this.size = size;
        copy = copy.subList(0, Math.min(size, copy.size()));
        this.stacks.clear();
        for (var i = 0; i < copy.size(); ++i)
            setStackInSlot(i, copy.get(i));
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = getStackInSlot(slot);
        //if (!CreativeItemsChecker.canInteractWith(stack, false)) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
