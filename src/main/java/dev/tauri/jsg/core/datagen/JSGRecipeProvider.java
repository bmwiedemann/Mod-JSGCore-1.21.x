package dev.tauri.jsg.core.datagen;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheType;
import dev.tauri.jsg.core.common.block.crystal.CrystalColor;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.tag.CoreItemTags;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JSGRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public JSGRecipeProvider(PackOutput pOutput, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> registries) {
        super(pOutput, registries);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void buildRecipes(net.minecraft.data.recipes.RecipeOutput pWriter) {
        CoreBlocks.CARTOUCHES.forEach((material, cartouches) -> {
            var baseBlock = CoreBlocks.CARTOUCHES_BLOCKS.get(material).get().getBlock();
            cartouches.forEach((type, cartouche) -> {
                var builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cartouche.get())
                        .unlockedBy("has_base_block", has(baseBlock))
                        .group("jsg:" + getItemName(baseBlock) + "_cartouche");
                switch (type) {
                    case SIX:
                        builder
                                .pattern(" B ")
                                .pattern(" B ")
                                .pattern(" B ")
                                .define('B', baseBlock);
                        break;
                    case SEVEN:
                        builder
                                .pattern("B ")
                                .pattern("C ")
                                .define('B', baseBlock)
                                .define('C', CoreBlocks.CARTOUCHES.get(material).get(CartoucheType.SIX).get());
                        break;
                    case EIGHT:
                        builder
                                .pattern("B ")
                                .pattern("C ")
                                .define('B', baseBlock)
                                .define('C', CoreBlocks.CARTOUCHES.get(material).get(CartoucheType.SEVEN).get());
                        break;
                    case SEVEN_POO:
                        builder
                                .pattern("C ")
                                .pattern("B ")
                                .define('B', baseBlock)
                                .define('C', CoreBlocks.CARTOUCHES.get(material).get(CartoucheType.SIX).get());
                        break;
                    case EIGHT_POO:
                        builder
                                .pattern("B ")
                                .pattern("C ")
                                .define('B', baseBlock)
                                .define('C', CoreBlocks.CARTOUCHES.get(material).get(CartoucheType.SEVEN_POO).get());
                        break;
                    case NINE_POO:
                        builder
                                .pattern("B ")
                                .pattern("C ")
                                .define('B', baseBlock)
                                .define('C', CoreBlocks.CARTOUCHES.get(material).get(CartoucheType.EIGHT_POO).get());
                        break;
                }
                builder.save(pWriter);
            });
        });

        CoreBlocks.CRYSTAL_BLOCK.forEach((cColor, crystalBlock) -> {
            CrystalColor color = (CrystalColor) cColor;
            String colorName = color.name().toLowerCase();
            var ingredient = CoreItems.CRYSTALS.get(color).get();

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, crystalBlock.get())
                    .group(JSGCore.MOD_ID + ":" + colorName + "_crystal_block")
                    .unlockedBy("has_crystal_" + colorName, has(ingredient))
                    .pattern("##")
                    .pattern("##")
                    .define('#', ingredient)
                    .save(pWriter);
        });

        // Copper ingot -> Copper Block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.EXPOSED_COPPER)
                .group("minecraft:exposed_copper")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItems.COPPER_INGOT_EXPOSED.get())
                .unlockedBy("has_ingot", has(CoreItems.COPPER_INGOT_EXPOSED.get()))
                .save(pWriter, locationCorrection("exposed_copper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.WEATHERED_COPPER)
                .group("minecraft:weathered_copper")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItems.COPPER_INGOT_WEATHERED.get())
                .unlockedBy("has_ingot", has(CoreItems.COPPER_INGOT_WEATHERED.get()))
                .save(pWriter, locationCorrection("weathered_copper"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.OXIDIZED_COPPER)
                .group("minecraft:oxidized_copper")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItems.COPPER_INGOT_WEATHERED.get())
                .unlockedBy("has_ingot", has(CoreItems.COPPER_INGOT_OXIDIZED.get()))
                .save(pWriter, locationCorrection("oxidized_copper"));

        // Raw ore -> Raw Ore block
        // Zkontolovat TAGY - PŘÍPADNĚ NAHRADIT FORGE TAGEM
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.RAW_ORE_NAQUADAH_BLOCK.get())
                .group("jsg_core:raw_naquadah_ore_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.RAW_MATERIAL_NAQUADAH)
                .unlockedBy("has_ingot", has(CoreItemTags.RAW_MATERIAL_NAQUADAH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.RAW_ORE_TITANIUM_BLOCK.get())
                .group("jsg_core:raw_titanium_ore_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.RAW_MATERIAL_TITANIUM)
                .unlockedBy("has_ingot", has(CoreItemTags.RAW_MATERIAL_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.RAW_ORE_TRINIUM_BLOCK.get())
                .group("jsg_core:raw_trinium_ore_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.RAW_MATERIAL_TRINIUM)
                .unlockedBy("has_ingot", has(CoreItemTags.RAW_MATERIAL_TRINIUM))
                .save(pWriter);

        //Ingots -> Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.NAQUADAH_RAW_BLOCK.get())
                .group("jsg_core:naquadah_alloy_raw_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.INGOT_NAQUADAH)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_NAQUADAH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.NAQUADAH_BLOCK.get())
                .group("jsg_core:naquadah_alloy_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.NAQUADAH_REFINED_BLOCK.get())
                .group("jsg_core:naquadah_alloy_refined_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.INGOT_NAQUADAH_REFINED)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_NAQUADAH_REFINED))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.TITANIUM_BLOCK.get())
                .group("jsg_core:titanium_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.INGOT_TITANIUM)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, CoreBlocks.TRINIUM_BLOCK.get())
                .group("jsg_core:trinium_block")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.INGOT_TRINIUM)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .save(pWriter);

        //Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CoreItems.JSG_HAMMER.get())
                .group("jsg_core:hammer")
                .pattern(" I ")
                .pattern(" SI")
                .pattern("S  ")
                .define('I', CoreItemTags.INGOT_TITANIUM)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CoreItems.JSG_KNIFE.get())
                .group("jsg_core:knife")
                .pattern(" I")
                .pattern("S ")
                .define('I', CoreItemTags.INGOT_TITANIUM)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CoreItems.JSG_SCREWDRIVER.get())
                .group("jsg_core:screwdriver")
                .pattern("  N")
                .pattern("BI ")
                .pattern("IB ")
                .define('N', CoreItemTags.NUGGET_TITANIUM)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.DYES_BLUE)
                .unlockedBy("has_titanium_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CoreItems.JSG_WRENCH.get())
                .group("jsg_core:wrench")
                .pattern(" N ")
                .pattern("RNN")
                .pattern("IR ")
                .define('N', CoreItemTags.NUGGET_TITANIUM)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DYES_RED)
                .unlockedBy("has_titanium_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, CoreItems.MORTAR_AND_PESTLE.get())
                .group("jsg_core:mortar_and_pestle")
                .requires(CoreItems.PESTLE.get())
                .requires(Items.BOWL)
                .unlockedBy("has_pestle", has(CoreItems.PESTLE.get()))
                .unlockedBy("has_bowl", has(Items.BOWL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, CoreItems.TRAP_LEMON.get())
                .group("jsg_core:lemon")
                .requires(CoreBlocks.LEMON_BLOCK.get())
                .requires(CoreItems.JSG_KNIFE.get())
                .unlockedBy("has_lemon", has(CoreBlocks.LEMON_BLOCK.get()))
                .save(pWriter);

        //Items
        //Food
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, CoreItems.FOOD_LEMON.get())
                .group("jsg_core:lemon")
                .requires(CoreItems.TRAP_LEMON.get())
                .requires(CoreItems.JSG_KNIFE.get())
                .unlockedBy("has_lemon", has(CoreBlocks.LEMON_BLOCK.get()))
                .save(pWriter);

        //Raw ores
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_ORE_RAW.get(), 9)
                .group("jsg_core:raw_naquadah_ore")
                .requires(CoreItemTags.STORAGE_BLOCK_RAW_ORE_NAQUADAH)
                .unlockedBy("has_raw_naquadah", has(CoreItemTags.STORAGE_BLOCK_RAW_ORE_NAQUADAH))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TITANIUM_ORE_RAW.get(), 9)
                .group("jsg_core:raw_titanium_ore")
                .requires(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TITANIUM)
                .unlockedBy("has_mraw_titanium", has(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TITANIUM))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TRINIUM_ORE_RAW.get(), 9)
                .group("jsg_core:raw_trinium_ore")
                .requires(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TRINIUM)
                .unlockedBy("has_raw_trinium", has(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TRINIUM))
                .save(pWriter);

        //Dusts
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, CoreItems.CHARCOAL_STICK_MIXTURE.get())
                .group("jsg_core:charcoal_stick_mixture")
                .requires(ItemTags.COALS)
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_coal", has(ItemTags.COALS))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_RAW_DUST.get())
                .group("jsg_core:naquadah_raw_dust")
                .requires(CoreItemTags.INGOT_NAQUADAH)
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_raw_naquadah_ingot", has(CoreItemTags.INGOT_NAQUADAH))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_DUST.get())
                .group("jsg_core:naquadah_dust")
                .requires(CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_naquadah_alloy_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_REFINED_DUST.get())
                .group("jsg_core:naquadah_refined_dust")
                .requires(CoreItemTags.INGOT_NAQUADAH_REFINED)
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_naquadah_reffined_ingot", has(CoreItemTags.INGOT_NAQUADAH_REFINED))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TITANIUM_DUST.get())
                .group("jsg_core:titanium_dust")
                .requires(CoreItemTags.INGOT_TITANIUM)
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_titanium_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TRINIUM_DUST.get())
                .group("jsg_core:trinium_dust")
                .requires(CoreItemTags.INGOT_TRINIUM)
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_trinium_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .save(pWriter);

        //Plates
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.PLATE_NAQUADAH_RAW.get())
                .group("jsg_core:raw_naquadah_plate")
                .requires(CoreItemTags.INGOT_NAQUADAH)
                .requires(CoreItems.JSG_HAMMER.get())
                .unlockedBy("has_raw_naquadah_ingot", has(CoreItemTags.INGOT_NAQUADAH))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.PLATE_NAQUADAH.get())
                .group("jsg_core:naquadah_plate")
                .requires(CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .requires(CoreItems.JSG_HAMMER.get())
                .unlockedBy("has_naqudah_alloy_ingot", has(CoreItemTags.PLATE_NAQUADAH))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.PLATE_NAQUADAH_REFINED.get())
                .group("jsg_core:naquadah_refined_plate")
                .requires(CoreItemTags.INGOT_NAQUADAH_REFINED)
                .requires(CoreItems.JSG_HAMMER.get())
                .unlockedBy("has_naquadah_refined_ingot", has(CoreItemTags.INGOT_NAQUADAH_REFINED))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.PLATE_TITANIUM.get())
                .group("jsg_core:titanium_plate")
                .requires(CoreItemTags.INGOT_TITANIUM)
                .requires(CoreItems.JSG_HAMMER.get())
                .unlockedBy("has_titanium_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.PLATE_TRINIUM.get())
                .group("jsg_core:trinium_dust")
                .requires(CoreItemTags.INGOT_TRINIUM)
                .requires(CoreItems.JSG_HAMMER.get())
                .unlockedBy("has_trinium_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .save(pWriter);

        //Gears
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.GEAR_NAQUADAH_RAW.get())
                .group("jsg_core:gear_naquadah_raw")
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', CoreItemTags.INGOT_NAQUADAH)
                .unlockedBy("has_raw_naquadah_ingot", has(CoreItemTags.INGOT_NAQUADAH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.GEAR_NAQUADAH.get())
                .group("jsg_core:gear_naquadah")
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_naquadah_alloy_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.GEAR_NAQUADAH_REFINED.get())
                .group("jsg_core:gear_naquadah_refined")
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', CoreItemTags.INGOT_NAQUADAH_REFINED)
                .unlockedBy("has_naquadah_refined_ingot", has(CoreItemTags.INGOT_NAQUADAH_REFINED))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.GEAR_TITANIUM.get())
                .group("jsg_core:gear_titanium")
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', CoreItemTags.INGOT_TITANIUM)
                .unlockedBy("has_titanium_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.GEAR_TRINIUM.get())
                .group("jsg_core:gear_trinium")
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', CoreItemTags.INGOT_TRINIUM)
                .unlockedBy("has_trinium_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .save(pWriter);
        //Upgrades

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.CRYSTAL_UPGRADE_CAPACITY.get())
                .group("jsg_core:capacity_upgrade")
                .pattern("P P")
                .pattern("RCA")
                .pattern("PPP")
                .define('P', Tags.Items.GLASS_PANES_COLORLESS)
                .define('R', CoreItemTags.INGOT_NAQUADAH)
                .define('C', CoreItemTags.GEM_RED_SMALL)
                .define('A', CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_glass_panes", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_raw_naquadah_ingot", has(CoreItemTags.INGOT_NAQUADAH))
                .unlockedBy("has_small_red_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_naquadah_alloy_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.CRYSTAL_UPGRADE_EFFICIENCY.get())
                .group("jsg_core:efficiency_upgrade")
                .pattern("P P")
                .pattern("GCG")
                .pattern("PPP")
                .define('P', Tags.Items.GLASS_PANES_COLORLESS)
                .define('G', CoreItemTags.GEAR_TRINIUM)
                .define('C', CoreItemTags.GEM_WHITE)
                .unlockedBy("has_glass_panes", has(Tags.Items.GLASS_PANES_COLORLESS))
                .unlockedBy("has_teinium_gear", has(CoreItemTags.GEAR_TRINIUM))
                .unlockedBy("has_white_crystal", has(CoreItemTags.GEM_WHITE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.CRYSTAL_ENERGY_BASIC.get())
                .group("jsg_core:basic_energy_crystal")
                .pattern("SDC")
                .pattern("DID")
                .pattern("CDS")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('C', CoreItemTags.GEM_RED)
                .define('S', CoreItemTags.GEM_RED_SMALL)
                .define('I', CoreItems.CIRCUIT_CONTROL_NAQUADAH.get())
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED))
                .unlockedBy("has_small_red_crystal", has(CoreItemTags.GEM_RED_SMALL))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_circuit", has(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.CRYSTAL_ENERGY_ADVANCED.get())
                .group("jsg_core:advanced_energy_crystal")
                .pattern("SIB")
                .pattern("CEC")
                .pattern("BIS")
                .define('E', Items.ECHO_SHARD)
                .define('C', CoreItemTags.GEM_YELLOW)
                .define('S', CoreItemTags.GEM_YELLOW_SMALL)
                .define('I', CoreItems.CIRCUIT_CONTROL_CRYSTAL.get())
                .define('B', CoreItems.CRYSTAL_ENERGY_BASIC.get())
                .unlockedBy("has_yellow_crystal", has(CoreItemTags.GEM_YELLOW))
                .unlockedBy("has_small_yellow_crystal", has(CoreItemTags.GEM_YELLOW_SMALL))
                .unlockedBy("has_shard", has(Items.ECHO_SHARD))
                .unlockedBy("has_basic_energy_crystal", has(CoreItems.CRYSTAL_ENERGY_BASIC.get()))
                .unlockedBy("has_circuit", has(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.CRYSTAL_ENERGY_ULTIMATE.get())
                .group("jsg_core:ultimate_energy_crystal")
                .pattern("SNA")
                .pattern("CTC")
                .pattern("ANS")
                .define('T', Tags.Items.NETHER_STARS)
                .define('C', CoreItemTags.GEM_PEGASUS)
                .define('S', CoreItemTags.GEM_PEGASUS_SMALL)
                .define('N', Tags.Items.INGOTS_NETHERITE)
                .define('A', CoreItems.CRYSTAL_ENERGY_ADVANCED.get())
                .unlockedBy("has_pegasus_crystal", has(CoreItemTags.GEM_PEGASUS))
                .unlockedBy("has_small_pegasus_crystal", has(CoreItemTags.GEM_PEGASUS_SMALL))
                .unlockedBy("has_nether_star", has(Tags.Items.NETHER_STARS))
                .unlockedBy("has_advanced_energy_crystal", has(CoreItems.CRYSTAL_ENERGY_ADVANCED.get()))
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .save(pWriter);

        //Utils
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.BLACK_CHALK.get(), 2)
                .group("jsg_core:black_chalk")
                .requires(Items.WATER_BUCKET)
                .requires(CoreItems.CHARCOAL_STICK_MIXTURE.get(), 3)
                .requires(Items.PAPER)
                .unlockedBy("has_charcocal_mixture", has(CoreItems.CHARCOAL_STICK_MIXTURE.get()))
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.CRUSHED_CALCITE.get(), 3)
                .group("jsg_core:crushed_calcite")
                .requires(CoreItems.MORTAR_AND_PESTLE.get())
                .requires(Items.CALCITE)
                .unlockedBy("has_mortar_and_pestle", has(CoreItems.MORTAR_AND_PESTLE.get()))
                .unlockedBy("has_calcite", has(Items.CALCITE))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NOTEBOOK_PAGE_EMPTY.get())
                .group("jsg_core:notebook_page")
                .requires(Items.PAPER)
                .requires(Ingredient.of(Items.INK_SAC, Items.GLOW_INK_SAC))
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_ink_sac", has(Items.INK_SAC))
                .unlockedBy("has_glowing_ink_sac", has(Items.GLOW_INK_SAC))
                .unlockedBy("has_feather", has(Tags.Items.FEATHERS))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(pWriter, locationCorrection("notebook_page_empty"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NOTEBOOK_PAGE_EMPTY.get())
                .group("jsg_core:notebook_page")
                .requires(CoreItems.NOTEBOOK_PAGE_FILLED.get())
                .unlockedBy("has_notebook_page", has(CoreItems.NOTEBOOK_PAGE_FILLED.get()))
                .save(pWriter, locationCorrection("notebook_page_erase"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.PESTLE.get())
                .group("jsg_core:pestle")
                .pattern("# ")
                .pattern(" #")
                .define('#', Tags.Items.STONE)
                .unlockedBy("has_stone", has(Tags.Items.STONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.CIRCUIT_CONTROL_BASE.get())
                .group("jsg_core:circuit_base")
                .pattern("PYP")
                .pattern("BWE")
                .pattern("PRP")
                .define('P', CoreItemTags.PLATE_TITANIUM)
                .define('Y', CoreItemTags.GEM_YELLOW)
                .define('B', CoreItemTags.GEM_BLUE)
                .define('W', CoreItemTags.GEM_WHITE_SMALL)
                .define('E', CoreItemTags.GEM_ENDER)
                .define('R', CoreItemTags.GEM_RED)
                .unlockedBy("has_titanium_plate", has(CoreItemTags.PLATE_TITANIUM))
                .unlockedBy("has_yellow_crystal", has(CoreItemTags.GEM_YELLOW))
                .unlockedBy("has_blue_crystal", has(CoreItemTags.GEM_BLUE))
                .unlockedBy("has_small_white_crystal", has(CoreItemTags.GEM_WHITE_SMALL))
                .unlockedBy("has_ender_crystal", has(CoreItemTags.GEM_ENDER))
                .unlockedBy("has_red_crystal", has(CoreItemTags.GEM_RED))
                .save(pWriter);

        //Nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_RAW_NUGGET.get(), 9)
                .group("jsg_core:raw_naquadah_nugget")
                .requires(CoreItemTags.INGOT_NAQUADAH)
                .unlockedBy("has_raw_naqudah_ingot", has(CoreItemTags.INGOT_NAQUADAH))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_NUGGET.get(), 9)
                .group("jsg_core:naquadah_nugget")
                .requires(CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .unlockedBy("has_naqudah_ingot", has(CoreItemTags.INGOT_NAQUADAH_ALLOY))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_REFINED_NUGGET.get(), 9)
                .group("jsg_core:naquadah_refined_nugget")
                .requires(CoreItemTags.INGOT_NAQUADAH_REFINED)
                .unlockedBy("has_naqudah_refined_ingot", has(CoreItemTags.INGOT_NAQUADAH_REFINED))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TITANIUM_NUGGET.get(), 9)
                .group("jsg_core:titanium_nugget")
                .requires(CoreItemTags.INGOT_TITANIUM)
                .unlockedBy("has_titanium_ingot", has(CoreItemTags.INGOT_TITANIUM))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TRINIUM_NUGGET.get(), 9)
                .group("jsg_core:trinium_nugget")
                .requires(CoreItemTags.INGOT_TRINIUM)
                .unlockedBy("has_trinium_ingot", has(CoreItemTags.INGOT_TRINIUM))
                .save(pWriter);

        //Ingots
        //from blocks
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.COPPER_INGOT_EXPOSED.get(), 9)
                .group("jsg_core:copper_ingot_exposed")
                .requires(Ingredient.of(Items.EXPOSED_COPPER, Items.WAXED_EXPOSED_COPPER))
                .unlockedBy("has_exposed_copper_block", has(Items.EXPOSED_COPPER))
                .unlockedBy("has_waxed_exposed_copper_block", has(Items.WAXED_EXPOSED_COPPER))
                .save(pWriter, locationCorrection("exposed_copper_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.COPPER_INGOT_WEATHERED.get(), 9)
                .group("jsg_core:copper_ingot_weathered")
                .requires(Ingredient.of(Items.WEATHERED_COPPER, Items.WAXED_WEATHERED_COPPER))
                .unlockedBy("has_weathered_copper_block", has(Items.WEATHERED_COPPER))
                .unlockedBy("has_waxed_weathered_copper_block", has(Items.WAXED_WEATHERED_COPPER))
                .save(pWriter, locationCorrection("weathered_copper_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.COPPER_INGOT_OXIDIZED.get(), 9)
                .group("jsg_core:copper_ingot_oxidized")
                .requires(Ingredient.of(Items.OXIDIZED_COPPER, Items.WAXED_OXIDIZED_COPPER))
                .unlockedBy("has_oxidized_copper_block", has(Items.OXIDIZED_COPPER))
                .unlockedBy("has_waxed_oxidized_copper_block", has(Items.WAXED_OXIDIZED_COPPER))
                .save(pWriter, locationCorrection("oxidized_copper_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_RAW.get(), 9)
                .group("jsg_core:raw_naquadah_ingot")
                .requires(CoreItemTags.STORAGE_BLOCK_NAQUADAH)
                .unlockedBy("has_raw_naquadah_block", has(CoreItemTags.STORAGE_BLOCK_NAQUADAH))
                .save(pWriter, locationCorrection("raw_naquadah_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY.get(), 9)
                .group("jsg_core:naquadah_alloy_ingot")
                .requires(CoreItemTags.STORAGE_BLOCK_NAQUADAH_ALLOY)
                .unlockedBy("has_naquadah_alloy_block", has(CoreItemTags.STORAGE_BLOCK_NAQUADAH_ALLOY))
                .save(pWriter, locationCorrection("naquadah_alloy_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_REFINED.get(), 9)
                .group("jsg_core:naquadah_refined_ingot")
                .requires(CoreItemTags.STORAGE_BLOCK_NAQUADAH_REFINED)
                .unlockedBy("has_naquadah_refined_block", has(CoreItemTags.STORAGE_BLOCK_NAQUADAH_REFINED))
                .save(pWriter, locationCorrection("naquadah_refined_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TITANIUM_INGOT.get(), 9)
                .group("jsg_core:titanium_ingot")
                .requires(CoreItemTags.STORAGE_BLOCK_TITANIUM)
                .unlockedBy("has_raw_titanium_block", has(CoreItemTags.STORAGE_BLOCK_TITANIUM))
                .save(pWriter, locationCorrection("titanium_block_to_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CoreItems.TRINIUM_INGOT.get(), 9)
                .group("jsg_core:trinium_ingot")
                .requires(CoreItemTags.STORAGE_BLOCK_TRINIUM)
                .unlockedBy("has_raw_trinium_block", has(CoreItemTags.STORAGE_BLOCK_TRINIUM))
                .save(pWriter, locationCorrection("trinium_block_to_ingot"));

        //from nuggets
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_RAW.get())
                .group("jsg_core:raw_naquadah_ingot")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.NUGGET_NAQUADAH)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_NAQUADAH))
                .save(pWriter, locationCorrection("raw_naquadah_nugget_to_ingot"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY.get())
                .group("jsg_core:naquadah_alloy_ingot")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.NUGGET_NAQUADAH_ALLOY)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_NAQUADAH_ALLOY))
                .save(pWriter, locationCorrection("naquadah_alloy_nugget_to_ingot"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_REFINED.get())
                .group("jsg_core:naquadah_refined_ingot")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.NUGGET_NAQUADAH_REFINED)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_NAQUADAH_REFINED))
                .save(pWriter, locationCorrection("naquadah_refined_nugget_to_ingot"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.TITANIUM_INGOT.get())
                .group("jsg_core:titanium_ingot")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.NUGGET_TITANIUM)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TITANIUM))
                .save(pWriter, locationCorrection("titanium_nugget_to_ingot"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CoreItems.TRINIUM_INGOT.get())
                .group("jsg_core:trinium_ingot")
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CoreItemTags.NUGGET_TRINIUM)
                .unlockedBy("has_nugget", has(CoreItemTags.NUGGET_TRINIUM))
                .save(pWriter, locationCorrection("trinium_nugget_to_ingot"));

        //Smelting
        oreSmelting(pWriter, NAQUADAH_ORES, RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_RAW.get(), 0.4f, 200, "jsg_core:raw_naquadah_ingot");
        oreSmelting(pWriter, TITANIUM_ORES, RecipeCategory.MISC, CoreItems.TITANIUM_INGOT.get(), 0.2f, 200, "jsg_core:titanium_ingot");
        oreSmelting(pWriter, TRINIUM_ORES, RecipeCategory.MISC, CoreItems.TRINIUM_INGOT.get(), 0.5f, 200, "jsg_core:trinium_ingot");

        oreSmelting(pWriter, List.of(CoreItems.NAQUADAH_RAW_DUST.get()), RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_RAW.get(), 0.1f, 200, "jsg_core:raw_naquadah_ingot");
        oreSmelting(pWriter, List.of(CoreItems.NAQUADAH_DUST.get()), RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY.get(), 0.1f, 200, "jsg_core:naquadah_alloy_ingot");
        oreSmelting(pWriter, List.of(CoreItems.NAQUADAH_REFINED_DUST.get()), RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_REFINED.get(), 0.1f, 200, "jsg_core:naquadah_refined_ingot");
        oreSmelting(pWriter, List.of(CoreItems.TITANIUM_DUST.get()), RecipeCategory.MISC, CoreItems.TITANIUM_INGOT.get(), 0.1f, 200, "jsg_core:titanium_ingot");
        oreSmelting(pWriter, List.of(CoreItems.TRINIUM_DUST.get()), RecipeCategory.MISC, CoreItems.TRINIUM_INGOT.get(), 0.1f, 200, "jsg_core:trinium_ingot");

        //Blasting
        oreBlasting(pWriter, NAQUADAH_ORES, RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_RAW.get(), 0.4f, 100, "jsg_core:raw_naquadah_ingot");
        oreBlasting(pWriter, TITANIUM_ORES, RecipeCategory.MISC, CoreItems.TITANIUM_INGOT.get(), 0.2f, 100, "jsg_core:titanium_ingot");
        oreBlasting(pWriter, TRINIUM_ORES, RecipeCategory.MISC, CoreItems.TRINIUM_INGOT.get(), 0.5f, 100, "jsg_core:trinium_ingot");

        oreBlasting(pWriter, List.of(CoreItems.NAQUADAH_RAW_DUST.get()), RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_RAW.get(), 0.1f, 100, "jsg_core:raw_naquadah_ingot");
        oreBlasting(pWriter, List.of(CoreItems.NAQUADAH_DUST.get()), RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY.get(), 0.1f, 100, "jsg_core:naquadah_alloy_ingot");
        oreBlasting(pWriter, List.of(CoreItems.NAQUADAH_REFINED_DUST.get()), RecipeCategory.MISC, CoreItems.NAQUADAH_ALLOY_REFINED.get(), 0.1f, 100, "jsg_core:naquadah_refined_ingot");
        oreBlasting(pWriter, List.of(CoreItems.TITANIUM_DUST.get()), RecipeCategory.MISC, CoreItems.TITANIUM_INGOT.get(), 0.1f, 100, "jsg_core:titanium_ingot");
        oreBlasting(pWriter, List.of(CoreItems.TRINIUM_DUST.get()), RecipeCategory.MISC, CoreItems.TRINIUM_INGOT.get(), 0.1f, 100, "jsg_core:trinium_ingot");
    }

    public static final List<ItemLike> NAQUADAH_ORES = Stream.concat(
                    CoreBlocks.ORE_NAQUADAH.values().stream().map(RegistryObject::get),
                    Stream.of(CoreItems.NAQUADAH_ORE_RAW.get()))
            .map(ItemLike.class::cast)
            .toList();

    public static final List<ItemLike> TITANIUM_ORES = Stream.concat(
                    CoreBlocks.ORE_TITANIUM.values().stream().map(RegistryObject::get),
                    Stream.of(CoreItems.TITANIUM_ORE_RAW.get()))
            .map(ItemLike.class::cast)
            .toList();

    public static final List<ItemLike> TRINIUM_ORES = Stream.concat(
                    CoreBlocks.ORE_TRINIUM.values().stream().map(RegistryObject::get),
                    Stream.of(CoreItems.TRINIUM_ORE_RAW.get()))
            .map(ItemLike.class::cast)
            .toList();

    private ResourceLocation locationCorrection(String name) {
        return JSGMapping.rl(JSGCore.MOD_ID, name);
    }

    @ParametersAreNonnullByDefault
    protected static void oreSmelting(net.minecraft.data.recipes.RecipeOutput pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    @ParametersAreNonnullByDefault
    protected static void oreBlasting(net.minecraft.data.recipes.RecipeOutput pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    @ParametersAreNonnullByDefault
    protected static void oreCooking(net.minecraft.data.recipes.RecipeOutput pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, JSGCore.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
