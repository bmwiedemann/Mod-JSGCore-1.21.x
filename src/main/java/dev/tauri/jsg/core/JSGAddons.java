package dev.tauri.jsg.core;

import net.minecraft.Util;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JSGAddons {
    private static final Map<String, JSGAddon> ADDONS = new HashMap<>();
    private static final Map<JSGAddon, Map<AddonInfo, String>> MODS_INFO_CACHE = new HashMap<>();

    public enum AddonInfo {
        NAME,
        ID,
        VERSION,
        AUTHORS,
        DESCRIPTION,
        DOCS_URL
    }

    public static Map<AddonInfo, String> getInfo(JSGAddon addon) {
        if (MODS_INFO_CACHE.containsKey(addon))
            return MODS_INFO_CACHE.get(addon);
        return Util.make(new HashMap<>(), map -> {
            map.put(AddonInfo.ID, addon.getId());
            ModList.get().getModContainerById(addon.getId()).ifPresent(container -> {
                map.put(AddonInfo.NAME, container.getModInfo().getDisplayName());
                map.put(AddonInfo.VERSION, container.getModInfo().getVersion().getQualifier());
                map.put(AddonInfo.AUTHORS, container.getModInfo().getConfig().getConfigElement("authors").orElse("").toString());
                map.put(AddonInfo.DESCRIPTION, container.getModInfo().getDescription());
                map.put(AddonInfo.DOCS_URL, container.getModInfo().getConfig().getConfigElement("displayURL").orElse("").toString());
            });
            MODS_INFO_CACHE.put(addon, map);
        });
    }

    public static Optional<JSGAddon> getAddon(String id) {
        return Optional.ofNullable(ADDONS.get(id));
    }

    public static void registerAddon(JSGAddon addon) {
        ADDONS.put(addon.getId(), addon);
        var loggerWrapper = addon.getLoggerWrapper();
        loggerWrapper.ifPresent(logger -> {
            var info = getInfo(addon);
            Util.make(new ArrayList<String>(), list -> {
                list.add("=======================================");
                var logo = addon.getWelcomeLogo();
                for (var line : logo) {
                    list.add("| " + line);
                }
                if (logo.length == 0) {
                    Optional.ofNullable(info.get(AddonInfo.NAME)).ifPresent(a -> {
                        list.add(" || " + a + " || ");
                        list.add("");
                    });
                }
                list.add("");
                Optional.ofNullable(info.get(AddonInfo.DESCRIPTION)).ifPresent(a -> {
                    list.add(" " + a);
                    list.add("");
                });
                Optional.ofNullable(info.get(AddonInfo.AUTHORS)).ifPresent(a -> list.add(" Authors: " + a));
                Optional.ofNullable(info.get(AddonInfo.DOCS_URL)).ifPresent(a -> list.add(" Docs: " + a));
                Optional.ofNullable(info.get(AddonInfo.VERSION)).ifPresent(a -> list.add(" Version: " + a));
                list.add("=======================================");
            }).forEach(logger::info);
        });
    }

    public static void onCoreCommonSetup() {
        ADDONS.forEach((modId, addon) -> addon.onJSGCoreLoad());
        JSGCore.logger.info("Registered addons: ({}) {}", ADDONS.size(), String.join(", ", ADDONS.values().stream().map(a -> Optional.ofNullable(getInfo(a).get(AddonInfo.NAME)).orElse(a.getId())).toList()));
    }
}
