package dev.tauri.jsg.core.client;

import dev.tauri.jsg.core.client.screen.config.ConfigScreen;
import dev.tauri.jsg.core.common.config.JSGConfigChild;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.fml.ModLoadingContext;

import java.util.List;

public class ConfigScreenClientRegister {
    public static void register(String modId, List<JSGConfigChild> configChildren) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((mc, screen) ->
                        new ConfigScreen(screen, modId, configChildren)
                )
        );
    }
}
