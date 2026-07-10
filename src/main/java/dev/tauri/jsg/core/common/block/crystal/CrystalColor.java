package dev.tauri.jsg.core.common.block.crystal;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public enum CrystalColor implements dev.tauri.jsg.core.common.block.crystal.ICrystalColor {
    BLUE(DyeColor.BLUE),
    ENDER(DyeColor.CYAN),
    PEGASUS(DyeColor.LIGHT_BLUE),
    RED(DyeColor.RED),
    WHITE(DyeColor.WHITE),
    YELLOW(DyeColor.YELLOW);


    public final DyeColor color;

    CrystalColor(DyeColor color) {
        this.color = color;
    }

    @Nullable
    public static ICrystalColor fromBlock(BlockState state, Supplier<Map<ICrystalColor, RegistryObject<Block>>> crystalBlocksSupplier) {
        for (var e : crystalBlocksSupplier.get().entrySet()) {
            if (state.is(e.getValue().get())) {
                return e.getKey();
            }
        }
        return null;
    }

    @Nullable
    public static ICrystalColor fromItem(ItemStack item, Supplier<Map<ICrystalColor, RegistryObject<? extends Item>>> crystalSeedsSupplier) {
        for (var e : crystalSeedsSupplier.get().entrySet()) {
            if (item.is(e.getValue().get())) {
                return e.getKey();
            }
        }
        return null;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }
}
