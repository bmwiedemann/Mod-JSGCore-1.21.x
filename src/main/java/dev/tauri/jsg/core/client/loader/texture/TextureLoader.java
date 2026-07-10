package dev.tauri.jsg.core.client.loader.texture;

import com.mojang.blaze3d.platform.NativeImage;
import dev.matrixlab.webp4j.WebPCodec;
import dev.matrixlab.webp4j.internal.PixelConverter;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.loader.FolderLoader;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.util.JSGColorUtil;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TextureLoader implements ITextureLoader {

    private final String modId;
    private final Class<?> modMainClass;
    public final String texturesPath;

    // TODO(Mine): Merge with ALL_MODS_LOADED_TEXTURES
    private final Map<ResourceLocation, Texture> LOADED_TEXTURES = new HashMap<>();

    private static final Map<String, Map<ResourceLocation, Texture>> ALL_MODS_LOADED_TEXTURES = new HashMap<>();

    private static final Map<BiomeOverlayInstance, NativeImage> LOADED_OVERLAY_TEXTURES = new HashMap<>();

    public static final String[] TEX_SUFFIXES = {".png", ".webp", ".jpg"};

    public TextureLoader(String modId, Class<?> modMainClass) {
        this.modId = modId;
        this.modMainClass = modMainClass;
        this.texturesPath = "assets/" + modId + "/textures/tesr";
        JSGCore.logger.info("Created TextureLoader for domain {}", modId);
    }

    @NotNull
    public static Map<ResourceLocation, Texture> getLoadedTextures(String modId) {
        return Optional.ofNullable(ALL_MODS_LOADED_TEXTURES.get(modId)).orElse(Collections.emptyMap());
    }

    @NotNull
    public static Texture getTexture(String modId, ResourceLocation resourceLocation) {
        return Optional.ofNullable(ALL_MODS_LOADED_TEXTURES.get(modId)).map(map -> {
            var pathWithoutExt = resourceLocation.getPath().split("\\.")[0];
            for (var ext : TEX_SUFFIXES) {
                var path = pathWithoutExt + ext;
                var rl = JSGMapping.rl(resourceLocation.getNamespace(), path);
                var tex = map.get(rl);
                if (tex != null)
                    return tex;
            }
            return Texture.getEmptyTexture();
        }).orElseGet(Texture::getEmptyTexture);
    }

    @NotNull
    public static Texture getTextureTryAllMods(ResourceLocation resourceLocation) {
        return ALL_MODS_LOADED_TEXTURES.values().stream().map(map -> {
            var pathWithoutExt = resourceLocation.getPath().split("\\.")[0];
            for (var ext : TEX_SUFFIXES) {
                var path = pathWithoutExt + ext;
                var rl = JSGMapping.rl(resourceLocation.getNamespace(), path);
                var tex = map.get(rl);
                if (tex != null)
                    return tex;
            }
            return Texture.getEmptyTexture();
        }).findFirst().orElseGet(Texture::getEmptyTexture);
    }

    @Override
    public Texture getTexture(ResourceLocation resourceLocation) {
        var pathWithoutExt = resourceLocation.getPath().split("\\.")[0];
        for (var ext : TEX_SUFFIXES) {
            var path = pathWithoutExt + ext;
            var rl = JSGMapping.rl(resourceLocation.getNamespace(), path);
            var tex = LOADED_TEXTURES.get(rl);
            if (tex != null)
                return tex;
        }
        return Texture.getEmptyTexture();
    }

    @Override
    public boolean isTextureLoaded(ResourceLocation resourceLocation) {
        var pathWithoutExt = resourceLocation.getPath().split("\\.")[0];
        for (var ext : TEX_SUFFIXES) {
            var path = pathWithoutExt + ext;
            var rl = JSGMapping.rl(resourceLocation.getNamespace(), path);
            if (LOADED_TEXTURES.containsKey(rl))
                return true;
        }
        return false;
    }

    @Override
    public void loadTextures() {
        AtomicReference<String> modName = new AtomicReference<>("");
        ModList.get().getModContainerById(modId).ifPresentOrElse(container -> modName.set(container.getModInfo().getDisplayName()), () -> {
        });
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try {
            for (Texture texture : LOADED_TEXTURES.values())
                texture.deleteTexture();
            LOADED_TEXTURES.clear();
            Optional.ofNullable(ALL_MODS_LOADED_TEXTURES.get(modId)).ifPresent(Map::clear);

            List<String> texturePaths = new ArrayList<>();
            List<String> ehPaths = new ArrayList<>();
            for (String texturePath : FolderLoader.getAllFiles(modMainClass, modId, texturesPath, TEX_SUFFIXES)) {
                texturePath = texturePath.replaceFirst("assets/" + modId + "/", "");
                if (texturePath.contains("animated"))
                    ehPaths.add(texturePath);
                else
                    texturePaths.add(texturePath);
            }
            // ----------------------------------

            // ----------------------------------
            // LOAD NORMAL TEXTURES
            long start = System.currentTimeMillis();
            ProgressMeter progress = StartupMessageManager.addProgressBar(modName.get() + " - Loading Textures", texturePaths.size());
            JSGCore.logger.info("Started loading textures for domain {}...", modId);
            for (String texturePath : texturePaths) {
                loadTexture(progress, texturePath, resourceManager);
            }
            progress.complete();
            JSGCore.logger.info("Loaded {} textures for domain {} in {} ms", texturePaths.size(), modId, System.currentTimeMillis() - start);
            // ----------------------------------

            // ----------------------------------
            // LOAD EVENT HORIZONS
            start = System.currentTimeMillis();
            progress = StartupMessageManager.addProgressBar(modName.get() + " - Animated Textures", ehPaths.size());
            JSGCore.logger.info("Started loading animated textures for domain {}...", modId);
            for (String texturePath : ehPaths) {
                loadTexture(progress, texturePath, resourceManager);
            }
            progress.complete();
            JSGCore.logger.info("Loaded {} animated textures for domain {} in {} ms", ehPaths.size(), modId, System.currentTimeMillis() - start);


        } catch (Exception e) {
            JSGCore.logger.error("Failed to load texture ", e);
        }
    }

    protected void loadTexture(ProgressMeter progress, String texturePath, ResourceManager resourceManager) {
        texturePath = texturePath.replaceFirst("assets/" + modId + "/", "").split("\\.")[0];
        for (var overlay : JSGCoreRegistries.R_BIOME_OVERLAY.get().getValues()) {
            if (overlay.suffix().isEmpty()) continue;
            if (texturePath.endsWith(overlay.suffix()))
                return;
        }
        Map<String, Exception> exceptionsBuffer = new HashMap<>();

        for (var ext : TEX_SUFFIXES) {
            var texturePathFull = texturePath + ext;
            ResourceLocation resourceLocation = JSGMapping.rl(modId, texturePathFull);
            try {
                JSGCore.logger.debug("Loading texture: {} for domain {}", texturePathFull, modId);
                Resource resource = resourceManager.getResource(resourceLocation).orElseThrow();
                InputStream stream = resource.open();
                var image = readTexture(stream, resourceLocation);
                var texture = new Texture(image, resourceLocation);
                putTexture(resourceLocation, texture);
                JSGCore.logger.debug("Texture {} with no overlay for domain {} loaded", texturePathFull, modId);
                if (texturePathFull.contains("template")) {
                    saveTransparentEH(image, texturePathFull, resourceManager);
                } else {
                    // generate overlays
                    generateOverlayTextures(texturePathFull, image, resourceManager, resourceLocation);
                }
                texture.free();
                JSGCore.logger.debug("Texture {} for domain {} loaded!", texturePathFull, modId);
                progress.increment();
                StartupNotificationManager.addModMessage("Loaded " + modId + ":" + texturePathFull.replaceFirst("textures/tesr/", ""));
                return;
            } catch (Exception e) {
                exceptionsBuffer.put(texturePathFull, e);
            }
        }
        JSGCore.logger.error("Failed to load texture {} (png, webp or jpg was not found)", texturePath);
        JSGCore.logger.error("Tried: (and failed because...)");
        for (var e : exceptionsBuffer.entrySet()) {
            JSGCore.logger.error("    {}: ", e.getKey(), e.getValue());
        }
    }

    protected void generateOverlayTextures(String texturePathFull, NativeImage originalImage, ResourceManager resourceManager, ResourceLocation originalTextureLocation) {
        for (var overlay : JSGCoreRegistries.R_BIOME_OVERLAY.get().getValues()) {
            if (overlay.suffix().isEmpty()) continue;
            if (texturePathFull.split("\\.")[0].endsWith(overlay.suffix()))
                return;
        }
        for (var overlay : JSGCoreRegistries.R_BIOME_OVERLAY.get().getValues()) {
            if (overlay.suffix().isEmpty()) continue;
            var overlayLocation = overlay.getOverlayTexture();
            if (overlayLocation == null) continue;
            if (LOADED_OVERLAY_TEXTURES.containsKey(overlay)) {
                generateOverlayTexture(overlay, texturePathFull, originalImage, LOADED_OVERLAY_TEXTURES.get(overlay), resourceManager, originalTextureLocation);
                continue;
            }

            for (var ext : TEX_SUFFIXES) {
                var overlayLocationFull = JSGMapping.rl(overlayLocation.getNamespace(), overlayLocation.getPath() + ext);
                var resourceOpt = resourceManager.getResource(overlayLocationFull);
                if (resourceOpt.isEmpty()) continue;
                var resource = resourceOpt.get();
                try (var overlayTexStream = resource.open()) {
                    var overlayImage = readTexture(overlayTexStream, overlayLocationFull);
                    LOADED_OVERLAY_TEXTURES.put(overlay, overlayImage);
                    generateOverlayTexture(overlay, texturePathFull, originalImage, overlayImage, resourceManager, originalTextureLocation);
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    protected void generateOverlayTexture(BiomeOverlayInstance overlay, String texturePathFull, NativeImage originalImage, NativeImage overlayImage, ResourceManager resourceManager, ResourceLocation originalTextureLocation) {
        if (overlayImage == null) {
            JSGCore.logger.error("overlayImage == null, cannot generate overlay-ed textures for {} ({})!", texturePathFull, modId);
            return;
        }
        var originalTexLocationPathSplit = originalTextureLocation.getPath().split("\\.");
        var resourceLocation = JSGMapping.rl(originalTextureLocation.getNamespace(), originalTexLocationPathSplit[0] + overlay.suffix() + (originalTexLocationPathSplit.length > 1 ? ("." + originalTexLocationPathSplit[1]) : ""));

        // Do not generate overlay texture if there is one in RP
        if (isTextureLoaded(resourceLocation)) return;
        for (var ext : TEX_SUFFIXES) {
            var alreadyInRPRL = JSGMapping.rl(originalTextureLocation.getNamespace(), originalTexLocationPathSplit[0] + overlay.suffix() + ext);
            var resourceOpt = resourceManager.getResource(alreadyInRPRL);
            if (resourceOpt.isEmpty()) continue;
            var resource = resourceOpt.get();
            try (var overlayTexStream = resource.open()) {
                putTexture(alreadyInRPRL, new Texture(readTexture(overlayTexStream, alreadyInRPRL), alreadyInRPRL).free());
                return;
            } catch (Exception ignored) {
            }
        }

        try {
            JSGCore.logger.debug("Loading overlay-ed texture: {} with overlay {} for domain {}", texturePathFull, overlay.getId().toString(), modId);
            var textureWithOverlay = new NativeImage(originalImage.getWidth(), originalImage.getHeight(), false);

            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {
                    int originalPixelARGB = originalImage.getPixelRGBA(x, y);
                    if ((originalPixelARGB >> 24) == 0) {
                        textureWithOverlay.setPixelRGBA(x, y, 0);
                        continue;
                    }
                    int overlayPixelARGB = overlayImage.getPixelRGBA(x % overlayImage.getWidth(), y % overlayImage.getHeight());
                    int texWithOverlayPixelARGB = JSGColorUtil.blendColors(originalPixelARGB, overlayPixelARGB);
                    textureWithOverlay.setPixelRGBA(x, y, texWithOverlayPixelARGB);
                }
            }

            putTexture(resourceLocation, new Texture(textureWithOverlay, resourceLocation).free());
        } catch (Exception e) {
            JSGCore.logger.error("Error while generating overlay texture for {} ({}) for domain {}", texturePathFull, overlay.getId().toString(), modId, e);
        }
    }

    @Override
    public ResourceLocation getTextureResource(String texture) {
        return JSGMapping.rl(modId, "textures/tesr/" + texture);
    }

    @Override
    public void putTexture(ResourceLocation resourceLocation, ITexture texture) {
        LOADED_TEXTURES.put(resourceLocation, (Texture) texture);
        Util.make(ALL_MODS_LOADED_TEXTURES.computeIfAbsent(modId, (modId) -> new HashMap<>()), map -> {
            map.put(resourceLocation, (Texture) texture);
            ALL_MODS_LOADED_TEXTURES.put(modId, map);
        });
    }

    private void saveTransparentEH(NativeImage image, String name, ResourceManager resourceManager) throws IOException {
        name = name.replaceFirst("template", "overlay").split("\\.")[0] + ".webp";
        var file = new File(JSGCore.modConfigDir.getParentFile(), "/assets/" + modId + "/textures/generated/" + name.replaceFirst("textures/", ""));
        var resourceLocation = JSGMapping.rl(modId, name);

        var resourceOpt = resourceManager.getResource(resourceLocation);
        if (resourceOpt.isPresent()) {
            var start = System.currentTimeMillis();
            JSGCore.logger.info("Loading texture overlay from Resource Pack... ({})", name);
            putTexture(resourceLocation, new Texture(readTexture(resourceOpt.get().open(), resourceLocation), resourceLocation));
            JSGCore.logger.info("Texture from RP ({}) loaded in {} ms", name, System.currentTimeMillis() - start);
            StartupNotificationManager.addModMessage("Loaded " + name);
            return;
        }

        if (!file.exists()) {
            var start = System.currentTimeMillis();
            JSGCore.logger.info("Creating texture overlay... ({})", name);
            var transparentImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (var x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    var pixelRGBA = new Color(image.getPixelRGBA(x, y), false);
                    int rgba = 0x00ffffff | (pixelRGBA.getBlue() << 24);
                    transparentImg.setRGB(x, y, rgba);
                }
            }
            var ignored = file.getParentFile().mkdirs();

            var bytes = WebPCodec.encodeLosslessImage(transparentImg);
            try (var outputStream = new FileOutputStream(file)) {
                outputStream.write(bytes);
            }
            transparentImg.flush();
            JSGCore.logger.info("Texture overlay ({}) created in {} ms", name, System.currentTimeMillis() - start);
        }
        var start = System.currentTimeMillis();
        JSGCore.logger.info("Loading previously generated texture... ({})", name);
        var stream = file.toURI().toURL().openStream();
        var texture = new Texture(readTexture(stream, resourceLocation), resourceLocation);
        putTexture(resourceLocation, texture);
        texture.free();
        JSGCore.logger.info("Texture ({}) loaded in {} ms", name, System.currentTimeMillis() - start);
        StartupNotificationManager.addModMessage("Loaded generated " + name);
    }

    protected static NativeImage readTexture(InputStream inputStream, ResourceLocation location) throws IOException {
        var name = location.getPath();
        if (!name.endsWith(".webp")) {
            return NativeImage.read(inputStream);
        }
        // else - load WebP
        if (!WebPCodec.isAvailable()) {
            JSGCore.logger.error("Failed to load WebP texture from {}! WebP is not supported!", location);
            throw new IOException("WebP is not supported!");
        }

        var image = WebPCodec.decodeImage(inputStream.readAllBytes());

        int width = image.getWidth();
        int height = image.getHeight();

        NativeImage nativeImage = new NativeImage(width, height, true);

        byte[] pixels = PixelConverter.toBytes(image);

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int b = pixels[index++] & 0xFF;
                int g = pixels[index++] & 0xFF;
                int r = pixels[index++] & 0xFF;
                int a = (image.getColorModel().hasAlpha() ? (pixels[index++] & 0xFF) : 0xff);

                int argb = (a << 24) | (r << 16) | (g << 8) | b;

                nativeImage.setPixelRGBA(x, y, argb);
            }
        }
        return nativeImage;
    }
}
