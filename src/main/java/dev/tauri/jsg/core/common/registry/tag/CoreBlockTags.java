package dev.tauri.jsg.core.common.registry.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CoreBlockTags {
    public static final TagKey<Block> WRENCH_ROTATABLE = tag("wrench_rotatable");
    public static final TagKey<Block> CAMO_BLACKLISTED = tag("camouflage_blacklisted");
    public static final TagKey<Block> BOSS_IMMUNE = tag("boss_immune_blocks");
    public static final TagKey<Block> CRYSTAL_BUDS = tag("crystal_buds");
    public static final TagKey<Block> STORAGE_NAQUADAH = tag("storage_blocks/naquadah");
    public static final TagKey<Block> STORAGE_NAQUADAH_ALLOY = tag("storage_blocks/naquadah_alloy");
    public static final TagKey<Block> STORAGE_REFINED_NAQUADAH = tag("storage_blocks/naquadah_refined");
    public static final TagKey<Block> STORAGE_TITANIUM = tag("storage_blocks/titanium");
    public static final TagKey<Block> STORAGE_TRINIUM = tag("storage_blocks/trinium");
    public static final TagKey<Block> STORAGE_RAW_NAQUADAH = tag("storage_blocks/raw_naquadah");
    public static final TagKey<Block> STORAGE_RAW_TITANIUM = tag("storage_blocks/raw_titanium");
    public static final TagKey<Block> STORAGE_RAW_TRINIUM = tag("storage_blocks/raw_trinium");
    public static final TagKey<Block> STORAGE_BLUE_CRYSTAL = tag("storage_blocks/blue_crystal");
    public static final TagKey<Block> STORAGE_ENDER_CRYSTAL = tag("storage_blocks/ender_crystal");
    public static final TagKey<Block> STORAGE_PEGASUS_CRYSTAL = tag("storage_blocks/pegasus_crystal");
    public static final TagKey<Block> STORAGE_RED_CRYSTAL = tag("storage_blocks/red_crystal");
    public static final TagKey<Block> STORAGE_WHITE_CRYSTAL = tag("storage_blocks/white_crystal");
    public static final TagKey<Block> STORAGE_YELLOW_CRYSTAL = tag("storage_blocks/yellow_crystal");
    public static final TagKey<Block> ORE_IN_GROUND_DEEPSLATE = tag("ores_in_ground/deepslate");
    public static final TagKey<Block> ORE_IN_GROUND_ENDSTONE = tag("ores_in_ground/endstone");
    public static final TagKey<Block> ORE_IN_GROUND_NETHERRACK = tag("ores_in_ground/netherrack");
    public static final TagKey<Block> ORE_IN_GROUND_SANDSTONE = tag("ores_in_ground/sandstone");
    public static final TagKey<Block> ORE_IN_GROUND_STONE = tag("ores_in_ground/stone");
    public static final TagKey<Block> ORE_NAQUADAH = tag("ores/naquadah");
    public static final TagKey<Block> ORE_TITANIUM = tag("ores/titanium");
    public static final TagKey<Block> ORE_TRINIUM = tag("ores/trinium");
    public static final TagKey<Block> ORE_RATES_SINGULAR = tag("ore_rates/singular");
    public static final TagKey<Block> BUDDINGS_BLUE = tag("buddings/blue_crystal");
    public static final TagKey<Block> BUDDINGS_ENDER = tag("buddings/ender_crystal");
    public static final TagKey<Block> BUDDINGS_PEGASUS = tag("buddings/pegasus_crystal");
    public static final TagKey<Block> BUDDINGS_RED = tag("buddings/red_crystal");
    public static final TagKey<Block> BUDDINGS_WHITE = tag("buddings/white_crystal");
    public static final TagKey<Block> BUDDINGS_YELLOW = tag("buddings/yellow_crystal");
    public static final TagKey<Block> BUDDING = tag("budding");
    public static final TagKey<Block> UNSTABLE_BUDDING = tag("unstable_budding");
    public static final TagKey<Block> ORES = tag("ores");
    public static final TagKey<Block> STORAGE_BLOCKS = tag("storage_blocks");
    public static final TagKey<Block> CRYSTAL_BLOCKS = tag("storage_blocks/crystal");
    public static final TagKey<Block> SUPPORT_LEMON = tag("support_lemon");
    public static final TagKey<Block> CARTOUCHES = tag("cartouches");
    public static final TagKey<Block> FLUID_CAULDRON_HEATING = tag("fluid_cauldron_heating");

    private static TagKey<Block> tag(String name) {
        return BlockTags.create(JSGMapping.rl(JSGCore.MOD_ID, name));
    }
}
