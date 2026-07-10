package dev.tauri.jsg.core.datagen.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.registry.tag.CoreBiomeTags;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JSGBiomeTagGenerator extends BiomeTagsProvider {
    public JSGBiomeTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, JSGCore.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        // biome overlays
        for (var overlay : BiomeOverlayInstance.values()) {
            Util.make(tag(Objects.requireNonNull(overlay.getOverlayBiomesTag())), (tag) -> {
                if (overlay == CoreBiomeOverlays.NORMAL.get()) {
                    tag.addTag(CoreBiomeTags.IS_TEMPERATE);
                    return;
                }
                if (overlay == CoreBiomeOverlays.FROST.get()) {
                    tag.addTag(CoreBiomeTags.IS_COLD);
                    return;
                }
                if (overlay == CoreBiomeOverlays.AGED.get()) {
                    tag.addTag(CoreBiomeTags.IS_SANDY).addTag(CoreBiomeTags.HAS_DRIPSTONE);
                    return;
                }
                if (overlay == CoreBiomeOverlays.MOSSY.get()) {
                    tag.addTag(CoreBiomeTags.IS_WET);
                    return;
                }
                if (overlay == CoreBiomeOverlays.SOOTY.get()) {
                    tag.addTag(CoreBiomeTags.IS_NETHER).addTag(CoreBiomeTags.IS_WARPED);
                }
            });
        }

        // generic
        tag(CoreBiomeTags.IS_BADLANDS).add(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS);
        tag(CoreBiomeTags.IS_COLD).add(
                Biomes.FROZEN_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN,
                Biomes.FROZEN_RIVER,
                Biomes.SNOWY_PLAINS,
                Biomes.SNOWY_TAIGA,
                Biomes.SNOWY_BEACH,
                Biomes.SNOWY_SLOPES,
                Biomes.GROVE,
                Biomes.JAGGED_PEAKS,
                Biomes.FROZEN_PEAKS,
                Biomes.ICE_SPIKES
        );
        tag(CoreBiomeTags.IS_COLD_SOLID).add(
                Biomes.FROZEN_RIVER,
                Biomes.SNOWY_PLAINS,
                Biomes.SNOWY_TAIGA,
                Biomes.GROVE,
                Biomes.ICE_SPIKES
        );
        tag(CoreBiomeTags.IS_DESERT).add(Biomes.DESERT);
        tag(CoreBiomeTags.IS_END).add(
                Biomes.THE_END,
                Biomes.SMALL_END_ISLANDS,
                Biomes.END_MIDLANDS,
                Biomes.END_HIGHLANDS,
                Biomes.END_BARRENS
        );
        tag(CoreBiomeTags.IS_FORREST).add(
                Biomes.FOREST,
                Biomes.FLOWER_FOREST,
                Biomes.WINDSWEPT_FOREST,
                Biomes.DARK_FOREST,
                Biomes.BIRCH_FOREST,
                Biomes.OLD_GROWTH_BIRCH_FOREST,
                Biomes.SPARSE_JUNGLE
        );
        tag(CoreBiomeTags.IS_FUNGI).add(Biomes.MUSHROOM_FIELDS);
        tag(CoreBiomeTags.IS_MOSSY).add(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.LUSH_CAVES);
        tag(CoreBiomeTags.IS_NETHER).add(Biomes.CRIMSON_FOREST, Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.WARPED_FOREST);
        tag(CoreBiomeTags.IS_OCEAN).add(
                Biomes.DEEP_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.DEEP_COLD_OCEAN,
                Biomes.OCEAN,
                Biomes.LUKEWARM_OCEAN,
                Biomes.COLD_OCEAN,
                Biomes.WARM_OCEAN
        );
        tag(CoreBiomeTags.IS_SANDY).add(
                Biomes.DESERT,
                Biomes.BADLANDS,
                Biomes.ERODED_BADLANDS,
                Biomes.WOODED_BADLANDS,
                Biomes.BEACH
        );
        tag(CoreBiomeTags.IS_SWAMP).add(
                Biomes.SWAMP,
                Biomes.MANGROVE_SWAMP
        );
        tag(CoreBiomeTags.IS_TEMPERATE).add(
                Biomes.PLAINS,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.MEADOW,
                Biomes.RIVER,
                Biomes.SAVANNA,
                Biomes.SAVANNA_PLATEAU,
                Biomes.WINDSWEPT_SAVANNA,
                Biomes.STONY_SHORE,
                Biomes.WINDSWEPT_GRAVELLY_HILLS,
                Biomes.WINDSWEPT_HILLS
        );
        tag(CoreBiomeTags.IS_WARPED).add(Biomes.WARPED_FOREST);
        tag(CoreBiomeTags.IS_WET).addTag(CoreBiomeTags.IS_OCEAN).add(
                Biomes.SWAMP,
                Biomes.MANGROVE_SWAMP,
                Biomes.JUNGLE,
                Biomes.BAMBOO_JUNGLE,
                Biomes.LUSH_CAVES
        );

        // structures / features containers
        tag(CoreBiomeTags.HAS_GEODES_BLUE).add(
                Biomes.SNOWY_PLAINS,
                Biomes.ICE_SPIKES,
                Biomes.SNOWY_TAIGA,
                Biomes.FROZEN_PEAKS,
                Biomes.JAGGED_PEAKS,
                Biomes.SNOWY_SLOPES,
                Biomes.GROVE
        );
        tag(CoreBiomeTags.HAS_GEODES_ENDER).add(
                Biomes.FOREST,
                Biomes.FLOWER_FOREST,
                Biomes.WINDSWEPT_FOREST,
                Biomes.DARK_FOREST,
                Biomes.OLD_GROWTH_BIRCH_FOREST,
                Biomes.BAMBOO_JUNGLE,
                Biomes.JUNGLE,
                Biomes.SPARSE_JUNGLE
        );
        tag(CoreBiomeTags.HAS_GEODES_PEGASUS).add(
                Biomes.DEEP_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.DEEP_COLD_OCEAN,
                Biomes.OCEAN,
                Biomes.LUKEWARM_OCEAN,
                Biomes.WARM_OCEAN,
                Biomes.COLD_OCEAN,
                Biomes.BEACH
        );
        tag(CoreBiomeTags.HAS_GEODES_WHITE).add(
                Biomes.SAVANNA,
                Biomes.SAVANNA_PLATEAU,
                Biomes.WINDSWEPT_SAVANNA
        );
        tag(CoreBiomeTags.HAS_GEODES_YELLOW).add(
                Biomes.DESERT,
                Biomes.BADLANDS,
                Biomes.ERODED_BADLANDS,
                Biomes.WOODED_BADLANDS
        );
        tag(CoreBiomeTags.HAS_GEODES_RED).addTag(CoreBiomeTags.IS_NETHER);

        tag(CoreBiomeTags.HAS_BUDDING_BLUE).addTag(CoreBiomeTags.HAS_GEODES_BLUE);
        tag(CoreBiomeTags.HAS_BUDDING_ENDER).addTag(CoreBiomeTags.HAS_GEODES_ENDER);
        tag(CoreBiomeTags.HAS_BUDDING_PEGASUS).addTag(CoreBiomeTags.HAS_GEODES_PEGASUS);
        tag(CoreBiomeTags.HAS_BUDDING_RED).addTag(CoreBiomeTags.HAS_GEODES_RED);
        tag(CoreBiomeTags.HAS_BUDDING_WHITE).addTag(CoreBiomeTags.HAS_GEODES_WHITE);
        tag(CoreBiomeTags.HAS_BUDDING_YELLOW).addTag(CoreBiomeTags.HAS_GEODES_YELLOW);

        tag(CoreBiomeTags.HAS_DRIPSTONE).add(Biomes.STONY_PEAKS, Biomes.DRIPSTONE_CAVES);
        tag(CoreBiomeTags.HAS_PODZOL).add(Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA);
        tag(CoreBiomeTags.HAS_SCULK).add(Biomes.DEEP_DARK);

        tag(CoreBiomeTags.HAS_NAQUADAH_GENERATED).addTag(CoreBiomeTags.IS_NETHER);
        tag(CoreBiomeTags.HAS_TITANIUM_GENERATED).addTag(BiomeTags.IS_OVERWORLD);
        tag(CoreBiomeTags.HAS_TRINIUM_GENERATED).addTag(BiomeTags.IS_END);
    }
}
