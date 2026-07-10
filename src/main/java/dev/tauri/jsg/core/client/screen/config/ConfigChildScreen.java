package dev.tauri.jsg.core.client.screen.config;

import dev.tauri.jsg.core.common.config.JSGConfigChild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class ConfigChildScreen extends Screen {
    public final JSGConfigChild configChild;
    public final Screen parentScreen;

    public static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    public static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    public static final int OPTIONS_LIST_ITEM_HEIGHT = 25 + 10;

    private ConfigList configList;

    public ConfigChildScreen(Screen parentScreen, JSGConfigChild configChild) {
        super(configChild.getTitle());
        this.parentScreen = parentScreen;
        this.configChild = configChild;
    }

    @Override
    public void init() {
        super.init();
        this.configList = new ConfigList(configChild, Minecraft.getInstance(), this.width, this.height, OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET, OPTIONS_LIST_ITEM_HEIGHT);
        this.addWidget(this.configList);
        this.addRenderableWidget(ConfigScreen.getBackButton(this, parentScreen));
    }

    @Override
    public void tick() {
        super.tick();
        configList.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        this.configList.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
