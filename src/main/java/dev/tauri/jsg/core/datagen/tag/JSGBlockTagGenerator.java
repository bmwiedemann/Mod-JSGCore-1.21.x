package dev.tauri.jsg.core.datagen.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.crystal.CrystalColor;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.helper.builder.block.OreBlockVariant;
import dev.tauri.jsg.core.common.registry.tag.CoreBlockTags;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class JSGBlockTagGenerator extends BlockTagsProvider {
    public JSGBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, JSGCore.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(CoreBlockTags.FLUID_CAULDRON_HEATING)
                .add(Blocks.LAVA, Blocks.FIRE, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);

        // Cartouches
        Util.make(tag(CoreBlockTags.CARTOUCHES), (tag) ->
                CoreBlocks.CARTOUCHES.forEach((type, variants) ->
                        variants.forEach((variant, block) ->
                                tag.add(block.get()))));

        // Minecraft
        // Mining tags
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CoreBlocks.BRAZIER_COAL.get())
                .addTag(CoreBlockTags.CRYSTAL_BUDS)
                .addTag(CoreBlockTags.ORES)
                .addTag(CoreBlockTags.STORAGE_BLOCKS)
                .addTag(CoreBlockTags.BUDDING)
                .addTag(CoreBlockTags.UNSTABLE_BUDDING)
                .addTag(CoreBlockTags.CARTOUCHES);

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(CoreBlockTags.CARTOUCHES);

        tag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(CoreBlockTags.ORE_NAQUADAH)
                .addTag(CoreBlockTags.ORE_TITANIUM)
                .addTag(CoreBlockTags.STORAGE_RAW_NAQUADAH)
                .addTag(CoreBlockTags.STORAGE_RAW_TITANIUM)
                .addTag(CoreBlockTags.STORAGE_NAQUADAH)
                .addTag(CoreBlockTags.STORAGE_NAQUADAH_ALLOY)
                .addTag(CoreBlockTags.STORAGE_TITANIUM);

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .addTag(CoreBlockTags.ORE_TRINIUM)
                .addTag(CoreBlockTags.STORAGE_RAW_TITANIUM)
                .addTag(CoreBlockTags.STORAGE_TRINIUM)
                .addTag(CoreBlockTags.STORAGE_REFINED_NAQUADAH);

        //Immunity Tags
        tag(BlockTags.DRAGON_IMMUNE).addTag(CoreBlockTags.BOSS_IMMUNE);
        tag(BlockTags.WITHER_IMMUNE).addTag(CoreBlockTags.BOSS_IMMUNE);

        tag(BlockTags.INVALID_SPAWN_INSIDE)
                .add(CoreBlocks.INVISIBLE_BLOCK.get());

        tag(BlockTags.CRYSTAL_SOUND_BLOCKS)
                .addTag(CoreBlockTags.BUDDING)
                .addTag(CoreBlockTags.CRYSTAL_BLOCKS);

        Util.make(tag(BlockTags.INSIDE_STEP_SOUND_BLOCKS), (tag) ->
                CoreBlocks.CRYSTAL_BUD_SMALL.forEach((color, bud) -> tag.add(bud.get())));

        tag(BlockTags.VIBRATION_RESONATORS).addTag(CoreBlockTags.CRYSTAL_BLOCKS);

        tag(BlockTags.INFINIBURN_OVERWORLD).add(CoreBlocks.BRAZIER_COAL.get());

        //Beacon blocks Tags
        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(CoreBlocks.NAQUADAH_BLOCK.get())
                .add(CoreBlocks.NAQUADAH_REFINED_BLOCK.get())
                .add(CoreBlocks.TITANIUM_BLOCK.get())
                .add(CoreBlocks.TRINIUM_BLOCK.get());

        // buds
        Util.make(tag(CoreBlockTags.CRYSTAL_BUDS), (tag) -> {
            CoreBlocks.CRYSTAL_BUD_SMALL.forEach((color, bud) -> tag.add(bud.get()));
            CoreBlocks.CRYSTAL_BUD_MEDIUM.forEach((color, bud) -> tag.add(bud.get()));
            CoreBlocks.CRYSTAL_BUD_LARGE.forEach((color, bud) -> tag.add(bud.get()));
            CoreBlocks.CRYSTAL_CLUSTER.forEach((color, bud) -> tag.add(bud.get()));
        });

        // buddings
        Util.make(tag(CoreBlockTags.BUDDING), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        buddings.forEach((color, budding) -> tag.add(budding.get()))));

        Util.make(tag(CoreBlockTags.UNSTABLE_BUDDING), (tag) ->
                CoreBlocks.UNSTABLE_CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        buddings.forEach((color, budding) -> tag.add(budding.get()))));

        Util.make(tag(CoreBlockTags.BUDDINGS_BLUE), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        tag.add(buddings.get(CrystalColor.BLUE).get())));
        Util.make(tag(CoreBlockTags.BUDDINGS_ENDER), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        tag.add(buddings.get(CrystalColor.ENDER).get())));
        Util.make(tag(CoreBlockTags.BUDDINGS_PEGASUS), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        tag.add(buddings.get(CrystalColor.PEGASUS).get())));
        Util.make(tag(CoreBlockTags.BUDDINGS_RED), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        tag.add(buddings.get(CrystalColor.RED).get())));
        Util.make(tag(CoreBlockTags.BUDDINGS_WHITE), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        tag.add(buddings.get(CrystalColor.WHITE).get())));
        Util.make(tag(CoreBlockTags.BUDDINGS_YELLOW), (tag) ->
                CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) ->
                        tag.add(buddings.get(CrystalColor.YELLOW).get())));

        // boss immune
        tag(CoreBlockTags.BOSS_IMMUNE)
                .add(CoreBlocks.INVISIBLE_BLOCK.get());

        // lemon
        tag(CoreBlockTags.SUPPORT_LEMON);


        // ores
        tag(CoreBlockTags.ORE_RATES_SINGULAR)
                .addTag(CoreBlockTags.ORE_NAQUADAH)
                .addTag(CoreBlockTags.ORE_TITANIUM)
                .addTag(CoreBlockTags.ORE_TRINIUM);

        Util.make(tag(CoreBlockTags.ORE_NAQUADAH), (tag) ->
                CoreBlocks.ORE_NAQUADAH.forEach((variant, ore) -> tag.add(ore.get())));

        Util.make(tag(CoreBlockTags.ORE_TITANIUM), (tag) ->
                CoreBlocks.ORE_TITANIUM.forEach((variant, ore) -> tag.add(ore.get())));

        Util.make(tag(CoreBlockTags.ORE_TRINIUM), (tag) ->
                CoreBlocks.ORE_TRINIUM.forEach((variant, ore) -> tag.add(ore.get())));

        Util.make(tag(CoreBlockTags.ORE_IN_GROUND_DEEPSLATE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.DEEPSLATE).get());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.DEEPSLATE).get());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.DEEPSLATE).get());
        });

        Util.make(tag(CoreBlockTags.ORE_IN_GROUND_ENDSTONE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.END).get());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.END).get());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.END).get());
        });

        Util.make(tag(CoreBlockTags.ORE_IN_GROUND_NETHERRACK), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.NETHER).get());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.NETHER).get());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.NETHER).get());
        });

        Util.make(tag(CoreBlockTags.ORE_IN_GROUND_SANDSTONE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.SAND).get());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.SAND).get());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.SAND).get());
        });

        Util.make(tag(CoreBlockTags.ORE_IN_GROUND_STONE), (tag) -> {
            tag.add(CoreBlocks.ORE_NAQUADAH.get(OreBlockVariant.STONE).get());
            tag.add(CoreBlocks.ORE_TITANIUM.get(OreBlockVariant.STONE).get());
            tag.add(CoreBlocks.ORE_TRINIUM.get(OreBlockVariant.STONE).get());
        });

        tag(CoreBlockTags.ORES)
                .addTag(CoreBlockTags.ORE_NAQUADAH)
                .addTag(CoreBlockTags.ORE_TITANIUM)
                .addTag(CoreBlockTags.ORE_TRINIUM);

        //Storage Blocks tags

        tag(CoreBlockTags.STORAGE_BLUE_CRYSTAL)
                .add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.BLUE).get());

        tag(CoreBlockTags.STORAGE_ENDER_CRYSTAL)
                .add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.ENDER).get());

        tag(CoreBlockTags.STORAGE_PEGASUS_CRYSTAL)
                .add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.PEGASUS).get());

        tag(CoreBlockTags.STORAGE_RED_CRYSTAL)
                .add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.RED).get());

        tag(CoreBlockTags.STORAGE_WHITE_CRYSTAL)
                .add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.WHITE).get());

        tag(CoreBlockTags.STORAGE_YELLOW_CRYSTAL)
                .add(CoreBlocks.CRYSTAL_BLOCK.get(CrystalColor.YELLOW).get());

        tag(CoreBlockTags.STORAGE_RAW_NAQUADAH)
                .add(CoreBlocks.RAW_ORE_NAQUADAH_BLOCK.get());

        tag(CoreBlockTags.STORAGE_RAW_TITANIUM)
                .add(CoreBlocks.RAW_ORE_TITANIUM_BLOCK.get());

        tag(CoreBlockTags.STORAGE_RAW_TRINIUM)
                .add(CoreBlocks.RAW_ORE_TRINIUM_BLOCK.get());

        tag(CoreBlockTags.STORAGE_NAQUADAH)
                .add(CoreBlocks.NAQUADAH_RAW_BLOCK.get());

        tag(CoreBlockTags.STORAGE_NAQUADAH_ALLOY)
                .add(CoreBlocks.NAQUADAH_BLOCK.get());

        tag(CoreBlockTags.STORAGE_REFINED_NAQUADAH)
                .add(CoreBlocks.NAQUADAH_REFINED_BLOCK.get());

        tag(CoreBlockTags.STORAGE_TITANIUM)
                .add(CoreBlocks.TITANIUM_BLOCK.get());

        tag(CoreBlockTags.STORAGE_TRINIUM)
                .add(CoreBlocks.TRINIUM_BLOCK.get());

        Util.make(tag(CoreBlockTags.CRYSTAL_BLOCKS), (tag) ->
                CoreBlocks.CRYSTAL_BLOCK.forEach((color, crystal) -> tag.add(crystal.get())));

        tag(CoreBlockTags.STORAGE_BLOCKS)
                .addTag(CoreBlockTags.CRYSTAL_BLOCKS)
                .addTag(CoreBlockTags.STORAGE_RAW_NAQUADAH)
                .addTag(CoreBlockTags.STORAGE_RAW_TITANIUM)
                .addTag(CoreBlockTags.STORAGE_RAW_TRINIUM)
                .addTag(CoreBlockTags.STORAGE_NAQUADAH)
                .addTag(CoreBlockTags.STORAGE_NAQUADAH_ALLOY)
                .addTag(CoreBlockTags.STORAGE_REFINED_NAQUADAH)
                .addTag(CoreBlockTags.STORAGE_TITANIUM)
                .addTag(CoreBlockTags.STORAGE_TRINIUM);
    }
}
