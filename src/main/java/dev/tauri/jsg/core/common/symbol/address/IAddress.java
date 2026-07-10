package dev.tauri.jsg.core.common.symbol.address;

import dev.tauri.jsg.core.common.entity.IAddressNotebookPageData;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface IAddress extends INBTSerializable<CompoundTag> {
    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag compound);

    @Override
    default CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        return serializeNBT();
    }

    @Override
    default void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag compound) {
        deserializeNBT(compound);
    }

    SymbolInterface get(int symbolIndex);

    int getSize();

    SymbolType<?> getSymbolType();

    CompoundTag getCompound(int[] symbolsToDisplay, ResourceKey<Biome> biome, @Nullable PointOfOrigin pointOfOrigin);

    NotebookPageType<? extends IAddressNotebookPageData> getNotebookPageType();

    IAddress addOriginIfMissingAndImmutable();
}
