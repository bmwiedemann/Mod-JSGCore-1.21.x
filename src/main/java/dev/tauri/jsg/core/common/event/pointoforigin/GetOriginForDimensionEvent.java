package dev.tauri.jsg.core.common.event.pointoforigin;

import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.event.JSGEvent;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class GetOriginForDimensionEvent extends JSGEvent {
    public final IPointOfOriginType type;
    public final ResourceKey<Level> dimension;
    public final BiomeOverlayInstance biomeOverlayInstance;

    protected Supplier<Optional<PointOfOrigin>> pointOfOriginSupplier = Optional::empty;

    public GetOriginForDimensionEvent(IPointOfOriginType type, ResourceKey<Level> dimension, BiomeOverlayInstance biomeOverlayInstance) {
        this.type = type;
        this.dimension = dimension;
        this.biomeOverlayInstance = biomeOverlayInstance;
    }

    public Optional<PointOfOrigin> getOrigin() {
        return pointOfOriginSupplier.get();
    }

    public void supply(@Nullable PointOfOrigin pointOfOriginSupplier) {
        this.pointOfOriginSupplier = () -> Optional.ofNullable(pointOfOriginSupplier);
    }

    public void supplyEmpty() {
        this.pointOfOriginSupplier = Optional::empty;
    }
}
