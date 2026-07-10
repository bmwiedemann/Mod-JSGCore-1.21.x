package dev.tauri.jsg.core.common.block.cartouche;

import dev.tauri.jsg.core.client.CoreModelsHolder;
import dev.tauri.jsg.core.client.IModelsHolder;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum CartoucheType {
    SIX("six_symbol_", () -> CoreModelsHolder.CARTOUCHE_PLATE_6, 6, false, 2),
    SEVEN("seven_symbol_", () -> CoreModelsHolder.CARTOUCHE_PLATE_7, 7, false, 2),
    EIGHT("eight_symbol_", () -> CoreModelsHolder.CARTOUCHE_PLATE_8, 8, false, 3),
    SEVEN_POO("seven_symbol_", "_with_origin", () -> CoreModelsHolder.CARTOUCHE_PLATE_7_POO, 6, true, 3),
    EIGHT_POO("eight_symbol_", "_with_origin", () -> CoreModelsHolder.CARTOUCHE_PLATE_8_POO, 7, true, 3),
    NINE_POO("nine_symbol_", "_with_origin", () -> CoreModelsHolder.CARTOUCHE_PLATE_9_POO, 8, true, 3);

    public final String prefix;
    public final String suffix;
    public final Supplier<IModelsHolder> model;
    public final boolean hasPoo;
    public final int symbolsCount; // doesn't include POO
    public final int height;

    CartoucheType(String prefix, Supplier<IModelsHolder> model, int symbolsCount, boolean hasPoo, int height) {
        this(prefix, "", model, symbolsCount, hasPoo, height);
    }

    CartoucheType(String prefix, String suffix, Supplier<IModelsHolder> model, int symbolsCount, boolean hasPoo, int height) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.model = model;
        this.symbolsCount = symbolsCount;
        this.hasPoo = hasPoo;
        this.height = height;
    }

    public JSGAxisAlignedBB box(boolean renderBox) {
        var box = new JSGAxisAlignedBB(0.15, 0, 0, 0.85, 1, 0.1);
        if (!renderBox) return box;
        return box.grow(0, height - 1, 0);
    }

    public static Map<CartoucheType, RegistryObject<Block>> registerTypes(Supplier<DeferredRegister<Block>> register, String baseName, Supplier<BlockState> material) {
        var map = new HashMap<CartoucheType, RegistryObject<Block>>();
        for (var t : values()) {
            map.put(t, register.get().register(t.prefix + baseName + t.suffix, () -> new CartoucheBlock(material, t)));
        }
        return map;
    }
}
