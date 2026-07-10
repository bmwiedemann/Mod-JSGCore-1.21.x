package dev.tauri.jsg.core.datagen.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.crystal.CrystalColor;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.helper.builder.block.OreBlockVariant;
import dev.tauri.jsg.core.common.registry.tag.CoreItemTags;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JSGItemTagGenerator extends ItemTagsProvider {
    public JSGItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, JSGCore.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(CoreItemTags.FLUID_CAULDRON_HEATING)
                .add(Items.LAVA_BUCKET, Items.CAMPFIRE, Items.SOUL_CAMPFIRE);

        tag(ItemTags.BEACON_PAYMENT_ITEMS)
                .add(CoreItems.NAQUADAH_ALLOY.get())
                .add(CoreItems.NAQUADAH_ALLOY_REFINED.get())
                .add(CoreItems.TITANIUM_INGOT.get())
                .add(CoreItems.TRINIUM_INGOT.get());

        //Crystallization boosters
        tag(CoreItemTags.CRYSTAL_FERTILIZERS)
                .add(CoreItems.CRUSHED_CALCITE.get());

        //Biome overlay
        for (var overlay : BiomeOverlayInstance.values()) {
            Util.make(tag(Objects.requireNonNull(overlay.getOverlayItemsTag())), (tag) -> {
                if (overlay == CoreBiomeOverlays.NORMAL.get()) {
                    tag.add(Blocks.STONE.asItem());
                    return;
                }
                if (overlay == CoreBiomeOverlays.AGED.get()) {
                    tag.add(Blocks.COBBLESTONE.asItem());
                    return;
                }
                if (overlay == CoreBiomeOverlays.FROST.get()) {
                    tag.add(Blocks.ICE.asItem());
                    tag.add(Blocks.PACKED_ICE.asItem());
                    tag.add(Blocks.BLUE_ICE.asItem());
                    return;
                }
                if (overlay == CoreBiomeOverlays.MOSSY.get()) {
                    tag.add(Blocks.VINE.asItem());
                    return;
                }
                if (overlay == CoreBiomeOverlays.SOOTY.get()) {
                    tag.add(Blocks.COAL_BLOCK.asItem());
                }
            });
        }

        //Tools
        tag(CoreItemTags.TOOL_WRENCH).add(CoreItems.JSG_WRENCH.get());

        // Dusts
        tag(CoreItemTags.DUST_NAQUADAH_RAW).add(CoreItems.NAQUADAH_RAW_DUST.get());
        tag(CoreItemTags.DUST_NAQUADAH_ALLOY).add(CoreItems.NAQUADAH_DUST.get());
        tag(CoreItemTags.DUST_NAQUADAH_REFINED).add(CoreItems.NAQUADAH_REFINED_DUST.get());
        tag(CoreItemTags.DUST_TITANIUM).add(CoreItems.TITANIUM_DUST.get());
        tag(CoreItemTags.DUST_TRINIUM).add(CoreItems.TRINIUM_DUST.get());
        tag(CoreItemTags.DUSTS)
                .addTag(CoreItemTags.DUST_NAQUADAH_RAW)
                .addTag(CoreItemTags.DUST_NAQUADAH_ALLOY)
                .addTag(CoreItemTags.DUST_NAQUADAH_REFINED)
                .addTag(CoreItemTags.DUST_TITANIUM)
                .addTag(CoreItemTags.DUST_TRINIUM);

        //Gears
        tag(CoreItemTags.GEAR_NAQUADAH).add(CoreItems.GEAR_NAQUADAH_RAW.get());
        tag(CoreItemTags.GEAR_NAQUADAH_ALLOY).add(CoreItems.GEAR_NAQUADAH.get());
        tag(CoreItemTags.GEAR_NAQUADAH_REFINED).add(CoreItems.GEAR_NAQUADAH_REFINED.get());
        tag(CoreItemTags.GEAR_TITANIUM).add(CoreItems.GEAR_TITANIUM.get());
        tag(CoreItemTags.GEAR_TRINIUM).add(CoreItems.GEAR_TRINIUM.get());
        tag(CoreItemTags.GEARS)
                .addTag(CoreItemTags.GEAR_NAQUADAH)
                .addTag(CoreItemTags.GEAR_NAQUADAH_ALLOY)
                .addTag(CoreItemTags.GEAR_NAQUADAH_REFINED)
                .addTag(CoreItemTags.GEAR_TITANIUM)
                .addTag(CoreItemTags.GEAR_TRINIUM);

        //Gems
        tag(CoreItemTags.GEM_BLUE).add(CoreItems.CRYSTAL_BLUE.get());
        tag(CoreItemTags.GEM_BLUE_SMALL).add(CoreItems.CRYSTAL_BLUE_SMALL.get());
        tag(CoreItemTags.GEM_ENDER).add(CoreItems.CRYSTAL_ENDER.get());
        tag(CoreItemTags.GEM_ENDER_SMALL).add(CoreItems.CRYSTAL_ENDER_SMALL.get());
        tag(CoreItemTags.GEM_PEGASUS).add(CoreItems.CRYSTAL_PEGASUS.get());
        tag(CoreItemTags.GEM_PEGASUS_SMALL).add(CoreItems.CRYSTAL_PEGASUS_SMALL.get());
        tag(CoreItemTags.GEM_RED).add(CoreItems.CRYSTAL_RED.get());
        tag(CoreItemTags.GEM_RED_SMALL).add(CoreItems.CRYSTAL_RED_SMALL.get());
        tag(CoreItemTags.GEM_WHITE).add(CoreItems.CRYSTAL_WHITE.get());
        tag(CoreItemTags.GEM_WHITE_SMALL).add(CoreItems.CRYSTAL_WHITE_SMALL.get());
        tag(CoreItemTags.GEM_YELLOW).add(CoreItems.CRYSTAL_YELLOW.get());
        tag(CoreItemTags.GEM_YELLOW_SMALL).add(CoreItems.CRYSTAL_YELLOW_SMALL.get());
        tag(CoreItemTags.GEMS)
                .addTag(CoreItemTags.GEM_BLUE)
                .addTag(CoreItemTags.GEM_ENDER)
                .addTag(CoreItemTags.GEM_PEGASUS)
                .addTag(CoreItemTags.GEM_RED)
                .addTag(CoreItemTags.GEM_WHITE)
                .addTag(CoreItemTags.GEM_YELLOW)
                .addTag(CoreItemTags.GEM_BLUE_SMALL)
                .addTag(CoreItemTags.GEM_ENDER_SMALL)
                .addTag(CoreItemTags.GEM_PEGASUS_SMALL)
                .addTag(CoreItemTags.GEM_RED_SMALL)
                .addTag(CoreItemTags.GEM_WHITE_SMALL)
                .addTag(CoreItemTags.GEM_YELLOW_SMALL);

        //Ingots
        tag(CoreItemTags.INGOT_COPPER_EXPOSED).add(CoreItems.COPPER_INGOT_EXPOSED.get());
        tag(CoreItemTags.INGOT_COPPER_OXIDIZED).add(CoreItems.COPPER_INGOT_OXIDIZED.get());
        tag(CoreItemTags.INGOT_COPPER_WEATHERED).add(CoreItems.COPPER_INGOT_WEATHERED.get());
        tag(CoreItemTags.INGOT_NAQUADAH).add(CoreItems.NAQUADAH_ALLOY_RAW.get());
        tag(CoreItemTags.INGOT_NAQUADAH_ALLOY).add(CoreItems.NAQUADAH_ALLOY.get());
        tag(CoreItemTags.INGOT_NAQUADAH_REFINED).add(CoreItems.NAQUADAH_ALLOY_REFINED.get());
        tag(CoreItemTags.INGOT_TITANIUM).add(CoreItems.TITANIUM_INGOT.get());
        tag(CoreItemTags.INGOT_TRINIUM).add(CoreItems.TRINIUM_INGOT.get());
        tag(CoreItemTags.INGOTS)
                .addTag(CoreItemTags.INGOT_COPPER_EXPOSED)
                .addTag(CoreItemTags.INGOT_COPPER_OXIDIZED)
                .addTag(CoreItemTags.INGOT_COPPER_WEATHERED)
                .addTag(CoreItemTags.INGOT_NAQUADAH)
                .addTag(CoreItemTags.INGOT_NAQUADAH_ALLOY)
                .addTag(CoreItemTags.INGOT_NAQUADAH_REFINED)
                .addTag(CoreItemTags.INGOT_TITANIUM)
                .addTag(CoreItemTags.INGOT_TRINIUM);

        //Nuggets
        tag(CoreItemTags.NUGGET_NAQUADAH).add(CoreItems.NAQUADAH_RAW_NUGGET.get());
        tag(CoreItemTags.NUGGET_NAQUADAH_ALLOY).add(CoreItems.NAQUADAH_NUGGET.get());
        tag(CoreItemTags.NUGGET_NAQUADAH_REFINED).add(CoreItems.NAQUADAH_REFINED_NUGGET.get());
        tag(CoreItemTags.NUGGET_TITANIUM).add(CoreItems.TITANIUM_NUGGET.get());
        tag(CoreItemTags.NUGGET_TRINIUM).add(CoreItems.TRINIUM_NUGGET.get());
        tag(CoreItemTags.NUGGETS)
                .addTag(CoreItemTags.NUGGET_NAQUADAH)
                .addTag(CoreItemTags.NUGGET_NAQUADAH_ALLOY)
                .addTag(CoreItemTags.NUGGET_NAQUADAH_REFINED)
                .addTag(CoreItemTags.NUGGET_TITANIUM)
                .addTag(CoreItemTags.NUGGET_TRINIUM);


        //Ores and Ores related tags

        Util.make(tag(CoreItemTags.ORE_NAQUADAH), (tag) ->
                CoreBlocks.ORE_NAQUADAH.forEach((variant, ore) -> tag.add(ore.get().asItem())));
        Util.make(tag(CoreItemTags.ORE_TITANIUM), (tag) ->
                CoreBlocks.ORE_TITANIUM.forEach((variant, ore) -> tag.add(ore.get().asItem())));
        Util.make(tag(CoreItemTags.ORE_TRINIUM), (tag) ->
                CoreBlocks.ORE_TRINIUM.forEach((variant, ore) -> tag.add(ore.get().asItem())));
        tag(CoreItemTags.ORES)
                .addTag(CoreItemTags.ORE_NAQUADAH)
                .addTag(CoreItemTags.ORE_TITANIUM)
                .addTag(CoreItemTags.ORE_TRINIUM);

        tag(CoreItemTags.ORE_RATES_SINGULAR).addTag(CoreItemTags.ORES);

        Util.make(tag(CoreItemTags.ORE_IN_GROUND_DEEPSLATE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.DEEPSLATE).get().asItem());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.DEEPSLATE).get().asItem());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.DEEPSLATE).get().asItem());
        });
        Util.make(tag(CoreItemTags.ORE_IN_GROUND_ENDSTONE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.END).get().asItem());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.END).get().asItem());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.END).get().asItem());
        });
        Util.make(tag(CoreItemTags.ORE_IN_GROUND_NETHERRACK), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.NETHER).get().asItem());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.NETHER).get().asItem());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.NETHER).get().asItem());
        });
        Util.make(tag(CoreItemTags.ORE_IN_GROUND_SANDSTONE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.SAND).get().asItem());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.SAND).get().asItem());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.SAND).get().asItem());
        });
        Util.make(tag(CoreItemTags.ORE_IN_GROUND_STONE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.STONE).get().asItem());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.STONE).get().asItem());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.STONE).get().asItem());
        });

        //Plates
        tag(CoreItemTags.PLATE_NAQUADAH).add(CoreItems.PLATE_NAQUADAH_RAW.get());
        tag(CoreItemTags.PLATE_NAQUADAH_ALLOY).add(CoreItems.PLATE_NAQUADAH.get());
        tag(CoreItemTags.PLATE_NAQUADAH_REFINED).add(CoreItems.PLATE_NAQUADAH_REFINED.get());
        tag(CoreItemTags.PLATE_TITANIUM).add(CoreItems.PLATE_TITANIUM.get());
        tag(CoreItemTags.PLATE_TRINIUM).add(CoreItems.PLATE_TRINIUM.get());
        tag(CoreItemTags.PLATES)
                .addTag(CoreItemTags.PLATE_NAQUADAH)
                .addTag(CoreItemTags.PLATE_NAQUADAH_ALLOY)
                .addTag(CoreItemTags.PLATE_NAQUADAH_REFINED)
                .addTag(CoreItemTags.PLATE_TITANIUM)
                .addTag(CoreItemTags.PLATE_TRINIUM);

        //Raw materials - raw ores
        tag(CoreItemTags.RAW_MATERIAL_NAQUADAH).add(CoreItems.NAQUADAH_ORE_RAW.get());
        tag(CoreItemTags.RAW_MATERIAL_TITANIUM).add(CoreItems.TITANIUM_ORE_RAW.get());
        tag(CoreItemTags.RAW_MATERIAL_TRINIUM).add(CoreItems.TRINIUM_ORE_RAW.get());
        tag(CoreItemTags.RAW_MATERIALS)
                .addTag(CoreItemTags.RAW_MATERIAL_NAQUADAH)
                .addTag(CoreItemTags.RAW_MATERIAL_TITANIUM)
                .addTag(CoreItemTags.RAW_MATERIAL_TRINIUM);

        //Storage Blocks
        tag(CoreItemTags.STORAGE_BLOCK_CRYSTAL_BLUE).add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.BLUE).get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_CRYSTAL_ENDER).add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.ENDER).get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_CRYSTAL_PEGASUS).add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.PEGASUS).get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_CRYSTAL_RED).add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.RED).get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_CRYSTAL_WHITE).add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.WHITE).get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_CRYSTAL_YELLOW).add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.YELLOW).get().asItem());
        Util.make(tag(CoreItemTags.CRYSTAL_BLOCKS), (tag) ->
                CoreBlocks.CRYSTAL_BLOCK.forEach((color, crystal) -> tag.add(crystal.get().asItem())));
        tag(CoreItemTags.STORAGE_BLOCK_RAW_ORE_NAQUADAH).add(CoreBlocks.RAW_ORE_NAQUADAH_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TITANIUM).add(CoreBlocks.RAW_ORE_TITANIUM_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TRINIUM).add(CoreBlocks.RAW_ORE_TRINIUM_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_NAQUADAH).add(CoreBlocks.NAQUADAH_RAW_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_NAQUADAH_ALLOY).add(CoreBlocks.NAQUADAH_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_NAQUADAH_REFINED).add(CoreBlocks.NAQUADAH_REFINED_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_TITANIUM).add(CoreBlocks.TITANIUM_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCK_TRINIUM).add(CoreBlocks.TRINIUM_BLOCK.get().asItem());
        tag(CoreItemTags.STORAGE_BLOCKS)
                .addTag(CoreItemTags.CRYSTAL_BLOCKS)
                .addTag(CoreItemTags.STORAGE_BLOCK_NAQUADAH)
                .addTag(CoreItemTags.STORAGE_BLOCK_NAQUADAH_ALLOY)
                .addTag(CoreItemTags.STORAGE_BLOCK_NAQUADAH_REFINED)
                .addTag(CoreItemTags.STORAGE_BLOCK_TITANIUM)
                .addTag(CoreItemTags.STORAGE_BLOCK_TRINIUM)
                .addTag(CoreItemTags.STORAGE_BLOCK_RAW_ORE_NAQUADAH)
                .addTag(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TITANIUM)
                .addTag(CoreItemTags.STORAGE_BLOCK_RAW_ORE_TRINIUM);
    }
}
