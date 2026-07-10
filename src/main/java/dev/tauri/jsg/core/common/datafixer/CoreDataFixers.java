package dev.tauri.jsg.core.common.datafixer;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.MissingMappingsEvent;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CoreDataFixers {
    @SubscribeEvent
    public static void onDataFix(MissingMappingsEvent event) {
        CoreItemsDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.ITEMS, JSGCore.MOD_ID));
    }
}
