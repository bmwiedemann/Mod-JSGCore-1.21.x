package dev.tauri.jsg.core.datagen;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.datagen.loot.JSGLootTableProvider;
import dev.tauri.jsg.core.datagen.tag.*;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class JSGDataGenerators {
    @SubscribeEvent
    public static void generate(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var exFileHelper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new dev.tauri.jsg.core.datagen.JSGRecipeProvider(output));
        generator.addProvider(event.includeServer(), JSGLootTableProvider.create(output));

        generator.addProvider(event.includeClient(), new JSGBlockStateProvider(output, exFileHelper));
        generator.addProvider(event.includeClient(), new JSGItemModelProvider(output, exFileHelper));

        var blockTagGenerator = generator.addProvider(event.includeServer(), new JSGBlockTagGenerator(output, lookupProvider, exFileHelper));
        generator.addProvider(event.includeServer(), new JSGItemTagGenerator(output, lookupProvider, blockTagGenerator.contentsGetter(), exFileHelper));

        generator.addProvider(event.includeServer(), new JSGBiomeTagGenerator(output, lookupProvider, exFileHelper));
        generator.addProvider(event.includeServer(), new JSGFluidTagGenerator(output, lookupProvider, exFileHelper));
        generator.addProvider(event.includeServer(), new JSGStructureTagGenerator(output, lookupProvider, exFileHelper));

        generator.addProvider(event.includeServer(), JSGAdvancementProvider.create(output, lookupProvider, exFileHelper));
    }
}
