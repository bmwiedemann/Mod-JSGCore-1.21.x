package dev.tauri.jsg.core.client.entity.vehicle.boat;

import dev.tauri.jsg.core.common.entity.vehicle.JSGBoatTypeWrapper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public interface JSGBoatRendererProvider {
    JSGBoatRenderer<?> apply(String modId, EntityRendererProvider.Context pContext);

    record Normal(JSGBoatTypeWrapper<?> boatTypeWrapper) implements JSGBoatRendererProvider {
        @Override
        public JSGBoatRenderer<?> apply(String modId, EntityRendererProvider.Context pContext) {
            return new JSGBoatRenderer<>(modId, boatTypeWrapper, pContext, false);
        }
    }

    record Chest(JSGBoatTypeWrapper<?> boatTypeWrapper) implements JSGBoatRendererProvider {
        @Override
        public JSGBoatRenderer<?> apply(String modId, EntityRendererProvider.Context pContext) {
            return new JSGBoatRenderer<>(modId, boatTypeWrapper, pContext, true);
        }
    }
}
