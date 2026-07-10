package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import net.neoforged.neoforge.registries.RegistryObject;

public class CoreBiomeOverlays {
    public static final RegistryObject<BiomeOverlayInstance> NORMAL = JSGCore.REGISTRY_HELPER.biomeOverlay().register("normal", () -> new BiomeOverlayInstance("normal", "", 11184810));
    public static final RegistryObject<BiomeOverlayInstance> FROST = JSGCore.REGISTRY_HELPER.biomeOverlay().register("frost", () -> new BiomeOverlayInstance("frost", "frost", 5636095));
    public static final RegistryObject<BiomeOverlayInstance> SOOTY = JSGCore.REGISTRY_HELPER.biomeOverlay().register("sooty", () -> new BiomeOverlayInstance("sooty", "sooty", 5592405));
    public static final RegistryObject<BiomeOverlayInstance> AGED = JSGCore.REGISTRY_HELPER.biomeOverlay().register("aged", () -> new BiomeOverlayInstance("aged", "aged", 11184810));
    public static final RegistryObject<BiomeOverlayInstance> MOSSY = JSGCore.REGISTRY_HELPER.biomeOverlay().register("mossy", () -> new BiomeOverlayInstance("mossy", "mossy", 43520));

    public static void init() {
    }
}
