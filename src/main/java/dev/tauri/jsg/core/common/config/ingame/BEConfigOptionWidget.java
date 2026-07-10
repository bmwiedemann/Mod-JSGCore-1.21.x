package dev.tauri.jsg.core.common.config.ingame;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public abstract class BEConfigOptionWidget<T> extends AbstractWidget {
    public static final int TITLE_HEIGHT = 12;

    protected IBEConfigOption<T> configOption;
    protected BEConfig config;
    protected String optionId;

    public BEConfigOptionWidget(BEConfig config, String optionId, IBEConfigOption<T> configOption, int pWidth, int pHeight, Component pMessage) {
        super(0, 0, pWidth, pHeight + TITLE_HEIGHT, pMessage);
        this.configOption = configOption;
        this.config = config;
        this.optionId = optionId;
    }

    public void onValueChangedByUser(T newValue) {
        config.getOption(optionId).ifPresent(option -> {
            if (option.parseAndSetValue(newValue))
                option.setChanged();
        });
    }

    public Component getComment() {
        return Component.translatable("gui.jsg.ingame_config." + optionId + ".comment");
    }

    public Component getTitle() {
        return Component.translatable("gui.jsg.ingame_config." + optionId + ".title");
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderTitle(pGuiGraphics);
    }

    public void renderTitle(GuiGraphics graphics) {
        GuiHelper.renderScrollingStringLeftAligned(graphics, Minecraft.getInstance().font, getTitle(), getX(), getX() + getWidth(), getY() + 2, 0xffffff, true);
    }

    public abstract String getDefaultValueToDisplay();

    public int getTitleHeight() {
        return TITLE_HEIGHT;
    }

    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY) {
        if (isHovered()) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(
                    getComment(),
                    Component.empty(),
                    Component.translatable("gui.jsg.ingame_config.default_value", getDefaultValueToDisplay()).withStyle(Style.EMPTY.applyFormats(ChatFormatting.ITALIC, ChatFormatting.GRAY))
            ), mouseX, mouseY);
        }
    }

    @Override
    public boolean clicked(double pMouseX, double pMouseY) {
        return super.clicked(pMouseX, pMouseY);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
