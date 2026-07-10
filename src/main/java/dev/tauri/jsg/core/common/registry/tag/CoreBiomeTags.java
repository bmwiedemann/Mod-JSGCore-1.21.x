package dev.tauri.jsg.core.common.registry.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class CoreBiomeTags {

    public static TagKey<Biome> IS_BADLANDS = tag("is_badlands");
    public static TagKey<Biome> IS_COLD = tag("is_cold");
    public static TagKey<Biome> IS_COLD_SOLID = tag("is_cold_solid");
    public static TagKey<Biome> IS_DESERT = tag("is_desert");
    public static TagKey<Biome> IS_END = tag("is_end");
    public static TagKey<Biome> IS_FORREST = tag("is_forrest");
    public static TagKey<Biome> IS_FUNGI = tag("is_fungi");
    public static TagKey<Biome> IS_MOSSY = tag("is_mossy");
    public static TagKey<Biome> IS_NETHER = tag("is_nether");
    public static TagKey<Biome> IS_OCEAN = tag("is_ocean");
    public static TagKey<Biome> IS_SANDY = tag("is_sandy");
    public static TagKey<Biome> IS_SWAMP = tag("is_swamp");
    public static TagKey<Biome> IS_TEMPERATE = tag("is_temperate");
    public static TagKey<Biome> IS_WARPED = tag("is_warped");
    public static TagKey<Biome> IS_WET = tag("is_wet");

    public static TagKey<Biome> HAS_BUDDING_BLUE = tag("has_budding_blue");
    public static TagKey<Biome> HAS_BUDDING_ENDER = tag("has_budding_ender");
    public static TagKey<Biome> HAS_BUDDING_PEGASUS = tag("has_budding_pegasus");
    public static TagKey<Biome> HAS_BUDDING_RED = tag("has_budding_red");
    public static TagKey<Biome> HAS_BUDDING_WHITE = tag("has_budding_white");
    public static TagKey<Biome> HAS_BUDDING_YELLOW = tag("has_budding_yellow");

    public static TagKey<Biome> HAS_GEODES_BLUE = tag("has_geodes_blue");
    public static TagKey<Biome> HAS_GEODES_ENDER = tag("has_geodes_ender");
    public static TagKey<Biome> HAS_GEODES_PEGASUS = tag("has_geodes_pegasus");
    public static TagKey<Biome> HAS_GEODES_RED = tag("has_geodes_red");
    public static TagKey<Biome> HAS_GEODES_WHITE = tag("has_geodes_white");
    public static TagKey<Biome> HAS_GEODES_YELLOW = tag("has_geodes_yellow");

    public static TagKey<Biome> HAS_DRIPSTONE = tag("has_dripstone");
    public static TagKey<Biome> HAS_PODZOL = tag("has_podzol");
    public static TagKey<Biome> HAS_SCULK = tag("has_sculk");

    public static TagKey<Biome> HAS_NAQUADAH_GENERATED = tag("has_naquadah_generated");
    public static TagKey<Biome> HAS_TITANIUM_GENERATED = tag("has_titanium_generated");
    public static TagKey<Biome> HAS_TRINIUM_GENERATED = tag("has_trinium_generated");

    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, JSGMapping.rl(JSGCore.MOD_ID, name));
    }
}
