package dev.tauri.jsg.core.common.config.ingame.widget;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.option.type.EnumLikeBEConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class EnumBEConfigOptionWidget<T> extends BEConfigOptionWidget<T> {
    protected final CycleButton<T> cycleButton;
    protected final Supplier<String> defaultName;

    public EnumBEConfigOptionWidget(BEConfig config, String optionId, int tabWidth, EnumLikeBEConfigOption<T> configOption) {
        super(config, optionId, configOption, tabWidth, 16, Component.empty());
        this.defaultName = () -> configOption.name(configOption.getDefaultValue());
        this.cycleButton = new CycleButton.Builder<T>(val -> Component.literal(configOption.name(val)))
                .withValues(configOption.values())
                .withInitialValue(configOption.getValue())
                .displayOnlyValue()
                .create(0, 0, tabWidth, 16, Component.empty(), (cycleButton, value) -> {
                    cycleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                    onValueChangedByUser(value);
                });
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        cycleButton.setX(getX());
        cycleButton.setY(getY() + getTitleHeight());
        cycleButton.visible = this.visible;
        cycleButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return cycleButton.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public String getDefaultValueToDisplay() {
        return defaultName.get();
    }
}
