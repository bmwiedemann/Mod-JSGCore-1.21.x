package dev.tauri.jsg.core.client;

import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CoreModelsHolder implements IModelsHolder {
    CARTOUCHE_PLATE_6("props/cartouche/6_chevron_plate.obj", "", false),
    CARTOUCHE_PLATE_7("props/cartouche/7_chevron_plate.obj", "", false),
    CARTOUCHE_PLATE_8("props/cartouche/8_chevron_plate.obj", "", false),
    CARTOUCHE_PLATE_7_POO("props/cartouche/7_chevron_plate_poo.obj", "", false),
    CARTOUCHE_PLATE_8_POO("props/cartouche/8_chevron_plate_poo.obj", "", false),
    CARTOUCHE_PLATE_9_POO("props/cartouche/9_chevron_plate_poo.obj", "", false),

    ;

    public final ResourceLocation model;
    public final Map<BiomeOverlayInstance, ResourceLocation> biomeTextureResourceMap = new HashMap<>();
    private final List<BiomeOverlayInstance> nonExistingReported = new ArrayList<>();

    CoreModelsHolder(String modelPath, String texturePath, boolean byOverlay) {
        this.model = getLoadersHolder().model().getModelResource(modelPath);
        loadEntry(texturePath, byOverlay);
    }

    @Override
    public @NotNull LoadersHolder getLoadersHolder() {
        return dev.tauri.jsg.core.client.CoreLoadersHolder.INSTANCE;
    }

    @Override
    public @NotNull ResourceLocation getModelLocation() {
        return model;
    }

    @Override
    public @NotNull Map<BiomeOverlayInstance, ResourceLocation> getBiomeTextureResourceMap() {
        return biomeTextureResourceMap;
    }

    @Override
    public @NotNull List<BiomeOverlayInstance> getNonExistingTexturesReported() {
        return nonExistingReported;
    }
}
