package dev.tauri.jsg.core.common.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * ItemStack NBT access in 1.20.1 style, backed by the {@code minecraft:custom_data}
 * component. Vanilla's DFU moves pre-1.20.5 item tags into that component, so data
 * written by the Forge versions of JSG stays readable.
 * <p>
 * Unlike {@code ItemStack.getOrCreateTag()}, returned tags are copies — mutations must
 * be written back via {@link #setTag} or done through {@link #update}.
 */
public class ItemNBT {
    public static boolean hasTag(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    @Nullable
    public static CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> mutator) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, mutator);
    }

    /**
     * 1.20.1-style ItemStack.of(tag) / stack.save(tag), using the running server's (or
     * client's) registry access for the component codecs.
     */
    public static ItemStack stackOf(@Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return ItemStack.EMPTY;
        var access = currentRegistries();
        return access == null ? ItemStack.EMPTY : ItemStack.parseOptional(access, tag);
    }

    public static CompoundTag saveStack(ItemStack stack, CompoundTag tag) {
        if (stack.isEmpty()) return tag;
        var access = currentRegistries();
        if (access == null) return tag;
        var saved = stack.save(access, tag);
        return saved instanceof CompoundTag compound ? compound : tag;
    }

    public static CompoundTag saveStack(ItemStack stack) {
        return saveStack(stack, new CompoundTag());
    }

    @Nullable
    private static net.minecraft.core.HolderLookup.Provider currentRegistries() {
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) return server.registryAccess();
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) return ClientRegistryAccess.get();
        return null;
    }

    private static class ClientRegistryAccess {
        @Nullable
        static net.minecraft.core.HolderLookup.Provider get() {
            var mc = net.minecraft.client.Minecraft.getInstance();
            return mc.level != null ? mc.level.registryAccess()
                    : mc.getConnection() != null ? mc.getConnection().registryAccess() : null;
        }
    }
}
