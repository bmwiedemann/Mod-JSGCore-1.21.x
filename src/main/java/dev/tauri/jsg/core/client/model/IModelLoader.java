package dev.tauri.jsg.core.client.model;

import net.minecraft.resources.ResourceLocation;

public interface IModelLoader {
    void loadModels();

    dev.tauri.jsg.core.client.model.AbstractOBJModel getModel(ResourceLocation resourceLocation);

    boolean isModelLoaded(ResourceLocation resourceLocation);

    ResourceLocation getModelResource(String model);

    void putModel(ResourceLocation resourceLocation, AbstractOBJModel model);
}
