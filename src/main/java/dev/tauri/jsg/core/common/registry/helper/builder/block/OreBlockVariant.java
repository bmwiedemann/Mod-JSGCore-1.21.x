package dev.tauri.jsg.core.common.registry.helper.builder.block;


import dev.tauri.jsg.core.common.block.crystal.ICrystalColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Map;
import java.util.function.Supplier;

public enum OreBlockVariant {
    SAND("sand", Blocks.SANDSTONE::defaultBlockState, () -> Blocks.SANDSTONE),
    NETHER("netherrack", Blocks.NETHERRACK::defaultBlockState, () -> Blocks.NETHERRACK),
    DEEPSLATE("deepslate", Blocks.DEEPSLATE::defaultBlockState, () -> Blocks.COBBLED_DEEPSLATE),
    END("endstone", Blocks.END_STONE::defaultBlockState, () -> Blocks.END_STONE),
    STONE(null, Blocks.STONE::defaultBlockState, () -> Blocks.COBBLESTONE),
    SELF("self", null, null);

    @Nullable
    public final String prefix;
    @Nullable
    public final Supplier<BlockState> variantbaseBlockStateSupplier;
    @Nullable
    public final Supplier<ItemLike> drops;

    @ParametersAreNullableByDefault
    OreBlockVariant(String prefix, Supplier<BlockState> variantbaseBlockStateSupplier, Supplier<ItemLike> drops) {
        this.prefix = prefix;
        this.variantbaseBlockStateSupplier = variantbaseBlockStateSupplier;
        this.drops = drops;
    }

    @Nullable
    public BlockState getOriginalBlock(ICrystalColor color, Supplier<Map<ICrystalColor, RegistryObject<Block>>> crystalBlocksSupplier) {
        if (this != SELF && this.variantbaseBlockStateSupplier != null) return this.variantbaseBlockStateSupplier.get();
        if (this == SELF) return crystalBlocksSupplier.get().get(color).get().defaultBlockState();
        return null;
    }

    @Nullable
    public ItemLike getDrop(ICrystalColor color, Supplier<Map<ICrystalColor, RegistryObject<Block>>> crystalBlocksSupplier) {
        if (this != SELF && this.drops != null) return this.drops.get();
        if (this == SELF) return crystalBlocksSupplier.get().get(color).get();
        return null;
    }

    @Nullable
    public static OreBlockVariant fromBlock(BlockState state, Supplier<Map<ICrystalColor, RegistryObject<Block>>> crystalBlocksSupplier) {
        for (var v : values()) {
            if (v.variantbaseBlockStateSupplier == null) {
                for (var e : crystalBlocksSupplier.get().entrySet()) {
                    if (state.is(e.getValue().get())) {
                        return SELF;
                    }
                }
                continue;
            }
            if (state.is(v.variantbaseBlockStateSupplier.get().getBlock())) {
                return v;
            }
        }
        return null;
    }
}
