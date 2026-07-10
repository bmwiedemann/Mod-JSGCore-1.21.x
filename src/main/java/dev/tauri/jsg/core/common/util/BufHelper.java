package dev.tauri.jsg.core.common.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/**
 * 1.20.1-style ItemStack buffer io. FriendlyByteBuf lost read/writeItem in 1.20.5;
 * packet buffers are RegistryFriendlyByteBuf at runtime, which the stream codec needs.
 */
public class BufHelper {
    public static void writeItemStack(FriendlyByteBuf buf, ItemStack stack) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, stack);
    }

    public static ItemStack readItemStack(FriendlyByteBuf buf) {
        return ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
    }
}
