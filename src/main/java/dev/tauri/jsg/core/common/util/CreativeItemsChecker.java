package dev.tauri.jsg.core.common.util;

import dev.tauri.jsg.core.common.item.ICreativeThing;
import net.minecraft.world.item.ItemStack;

public class CreativeItemsChecker {
    public static boolean canInteractWith(ItemStack stack, boolean isCreative) {
        if (isCreative) return true;
        if (stack == null) return true;
        if (stack.getItem() instanceof ICreativeThing c)
            return !c.isCreativeOnly();
        return true;
    }
}
