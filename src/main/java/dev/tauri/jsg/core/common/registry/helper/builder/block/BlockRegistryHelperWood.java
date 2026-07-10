package dev.tauri.jsg.core.common.registry.helper.builder.block;

import dev.tauri.jsg.core.common.block.core.SaplingBlockTabbed;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockRegistryHelperWood extends RegistryHelper<Block, BlockRegistryHelperWood.WoodBlockBuilder> {
    public BlockRegistryHelperWood(Supplier<DeferredRegister<Block>> registry) {
        super(registry, WoodBlockBuilder::new);
    }

    public static class WoodBlockBuilder extends BlockRegistryHelperGeneric.GenericBlockBuilder {
        public WoodBlockBuilder(RegistryHelper<Block, WoodBlockBuilder> registryHelper, String name) {
            super(registryHelper, name);
        }

        public WoodBlockBuilder(String name, WoodBlockBuilder other) {
            super(name, other);
        }

        @Override
        public WoodBlockBuilder applyTooltip(@NotNull TooltipApplier<Block> tooltipApplier) {
            return (WoodBlockBuilder) super.applyTooltip(tooltipApplier);
        }

        @Override
        public WoodBlockBuilder clearTooltip() {
            return (WoodBlockBuilder) super.clearTooltip();
        }

        @Override
        public WoodBlockBuilder setFlammability(int flammability) {
            return (WoodBlockBuilder) super.setFlammability(flammability);
        }

        @Override
        public WoodBlockBuilder setFireSpreadSpeed(int fireSpreadSpeed) {
            return (WoodBlockBuilder) super.setFireSpreadSpeed(fireSpreadSpeed);
        }

        @Override
        public WoodBlockBuilder addToolStateModifier(Predicate<ItemStack> toolPredicate, Function<BlockState, BlockState> resultGetter) {
            return (WoodBlockBuilder) super.addToolStateModifier(toolPredicate, resultGetter);
        }

        @Override
        public WoodBlockBuilder setProperties(BlockBehaviour.Properties properties) {
            return (WoodBlockBuilder) super.setProperties(properties);
        }

        @Override
        public WoodBlockBuilder setInTabs(List<RegistryObject<CreativeModeTab>> tabs) {
            return (WoodBlockBuilder) super.setInTabs(tabs);
        }


        public RegistryObject<Block> buildSapling(Supplier<AbstractTreeGrower> grower) {
            return registryHelper.registry.get().register(name, () -> new SaplingBlockTabbed(grower.get(), properties) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return flammability;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return fireSpreadSpeed;
                }

                @Override
                public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
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
