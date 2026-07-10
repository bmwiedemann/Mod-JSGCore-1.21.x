package dev.tauri.jsg.core.common.config.ingame.widget;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.option.type.IntegerBEConfigOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RangedIntBEConfigOptionWidget extends BEConfigOptionWidget<Integer> {
    protected final ExtendedSlider sliderButton;

    public RangedIntBEConfigOptionWidget(BEConfig config, String optionId, int tabWidth, IntegerBEConfigOption configOption) {
        super(config, optionId, configOption, tabWidth, 16, Component.empty());
        this.sliderButton = new ExtendedSlider(0, 0, tabWidth, 16,
                Component.empty(), Component.empty(),
                configOption.getMin().orElse(Integer.MIN_VALUE), configOption.getMax().orElse(Integer.MAX_VALUE), configOption.getValue(), true) {
            @Override
            protected void applyValue() {
                onValueChangedByUser(getValueInt());
            }

            @Override
            public void setValue(double value) {
                var lastValue = this.value;
                super.setValue(value);
                if (lastValue != value) {
                    applyValue();
                }
            }
        };
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        sliderButton.setX(getX());
        sliderButton.setY(getY() + getTitleHeight());
        sliderButton.visible = this.visible;
        sliderButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return sliderButton.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return sliderButton.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return sliderButton.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public String getDefaultValueToDisplay() {
        return String.valueOf(configOption.getDefaultValue());
    }
}
