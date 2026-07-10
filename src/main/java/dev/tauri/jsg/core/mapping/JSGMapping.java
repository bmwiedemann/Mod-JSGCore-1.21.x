package dev.tauri.jsg.core.mapping;

import net.minecraft.resources.ResourceLocation;

public class JSGMapping {
    public static ResourceLocation rl(String modId, String path) {
        return new ResourceLocation(modId, path);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation fixRL(String id) {
        if (!id.contains(":")) id = "jsg:" + id;
        return new ResourceLocation(id);
    }
}
