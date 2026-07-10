package dev.tauri.jsg.core.common.integration.jei;

import dev.tauri.jsg.core.client.screen.tab.tabs.TabbedContainerInterface;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class JEIAdvancedGuiHandler implements IGuiContainerHandler<AbstractContainerScreen<?>> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull AbstractContainerScreen<?> containerScreen) {
        if (containerScreen instanceof TabbedContainerInterface tabbed) {
            return tabbed.getGuiExtraAreas();
        }
        return Collections.emptyList();
    }
}
