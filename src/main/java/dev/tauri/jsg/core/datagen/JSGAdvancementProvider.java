package dev.tauri.jsg.core.datagen;


import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.crystal.CrystalColor;
import dev.tauri.jsg.core.common.registry.CoreAdvancements;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class JSGAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    @ParametersAreNonnullByDefault
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(
                        CoreItems.ICON_ROOT_ADVANCEMENT.get(),
                        Component.translatable("advancement.jsg_core.root.title"),
                        Component.empty(),
                        JSGMapping.rl("textures/block/sandstone_top.png"),
                        FrameType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("has_stone", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.STONE).build()
                ))
                .save(saver, CoreAdvancements.ROOT, fileHelper);

        Advancement emptyPage = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        CoreItems.NOTEBOOK_PAGE_EMPTY.get(),
                        Component.translatable("advancement.jsg_core.page_empty.title"),
                        Component.translatable("advancement.jsg_core.page_empty.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_page", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.NOTEBOOK_PAGE_EMPTY.get()))
                .save(saver, CoreAdvancements.PAGE_EMPTY, fileHelper);

        Advancement.Builder.advancement()
                .parent(emptyPage)
                .display(
                        CoreItems.NOTEBOOK_PAGE_FILLED.get(),
                        Component.translatable("advancement.jsg_core.page_erase.title"),
                        Component.translatable("advancement.jsg_core.page_erase.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        true
                )
                .addCriterion("erase_page", RecipeCraftedTrigger.TriggerInstance.craftedItem(JSGMapping.rl(JSGCore.MOD_ID, "notebook_page_erase")))
                .save(saver, CoreAdvancements.ERASE_PAGE, fileHelper);


        Advancement mortar = Advancement.Builder.advancement()
                .parent(emptyPage)
                .display(
                        CoreItems.MORTAR_AND_PESTLE.get(),
                        Component.translatable("advancement.jsg_core.crafted_mortar_and_pestle.title"),
                        Component.translatable("advancement.jsg_core.crafted_mortar_and_pestle.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_mortar_and_pestle", RecipeCraftedTrigger.TriggerInstance.craftedItem(JSGMapping.rl(JSGCore.MOD_ID, "mortar_and_pestle")))
                .save(saver, CoreAdvancements.MORTAR_AND_PESTLE, fileHelper);

        Advancement.Builder.advancement()
                .parent(mortar)
                .display(
                        CoreItems.CRUSHED_CALCITE.get(),
                        Component.translatable("advancement.jsg_core.crafted_crushed_calcite.title"),
                        Component.translatable("advancement.jsg_core.crafted_crushed_calcite.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_crushed_calcite", RecipeCraftedTrigger.TriggerInstance.craftedItem(JSGMapping.rl(JSGCore.MOD_ID, "crushed_calcite")))
                .save(saver, CoreAdvancements.CRUSHED_CALCITE, fileHelper);

        Advancement lemon = Advancement.Builder.advancement()
                .parent(emptyPage)
                .display(
                        CoreBlocks.LEMON_BLOCK.get(),
                        Component.translatable("advancement.jsg_core.lemon_obtained.title"),
                        Component.translatable("advancement.jsg_core.lemon_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_lemon", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.LEMON_BLOCK.get()))
                .save(saver, CoreAdvancements.LEMON, fileHelper);

        //TODO(Fredy): Edit after adding trap mechanics
        Advancement trapLemon = Advancement.Builder.advancement()
                .parent(lemon)
                .display(
                        CoreItems.TRAP_LEMON.get(),
                        Component.translatable("advancement.jsg_core.lemon_trap_planted.title"),
                        Component.translatable("advancement.jsg_core.lemon_trap_planted.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("lemon_trap_planted", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.TRAP_LEMON.get()))
                .save(saver, CoreAdvancements.LEMON_TRAP, fileHelper);

        Advancement.Builder.advancement()
                .parent(trapLemon)
                .display(
                        CoreItems.FOOD_LEMON.get(),
                        Component.translatable("advancement.jsg_core.food_lemon_consumed.title"),
                        Component.translatable("advancement.jsg_core.food_lemon_consumed.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("eat_lemon", ConsumeItemTrigger.TriggerInstance.usedItem(CoreItems.FOOD_LEMON.get()))
                .save(saver, CoreAdvancements.LEMON_CONSUMED, fileHelper);

        Advancement copper = Advancement.Builder.advancement()
                .parent(emptyPage)
                .display(
                        Items.COPPER_INGOT,
                        Component.translatable("advancement.jsg_core.copper_ingot_obtained.title"),
                        Component.translatable("advancement.jsg_core.copper_ingot_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
                .save(saver, CoreAdvancements.COPPER, fileHelper);

        Advancement exposed = Advancement.Builder.advancement()
                .parent(copper)
                .display(
                        CoreItems.COPPER_INGOT_EXPOSED.get(),
                        Component.translatable("advancement.jsg_core.copper_ingot_exposed_obtained.title"),
                        Component.translatable("advancement.jsg_core.copper_ingot_exposed_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_copper", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.COPPER_INGOT_EXPOSED.get()))
                .save(saver, CoreAdvancements.COPPER_EXPOSED, fileHelper);

        Advancement weathered = Advancement.Builder.advancement()
                .parent(exposed)
                .display(
                        CoreItems.COPPER_INGOT_WEATHERED.get(),
                        Component.translatable("advancement.jsg_core.copper_ingot_weathered_obtained.title"),
                        Component.translatable("advancement.jsg_core.copper_ingot_weathered_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_copper", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.COPPER_INGOT_WEATHERED.get()))
                .save(saver, CoreAdvancements.COPPER_WEATHERED, fileHelper);

        Advancement.Builder.advancement()
                .parent(weathered)
                .display(
                        CoreItems.COPPER_INGOT_OXIDIZED.get(),
                        Component.translatable("advancement.jsg_core.copper_ingot_oxidized_obtained.title"),
                        Component.translatable("advancement.jsg_core.copper_ingot_oxidized_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_copper", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.COPPER_INGOT_OXIDIZED.get()))
                .save(saver, CoreAdvancements.COPPER_OXIDIZED, fileHelper);

        Advancement titanium = Advancement.Builder.advancement()
                .parent(copper)
                .display(
                        CoreItems.TITANIUM_INGOT.get(),
                        Component.translatable("advancement.jsg_core.titanium_ingot_obtained.title"),
                        Component.translatable("advancement.jsg_core.titanium_ingot_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_titanium", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.TITANIUM_INGOT.get()))
                .save(saver, CoreAdvancements.TITANIUM, fileHelper);

        Advancement.Builder.advancement()
                .parent(titanium)
                .display(
                        CoreItems.TRINIUM_INGOT.get(),
                        Component.translatable("advancement.jsg_core.trinium_ingot_obtained.title"),
                        Component.translatable("advancement.jsg_core.trinium_ingot_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_trinium", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.TRINIUM_INGOT.get()))
                .save(saver, CoreAdvancements.TRINIUM, fileHelper);

        Advancement cauldron = Advancement.Builder.advancement()
                .parent(copper)
                .display(
                        Blocks.CAULDRON,
                        Component.translatable("advancement.jsg_core.cauldron_obtained.title"),
                        Component.translatable("advancement.jsg_core.cauldron_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cauldron", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CAULDRON))
                .save(saver, CoreAdvancements.CAULDRON, fileHelper);

        Advancement circuit = Advancement.Builder.advancement()
                .parent(cauldron)
                .display(
                        CoreItems.CIRCUIT_CONTROL_NAQUADAH.get(),
                        Component.translatable("advancement.jsg_core.circuit_control_naquadah_obtained.title"),
                        Component.translatable("advancement.jsg_core.circuit_control_naquadah_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_circuit", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .save(saver, CoreAdvancements.CIRCUIT_CONTROL_NAQUADAH, fileHelper);

        Advancement advanced_circuit = Advancement.Builder.advancement()
                .parent(circuit)
                .display(
                        CoreItems.CIRCUIT_CONTROL_CRYSTAL.get(),
                        Component.translatable("advancement.jsg_core.circuit_control_crystal_obtained.title"),
                        Component.translatable("advancement.jsg_core.circuit_control_crystal_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_circuit", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .save(saver, CoreAdvancements.CIRCUIT_CONTROL_CRYSTAL, fileHelper);

        Advancement efficiency_crystal = Advancement.Builder.advancement()
                .parent(advanced_circuit)
                .display(
                        CoreItems.CRYSTAL_UPGRADE_EFFICIENCY.get(),
                        Component.translatable("advancement.jsg_core.efficiency_crystal_obtain.title"),
                        Component.translatable("advancement.jsg_core.efficiency_crystal_obtain.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_UPGRADE_EFFICIENCY.get()))
                .save(saver, CoreAdvancements.CRYSTAL_UPGRADE_EFFICIENCY, fileHelper);

        Advancement.Builder.advancement()
                .parent(efficiency_crystal)
                .display(
                        CoreItems.CRYSTAL_UPGRADE_CAPACITY.get(),
                        Component.translatable("advancement.jsg_core.capacity_crystal_obtain.title"),
                        Component.translatable("advancement.jsg_core.capacity_crystal_obtain.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_UPGRADE_CAPACITY.get()))
                .save(saver, CoreAdvancements.CRYSTAL_UPGRADE_CAPACITY, fileHelper);

        Advancement liquidNaquadah = Advancement.Builder.advancement()
                .parent(cauldron)
                .display(
                        CoreFluids.MOLTEN_NAQUADAH_RAW.bucket.get(),
                        Component.translatable("advancement.jsg_core.raw_naquadah_bucket_obtained.title"),
                        Component.translatable("advancement.jsg_core.raw_naquadah_bucket_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_molten_naquadah", InventoryChangeTrigger.TriggerInstance.hasItems(CoreFluids.MOLTEN_NAQUADAH_RAW.bucket.get()))
                .save(saver, CoreAdvancements.RAW_NAQUADAH_BUCKET, fileHelper);

        Advancement naquadahAlloy = Advancement.Builder.advancement()
                .parent(liquidNaquadah)
                .display(
                        CoreItems.NAQUADAH_ALLOY.get(),
                        Component.translatable("advancement.jsg_core.naquadah_alloy_obtained.title"),
                        Component.translatable("advancement.jsg_core.naquadah_alloy_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_naquadah_alloy", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.NAQUADAH_ALLOY.get()))
                .save(saver, CoreAdvancements.NAQUADAH_ALLOY, fileHelper);

        Advancement.Builder.advancement()
                .parent(naquadahAlloy)
                .display(
                        CoreItems.NAQUADAH_ALLOY_REFINED.get(),
                        Component.translatable("advancement.jsg_core.naquadah_alloy_refined_obtained.title"),
                        Component.translatable("advancement.jsg_core.naquadah_alloy_refined_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_naquadah_alloy_refined", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.NAQUADAH_ALLOY_REFINED.get()))
                .save(saver, CoreAdvancements.NAQUADAH_ALLOY_REFINED, fileHelper);

        Advancement clusterBlue = Advancement.Builder.advancement()
                .parent(emptyPage)
                .display(
                        CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.BLUE).get(),
                        Component.translatable("advancement.jsg_core.cluster_blue_obtained.title"),
                        Component.translatable("advancement.jsg_core.cluster_blue_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.BLUE).get()))
                .save(saver, CoreAdvancements.CLUSTER_BLUE, fileHelper);

        Advancement clusterRed = Advancement.Builder.advancement()
                .parent(clusterBlue)
                .display(
                        CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.RED).get(),
                        Component.translatable("advancement.jsg_core.cluster_red_obtained.title"),
                        Component.translatable("advancement.jsg_core.cluster_red_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.RED).get()))
                .save(saver, CoreAdvancements.CLUSTER_RED, fileHelper);

        Advancement clusterEnder = Advancement.Builder.advancement()
                .parent(clusterRed)
                .display(
                        CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.ENDER).get(),
                        Component.translatable("advancement.jsg_core.cluster_ender_obtained.title"),
                        Component.translatable("advancement.jsg_core.cluster_ender_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.ENDER).get()))
                .save(saver, CoreAdvancements.CLUSTER_ENDER, fileHelper);

        Advancement clusterYellow = Advancement.Builder.advancement()
                .parent(clusterEnder)
                .display(
                        CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.YELLOW).get(),
                        Component.translatable("advancement.jsg_core.cluster_yellow_obtained.title"),
                        Component.translatable("advancement.jsg_core.cluster_yellow_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.YELLOW).get()))
                .save(saver, CoreAdvancements.CLUSTER_YELLOW, fileHelper);

        Advancement clusterWhite = Advancement.Builder.advancement()
                .parent(clusterYellow)
                .display(
                        CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.WHITE).get(),
                        Component.translatable("advancement.jsg_core.cluster_white_obtained.title"),
                        Component.translatable("advancement.jsg_core.cluster_white_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.WHITE).get()))
                .save(saver, CoreAdvancements.CLUSTER_WHITE, fileHelper);

        Advancement.Builder.advancement()
                .parent(clusterWhite)
                .display(
                        CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.PEGASUS).get(),
                        Component.translatable("advancement.jsg_core.cluster_pegasus_obtained.title"),
                        Component.translatable("advancement.jsg_core.cluster_pegasus_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(CoreBlocks.CRYSTAL_CLUSTER.get(CrystalColor.PEGASUS).get()))
                .save(saver, CoreAdvancements.CLUSTER_PEGASUS, fileHelper);

        //Redo after registry with map (same as clusters)
        Advancement seedBlue = Advancement.Builder.advancement()
                .parent(clusterBlue)
                .display(
                        CoreItems.CRYSTAL_BLUE_SEED.get(),
                        Component.translatable("advancement.jsg_core.seed_blue_obtained.title"),
                        Component.translatable("advancement.jsg_core.seed_blue_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_seed", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_BLUE_SEED.get()))
                .save(saver, CoreAdvancements.SEED_BLUE, fileHelper);

        Advancement seedRed = Advancement.Builder.advancement()
                .parent(seedBlue)
                .display(
                        CoreItems.CRYSTAL_RED_SEED.get(),
                        Component.translatable("advancement.jsg_core.seed_red_obtained.title"),
                        Component.translatable("advancement.jsg_core.seed_red_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_seed", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_RED_SEED.get()))
                .save(saver, CoreAdvancements.SEED_RED, fileHelper);

        Advancement seedEnder = Advancement.Builder.advancement()
                .parent(seedRed)
                .display(
                        CoreItems.CRYSTAL_ENDER_SEED.get(),
                        Component.translatable("advancement.jsg_core.seed_ender_obtained.title"),
                        Component.translatable("advancement.jsg_core.seed_ender_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_seed", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_ENDER_SEED.get()))
                .save(saver, CoreAdvancements.SEED_ENDER, fileHelper);

        Advancement seedYellow = Advancement.Builder.advancement()
                .parent(seedEnder)
                .display(
                        CoreItems.CRYSTAL_YELLOW_SEED.get(),
                        Component.translatable("advancement.jsg_core.seed_yellow_obtained.title"),
                        Component.translatable("advancement.jsg_core.seed_yellow_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_seed", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_YELLOW_SEED.get()))
                .save(saver, CoreAdvancements.SEED_YELLOW, fileHelper);

        Advancement seedWhite = Advancement.Builder.advancement()
                .parent(seedYellow)
                .display(
                        CoreItems.CRYSTAL_WHITE_SEED.get(),
                        Component.translatable("advancement.jsg_core.seed_white_obtained.title"),
                        Component.translatable("advancement.jsg_core.seed_white_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_seed", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_WHITE_SEED.get()))
                .save(saver, CoreAdvancements.SEED_WHITE, fileHelper);

        Advancement.Builder.advancement()
                .parent(seedWhite)
                .display(
                        CoreItems.CRYSTAL_PEGASUS_SEED.get(),
                        Component.translatable("advancement.jsg_core.seed_pegasus_obtained.title"),
                        Component.translatable("advancement.jsg_core.seed_pegasus_obtained.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_seed", InventoryChangeTrigger.TriggerInstance.hasItems(CoreItems.CRYSTAL_PEGASUS_SEED.get()))
                .save(saver, CoreAdvancements.SEED_PEGASUS, fileHelper);
    }

    public static ForgeAdvancementProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        return new ForgeAdvancementProvider(output, registries, helper, List.of(new JSGAdvancementProvider()));
    }
}
