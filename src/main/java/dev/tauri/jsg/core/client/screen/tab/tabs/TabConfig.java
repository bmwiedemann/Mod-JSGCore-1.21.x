package dev.tauri.jsg.core.client.screen.tab.tabs;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import net.minecraft.client.gui.GuiGraphics;

import java.util.LinkedList;

public class TabConfig extends dev.tauri.jsg.core.client.screen.tab.tabs.TabScrollAble {
    public final LinkedList<BEConfigOptionWidget<?>> optionsWidgets = new LinkedList<>();

    public BEConfig config;
    private Runnable onTabClose = null;

    protected TabConfig(TabConfigBuilder builder) {
        super(builder);
        this.config = builder.config;
        updateConfig(builder.config, true);
    }

    public static TabConfigBuilder builder() {
        return new TabConfigBuilder();
    }

    @Override
    public double scrollRate() {
        return 4.5f;
    }

    @Override
    protected void renderContents(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        updateConfig(null, false); // update pos of fields
        optionsWidgets.forEach((optionWidget) -> optionWidget.render(pGuiGraphics, pMouseX, pMouseY, 0));
    }

    @Override
    protected void renderContentsFg(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (!isVisible() || !isOpen()) return;
        optionsWidgets.forEach(optionWidget -> optionWidget.renderFg(pGuiGraphics, pMouseX, pMouseY));
    }

    @Override
    public int getInnerHeight() {
        return optionsWidgets.stream().mapToInt(BEConfigOptionWidget::getHeight).sum();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        optionsWidgets.forEach(widget -> widget.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (super.keyTyped(typedChar, keyCode)) {
            return true;
        }
        return optionsWidgets.stream().anyMatch(widget -> widget.keyPressed(typedChar, keyCode, 0));
    }

    @Override
    public boolean charTyped(char character, int intChar) {
        if (super.charTyped(character, intChar)) {
            return true;
        }
        return optionsWidgets.stream().anyMatch(widget -> widget.charTyped(character, intChar));
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
            return true;
        }
        return optionsWidgets.stream().anyMatch(widget -> {
            if (!widget.clicked(pMouseX, pMouseY)) return false;
            return widget.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        });
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseReleased(pMouseX, pMouseY, pButton)) {
            return true;
        }
        return optionsWidgets.stream().anyMatch(widget -> {
            if (!widget.clicked(pMouseX, pMouseY)) return false;
            return widget.mouseReleased(pMouseX, pMouseY, pButton);
        });
    }

    public void setOnTabClose(Runnable onTabClose) {
        this.onTabClose = onTabClose;
    }

    @Override
    public void closeTab() {
        if (onTabClose != null)
            onTabClose.run();
        super.closeTab();
    }

    public BEConfig getConfig() {
        return config;
    }

    public void updateConfig(BEConfig config, boolean resetFields) {
        if (config != null)
            this.config = config;
        if (resetFields) {
            optionsWidgets.clear();
            if (config != null) {
                config.getOptions().forEach((id, option) -> {
                    var widget = option.createGUIWidget(config, getWidth() - innerPadding(), id);
                    if (widget == null) return;
                    optionsWidgets.add(widget);
                });
            }
        }
        var y = 0;
        for (var modifier : optionsWidgets) {
            modifier.setY((int) (y + getY() - scrollAmount()));
            modifier.setX(getX());
            y += modifier.getHeight();
        }
    }

    public static class TabConfigBuilder extends TabBuilder {

        private BEConfig config;

        public TabConfigBuilder setConfig(BEConfig config) {
            this.config = config;
            return this;
        }

        @Override
        public TabConfig build() {
            return new TabConfig(this);
        }
    }
}
