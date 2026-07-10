package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;

import java.util.List;
import java.util.function.Supplier;

public interface IBiomeOverlayProvider {
    List<Supplier<BiomeOverlayInstance>> getSupportedOverlays();
}
