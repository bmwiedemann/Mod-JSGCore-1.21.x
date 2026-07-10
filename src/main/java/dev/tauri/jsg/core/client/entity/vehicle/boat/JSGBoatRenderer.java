package dev.tauri.jsg.core.client.entity.vehicle.boat;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import dev.tauri.jsg.core.common.entity.vehicle.JSGBoat;
import dev.tauri.jsg.core.common.entity.vehicle.JSGBoatTypeWrapper;
import dev.tauri.jsg.core.common.entity.vehicle.JSGChestBoat;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

public class JSGBoatRenderer<T extends Enum<T> & JSGBoatTypeWrapper.Type> extends BoatRenderer {
    private final Map<T, Pair<ResourceLocation, ListModel<Boat>>> boatResources;
    public final JSGBoatTypeWrapper<T> boatTypeWrapper;

    public JSGBoatRenderer(String modId, JSGBoatTypeWrapper<T> boatTypeWrapper, EntityRendererProvider.Context pContext, boolean pChestBoat) {
        super(pContext, pChestBoat);
        this.boatTypeWrapper = boatTypeWrapper;
        this.boatResources = Stream.of(boatTypeWrapper.values).collect(ImmutableMap.toImmutableMap(type -> type,
                type -> Pair.of(JSGMapping.rl(modId, getTextureLocation(type, pChestBoat)), createBoatModel(modId, pContext, type, pChestBoat))));
    }

    private String getTextureLocation(T pType, boolean pChestBoat) {
        return pChestBoat ? "textures/entity/chest_boat/" + pType.getName() + ".png" : "textures/entity/boat/" + pType.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(String modId, EntityRendererProvider.Context pContext, T pType, boolean pChestBoat) {
        ModelLayerLocation modellayerlocation = pChestBoat ? ModelLayers.createChestBoatModelName(Boat.Type.OAK) : ModelLayers.createBoatModelName(Boat.Type.OAK);
        var modelpart = pContext.bakeLayer(modellayerlocation);
        return pChestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public ModelLayerLocation createBoatModelName(String modId, T pType) {
        return createLocation(modId, "boat/" + pType.getName());
    }

    public ModelLayerLocation createChestBoatModelName(String modId, T pType) {
        return createLocation(modId, "chest_boat/" + pType.getName());
    }

    private static ModelLayerLocation createLocation(String modId, String pPath) {
        return new ModelLayerLocation(JSGMapping.rl(modId, pPath), "main");
    }

    @SuppressWarnings("all")
    public @NotNull Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(@NotNull Boat boat) {
        if (boat instanceof JSGBoat<?> modBoat) {
            return this.boatResources.get(modBoat.getCustomVariant());
        } else if (boat instanceof JSGChestBoat<?> modChestBoatEntity) {
            return this.boatResources.get(modChestBoatEntity.getCustomVariant());
        } else {
            return null;
        }
    }
}
