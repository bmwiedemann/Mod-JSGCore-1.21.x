package dev.tauri.jsg.core.client.screen.tab;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.screen.tab.tabs.*;
import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class TabbedContainerScreen<T extends AbstractContainerMenu & dev.tauri.jsg.core.client.screen.tab.OpenTabHolderInterface> extends AbstractContainerScreen<T> implements TabbedContainerInterface {
    public static final ResourceLocation CONFIG_TAB_BG = JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/tabs_general.png");
    public static final ResourceLocation OVERLAY_TAB_BG = JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/tabs_general.png");
    protected final List<Tab> tabs = new ArrayList<>();


    public TabbedContainerScreen(T container, Inventory playerInventory, Component title, int width, int height) {
        super(container, playerInventory, title);
        this.imageWidth = width;
        this.imageHeight = height;
        this.width = width;
        this.height = height;
    }

    @Override
    public void init() {
        super.init();
        tabs.clear();
        initTabs(tabs);
    }

    protected abstract void initTabs(List<Tab> tabs);

    public void renderTabsBg(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Tab.updatePositions(tabs);
        graphics.pose().pushPose();
        for (Tab tab : tabs) {
            tab.render(graphics, mouseX, mouseY);
        }
        graphics.pose().popPose();
    }

    public void renderTabsFg(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        for (Tab tab : tabs) {
            tab.renderFg(graphics, mouseX, mouseY);
        }
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);

            if (tab.isCursorOnTab((int) mouseX, (int) mouseY)) {
                Tab.tabsInteract(tabs, i);
                menu.updateTabSlots();
                break;
            }

        }
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                tab.mouseClicked((int) mouseX, (int) mouseY, mouseButton);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double v, double v1, double v2) {
        super.mouseScrolled(v, v1, v2);
        int wheel = (int) v2;
        if (wheel != 0) {
            for (Tab tab : tabs) {
                if (tab instanceof TabScrollAble && tab.isVisible() && tab.isOpen()) {
                    if (tab.isCursorOnTabBody((int) v, (int) v1)) {
                        ((TabScrollAble) tab).mouseScrolled(v, v1, v2);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int t) {
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                if (tab.keyTyped((char) typedChar, keyCode))
                    return true;
            }
        }
        return super.keyPressed(typedChar, keyCode, t);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                if (tab.charTyped(typedChar, keyCode))
                    return true;
            }
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                if (tab.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))
                    return true;
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                if (tab.mouseReleased(pMouseX, pMouseY, pButton))
                    return true;
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public List<Rect2i> getGuiExtraAreas() {
        return tabs.stream()
                .map(Tab::getArea)
                .collect(Collectors.toList());
    }


    public static TabConfig createConfigTab(BEConfig config, int guiwidth, int guiheight, int leftPos, int topPos) {
        return (TabConfig) TabConfig.builder()
                .setConfig(config)
                .setGuiSize(guiwidth, guiheight)
                .setGuiPosition(leftPos, topPos)
                .setTabPosition(-21, 11 + 22 * 3)
                .setOpenX(-(guiwidth - 3))
                .setHiddenX(-6)
                .setTabSize(guiwidth - 3, 96)
                .setTabTitle(I18n.format("gui.configuration"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(CONFIG_TAB_BG, 512)
                .setBackgroundTextureLocation(176, 165)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(326, 66).build();
    }

    public static TabBiomeOverlay createOverlayTab(int guiwidth, int guiheight, int leftPos, int topPos) {
        return (TabBiomeOverlay) TabBiomeOverlay.builder()
                .setSlotTexture(6, 179)
                .setGuiSize(guiwidth, guiheight)
                .setGuiPosition(leftPos, topPos)
                .setTabPosition(176 - 107, 2)
                .setOpenX(176)
                .setHiddenX(54)
                .setTabSize(128, 51)
                .setTabTitle(I18n.format("gui.biome_overlay"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(OVERLAY_TAB_BG, 512)
                .setBackgroundTextureLocation(176 + 24, 113)
                .setIconRenderPos(107, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 3).build();
    }
}
