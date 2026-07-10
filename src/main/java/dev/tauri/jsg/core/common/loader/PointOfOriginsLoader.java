package dev.tauri.jsg.core.common.loader;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.NativeImage;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.CoreLoadersHolder;
import dev.tauri.jsg.core.client.LoadersHolder;
import dev.tauri.jsg.core.client.loader.model.OBJLoader;
import dev.tauri.jsg.core.client.loader.texture.Texture;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.event.pointoforigin.GetOriginForDimensionEvent;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginsLoader;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.progress.ProgressMeter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public class PointOfOriginsLoader implements IPointOfOriginsLoader {
    public static final PointOfOriginsLoader INSTANCE = new PointOfOriginsLoader(CoreLoadersHolder.INSTANCE, null);

    private final LoadersHolder loaders;
    @Nullable
    private File configFolder;

    private final Map<IPointOfOriginType, Map<ResourceLocation, PointOfOrigin>> LOADED_ORIGINS = new HashMap<>();
    private final Map<IPointOfOriginType, List<ResourceLocation>> ORIGINS_TO_LOAD = new HashMap<>();

    public PointOfOriginsLoader(LoadersHolder loaders, @Nullable File configFolder) {
        this.loaders = loaders;
        this.configFolder = configFolder;
    }

    public void setConfigFolder(File configFolder) {
        this.configFolder = configFolder;
    }

    @Override
    public Optional<Map<ResourceLocation, PointOfOrigin>> getLoadedOrigins(IPointOfOriginType type) {
        return Optional.ofNullable(LOADED_ORIGINS.get(type));
    }

    @Override
    public Map<IPointOfOriginType, Map<ResourceLocation, PointOfOrigin>> getLoadedOrigins() {
        return new HashMap<>(LOADED_ORIGINS);
    }

    @Override
    public Map<IPointOfOriginType, List<ResourceLocation>> getRegisteredPointOfOrigins() {
        return new HashMap<>(ORIGINS_TO_LOAD);
    }

    @Override
    public Optional<IPointOfOriginType> getPoOType(ResourceLocation namespace) {
        return Optional.ofNullable(JSGCoreRegistries.R_POINT_OF_ORIGIN_TYPE.get().get(namespace));
    }

    @Override
    public void loadServer() {
        ORIGINS_TO_LOAD.clear();
        if (configFolder == null) return;
        var jsonFile = new File(configFolder, "jsg/PointOfOrigins.json");
        if (!jsonFile.exists()) {
            writeServer(jsonFile, Util.make(Maps.newHashMap(), (map) -> {
                for (var type : JSGCoreRegistries.R_POINT_OF_ORIGIN_TYPE.get()) {
                    map.put(type.getPoONamespaceIdentifier().toString(), type.getPoODefaults().stream().map(ResourceLocation::toString).toList());
                }
            }));
        }
        if (!jsonFile.exists()) {
            JSGCore.logger.error("Error while trying to read origins: FILE NOT FOUND");
            return;
        }
        try (var fr = new FileReader(jsonFile)) {
            Type jsonType = new TypeToken<Map<String, List<String>>>() {
            }.getType();
            Map<String, List<String>> inputMap = new HashMap<>(new GsonBuilder().create().fromJson(fr, jsonType));
            boolean changed = false;
            for (var defaultType : JSGCoreRegistries.R_POINT_OF_ORIGIN_TYPE.get()) {
                var typeId = defaultType.getPoONamespaceIdentifier();
                if (!inputMap.containsKey(typeId.toString()) || inputMap.get(typeId.toString()).isEmpty()) {
                    inputMap.put(typeId.toString(), defaultType.getPoODefaults().stream().map(ResourceLocation::toString).toList());
                    changed = true;
                }
                ORIGINS_TO_LOAD.put(defaultType, inputMap.get(typeId.toString()).stream().map(ResourceLocation::parse).toList());
            }
            if (tryLoadAddons(inputMap) || changed)
                writeServer(jsonFile, inputMap);
        } catch (Exception e) {
            JSGCore.logger.error("Error while trying to read origins:", e);
        }
    }

    protected boolean tryLoadAddons(Map<String, List<String>> jsgPoOMap) {
        if (configFolder == null) return false;
        var assetsFolder = new File(configFolder.getParentFile(), "assets/");
        if (!assetsFolder.exists() || !assetsFolder.isDirectory()) return false;
        Type jsonType = new TypeToken<Map<String, List<String>>>() {
        }.getType();
        AtomicBoolean changed = new AtomicBoolean(false);
        Arrays.stream(Objects.requireNonNull(assetsFolder.listFiles(File::isDirectory))).forEach(namespaceFolder -> {
            var loaderJson = new File(namespaceFolder, "/loader/PointOfOrigins.json");
            if (!loaderJson.exists()) return;
            try (var addonJson = new FileReader(loaderJson)) {
                Map<String, List<String>> addonInputMap = new HashMap<>(new GsonBuilder().create().fromJson(addonJson, jsonType));
                for (var defaultType : JSGCoreRegistries.R_POINT_OF_ORIGIN_TYPE.get()) {
                    var typeId = defaultType.getPoONamespaceIdentifier();
                    if (!addonInputMap.containsKey(typeId.toString()) || addonInputMap.get(typeId.toString()).isEmpty())
                        continue;

                    var jsgPoOTypeList = jsgPoOMap.getOrDefault(typeId.toString(), new ArrayList<>());
                    jsgPoOTypeList.addAll(addonInputMap.get(typeId.toString()).stream().filter(origin -> !jsgPoOTypeList.contains(origin)).toList());
                    jsgPoOMap.put(typeId.toString(), jsgPoOTypeList);
                    changed.set(true);

                    ORIGINS_TO_LOAD.put(defaultType, jsgPoOMap.get(typeId.toString()).stream().map(ResourceLocation::parse).toList());
                }
            } catch (Exception e) {
                JSGCore.logger.error("Error while trying to read origins from addon {}:", namespaceFolder.getName(), e);
            }
            if (!loaderJson.delete()) {
                JSGCore.logger.error("Failed to delete addon's PoO loader file {}", loaderJson.getAbsolutePath());
            }
        });
        return changed.get();
    }

    private void writeServer(File jsonFile, Map<String, List<String>> outputMap) {
        Type jsonType = new TypeToken<Map<String, List<String>>>() {
        }.getType();
        var outputJson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.PROTECTED).create().toJson(outputMap, jsonType);
        try (var fw = new FileWriter(jsonFile)) {
            fw.write(outputJson);
        } catch (Exception e) {
            JSGCore.logger.error("Error while trying to write origins:", e);
        }
    }

    @Override
    public int getTotalCount() {
        var count = 0;
        for (var e : ORIGINS_TO_LOAD.entrySet()) {
            count += e.getValue().size();
        }
        return count;
    }

    @Override
    @SuppressWarnings("all")
    public void loadResources(ProgressMeter progressMeter) {
        if (configFolder == null) return;
        var map = new HashMap<IPointOfOriginType, Map<ResourceLocation, PointOfOrigin>>();
        var resourceManager = Minecraft.getInstance().getResourceManager();
        for (var typeEntry : ORIGINS_TO_LOAD.entrySet()) {
            var type = typeEntry.getKey();
            var originsLocations = typeEntry.getValue();
            var typeMap = map.getOrDefault(type, new HashMap<>());
            for (var originLocation : originsLocations) {
                var olNamespace = originLocation.getNamespace();
                var olPath = originLocation.getPath();

                // load textures
                var texturesPath = "textures/point_of_origins/" + type.getPoONamespaceIdentifier().getNamespace() + "/" + type.getPoONamespaceIdentifier().getPath() + "/" + olPath + "/";
                var texturesLoc = new File(configFolder.getParentFile(), "assets/" + olNamespace + "/" + texturesPath);
                //texturesLoc.mkdirs();
                for (var texType : type.getPoOTexturesTypes()) {
                    var texLoc = new File(texturesLoc, texType);
                    var texResourceLocation = JSGMapping.rl(olNamespace, texturesPath + texType);
                    try (var inputStream = getInputStreamForResource(texLoc, texResourceLocation, resourceManager)) {
                        var image = NativeImage.read(inputStream);
                        loaders.texture().putTexture(texResourceLocation, new Texture(image, texResourceLocation).free());
                    } catch (Exception e) {
                        JSGCore.logger.error("Error while loading resource " + texResourceLocation + " at " + texLoc.getAbsolutePath(), e);
                    }
                }

                // load models
                var modelsPath = "models/point_of_origins/" + type.getPoONamespaceIdentifier().getNamespace() + "/" + type.getPoONamespaceIdentifier().getPath() + "/" + olPath + "/";
                var modelsLoc = new File(configFolder.getParentFile(), "assets/" + olNamespace + "/" + modelsPath);
                //modelsLoc.mkdirs();
                for (var modelType : type.getPoOModelsTypes()) {
                    var modelLoc = new File(modelsLoc, modelType);
                    var modelResourceLocation = JSGMapping.rl(olNamespace, modelsPath + modelType);
                    try (var inputStream = getInputStreamForResource(modelLoc, modelResourceLocation, resourceManager)) {
                        loaders.model().putModel(modelResourceLocation, OBJLoader.loadModel(inputStream));
                    } catch (Exception e) {
                        JSGCore.logger.error("Error while loading resource " + modelResourceLocation + " at " + modelLoc.getAbsolutePath(), e);
                    }
                }
                typeMap.put(originLocation, new PointOfOrigin(originLocation, type));
                progressMeter.increment();
            }
            map.put(type, typeMap);
        }
        LOADED_ORIGINS.putAll(map);
    }

    @Override
    @Nullable
    public PointOfOrigin getOriginFor(IPointOfOriginType type, ResourceKey<Level> dimension, BiomeOverlayInstance biomeOverlayInstance) {
        var event = new GetOriginForDimensionEvent(type, dimension, biomeOverlayInstance);
        event.post();
        return event.getOrigin()
                .or(() -> JSGDimensionConfig.INSTANCE.getConfigEntrySafe(dimension).map(e -> e.getOrigin(type, biomeOverlayInstance)))
                .orElse(null);
    }

    private InputStream getInputStreamForResource(File assetsLocation, ResourceLocation localLocation, ResourceManager resourceManager) throws IOException {
        var resourcePackResource = resourceManager.getResource(localLocation);
        if (resourcePackResource.isPresent())
            return resourcePackResource.get().open();
        if (assetsLocation.exists())
            return new FileInputStream(assetsLocation);
        throw new IOException("localResource is empty");
    }
}
