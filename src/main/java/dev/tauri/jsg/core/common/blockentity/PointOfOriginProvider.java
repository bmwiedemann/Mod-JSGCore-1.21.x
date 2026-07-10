package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import org.jetbrains.annotations.Nullable;

public interface PointOfOriginProvider {
    @Nullable
    PointOfOrigin getPointOfOrigin();
}
