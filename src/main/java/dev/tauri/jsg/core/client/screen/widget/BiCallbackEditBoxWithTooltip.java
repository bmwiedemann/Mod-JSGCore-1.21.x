package dev.tauri.jsg.core.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class BiCallbackEditBoxWithTooltip extends dev.tauri.jsg.core.client.screen.widget.BiCallbackEditBox implements ForegroundRenderable {
    protected final List<Component> tooltip;

    public BiCallbackEditBoxWithTooltip(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, Consumer<String> onKeyDown, Consumer<String> onChanged, List<Component> tooltip) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage, onKeyDown, onChanged);
        this.tooltip = tooltip;
    }

    @Override
    public boolean renderForeground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        return false;
    }

    @Override
    public boolean renderTooltips(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        if (otherRendered) return false;
        if (isHovered()) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, pMouseX, pMouseY);
            return true;
        }
        return false;
    }
}
