package dev.tauri.jsg.core.common.registry.helper.builder.block;

import dev.tauri.jsg.core.common.block.JSGTabbedBlock;
import dev.tauri.jsg.core.common.block.core.*;
import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryHelper;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryObjectBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.ItemAbility;
import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockRegistryHelperGeneric extends RegistryHelper<Block, BlockRegistryHelperGeneric.GenericBlockBuilder> {
    public BlockRegistryHelperGeneric(Supplier<JSGDeferredRegister<Block>> registry) {
        super(registry, GenericBlockBuilder::new);
    }

    public static class GenericBlockBuilder extends RegistryObjectBuilder<RegistryHelper<Block, ? extends GenericBlockBuilder>> {
        public GenericBlockBuilder(RegistryHelper<Block, ? extends GenericBlockBuilder> registryHelper, String name) {
            super(registryHelper, name);
        }

        public GenericBlockBuilder(String name, GenericBlockBuilder other) {
            super(name, other);
            this.tooltipApplier = other.tooltipApplier;
            this.flammability = other.flammability;
            this.fireSpreadSpeed = other.fireSpreadSpeed;
            this.toolStateModifiers.clear();
            this.toolStateModifiers.putAll(other.toolStateModifiers);
            this.properties = other.properties;
        }

        protected TooltipApplier<Block> tooltipApplier = (item, itemStack, blockGetter, components, tooltipFlag) -> ItemHelper.applyGenericToolTip(item.getDescriptionId(), components, tooltipFlag);
        protected int flammability;
        protected int fireSpreadSpeed;
        protected final Map<Predicate<ItemStack>, Function<BlockState, BlockState>> toolStateModifiers = new HashMap<>();
        protected BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();

        public GenericBlockBuilder applyTooltip(@NotNull TooltipApplier<Block> tooltipApplier) {
            this.tooltipApplier = tooltipApplier;
            return this;
        }

        public GenericBlockBuilder clearTooltip() {
            this.tooltipApplier = (item, itemStack, blockGetter, components, tooltipFlag) -> {
            };
            return this;
        }

        public GenericBlockBuilder setFlammability(int flammability) {
            this.flammability = flammability;
            return this;
        }

        public GenericBlockBuilder setFireSpreadSpeed(int fireSpreadSpeed) {
            this.fireSpreadSpeed = fireSpreadSpeed;
            return this;
        }

        public GenericBlockBuilder addToolStateModifier(Predicate<ItemStack> toolPredicate, Function<BlockState, BlockState> resultGetter) {
            toolStateModifiers.put(toolPredicate, resultGetter);
            return this;
        }

        public GenericBlockBuilder addToolStateModifierPillar(Predicate<ItemStack> toolPredicate, Supplier<Block> resultGetter) {
            return addToolStateModifier(toolPredicate, state -> resultGetter.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
        }

        public GenericBlockBuilder setProperties(BlockBehaviour.Properties properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public GenericBlockBuilder setInTabs(List<RegistryObject<CreativeModeTab>> tabs) {
            return (GenericBlockBuilder) super.setInTabs(tabs);
        }

        // -----------------------------------------

        public RegistryObject<Block> buildGeneric() {
            return registryHelper.registry.get().register(name, () -> new JSGTabbedBlock(properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildFallingGeneric() {
            return registryHelper.registry.get().register(name, () -> new JSGTabbedFallingBlock(properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildStairs(Supplier<BlockState> baseBlockState) {
            return registryHelper.registry.get().register(name, () -> new StairBlockTabbed(baseBlockState, properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildSlab() {
            return registryHelper.registry.get().register(name, () -> new SlabBlockTabbed(properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }


        public RegistryObject<Block> buildLeaves() {
            return registryHelper.registry.get().register(name, () -> new LeavesBlockTabbed(properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }


        public RegistryObject<Block> buildFence() {
            return registryHelper.registry.get().register(name, () -> new FenceBlockTabbed(properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }


        public RegistryObject<Block> buildFenceGate(Supplier<SoundEvent> openSound, Supplier<SoundEvent> closeSound) {
            return registryHelper.registry.get().register(name, () -> new FenceGateBlockTabbed(properties, openSound.get(), closeSound.get()) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }


        public RegistryObject<Block> buildButton(BlockSetType type, int duration, boolean arrowCanPress) {
            return registryHelper.registry.get().register(name, () -> new ButtonBlockTabbed(properties, type, duration, arrowCanPress) {

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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }


        public RegistryObject<Block> buildPressurePlate(PressurePlateBlock.Sensitivity sensitivity, BlockSetType type) {
            return registryHelper.registry.get().register(name, () -> new PressurePlateBlockTabbed(sensitivity, properties, type) {

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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildDoor(BlockSetType type) {
            return registryHelper.registry.get().register(name, () -> new DoorBlockTabbed(properties, type) {

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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildTrapdoor(BlockSetType type) {
            return registryHelper.registry.get().register(name, () -> new TrapdoorBlockTabbed(properties, type) {

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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildPillar(MapColor pTopMapColor, MapColor pSideMapColor) {
            return registryHelper.registry.get().register(name, () -> new RotatedPillarBlockTabbed(properties.mapColor((bs) -> bs.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? pTopMapColor : pSideMapColor)) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildGlass(DyeColor beaconColor) {
            return registryHelper.registry.get().register(name, () -> new GlassBlockTabbed(beaconColor, properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<Block> buildGlassPane(DyeColor beaconColor) {
            return registryHelper.registry.get().register(name, () -> new GlassPaneBlockTabbed(beaconColor, properties) {
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
                public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, itemStack, blockGetter, components, tooltipFlag);
                }
            });
        }
    }
}
