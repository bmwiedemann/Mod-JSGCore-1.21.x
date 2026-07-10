package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.renderer.blockentity.cartouche.CartoucheRenderer;
import dev.tauri.jsg.core.common.blockentity.CartoucheBE;
import dev.tauri.jsg.core.common.blockentity.FluidCauldronBE;
import dev.tauri.jsg.core.common.registry.helper.FluidHelper;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import net.minecraft.Util;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class CoreBlockEntities {
    public static final RegistryObject<BlockEntityType<CartoucheBE>> CARTOUCHE = JSGCore.REGISTRY_HELPER.be().register("cartouche", RegistryHelper.beSupplier(CartoucheBE::new, Util.make(new ArrayList<>(), (list) -> {
        for (var types : CoreBlocks.CARTOUCHES.values()) {
            list.addAll(types.values());
        }
    })));
    public static final RegistryObject<BlockEntityType<FluidCauldronBE>> FLUID_CAULDRON = JSGCore.REGISTRY_HELPER.be().register("fluid_cauldron", RegistryHelper.beSupplier(FluidCauldronBE::new, Util.make(new ArrayList<>(), (list) -> {
        for (var fluid : FluidHelper.getMoltenFluids().values()) {
            list.add(fluid.cauldron);
        }
    })));

    public static void init() {
        JSGCore.REGISTRY_HELPER.beRenderers(() -> List.of(
                new RegistryHelper.BlockEntityRendererPair<>(CARTOUCHE.get(), CartoucheRenderer::new)
        ));
    }
}
