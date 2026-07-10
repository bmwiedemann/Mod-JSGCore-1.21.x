package dev.tauri.jsg.core.common.symbol.pointoforigin;

import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public interface IPointOfOriginsLoader {
    Optional<Map<ResourceLocation, PointOfOrigin>> getLoadedOrigins(IPointOfOriginType type);

    Map<IPointOfOriginType, Map<ResourceLocation, PointOfOrigin>> getLoadedOrigins();

    Map<IPointOfOriginType, List<ResourceLocation>> getRegisteredPointOfOrigins();

    @Nullable
    default PointOfOrigin getOriginByIdOrElse(IPointOfOriginType type, ResourceLocation id, @Nullable Supplier<PointOfOrigin> fallback) {
        var mapOpt = getLoadedOrigins(type);
        if (mapOpt.isEmpty()) {
            if (fallback == null) return null;
            return fallback.get();
        }
        var map = mapOpt.get();
        var origin = map.get(id);
        if (origin == null) {
            if (fallback == null) return null;
            return fallback.get();
        }
        return origin;
    }

    Optional<IPointOfOriginType> getPoOType(ResourceLocation namespace);

    void loadServer();

    int getTotalCount();

    void loadResources(ProgressMeter progressMeter);

    @Nullable
    PointOfOrigin getOriginFor(IPointOfOriginType type, ResourceKey<Level> dimension, BiomeOverlayInstance biomeOverlayInstance);
}
