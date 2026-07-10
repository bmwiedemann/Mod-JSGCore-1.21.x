package dev.tauri.jsg.core.common.config.ingame.widget;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.IBEConfigOption;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

public class BooleanBEConfigOptionWidget extends BEConfigOptionWidget<Boolean> {
    protected final CycleButton<Boolean> cycleButton;

    public BooleanBEConfigOptionWidget(BEConfig config, String optionId, int tabWidth, IBEConfigOption<Boolean> configOption) {
        super(config, optionId, configOption, tabWidth, 16, Component.empty());
        this.cycleButton = CycleButton.booleanBuilder(
                        Component.translatable("gui.jsg.true").withStyle(ChatFormatting.GREEN),
                        Component.translatable("gui.jsg.false").withStyle(ChatFormatting.RED))
                .withInitialValue(configOption.getValue())
                .displayOnlyValue()
                .create(0, 0, tabWidth, 16, Component.empty(), (cycleButton, isTrue) -> {
                    cycleButton.playDownSound(Minecraft.getInstance().getSoundManager());
                    onValueChangedByUser(isTrue);
                });
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        cycleButton.setX(getX());
        cycleButton.setY(getY() + getTitleHeight());
        cycleButton.visible = visible;
        cycleButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return cycleButton.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public String getDefaultValueToDisplay() {
        return configOption.getDefaultValue() ? Component.translatable("gui.jsg.true").getString() : Component.translatable("gui.jsg.false").getString();
    }
}
