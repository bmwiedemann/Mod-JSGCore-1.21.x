package dev.tauri.jsg.core.common.config.ingame.widget;

import dev.tauri.jsg.core.client.screen.widget.FilteredEditBox;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.option.type.IntegerBEConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Scanner;

@ParametersAreNonnullByDefault
public class IntegerBEConfigOptionWidget extends BEConfigOptionWidget<Integer> {
    protected final EditBox textField;
    private boolean init = false;

    public IntegerBEConfigOptionWidget(BEConfig config, String optionId, int tabWidth, IntegerBEConfigOption configOption) {
        super(config, optionId, configOption, tabWidth, 16, Component.empty());
        this.textField = new FilteredEditBox(Minecraft.getInstance().font, 0, 0, tabWidth, 16, Component.empty(), (v) -> {
            if (v.isEmpty()) return;
            if (!init) return;
            onValueChangedByUser(Integer.parseInt(v));
        }, (v) -> {
            var sc = new Scanner(v.trim());
            if (!sc.hasNextInt(10)) return false;
            sc.nextInt(10);
            return !sc.hasNext();
        });
        textField.setValue(String.valueOf(configOption.getValue()));
        init = true;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        textField.setX(getX());
        textField.setY(getY() + getTitleHeight());
        textField.visible = this.visible;
        textField.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return textField.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return textField.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return textField.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return textField.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return textField.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public String getDefaultValueToDisplay() {
        return String.valueOf(configOption.getDefaultValue());
    }
}
