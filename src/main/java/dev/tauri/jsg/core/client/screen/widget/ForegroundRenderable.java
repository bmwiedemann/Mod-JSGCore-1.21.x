package dev.tauri.jsg.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public interface ForegroundRenderable {
    boolean renderForeground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered);

    default boolean renderTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        return false;
    }
}
