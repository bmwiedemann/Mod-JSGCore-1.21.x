package dev.tauri.jsg.core.client.screen.config;

import dev.tauri.jsg.core.common.config.JSGConfigChild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigScreen extends Screen {

    private static final int BACK_BUTTON_WIDTH = 200;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int BACK_BUTTON_TOP_OFFSET = 26;

    public static AbstractButton getBackButton(Screen screen, Screen parentScreen) {
        return Button.builder(CommonComponents.GUI_BACK,
                        (button) -> Minecraft.getInstance().setScreen(parentScreen))
                .bounds((screen.width - BACK_BUTTON_WIDTH) / 2, screen.height - BACK_BUTTON_TOP_OFFSET, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT).build();
    }

    private final Screen parentScreen;
    private final List<JSGConfigChild> configChildren;

    public ConfigScreen(Screen parentScreen, String modId, List<JSGConfigChild> configChildren) {
        super(Component.translatable("gui." + modId + ".config"));
        this.parentScreen = parentScreen;
        this.configChildren = configChildren;
    }

    @Override
    public void init() {
        super.init();

        var y = BACK_BUTTON_TOP_OFFSET;
        for (var configChild : configChildren) {
            this.addRenderableWidget(Button.builder(configChild.getTitle(),
                    (button) -> Minecraft.getInstance().setScreen(new dev.tauri.jsg.core.client.screen.config.ConfigChildScreen(this, configChild))).bounds(this.width / 2 - 100, y, 200, 20).build());
            y += 24;
        }

        this.addRenderableWidget(getBackButton(this, parentScreen));
    }

    public void popScreen() {
        Minecraft.getInstance().setScreen(this.parentScreen);
    }

    @Override
    public void onClose() {
        this.popScreen();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
