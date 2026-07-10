package dev.tauri.jsg.core.datagen.loot;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.CoreItems;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class JSGBlockLootTables extends BlockLootSubProvider {
    protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    public JSGBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        CoreBlocks.CARTOUCHES.forEach((block, types) -> types.forEach((type, blockRegObj) -> dropSelf(blockRegObj.get())));

        dropOther(CoreBlocks.STATIC_SMOOTH_SANDSTONE.get(), Blocks.SMOOTH_STONE);
        dropSelf(CoreBlocks.LEMON_BLOCK.get());
        dropSelf(CoreBlocks.BRAZIER_COAL.get());
        dropSelf(CoreBlocks.NAQUADAH_BLOCK.get());
        dropSelf(CoreBlocks.NAQUADAH_RAW_BLOCK.get());
        dropSelf(CoreBlocks.NAQUADAH_REFINED_BLOCK.get());
        dropSelf(CoreBlocks.RAW_ORE_NAQUADAH_BLOCK.get());
        dropSelf(CoreBlocks.TITANIUM_BLOCK.get());
        dropSelf(CoreBlocks.RAW_ORE_TITANIUM_BLOCK.get());
        dropSelf(CoreBlocks.TRINIUM_BLOCK.get());
        dropSelf(CoreBlocks.RAW_ORE_TRINIUM_BLOCK.get());

        dropOther(CoreFluids.MOLTEN_NAQUADAH_ALLOY.cauldron.get(), Items.CAULDRON);
        dropOther(CoreFluids.MOLTEN_NAQUADAH_RAW.cauldron.get(), Items.CAULDRON);
        dropOther(CoreFluids.MOLTEN_NAQUADAH_REFINED.cauldron.get(), Items.CAULDRON);
        dropOther(CoreFluids.MOLTEN_TITANIUM.cauldron.get(), Items.CAULDRON);
        dropOther(CoreFluids.MOLTEN_TRINIUM.cauldron.get(), Items.CAULDRON);

        CoreBlocks.ORE_NAQUADAH.forEach((variant, block) -> dropOre(block.get(), CoreItems.NAQUADAH_ORE_RAW.get()));
        CoreBlocks.ORE_TITANIUM.forEach((variant, block) -> dropOre(block.get(), CoreItems.TITANIUM_ORE_RAW.get()));
        CoreBlocks.ORE_TRINIUM.forEach((variant, block) -> dropOre(block.get(), CoreItems.TRINIUM_ORE_RAW.get()));

        // crystals
        CoreBlocks.CRYSTAL_BLOCK.forEach((color, block) -> dropSelf(block.get()));
        CoreBlocks.CRYSTAL_CLUSTER.forEach((color, block) -> add(block.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(applyExplosionDecay(block.get(), LootItem.lootTableItem(block.get()))
                                .when(HAS_SILK_TOUCH)
                                .otherwise(applyExplosionDecay(CoreItems.CRYSTALS.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS.get(color).get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                                        .otherwise(applyExplosionDecay(CoreItems.CRYSTALS.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS.get(color).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))))
                                )
                        )
                ).withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(applyExplosionDecay(block.get(), LootItem.lootTableItem(block.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(0))))
                                .when(HAS_SILK_TOUCH)
                                .otherwise(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                                        .otherwise(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                                )
                        )
                )
        ));

        CoreBlocks.CRYSTAL_BUDDING.forEach((variant, buddings) -> buddings.forEach((color, budding) -> {
            var seed = CoreItems.CRYSTAL_SEEDS.get(color).get();
            var drop = variant.getDrop(color, () -> CoreBlocks.CRYSTAL_BLOCK);
            if (drop == null) drop = Blocks.COBBLESTONE;
            add(budding.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1f))
                            .setBonusRolls(ConstantValue.exactly(0))
                            .add(applyExplosionDecay(budding.get(), LootItem.lootTableItem(budding.get()))
                                    .when(HAS_SILK_TOUCH)
                                    .otherwise(applyExplosionDecay(seed, LootItem.lootTableItem(seed))
                                            .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)))
                                            .otherwise(applyExplosionDecay(seed, LootItem.lootTableItem(seed).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))))
                                    )
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1f))
                            .setBonusRolls(ConstantValue.exactly(0))
                            .add(applyExplosionDecay(budding.get(), LootItem.lootTableItem(budding.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(0))))
                                    .when(HAS_SILK_TOUCH)
                                    .otherwise(applyExplosionDecay(drop, LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1))))
                                            .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)))
                                    )
                            )
                    )
            );
        }));
        CoreBlocks.UNSTABLE_CRYSTAL_BUDDING.forEach((variant, buddings) -> buddings.forEach((color, budding) -> {
            var seed = CoreItems.CRYSTAL_SEEDS.get(color).get();
            var drop = variant.getDrop(color, () -> CoreBlocks.CRYSTAL_BLOCK);
            if (drop == null) drop = Blocks.COBBLESTONE;
            add(budding.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1f))
                            .setBonusRolls(ConstantValue.exactly(0))
                            .add(applyExplosionDecay(drop, LootItem.lootTableItem(drop)))
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1f))
                            .setBonusRolls(ConstantValue.exactly(0))
                            .add(applyExplosionDecay(seed, LootItem.lootTableItem(seed).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))))
                    )
            );
        }));
        CoreBlocks.CRYSTAL_BUD_LARGE.forEach((color, block) -> add(block.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(applyExplosionDecay(block.get(), LootItem.lootTableItem(block.get()))
                                .when(HAS_SILK_TOUCH)
                                .otherwise(applyExplosionDecay(CoreItems.CRYSTALS.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS.get(color).get())).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)))
                                )
                        )
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))))
                                .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)))
                                .otherwise(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))))
                        )
                )
        ));
        CoreBlocks.CRYSTAL_BUD_MEDIUM.forEach((color, block) -> add(block.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(applyExplosionDecay(block.get(), LootItem.lootTableItem(block.get())
                                .when(HAS_SILK_TOUCH))
                                .otherwise(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)))
                                        .otherwise(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                                )
                        )
                )
        ));
        CoreBlocks.CRYSTAL_BUD_SMALL.forEach((color, block) -> add(block.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(applyExplosionDecay(block.get(), LootItem.lootTableItem(block.get())
                                .when(HAS_SILK_TOUCH))
                                .otherwise(applyExplosionDecay(CoreItems.CRYSTALS_SMALL.get(color).get(), LootItem.lootTableItem(CoreItems.CRYSTALS_SMALL.get(color).get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)))
                                )
                        )
                )
        ));
    }

    @Override
    @NotNull
    protected Iterable<Block> getKnownBlocks() {
        return JSGCore.REGISTRY_HELPER.block().getEntries().stream().map(RegistryObject::get)::iterator;
    }

    protected void dropNothing(Block block) {
        add(block, LootTable.lootTable());
    }

    protected void dropOre(Block block, Item item) {
        add(block, createOreDrop(block, item));
    }

    protected void dropOre(Block block, Item item, NumberProvider dropsCount) {
        add(block, createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(dropsCount)).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));
    }

    protected void dropAndCopyNBT(Block block, CopyNbtFunction.Builder copyNbtFunctionBuilder) {
        add(block, LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(block).apply(copyNbtFunctionBuilder))
                ))
        );
    }
}
