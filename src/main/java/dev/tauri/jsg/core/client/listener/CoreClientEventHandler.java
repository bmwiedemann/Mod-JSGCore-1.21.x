package dev.tauri.jsg.core.client.listener;

import dev.tauri.jsg.core.common.util.ItemNBT;
import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.LoadersHolder;
import dev.tauri.jsg.core.client.screen.overlay.DebugTextureOverlay;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.Optional;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CoreClientEventHandler {

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        for (var m : InputHandler.KEY_BINDINGS)
            event.register(m);
    }

    @SubscribeEvent
    public static void onResourcesReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((pPreparationBarrier, pResourceManager, pPreparationsProfiler, pReloadProfiler, pBackgroundExecutor, pGameExecutor) ->
                pPreparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    LoadersHolder.load(pReloadProfiler);
                    // turn off music
                    try {
                        Minecraft.getInstance().getSoundManager().stop();
                    } catch (Exception ignored) {
                    }
                }, pBackgroundExecutor));
    }

    @SubscribeEvent
    public static void registerColoredItems(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layerIndex) -> {
            if (layerIndex != 1) return -1;
            if (!ItemNBT.hasTag(stack)) return -1;
            var tag = ItemNBT.getOrCreateTag(stack);
            if (!tag.contains("biome")) return -1;
            var access = Optional.ofNullable(Minecraft.getInstance().level).map(Level::registryAccess)
                    .orElseGet(() -> Optional.ofNullable(Minecraft.getInstance().getConnection()).map(ClientPacketListener::registryAccess)
                            .orElseGet(() -> Optional.ofNullable(Minecraft.getInstance().getSingleplayerServer()).map(MinecraftServer::registryAccess).orElse(null)));
            var biomeLocation = JSGMapping.rl(tag.getString("biome"));
            return PageNotebookItemFilled.getColorForBiome(stack, access, ResourceKey.create(Registries.BIOME, biomeLocation));
        }, CoreItems.NOTEBOOK_PAGE_FILLED.get());
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(dev.tauri.jsg.core.mapping.JSGMapping.rl(JSGCore.MOD_ID, "texture_debug_overlay"), DebugTextureOverlay::render);
    }
}
