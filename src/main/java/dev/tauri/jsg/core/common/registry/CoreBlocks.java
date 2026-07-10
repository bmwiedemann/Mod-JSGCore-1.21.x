package dev.tauri.jsg.core.common.registry;

import com.google.common.collect.Maps;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.LemonBlock;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheType;
import dev.tauri.jsg.core.common.block.core.InvisibleBlock;
import dev.tauri.jsg.core.common.block.crystal.CrystalBudType;
import dev.tauri.jsg.core.common.block.crystal.ICrystalColor;
import dev.tauri.jsg.core.common.registry.helper.CoreRegistryHelpers;
import dev.tauri.jsg.core.common.registry.helper.builder.block.OreBlockVariant;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CoreBlocks {
    private static final DeferredRegister<Block> REGISTER = JSGCore.REGISTRY_HELPER.block();

    
    public static final RegistryObject<Block> INVISIBLE_BLOCK = REGISTER.register("invisible_block", InvisibleBlock::new);

    /**
     * ORE
     */
    public static final Map<OreBlockVariant, RegistryObject<Block>> ORE_NAQUADAH = CoreRegistryHelpers.ORE_HELPER.builder("naquadah_ore").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildAll();
    public static final Map<OreBlockVariant, RegistryObject<Block>> ORE_TITANIUM = CoreRegistryHelpers.ORE_HELPER.builder("titanium_ore").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildAll();
    public static final Map<OreBlockVariant, RegistryObject<Block>> ORE_TRINIUM = CoreRegistryHelpers.ORE_HELPER.builder("trinium_ore").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildAll();

    public static final RegistryObject<Block> RAW_ORE_NAQUADAH_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("raw_naquadah_ore_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.COLOR_GREEN)).buildGeneric();
    public static final RegistryObject<Block> RAW_ORE_TITANIUM_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("raw_titanium_ore_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.COLOR_GRAY)).buildGeneric();
    public static final RegistryObject<Block> RAW_ORE_TRINIUM_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("raw_trinium_ore_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.COLOR_LIGHT_GRAY)).buildGeneric();

    public static final RegistryObject<Block> NAQUADAH_RAW_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("naquadah_alloy_raw_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_GREEN)).buildGeneric();
    public static final RegistryObject<Block> NAQUADAH_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("naquadah_alloy_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_LIGHT_BLUE)).buildGeneric();
    public static final RegistryObject<Block> NAQUADAH_REFINED_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("naquadah_alloy_refined_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).mapColor(MapColor.EMERALD)).buildGeneric();
    public static final RegistryObject<Block> TITANIUM_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("titanium_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).mapColor(MapColor.METAL)).buildGeneric();
    public static final RegistryObject<Block> TRINIUM_BLOCK = CoreRegistryHelpers.BLOCK_HELPER.builder("trinium_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).mapColor(MapColor.QUARTZ)).buildGeneric();

    /**
     * CRYSTAL
     */
    public static final Map<ICrystalColor, RegistryObject<Block>> CRYSTAL_BLOCK = CoreRegistryHelpers.ORE_HELPER.builder("{color}_crystal_block").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)).buildCrystalBlock();

    public static final Map<ICrystalColor, RegistryObject<Block>> CRYSTAL_BUD_SMALL = CoreRegistryHelpers.ORE_HELPER.builder("small_{color}_crystal_bud").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildCrystalBuds(CrystalBudType.SMALL);
    public static final Map<ICrystalColor, RegistryObject<Block>> CRYSTAL_BUD_MEDIUM = CoreRegistryHelpers.ORE_HELPER.builder("medium_{color}_crystal_bud").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildCrystalBuds(CrystalBudType.MEDIUM);
    public static final Map<ICrystalColor, RegistryObject<Block>> CRYSTAL_BUD_LARGE = CoreRegistryHelpers.ORE_HELPER.builder("large_{color}_crystal_bud").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildCrystalBuds(CrystalBudType.LARGE);
    public static final Map<ICrystalColor, RegistryObject<Block>> CRYSTAL_CLUSTER = CoreRegistryHelpers.ORE_HELPER.builder("{color}_crystal_cluster").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildCrystalBuds(CrystalBudType.CLUSTER);
    public static final Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>> CRYSTAL_BUDDING = CoreRegistryHelpers.ORE_HELPER.builder("budding_{color}_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.BUDDING_AMETHYST)).buildCrystalBuddings((color, size) -> switch (size) {
        case SMALL -> CRYSTAL_BUD_SMALL.get(color).get();
        case MEDIUM -> CRYSTAL_BUD_MEDIUM.get(color).get();
        case LARGE -> CRYSTAL_BUD_LARGE.get(color).get();
        case CLUSTER -> CRYSTAL_CLUSTER.get(color).get();
    });
    public static final Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>> UNSTABLE_CRYSTAL_BUDDING = CoreRegistryHelpers.ORE_HELPER.builder("unstable_budding_{color}_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.BUDDING_AMETHYST)).buildCrystalUnstableBuddings(() -> CRYSTAL_BUDDING, () -> CRYSTAL_BLOCK);

    public static final RegistryObject<Block> LEMON_BLOCK = REGISTER.register("lemon", LemonBlock::new);
    public static final RegistryObject<Block> STATIC_SMOOTH_SANDSTONE = CoreRegistryHelpers.BLOCK_HELPER.builder("static_smooth_sandstone").clearTooltip().setProperties(BlockBehaviour.Properties.copy(Blocks.SMOOTH_SANDSTONE).pushReaction(PushReaction.BLOCK)).setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).buildGeneric();
    public static final RegistryObject<Block> BRAZIER_COAL = CoreRegistryHelpers.BLOCK_HELPER.builder("brazier_coal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_BUILDING_BLOCKS)).setProperties(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK).mapColor(MapColor.COLOR_BLACK)).buildGeneric();

    /**
     * CARTOUCHES
     */
    public static final Map<String, Supplier<BlockState>> CARTOUCHES_BLOCKS = Util.make(Maps.newHashMap(), (map) -> {
        map.put("stone", Blocks.STONE::defaultBlockState);
        map.put("smooth_sandstone", Blocks.SMOOTH_SANDSTONE::defaultBlockState);
        map.put("smooth_red_sandstone", Blocks.SMOOTH_RED_SANDSTONE::defaultBlockState);
        map.put("polished_diorite", Blocks.POLISHED_DIORITE::defaultBlockState);
        map.put("polished_granite", Blocks.POLISHED_GRANITE::defaultBlockState);
        map.put("polished_deepslate", Blocks.POLISHED_DEEPSLATE::defaultBlockState);
        map.put("prismarine_bricks", Blocks.PRISMARINE_BRICKS::defaultBlockState);
        map.put("netherrack", Blocks.NETHERRACK::defaultBlockState);
        map.put("nether_bricks", Blocks.NETHER_BRICKS::defaultBlockState);
        map.put("red_nether_bricks", Blocks.RED_NETHER_BRICKS::defaultBlockState);
        map.put("smooth_basalt", Blocks.SMOOTH_BASALT::defaultBlockState);
        map.put("end_stone_bricks", Blocks.END_STONE_BRICKS::defaultBlockState);
        map.put("purpur_block", Blocks.PURPUR_BLOCK::defaultBlockState);
    });
    public static final Map<String, Map<CartoucheType, RegistryObject<Block>>> CARTOUCHES = Util.make(Maps.newHashMap(), (map) -> {
        for (var b : CARTOUCHES_BLOCKS.entrySet()) {
            map.put(b.getKey(), CartoucheType.registerTypes(() -> REGISTER, b.getKey() + "_cartouche", b.getValue()));
        }
    });

    public static void init() {
    }
}
