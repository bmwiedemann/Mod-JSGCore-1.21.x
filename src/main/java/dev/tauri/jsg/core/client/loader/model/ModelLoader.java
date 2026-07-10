package dev.tauri.jsg.core.client.loader.model;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.model.IModelLoader;
import dev.tauri.jsg.core.common.loader.FolderLoader;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import net.neoforged.fml.loading.progress.ProgressMeter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ModelLoader implements IModelLoader {

    private final String modId;
    private final Class<?> modMainClass;
    public final String modelsPath;
    private final Map<ResourceLocation, OBJModel> LOADED_MODELS = new HashMap<>();

    public static final OBJModel EMPTY_MODEL = new OBJModel(new float[0], new float[0], new float[0], new int[0]);

    public ModelLoader(String modId, Class<?> modMainClass) {
        this.modId = modId;
        this.modMainClass = modMainClass;
        this.modelsPath = "assets/" + modId + "/models/tesr";
        JSGCore.logger.info("Created ModelLoader for domain {}", modId);
    }

    @NotNull
    public OBJModel getModel(ResourceLocation resourceLocation) {
        if (resourceLocation == null) return EMPTY_MODEL;
        var model = LOADED_MODELS.get(resourceLocation);
        if (model == null) return EMPTY_MODEL;
        return model;
    }

    @Override
    public boolean isModelLoaded(ResourceLocation resourceLocation) {
        return LOADED_MODELS.containsKey(resourceLocation);
    }

    public void loadModels() {
        AtomicReference<String> modName = new AtomicReference<>("");
        ModList.get().getModContainerById(modId).ifPresentOrElse(container -> modName.set(container.getModInfo().getDisplayName()), () -> {
        });
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try {
            LOADED_MODELS.clear();

            List<String> modelPaths = FolderLoader.getAllFiles(modMainClass, modId, modelsPath, ".obj");

            long start = System.currentTimeMillis();

            ProgressMeter progress = StartupNotificationManager.addProgressBar(modName.get() + " - Loading Models", modelPaths.size());

            JSGCore.logger.info("Started loading models for domain {}...", modId);
            for (String modelPath : modelPaths) {
                try {
                    JSGCore.logger.debug("Loading model: {} for domain {}", modelPath, modId);
                    String modelResourcePath = modelPath.replaceFirst("assets/" + modId + "/", "");
                    var location = JSGMapping.rl(modId, modelResourcePath);

                    var resource = resourceManager.getResource(location).orElseThrow();
                    var stream = resource.open();
                    LOADED_MODELS.put(location, OBJLoader.loadModel(stream));
                    stream.close();

                    JSGCore.logger.debug("Model {} for domain {} loaded!", modelPath, modId);
                    progress.increment();
                    StartupNotificationManager.addModMessage("Loaded " + modId + ":" + modelResourcePath.replaceFirst("textures/tesr/", ""));
                } catch (Exception e) {
                    JSGCore.logger.error("Failed to load model {}", modelPath, e);
                }
            }

            progress.complete();

            JSGCore.logger.info("Loaded {} models for domain {} in {} ms", modelPaths.size(), modId, System.currentTimeMillis() - start);
        } catch (Exception e) {
            JSGCore.logger.error("Error while loading models for mod {}", modMainClass.toString(), e);
        }
    }

    public ResourceLocation getModelResource(String model) {
        return JSGMapping.rl(modId, "models/tesr/" + model);
    }

    @Override
    public void putModel(ResourceLocation resourceLocation, AbstractOBJModel model) {
        LOADED_MODELS.put(resourceLocation, (OBJModel) model);
    }
}
