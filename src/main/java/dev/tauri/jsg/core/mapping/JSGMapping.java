package dev.tauri.jsg.core.mapping;

import net.minecraft.resources.ResourceLocation;

public class JSGMapping {
    public static ResourceLocation rl(String modId, String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.parse(path);
    }

    public static ResourceLocation fixRL(String id) {
        if (!id.contains(":")) id = "jsg:" + id;
        return ResourceLocation.parse(id);
    }
}
