package dev.tauri.jsg.core.client;

import dev.tauri.jsg.core.client.screen.config.ConfigScreen;
import dev.tauri.jsg.core.common.config.JSGConfigChild;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.List;

public class ConfigScreenClientRegister {
    public static void register(String modId, List<JSGConfigChild> configChildren) {
        ModLoadingContext.get().getActiveContainer().registerExtensionPoint(IConfigScreenFactory.class,
                (container, screen) -> new ConfigScreen(screen, modId, configChildren));
    }
}
