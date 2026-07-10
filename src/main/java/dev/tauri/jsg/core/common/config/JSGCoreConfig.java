package dev.tauri.jsg.core.common.config;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.ConfigScreenClientRegister;
import dev.tauri.jsg.core.common.config.values.JSGConfigValue;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

public class JSGCoreConfig {
    public static final JSGConfigChild C_GENERAL = new JSGConfigChild(() -> General.BUILDER, "General", JSGCore.MOD_ID);
    public static final JSGConfigChild C_CC = new JSGConfigChild(() -> ComputersIntegration.BUILDER, "ComputersIntegration", JSGCore.MOD_ID);
    public static final JSGConfigChild C_ENERGY = new JSGConfigChild(() -> Energy.BUILDER, "Energy", JSGCore.MOD_ID);

    public static class General {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final JSGConfigValue.DoubleValue visualGlyphTransparency = C_GENERAL.add(new JSGConfigValue.DoubleValue(BUILDER,
                "Notebook page Glyph transparency", 0.75, 0, 1, true,
                "Specifies transparency of glyphs on notebook page",
                "SIDE: CLIENT"
        ));
    }

    public static class ComputersIntegration {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final JSGConfigValue.IntValue wirelessRange = C_CC.add(new JSGConfigValue.IntValue(BUILDER,
                "Devices wireless range", 20, 0, 150,
                "Defines wireless range of devices for OC/CC",
                "SIDE: SERVER"
        ));

    }

    public static class Energy {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final JSGConfigValue.LongValue basicEnergyCrystalCapacity = C_ENERGY.add(new JSGConfigValue.LongValue(BUILDER, "Basic energy crystal.Capacity", Integer.MAX_VALUE, 4608L, Long.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.LongValue basicEnergyCrystalEnergyReceiveSpeed = C_ENERGY.add(new JSGConfigValue.LongValue(BUILDER, "Basic energy crystal.Max power input", Integer.MAX_VALUE / 100L, 1L, Long.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.LongValue advancedEnergyCrystalCapacity = C_ENERGY.add(new JSGConfigValue.LongValue(BUILDER, "Advanced energy crystal.Capacity", Integer.MAX_VALUE * 2L, 4608L, Long.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.LongValue advancedEnergyCrystalEnergyReceiveSpeed = C_ENERGY.add(new JSGConfigValue.LongValue(BUILDER, "Advanced energy crystal.Max power input", Integer.MAX_VALUE / 200L, 1L, Long.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.LongValue ultimateEnergyCrystalCapacity = C_ENERGY.add(new JSGConfigValue.LongValue(BUILDER, "Ultimate energy crystal.Capacity", Integer.MAX_VALUE * 4L, 4608L, Long.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));

        public static final JSGConfigValue.LongValue ultimateEnergyCrystalEnergyReceiveSpeed = C_ENERGY.add(new JSGConfigValue.LongValue(BUILDER, "Ultimate energy crystal.Max power input", Integer.MAX_VALUE / 400L, 1L, Long.MAX_VALUE,
                "SIDE: SERVER/CLIENT"
        ));
    }

    // ----------------------------------------------------
    // REGISTRATION

    public static final String CONFIG_GENERAL_VERSION = "1.0";
    private static final String CONFIG_FILE_NAME = "jsg/core_" + CONFIG_GENERAL_VERSION + "/";

    public static final ArrayList<JSGConfigChild> LIST = new ArrayList<>();

    public static void register() {
        LIST.clear();
        LIST.add(C_GENERAL);
        LIST.add(C_CC);
        LIST.add(C_ENERGY);

        register(JSGCore.MOD_ID, CONFIG_FILE_NAME, LIST);
    }

    public static void register(String modId, String path, List<JSGConfigChild> list) {
        for (JSGConfigChild child : list) {
            child.builtSpec = child.builder.get().build();
            ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, child.builtSpec, path + child.name + ".toml");
        }
        if (FMLEnvironment.dist.isClient()) ConfigScreenClientRegister.register(modId, list);
    }

    public static void load() {
    }
}
