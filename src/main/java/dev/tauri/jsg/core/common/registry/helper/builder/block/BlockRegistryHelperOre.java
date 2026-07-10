package dev.tauri.jsg.core.common.registry.helper.builder.block;

import dev.tauri.jsg.core.common.block.core.JSGOreBlock;
import dev.tauri.jsg.core.common.block.crystal.*;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockRegistryHelperOre extends RegistryHelper<Block, BlockRegistryHelperOre.OreBlockBuilder> {
    public BlockRegistryHelperOre(Supplier<JSGDeferredRegister<Block>> registry) {
        super(registry, OreBlockBuilder::new);
    }

    public static class OreBlockBuilder extends BlockRegistryHelperGeneric.GenericBlockBuilder {
        public OreBlockBuilder(RegistryHelper<Block, OreBlockBuilder> registryHelper, String name) {
            super(registryHelper, name);
        }

        public OreBlockBuilder(String name, OreBlockBuilder other) {
            super(name, other);
        }

        @Override
        public OreBlockBuilder applyTooltip(@NotNull TooltipApplier<Block> tooltipApplier) {
            return (OreBlockBuilder) super.applyTooltip(tooltipApplier);
        }

        @Override
        public OreBlockBuilder clearTooltip() {
            return (OreBlockBuilder) super.clearTooltip();
        }

        @Override
        public OreBlockBuilder setFlammability(int flammability) {
            return (OreBlockBuilder) super.setFlammability(flammability);
        }

        @Override
        public OreBlockBuilder setFireSpreadSpeed(int fireSpreadSpeed) {
            return (OreBlockBuilder) super.setFireSpreadSpeed(fireSpreadSpeed);
        }

        @Override
        public OreBlockBuilder addToolStateModifier(Predicate<ItemStack> toolPredicate, Function<BlockState, BlockState> resultGetter) {
            return (OreBlockBuilder) super.addToolStateModifier(toolPredicate, resultGetter);
        }

        @Override
        public OreBlockBuilder setProperties(BlockBehaviour.Properties properties) {
            return (OreBlockBuilder) super.setProperties(properties);
        }

        @Override
        public OreBlockBuilder setInTabs(List<RegistryObject<CreativeModeTab>> tabs) {
            return (OreBlockBuilder) super.setInTabs(tabs);
        }

        public RegistryObject<Block> buildSingle() {
            return registryHelper.registry.get().register(name, () -> new JSGOreBlock(properties) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return flammability;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return fireSpreadSpeed;
                }

                @Override
                public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
                    if (toolStateModifiers != null) {
                        for (var e : toolStateModifiers.entrySet()) {
                            if (e.getKey().test(context.getItemInHand())) {
                                return e.getValue().apply(state);
                            }
                        }
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }

                @Override
                public List<RegistryObject<CreativeModeTab>> getTabs() {
                    return tabs;
                }

                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, context, components, tooltipFlag);
                }
            });
        }

        public Map<OreBlockVariant, RegistryObject<Block>> buildAll() {
            Map<OreBlockVariant, RegistryObject<Block>> result = new HashMap<>();
            for (var type : OreBlockVariant.values()) {
                if (type == OreBlockVariant.SELF) continue;
                var nameFinal = (type.prefix != null ? (type.prefix + "_") : "") + name;
                var variantBuilder = new OreBlockBuilder(nameFinal, this);
                var block = variantBuilder.buildSingle();
                result.put(type, block);
            }
            return result;
        }


        public Map<ICrystalColor, RegistryObject<Block>> buildCrystalBlock() {
            Map<ICrystalColor, RegistryObject<Block>> result = new HashMap<>();
            for (var color : CrystalColor.values()) {
                String nameFinal = name.replaceAll("\\{color}", color.name().toLowerCase());
                var variantBuilder = new OreBlockBuilder(nameFinal, this);
                var block = variantBuilder.buildGeneric();
                result.put(color, block);
            }
            return result;
        }

        public RegistryObject<Block> buildCrystalBuddingSingle(CrystalColor color, Function<CrystalBudType, Block> blockBySizeGetter) {
            return registryHelper.registry.get().register(name, () -> new CrystalBuddingBlock(color, blockBySizeGetter, properties) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return flammability;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return fireSpreadSpeed;
                }

                @Override
                public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
                    if (toolStateModifiers != null) {
                        for (var e : toolStateModifiers.entrySet()) {
                            if (e.getKey().test(context.getItemInHand())) {
                                return e.getValue().apply(state);
                            }
                        }
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }

                @Override
                public List<RegistryObject<CreativeModeTab>> getTabs() {
                    return tabs;
                }

                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, context, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildCrystalUnstableBuddingSingle(CrystalColor color, Supplier<Block> stableBudding, Supplier<BlockState> originalBlockSupplier) {
            return registryHelper.registry.get().register(name, () -> new CrystalUnstableBuddingBlock(color, properties, stableBudding, originalBlockSupplier) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return flammability;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return fireSpreadSpeed;
                }

                @Override
                public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
                    if (toolStateModifiers != null) {
                        for (var e : toolStateModifiers.entrySet()) {
                            if (e.getKey().test(context.getItemInHand())) {
                                return e.getValue().apply(state);
                            }
                        }
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }

                @Override
                public List<RegistryObject<CreativeModeTab>> getTabs() {
                    return tabs;
                }

                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, context, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildCrystalBudSingle(CrystalBudType budType, CrystalColor color) {
            return registryHelper.registry.get().register(name, () -> new dev.tauri.jsg.core.common.block.crystal.CrystalBudBlock(budType, color) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return flammability;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return fireSpreadSpeed;
                }

                @Override
                public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
                    if (toolStateModifiers != null) {
                        for (var e : toolStateModifiers.entrySet()) {
                            if (e.getKey().test(context.getItemInHand())) {
                                return e.getValue().apply(state);
                            }
                        }
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }

                @Override
                public List<RegistryObject<CreativeModeTab>> getTabs() {
                    return tabs;
                }

                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, context, components, tooltipFlag);
                }
            });
        }

        public Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>> buildCrystalBuddings(BiFunction<CrystalColor, CrystalBudType, Block> blockBySizeGetter) {
            Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>> result = new HashMap<>();
            for (var type : OreBlockVariant.values()) {
                Map<ICrystalColor, RegistryObject<Block>> result2 = new HashMap<>();
                for (var color : CrystalColor.values()) {
                    String nameFinal = (type.prefix != null ? (type.prefix + "_") : "") + name.replaceAll("\\{color}", color.name().toLowerCase());
                    var variantBuilder = new OreBlockBuilder(nameFinal, this);
                    RegistryObject<Block> block = variantBuilder.buildCrystalBuddingSingle(color, (size) -> blockBySizeGetter.apply(color, size));
                    result2.put(color, block);
                }
                result.put(type, result2);
            }
            return result;
        }

        public Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>> buildCrystalUnstableBuddings(Supplier<Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>>> stableBuddings, Supplier<Map<ICrystalColor, RegistryObject<Block>>> crystalBlocksSupplier) {
            Map<OreBlockVariant, Map<ICrystalColor, RegistryObject<Block>>> result = new HashMap<>();
            for (var type : OreBlockVariant.values()) {
                Map<ICrystalColor, RegistryObject<Block>> result2 = new HashMap<>();
                for (var color : CrystalColor.values()) {
                    String nameFinal = (type.prefix != null ? (type.prefix + "_") : "") + name.replaceAll("\\{color}", color.name().toLowerCase());
                    var variantBuilder = new OreBlockBuilder(nameFinal, this);
                    RegistryObject<Block> block = variantBuilder.buildCrystalUnstableBuddingSingle(color, () -> stableBuddings.get().get(type).get(color).get(), () -> type.getOriginalBlock(color, crystalBlocksSupplier));
                    result2.put(color, block);
                }
                result.put(type, result2);
            }
            return result;
        }

        public Map<ICrystalColor, RegistryObject<Block>> buildCrystalBuds(CrystalBudType budType) {
            Map<ICrystalColor, RegistryObject<Block>> result = new HashMap<>();
            for (var color : CrystalColor.values()) {
                String nameFinal = name.replaceAll("\\{color}", color.name().toLowerCase());
                var variantBuilder = new OreBlockBuilder(nameFinal, this);
                RegistryObject<Block> block = variantBuilder.buildCrystalBudSingle(budType, color);
                result.put(color, block);
            }
            return result;
        }
    }
}
