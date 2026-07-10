package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.crystal.CrystalColor;
import dev.tauri.jsg.core.common.block.crystal.ICrystalColor;
import dev.tauri.jsg.core.common.config.JSGCoreConfig;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.common.item.CommonUpgrade;
import dev.tauri.jsg.core.common.item.EnergyItem;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.item.JSGWrench;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemEmpty;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.registry.helper.CoreRegistryHelpers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import java.util.List;
import java.util.Map;

public class CoreItems {
    /**
     * Advancement icons
     */
    public static final RegistryObject<JSGItem> ICON_ROOT_ADVANCEMENT = CoreRegistryHelpers.ITEM_HELPER.builder("icon_root_advancement").clearTooltip().buildGeneric();

    /**
     * Notebook pages
     */

    public static final RegistryObject<JSGItem> NOTEBOOK_PAGE_EMPTY = JSGCore.REGISTRY_HELPER.item().register("page_notebook_empty", PageNotebookItemEmpty::new);
    public static final RegistryObject<JSGItem> NOTEBOOK_PAGE_FILLED = JSGCore.REGISTRY_HELPER.item().register("page_notebook_filled", PageNotebookItemFilled::new);
    public static final RegistryObject<JSGItem> NOTEBOOK_ITEM = JSGCore.REGISTRY_HELPER.item().register("notebook", NotebookItem::new);

