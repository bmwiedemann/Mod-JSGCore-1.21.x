package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.capability.ItemEnergyCapabilityProvider;
import dev.tauri.jsg.core.common.power.JSGEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnergyItem extends JSGItem implements ICreativeThing, IMultiItem {
    protected final Supplier<Long> capacity;
    protected final Supplier<Long> maxReceive;
    protected final Supplier<Long> maxExtract;

    public EnergyItem(Supplier<Long> capacity, Properties properties) {
        this(capacity, () -> Long.MAX_VALUE, properties);
    }

    public EnergyItem(Supplier<Long> capacity, Supplier<Long> maxTransfer, Properties properties) {
        this(capacity, maxTransfer, maxTransfer, properties);
    }

    public EnergyItem(Supplier<Long> capacity, Supplier<Long> maxReceive, Supplier<Long> maxExtract, Properties properties) {
        super(properties);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public EnergyItem(Supplier<Long> capacity, Properties properties, RegistryObject<CreativeModeTab> tab) {
        this(capacity, () -> Long.MAX_VALUE, properties, tab);
    }

    public EnergyItem(Supplier<Long> capacity, Supplier<Long> maxTransfer, Properties properties, RegistryObject<CreativeModeTab> tab) {
        this(capacity, maxTransfer, maxTransfer, properties, tab);
    }

    public EnergyItem(Supplier<Long> capacity, Supplier<Long> maxReceive, Supplier<Long> maxExtract, Properties properties, RegistryObject<CreativeModeTab> tab) {
        super(properties, tab);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public EnergyItem(Supplier<Long> capacity, Properties properties, List<RegistryObject<CreativeModeTab>> tabs) {
        this(capacity, () -> Long.MAX_VALUE, properties, tabs);
    }

    public EnergyItem(Supplier<Long> capacity, Supplier<Long> maxTransfer, Properties properties, List<RegistryObject<CreativeModeTab>> tabs) {
        this(capacity, maxTransfer, maxTransfer, properties, tabs);
    }

    public EnergyItem(Supplier<Long> capacity, Supplier<Long> maxReceive, Supplier<Long> maxExtract, Properties properties, List<RegistryObject<CreativeModeTab>> tabs) {
        super(properties, tabs);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        var energyStorageOpt = itemStack.getCapability(ForgeCapabilities.ENERGY, null).resolve();
        return energyStorageOpt.map(energyStorage -> energyStorage.getEnergyStored() > 0).orElse(false);
    }

    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isCreativeOnly() {
        return isCreative();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemEnergyCapabilityProvider(stack, capacity.get(), maxReceive.get(), maxExtract.get(), isCreative());
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return !isCreative();
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        if (isCreative()) return Item.MAX_BAR_WIDTH;
        var energyStorageOpt = itemStack.getCapability(ForgeCapabilities.ENERGY, null).resolve();
        return energyStorageOpt
                .map(energyStorage -> (int) (JSGEnergyStorage.getEnergyPercent(energyStorage) * Item.MAX_BAR_WIDTH))
                .orElse(0);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = getBarWidth(itemStack) / (float) Item.MAX_BAR_WIDTH;
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (isCreative()) return;
        var energyStorageOpt = stack.getCapability(ForgeCapabilities.ENERGY, null).resolve();
        energyStorageOpt.ifPresent(energyStorage -> {
            components.add(Component.literal(ChatFormatting.GRAY + JSGEnergyStorage.energyToString(energyStorage)));
            components.add(Component.literal(ChatFormatting.GRAY + String.format("%.2f", JSGEnergyStorage.getEnergyPercent(energyStorage) * 100) + "%"));
        });
    }

    @Override
    public void addAdditional(CreativeModeTab.Output output) {
        if (isCreative()) return;
        var stack = new ItemStack(this);
        var caps = stack.getCapability(ForgeCapabilities.ENERGY, null).resolve();
        if (caps.isEmpty()) return;
        var energyStorage = caps.get();
        if (!(energyStorage instanceof JSGEnergyStorage itemEnergyStorage)) return;
        itemEnergyStorage.setEnergy(itemEnergyStorage.getTrueMaxEnergyStored(), true);
        output.accept(stack);
    }
}
