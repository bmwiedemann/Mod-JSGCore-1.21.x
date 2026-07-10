package dev.tauri.jsg.core.common.datafixer;

import dev.tauri.jsg.core.JSGCore;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid = JSGCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoreDataFixers {
    @SubscribeEvent
    public static void onDataFix(MissingMappingsEvent event) {
        CoreItemsDataFixer.fixMappings(event.getMappings(ForgeRegistries.Keys.ITEMS, JSGCore.MOD_ID));
    }
}