    /**
     * Upgrades
     */
    public static final RegistryObject<JSGItem> CRYSTAL_UPGRADE_CAPACITY = CoreRegistryHelpers.ITEM_HELPER.builder("crystal_upgrade_capacity").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> CommonUpgrade.CAPACITY_UPGRADE);
    public static final RegistryObject<JSGItem> CRYSTAL_UPGRADE_EFFICIENCY = CoreRegistryHelpers.ITEM_HELPER.builder("crystal_upgrade_efficiency").setInTabs(List.of(CoreTabs.TAB_UPGRADES)).buildUpgrade(() -> CommonUpgrade.EFFICIENCY_UPGRADE);

    public static final RegistryObject<EnergyItem> CRYSTAL_ENERGY_BASIC = CoreRegistryHelpers.ITEM_HELPER.builder("basic_energy_crystal").setProperties(new Item.Properties().rarity(Rarity.COMMON)).setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_ENERGY)).buildEnergy(JSGCoreConfig.Energy.basicEnergyCrystalCapacity, JSGCoreConfig.Energy.basicEnergyCrystalEnergyReceiveSpeed, () -> Long.MAX_VALUE);
    public static final RegistryObject<EnergyItem> CRYSTAL_ENERGY_ADVANCED = CoreRegistryHelpers.ITEM_HELPER.builder("advanced_energy_crystal").setProperties(new Item.Properties().rarity(Rarity.UNCOMMON)).setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_ENERGY)).buildEnergy(JSGCoreConfig.Energy.advancedEnergyCrystalCapacity, JSGCoreConfig.Energy.advancedEnergyCrystalEnergyReceiveSpeed, () -> Long.MAX_VALUE);
    public static final RegistryObject<EnergyItem> CRYSTAL_ENERGY_ULTIMATE = CoreRegistryHelpers.ITEM_HELPER.builder("ultimate_energy_crystal").setProperties(new Item.Properties().rarity(Rarity.RARE)).setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_ENERGY)).buildEnergy(JSGCoreConfig.Energy.ultimateEnergyCrystalCapacity, JSGCoreConfig.Energy.ultimateEnergyCrystalEnergyReceiveSpeed, () -> Long.MAX_VALUE);
    public static final RegistryObject<EnergyItem> CRYSTAL_ENERGY_CREATIVE = CoreRegistryHelpers.ITEM_HELPER.builder("creative_energy_crystal").setProperties(new Item.Properties().rarity(Rarity.EPIC)).setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_ENERGY)).buildEnergyCreative();

    /**
     * Different Naquadah(main Stargate building material) stages of purity
     */
    public static final RegistryObject<JSGItem> NAQUADAH_ORE_RAW = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_ALLOY_RAW = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_alloy_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_RAW_DUST = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_raw_dust").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_RAW_NUGGET = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_raw_nugget").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    public static final RegistryObject<JSGItem> NAQUADAH_ALLOY = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_alloy").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_DUST = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_dust").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_NUGGET = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_nugget").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    public static final RegistryObject<JSGItem> NAQUADAH_ALLOY_REFINED = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_alloy_refined").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_REFINED_DUST = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_refined_dust").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> NAQUADAH_REFINED_NUGGET = CoreRegistryHelpers.ITEM_HELPER.builder("naquadah_refined_nugget").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    /**
     * Titanium and Trinium
     */
    public static final RegistryObject<JSGItem> TITANIUM_ORE_RAW = CoreRegistryHelpers.ITEM_HELPER.builder("titanium_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> TITANIUM_INGOT = CoreRegistryHelpers.ITEM_HELPER.builder("titanium_ingot").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> TITANIUM_DUST = CoreRegistryHelpers.ITEM_HELPER.builder("titanium_dust").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> TITANIUM_NUGGET = CoreRegistryHelpers.ITEM_HELPER.builder("titanium_nugget").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    public static final RegistryObject<JSGItem> TRINIUM_ORE_RAW = CoreRegistryHelpers.ITEM_HELPER.builder("trinium_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> TRINIUM_INGOT = CoreRegistryHelpers.ITEM_HELPER.builder("trinium_ingot").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> TRINIUM_DUST = CoreRegistryHelpers.ITEM_HELPER.builder("trinium_dust").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> TRINIUM_NUGGET = CoreRegistryHelpers.ITEM_HELPER.builder("trinium_nugget").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();


    public static final RegistryObject<JSGItem> COPPER_INGOT_EXPOSED = CoreRegistryHelpers.ITEM_HELPER.builder("exposed_copper_ingot").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> COPPER_INGOT_WEATHERED = CoreRegistryHelpers.ITEM_HELPER.builder("weathered_copper_ingot").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> COPPER_INGOT_OXIDIZED = CoreRegistryHelpers.ITEM_HELPER.builder("oxidized_copper_ingot").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();


    /**
     * Crafting items
     */
    public static final RegistryObject<JSGItem> CRYSTAL_BLUE_SMALL = CoreRegistryHelpers.ITEM_HELPER.builder("small_blue_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_BLUE = CoreRegistryHelpers.ITEM_HELPER.builder("blue_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_BLUE_SEED = CoreRegistryHelpers.ITEM_HELPER.builder("seed_blue_crystal").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_RED_SMALL = CoreRegistryHelpers.ITEM_HELPER.builder("small_red_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_RED = CoreRegistryHelpers.ITEM_HELPER.builder("red_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_RED_SEED = CoreRegistryHelpers.ITEM_HELPER.builder("seed_red_crystal").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_ENDER_SMALL = CoreRegistryHelpers.ITEM_HELPER.builder("small_ender_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_ENDER = CoreRegistryHelpers.ITEM_HELPER.builder("ender_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_ENDER_SEED = CoreRegistryHelpers.ITEM_HELPER.builder("seed_ender_crystal").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_YELLOW_SMALL = CoreRegistryHelpers.ITEM_HELPER.builder("small_yellow_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_YELLOW = CoreRegistryHelpers.ITEM_HELPER.builder("yellow_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_YELLOW_SEED = CoreRegistryHelpers.ITEM_HELPER.builder("seed_yellow_crystal").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_WHITE_SMALL = CoreRegistryHelpers.ITEM_HELPER.builder("small_white_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_WHITE = CoreRegistryHelpers.ITEM_HELPER.builder("white_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_WHITE_SEED = CoreRegistryHelpers.ITEM_HELPER.builder("seed_white_crystal").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_PEGASUS_SMALL = CoreRegistryHelpers.ITEM_HELPER.builder("small_pegasus_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_PEGASUS = CoreRegistryHelpers.ITEM_HELPER.builder("pegasus_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CRYSTAL_PEGASUS_SEED = CoreRegistryHelpers.ITEM_HELPER.builder("seed_pegasus_crystal").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    public static final RegistryObject<JSGItem> CRYSTAL_TOKRA = CoreRegistryHelpers.ITEM_HELPER.builder("tokra_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    public static final RegistryObject<JSGItem> CIRCUIT_CONTROL_BASE = CoreRegistryHelpers.ITEM_HELPER.builder("circuit_control_base").setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CIRCUIT_CONTROL_CRYSTAL = CoreRegistryHelpers.ITEM_HELPER.builder("circuit_control_crystal").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> CIRCUIT_CONTROL_NAQUADAH = CoreRegistryHelpers.ITEM_HELPER.builder("circuit_control_naquadah").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    public static final RegistryObject<JSGItem> PESTLE = CoreRegistryHelpers.ITEM_HELPER.builder("pestle").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> MORTAR_AND_PESTLE = CoreRegistryHelpers.ITEM_HELPER.builder("mortar_and_pestle").clearTooltip().setInTabs(List.of(CoreTabs.TAB_TOOLS)).buildDurability(16, true);
    public static final RegistryObject<JSGItem> CHARCOAL_STICK_MIXTURE = CoreRegistryHelpers.ITEM_HELPER.builder("charcoal_stick_mixture").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> BLACK_CHALK = JSGCore.REGISTRY_HELPER.item().register("black_chalk", dev.tauri.jsg.core.common.item.BlackChalkItem::new);
    public static final RegistryObject<JSGItem> CRUSHED_CALCITE = CoreRegistryHelpers.ITEM_HELPER.builder("crushed_calcite").setInTabs(List.of(CoreTabs.TAB_TOOLS, CoreTabs.TAB_RESOURCES)).buildGeneric();

    /**
     * TOOLS
     */
    public static final RegistryObject<JSGItem> TAURI_ANCIENT_ADAPTER = CoreRegistryHelpers.ITEM_HELPER.builder("tauri_ancient_adapter").setInTabs(List.of(CoreTabs.TAB_TOOLS)).buildGeneric();

    public static final RegistryObject<JSGItem> JSG_HAMMER = CoreRegistryHelpers.ITEM_HELPER.builder("hammer").setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_TOOLS)).buildDurability(25, true);
    public static final RegistryObject<JSGItem> JSG_KNIFE = CoreRegistryHelpers.ITEM_HELPER.builder("knife").setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_TOOLS)).buildDurability(32, true);
    public static final RegistryObject<JSGItem> JSG_SCREWDRIVER = CoreRegistryHelpers.ITEM_HELPER.builder("screwdriver").setMaxStack(1).setInTabs(List.of(CoreTabs.TAB_TOOLS)).buildDurability(150, true);
    public static final RegistryObject<JSGItem> JSG_WRENCH = JSGCore.REGISTRY_HELPER.item().register("wrench", JSGWrench::new);

    /**
     * GEARS
     */
    public static final RegistryObject<JSGItem> GEAR_TITANIUM = CoreRegistryHelpers.ITEM_HELPER.builder("gear_titanium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> GEAR_TRINIUM = CoreRegistryHelpers.ITEM_HELPER.builder("gear_trinium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> GEAR_NAQUADAH_RAW = CoreRegistryHelpers.ITEM_HELPER.builder("gear_naquadah_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> GEAR_NAQUADAH = CoreRegistryHelpers.ITEM_HELPER.builder("gear_naquadah").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> GEAR_NAQUADAH_REFINED = CoreRegistryHelpers.ITEM_HELPER.builder("gear_naquadah_refined").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    /**
     * PLATES
     */
    public static final RegistryObject<JSGItem> PLATE_TITANIUM = CoreRegistryHelpers.ITEM_HELPER.builder("plate_titanium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> PLATE_TRINIUM = CoreRegistryHelpers.ITEM_HELPER.builder("plate_trinium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> PLATE_NAQUADAH_RAW = CoreRegistryHelpers.ITEM_HELPER.builder("plate_naquadah_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> PLATE_NAQUADAH = CoreRegistryHelpers.ITEM_HELPER.builder("plate_naquadah").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();
    public static final RegistryObject<JSGItem> PLATE_NAQUADAH_REFINED = CoreRegistryHelpers.ITEM_HELPER.builder("plate_naquadah_refined").clearTooltip().setInTabs(List.of(CoreTabs.TAB_RESOURCES)).buildGeneric();

    /**
     * LEMON ALL VARIANTS EVEN FOOD!
     */

    public static final RegistryObject<JSGItem> TRAP_LEMON = CoreRegistryHelpers.ITEM_HELPER.builder("trap_lemon").clearTooltip().setInTabs(List.of(CoreTabs.TAB_TOOLS)).setMaxStack(32).buildGeneric();
    public static final RegistryObject<JSGItem> FOOD_LEMON = CoreRegistryHelpers.ITEM_HELPER.builder("slice_lemon").setProperties(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 1, false, false), 0.3f).alwaysEat().build())).clearTooltip().buildGeneric();


    public static final Map<ICrystalColor, RegistryObject<? extends Item>> CRYSTAL_SEEDS = Map.of(
            CrystalColor.BLUE, CRYSTAL_BLUE_SEED,
            CrystalColor.ENDER, CRYSTAL_ENDER_SEED,
            CrystalColor.PEGASUS, CRYSTAL_PEGASUS_SEED,
            CrystalColor.RED, CRYSTAL_RED_SEED,
            CrystalColor.WHITE, CRYSTAL_WHITE_SEED,
            CrystalColor.YELLOW, CRYSTAL_YELLOW_SEED
    );

    public static final Map<ICrystalColor, RegistryObject<? extends Item>> CRYSTALS = Map.of(
            CrystalColor.BLUE, CRYSTAL_BLUE,
            CrystalColor.ENDER, CRYSTAL_ENDER,
            CrystalColor.PEGASUS, CRYSTAL_PEGASUS,
            CrystalColor.RED, CRYSTAL_RED,
            CrystalColor.WHITE, CRYSTAL_WHITE,
            CrystalColor.YELLOW, CRYSTAL_YELLOW
    );

    public static final Map<ICrystalColor, RegistryObject<? extends Item>> CRYSTALS_SMALL = Map.of(
            CrystalColor.BLUE, CRYSTAL_BLUE_SMALL,
            CrystalColor.ENDER, CRYSTAL_ENDER_SMALL,
            CrystalColor.PEGASUS, CRYSTAL_PEGASUS_SMALL,
            CrystalColor.RED, CRYSTAL_RED_SMALL,
            CrystalColor.WHITE, CRYSTAL_WHITE_SMALL,
            CrystalColor.YELLOW, CRYSTAL_YELLOW_SMALL
    );


    public static void init() {
        /*
         * Conditional registrations - items for integrations etc.
         */
        Integrations.CREATE.addOnLoad(() -> {
            CoreRegistryHelpers.ITEM_HELPER.builder("incomplete_gear_naquadah_raw").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("incomplete_gear_naquadah").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("incomplete_gear_naquadah_refined").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("incomplete_gear_titanium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
            CoreRegistryHelpers.ITEM_HELPER.builder("incomplete_gear_trinium").clearTooltip().setInTabs(List.of(CoreTabs.TAB_INTEGRATIONS)).buildGeneric();
        });
    }
}
