package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.item.IUpgradeItem;
import dev.tauri.jsg.core.common.util.IUpgrade;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Simple interface to allow upgrades insert into TE. `tryInsertStack` should be triggered in block class on interact
 */
public interface IUpgradable {
    private BlockEntity self() {
        return (BlockEntity) this;
    }

    default IItemHandler getItemHandler() {
        return self().getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().orElseThrow();
    }

    default boolean hasUpgrade(IUpgrade upgrade) {
        final IItemHandler itemHandler = getItemHandler();
        final Iterator<Integer> iter = getUpgradeSlotsIterator();

        while (iter.hasNext()) {
            int slot = iter.next();
            if (itemHandler.getStackInSlot(slot).getItem() instanceof IUpgradeItem uItem && uItem.getUpgrade() == upgrade) {
                return true;
            }
        }
        return false;
    }

    default boolean hasUpgrade(Item item) {
        final IItemHandler itemHandler = getItemHandler();
        final Iterator<Integer> iter = getUpgradeSlotsIterator();

        while (iter.hasNext()) {
            int slot = iter.next();
            if (itemHandler.getStackInSlot(slot).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get upgrade slot iterator. Used in interface. You can use `IntStream.range(min, max).iterator()`
     *
     * @return upgrade slot iterator
     */
    default Iterator<Integer> getUpgradeSlotsIterator() {
        return IntStream.range(0, getItemHandler().getSlots()).iterator();
    }

    /**
     * Try insert upgrade item into TE
     *
     * @param player player who inserted upgrade
     * @param hand   used hand
     * @return true if inserted successfully, false if not
     */
    default boolean tryInsertUpgrade(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEmpty())
            return false;

        // we do not want to insert notebook pages by miss-click  - Mine
        //if (stack.getItem().equals(ItemRegistry.PAGE_NOTEBOOK_ITEM.get()))
        //    return false;

        IItemHandler itemHandler = getItemHandler();

        Iterator<Integer> iterator = getUpgradeSlotsIterator();
        while (iterator.hasNext()) {
            int slot = iterator.next();

            if (itemHandler.getStackInSlot(slot).isEmpty() && itemHandler.isItemValid(slot, stack)) {
                // Maybe should not take item in creative mode
                player.setItemInHand(hand, itemHandler.insertItem(slot, stack, false));
                return true;
            }
        }
        return false;
    }
}
