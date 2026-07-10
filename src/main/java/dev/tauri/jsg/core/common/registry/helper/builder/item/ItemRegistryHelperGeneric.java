package dev.tauri.jsg.core.common.registry.helper.builder.item;

import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.item.EnergyItem;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.item.JSGSpawnEggItem;
import dev.tauri.jsg.core.common.item.JSGUpgradeItem;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryHelper;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryObjectBuilder;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.common.util.IUpgrade;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

public class ItemRegistryHelperGeneric extends RegistryHelper<Item, ItemRegistryHelperGeneric.GenericItemBuilder> {
    public ItemRegistryHelperGeneric(Supplier<DeferredRegister<Item>> registry) {
        super(registry, GenericItemBuilder::new);
    }

    public static class GenericItemBuilder extends RegistryObjectBuilder<RegistryHelper<Item, ? extends GenericItemBuilder>> {
        public GenericItemBuilder(RegistryHelper<Item, ? extends GenericItemBuilder> registryHelper, String name) {
            super(registryHelper, name);
        }

        public GenericItemBuilder(String name, GenericItemBuilder other) {
            super(name, other);
            this.tooltipApplier = other.tooltipApplier;
            this.properties = other.properties;
            this.maxStack = other.maxStack;
        }

        protected TooltipApplier<Item> tooltipApplier = (item, itemStack, blockGetter, components, tooltipFlag) -> ItemHelper.applyGenericToolTip(item.getDescriptionId(), components, tooltipFlag);
        protected Item.Properties properties = new Item.Properties();
        protected int maxStack = 64;

        public GenericItemBuilder applyTooltip(@NotNull TooltipApplier<Item> tooltipApplier) {
            this.tooltipApplier = tooltipApplier;
            return this;
        }

        public GenericItemBuilder applyTooltip(@Nonnull Supplier<List<Component>> tooltip, @Nullable I18n.AdvancedTooltip tooltipAdvanced) {
            return applyTooltip((item, itemStack, blockGetter, components, tooltipFlag) -> {
                ItemHelper.applyToolTip(tooltip.get(), tooltipAdvanced, components, tooltipFlag);
            });
        }

        public GenericItemBuilder clearTooltip() {
            this.tooltipApplier = (item, itemStack, blockGetter, components, tooltipFlag) -> {
            };
            return this;
        }

        public GenericItemBuilder setProperties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        public GenericItemBuilder setMaxStack(int maxStack) {
            this.maxStack = maxStack;
            return this;
        }

        @Override
        public GenericItemBuilder setInTabs(List<RegistryObject<CreativeModeTab>> tabs) {
            return (GenericItemBuilder) super.setInTabs(tabs);
        }

        // -----------------------------------------

        public RegistryObject<JSGItem> buildGeneric() {
            return registryHelper.registry.get().register(name, () -> new JSGItem(properties.stacksTo(maxStack), tabs) {
                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, stack, level, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<JSGItem> buildUpgrade(Supplier<IUpgrade> upgrade) {
            return registryHelper.registry.get().register(name, () -> new JSGUpgradeItem(properties.stacksTo(maxStack), tabs, upgrade) {
                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
                    tooltipApplier.apply(this, stack, level, components, tooltipFlag);
                }
            });
        }

        public RegistryObject<JSGItem> buildDurability(int durability, boolean shouldStayInCrafting) {
            return registryHelper.registry.get().register(name, () -> {
                Item.Properties props = properties.stacksTo(maxStack);
                if (durability > 0)
                    props.durability(durability);
                return new JSGItem(props, tabs) {

                    @Override
                    @ParametersAreNonnullByDefault
                    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
                        return true;
                    }

                    @Override
                    public boolean isDamageable(ItemStack stack) {
                        return true;
                    }

                    @Override
                    @ParametersAreNonnullByDefault
                    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
                        tooltipApplier.apply(this, stack, level, components, tooltipFlag);
                        components.add(Component.empty());
                        components.add(Component.literal(String.format("%.2f", (((double) (getMaxDamage(stack) - getDamage(stack)) / ((double) getMaxDamage(stack))) * 100)) + "%").withStyle(ChatFormatting.GRAY));
                    }

                    @Nonnull
                    @Override
                    public ItemStack getDefaultInstance() {
                        ItemStack itemStack = new ItemStack(this);
                        setDamage(itemStack, 0);
                        return itemStack;
                    }

                    @Override
                    public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack, @Nonnull Enchantment enchantment) {
                        return (enchantment instanceof DigDurabilityEnchantment);
                    }

                    @Override
                    @SuppressWarnings("deprecation")
                    public int getEnchantmentValue() {
                        return 3;
                    }

                    @Override
                    public boolean hasCraftingRemainingItem(@Nonnull ItemStack stack) {
                        return shouldStayInCrafting;
                    }

                    @Override
                    public void setDamage(ItemStack stack, int damage) {
                        super.setDamage(stack, damage);
                        if (getMaxDamage(stack) <= damage) stack.setCount(0);
                    }

                    @Override
                    public int getMaxDamage(ItemStack stack) {
                        return durability;
                    }

                    @Nonnull
                    @Override
                    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
                        ItemStack it = itemStack.copy();
                        it.setDamageValue(itemStack.getDamageValue() + 1);
                        return it;
                    }
                };
            });

        }

        public RegistryObject<JSGSpawnEggItem> buildSpawnEgg(Supplier<? extends EntityType<? extends Mob>> entityType, int color1, int color2) {
            return registryHelper.registry.get().register(name, () -> new JSGSpawnEggItem(entityType, color1, color2, properties));
        }

        public RegistryObject<EnergyItem> buildEnergy(Supplier<Long> capacity) {
            return buildEnergy(capacity, () -> Long.MAX_VALUE);
        }

        public RegistryObject<EnergyItem> buildEnergy(Supplier<Long> capacity, Supplier<Long> maxTransfer) {
            return buildEnergy(capacity, maxTransfer, maxTransfer);
        }

        public RegistryObject<EnergyItem> buildEnergy(Supplier<Long> capacity, Supplier<Long> maxReceive, Supplier<Long> maxExtract) {
            return registryHelper.registry.get().register(name, () -> new EnergyItem(capacity, maxReceive, maxExtract, properties.stacksTo(maxStack), tabs));
        }

        public RegistryObject<EnergyItem> buildEnergyCreative() {
            return registryHelper.registry.get().register(name, () -> new EnergyItem(() -> (long) Integer.MAX_VALUE, () -> Long.MAX_VALUE, () -> Long.MAX_VALUE, properties.stacksTo(maxStack), tabs) {
                @Override
                public boolean isCreative() {
                    return true;
                }
            });
        }
    }
}
