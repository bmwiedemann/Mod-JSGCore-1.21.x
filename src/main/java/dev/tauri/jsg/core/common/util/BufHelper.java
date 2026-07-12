package dev.tauri.jsg.core.common.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 1.20.1-style ItemStack buffer io. FriendlyByteBuf lost read/writeItem in 1.20.5;
 * the stream codec needs a RegistryFriendlyByteBuf. State classes wrap packet buffers
 * in plain FriendlyByteBufs, so upgrade with the running side's registry access when
 * the buffer isn't registry-aware already.
 */
public class BufHelper {
    public static void writeItemStack(FriendlyByteBuf buf, ItemStack stack) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(registryBuf(buf), stack);
    }

    public static ItemStack readItemStack(FriendlyByteBuf buf) {
        return ItemStack.OPTIONAL_STREAM_CODEC.decode(registryBuf(buf));
    }

    private static RegistryFriendlyByteBuf registryBuf(FriendlyByteBuf buf) {
        if (buf instanceof RegistryFriendlyByteBuf registryBuf) return registryBuf;
        RegistryAccess access = currentRegistryAccess();
        if (access == null)
            throw new IllegalStateException("No registry access available for ItemStack buffer io");
        return new RegistryFriendlyByteBuf(buf, access);
    }

    @Nullable
    private static RegistryAccess currentRegistryAccess() {
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) return server.registryAccess();
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) return ClientRegistryAccess.get();
        return null;
    }

    private static class ClientRegistryAccess {
        @Nullable
        static RegistryAccess get() {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null) return mc.level.registryAccess();
            return mc.getConnection() != null ? mc.getConnection().registryAccess() : null;
        }
    }
}
