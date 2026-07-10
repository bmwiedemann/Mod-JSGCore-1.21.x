package dev.tauri.jsg.core.client.screen.tab.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.client.screen.tab.ITab;
import dev.tauri.jsg.core.client.screen.tab.TabSideEnum;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.screen.util.JSGRect2i;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class Tab implements ITab {

    // Container info (position and size)
    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;

    // Tab info (location & position)
    protected int defaultX;
    protected int defaultY;
    protected int openX;
    protected int hiddenX;
    protected int width;
    protected int height;
    protected String tabTitle;
    protected TabSideEnum side;

    // Background texture
    protected ResourceLocation bgTexLocation;
    protected int textureSize;
    protected int bgTexX;
    protected int bgTexY;

    // Icon texture
    protected int iconX;
    protected int iconY;
    protected int iconWidth;
    protected int iconHeight;
    protected int iconTexX;
    protected int iconTexY;

    protected Tab(TabBuilder builder) {
        this.guiLeft = builder.guiLeft;
        this.guiTop = builder.guiTop;
        this.xSize = builder.xSize;
        this.ySize = builder.ySize;

        this.defaultX = builder.defaultX;
        this.defaultY = builder.defaultY;
        this.openX = builder.openX;
        this.hiddenX = builder.hiddenX;
        this.width = builder.width;
        this.height = builder.height;
        this.tabTitle = builder.tabTitle;
        this.side = builder.side;

        this.bgTexLocation = builder.bgTexLocation;
        this.textureSize = builder.textureSize;
        this.bgTexX = builder.bgTexX;
        this.bgTexY = builder.bgTexY;

        this.iconX = builder.iconX;
        this.iconY = builder.iconY;
        this.iconWidth = builder.iconWidth;
        this.iconHeight = builder.iconHeight;
        this.iconTexX = builder.iconTexX;
        this.iconTexY = builder.iconTexY;

        startingOffsetX = defaultX;
    }

    private boolean isVisible = true;

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    private boolean animate = false;
    private int startingOffsetX = 0;
    private boolean isTabOpen = false;
    private boolean isTabHidden = false;

    public boolean isOpen() {
        return isTabOpen;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isHidden() {
        return isTabHidden;
    }

    private float offsetPerTick;
    private long animationStart;
    private int animationTime;

    protected int currentOffsetX = 0;

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isVisible)
            return;

        Minecraft mc = Minecraft.getInstance();
        updateAnimation(mc);

        Font fontRenderer = mc.font;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        ITexture.bindTextureWithMc(bgTexLocation);
        GuiHelper.drawModalRectWithCustomSizedTexture(guiLeft + currentOffsetX, guiTop + defaultY, bgTexX, bgTexY, width, height, textureSize, textureSize);
        GuiHelper.drawModalRectWithCustomSizedTexture(guiLeft + iconX + currentOffsetX, guiTop + defaultY + iconY, iconTexX, iconTexY, iconWidth, iconHeight, textureSize, textureSize);

        graphics.drawString(fontRenderer, Component.literal(tabTitle), guiLeft + currentOffsetX + (side.left() ? 24 : 0) + 4, guiTop + defaultY + 10, 4210752, false);

        RenderSystem.disableBlend();
    }

    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isVisible)
            return;

        if (!isTabOpen && isCursorOnTab(mouseX, mouseY)) {
            graphics.renderTooltip(Minecraft.getInstance().font, List.of(
                    Component.literal(tabTitle)
            ), Optional.empty(), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    public boolean isCursorOnTab(int mouseX, int mouseY) {
        int xOffset = 0;
        if (isTabHidden && hiddenX < defaultX) {
            // hide - slides to the left
            xOffset = 15;
        }

        return isVisible && GuiHelper.isPointInRegion(guiLeft + iconX + currentOffsetX + xOffset, guiTop + defaultY + iconY, iconWidth - (isTabHidden ? 15 : 0), iconHeight, mouseX, mouseY);
    }

    public boolean isCursorOnTabBody(int mouseX, int mouseY) {
        int xOffset = 0;
        if (isTabHidden && hiddenX < defaultX) {
            // hide - slides to the left
            xOffset = 15;
        }

        return isVisible && GuiHelper.isPointInRegion(guiLeft + currentOffsetX + xOffset, guiTop + defaultY, width, height, mouseX, mouseY);
    }

    public void openTab() {
        animateTo(openX, 10);

        isTabHidden = false;
        isTabOpen = true;
    }

    public void closeTab() {
        animateTo(defaultX, isTabOpen ? 10 : 5);

        isTabHidden = false;
        isTabOpen = false;
    }

    public void hideTab() {
        animateTo(hiddenX, isTabOpen ? 10 : 5);

        isTabHidden = true;
        isTabOpen = false;
    }

    public void animateTo(int targetOffsetX, int animationTime) {
        this.animationTime = animationTime;
        startingOffsetX = currentOffsetX;

        offsetPerTick = (float) (targetOffsetX - startingOffsetX) / animationTime;
        animationStart = Objects.requireNonNull(Minecraft.getInstance().level).getGameTime();

        animate = true;
    }

    public void updateAnimation(Minecraft mc) {
        currentOffsetX = startingOffsetX;

        if (animate) {
            float effTick = Objects.requireNonNull(mc.level).getGameTime() - animationStart + mc.getTimer().getGameTimeDeltaPartialTick(true);

            if (effTick < animationTime) {
                currentOffsetX += Math.round(offsetPerTick * effTick);
            } else {
                animate = false;
                currentOffsetX += (int) (offsetPerTick * animationTime);
                startingOffsetX = currentOffsetX;
            }
        }
    }

    public JSGRect2i getArea() {
        int tabHeight = (isTabOpen | animate) ? height : 34;

        if (side.left()) {
            return new JSGRect2i(guiLeft + currentOffsetX, guiTop + defaultY, Math.abs(currentOffsetX), tabHeight);
        }

        // right
        return new JSGRect2i(guiLeft + xSize, guiTop + defaultY, Math.abs(width + currentOffsetX - xSize), tabHeight);
        //return new JSGRect2i(0, 0, 5000, ySize);
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }

    public boolean charTyped(char typedChar, int keyCode) {
        return false;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return false;
    }

    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    public void updateScreen() {

    }

    // ------------------------------------------------------------------------------------------------
    // Interaction

    /**
     * Interact with tab (clicked).
     *
     * @return {@code true} if the Tab will be opening, {@code false} if closing.
     */
    public static boolean tabsInteract(List<Tab> tabs, int tabIndex) {
        Tab tab = tabs.get(tabIndex);

        // Tabs higher than clicked one
        for (Tab tab2 : tabs.subList(0, tabIndex)) {
            if (tab.side == tab2.side) {
                tab2.closeTab();
            }
        }

        // Tabs lower than clicked one
        for (Tab tab2 : tabs.subList(tabIndex + 1, tabs.size())) {
            if (tab.side == tab2.side) {
                if (tab.isOpen())
                    tab2.closeTab();
                else
                    tab2.hideTab();
            }
        }

        if (tab.isOpen()) {
            tab.closeTab();
            return false;
        } else {
            tab.openTab();
            return true;
        }
    }

    /**
     * Sorts tabs so only visible (enabled by some upgrade) occupy space.
     */
    public static void updatePositions(List<Tab> tabs) {
        int yPosLeft = 11;
        int yPosRight = 2;

        for (Tab tab : tabs) {
            if (tab.isVisible()) {
                if (tab.side.left()) {
                    tab.defaultY = yPosLeft;
                    yPosLeft += 22;
                } else {
                    // right side
                    tab.defaultY = yPosRight;
                    yPosRight += 22;
                }
            }
        }
    }


    // ------------------------------------------------------------------------------------------------
    // Builder

    public static abstract class TabBuilder implements ITab.ITabBuilder {

        // Container info (position) and ID
        private int guiLeft;
        private int guiTop;
        private int xSize;
        private int ySize;

        // Tab info (location & position)
        private int defaultX;
        private int defaultY;
        private int openX;
        private int hiddenX;
        private int width;
        private int height;
        private String tabTitle;
        private TabSideEnum side;

        // Background texture
        private ResourceLocation bgTexLocation;
        private int textureSize;
        private int bgTexX;
        private int bgTexY;

        // Icon texture
        private int iconX;
        private int iconY;
        private int iconWidth;
        private int iconHeight;
        private int iconTexX;
        private int iconTexY;

        public TabBuilder setGuiSize(int xSize, int ySize) {
            this.xSize = xSize;
            this.ySize = ySize;

            return this;
        }

        public TabBuilder setGuiPosition(int guiLeft, int guiTop) {
            this.guiLeft = guiLeft;
            this.guiTop = guiTop;

            return this;
        }

        public TabBuilder setTabPosition(int defaultX, int defaultY) {
            this.defaultX = defaultX;
            this.defaultY = defaultY;

            return this;
        }

        public TabBuilder setOpenX(int openX) {
            this.openX = openX;

            return this;
        }

        public TabBuilder setHiddenX(int hiddenX) {
            this.hiddenX = hiddenX;

            return this;
        }

        public TabBuilder setTabSize(int width, int height) {
            this.width = width;
            this.height = height;

            return this;
        }

        public TabBuilder setTabTitle(String tabTitle) {
            this.tabTitle = tabTitle;

            return this;
        }

        public TabBuilder setTabSide(TabSideEnum side) {
            this.side = side;

            return this;
        }

        public TabBuilder setTexture(ResourceLocation bgTexLocation, int texureSize) {
            this.bgTexLocation = bgTexLocation;
            this.textureSize = texureSize;

            return this;
        }

        public TabBuilder setBackgroundTextureLocation(int bgTexX, int bgTexY) {
            this.bgTexX = bgTexX;
            this.bgTexY = bgTexY;

            return this;
        }

        public TabBuilder setIconRenderPos(int iconX, int iconY) {
            this.iconX = iconX;
            this.iconY = iconY;

            return this;
        }

        public TabBuilder setIconSize(int iconWidth, int iconHeight) {
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;

            return this;
        }

        public TabBuilder setIconTextureLocation(int iconTexX, int iconTexY) {
            this.iconTexX = iconTexX;
            this.iconTexY = iconTexY;

            return this;
        }

        public abstract Tab build();
    }


    // ------------------------------------------------------------------------------------------------
    // Tab slot

    public class SlotTab extends SlotHandler {

        public final Slot slot;

        private final UpdateSlotPositionInterface updateSlotPosition;

        public SlotTab(SlotHandler slot, UpdateSlotPositionInterface updateSlotPosition) {
            this(slot, updateSlotPosition, slot.x, slot.y);
        }

        public SlotTab(SlotHandler slot, UpdateSlotPositionInterface updateSlotPosition, int x, int y) {
            super(slot.getItemHandler(), slot.getSlotIndex(), x, y);
            this.slot = slot;
            this.slot.index = slot.getSlotIndex();

            this.updateSlotPosition = updateSlotPosition;
        }

        public void setSlotIndex(int id) {
            slot.index = id;
        }

        @Override
        public boolean isActive() {
            return isTabOpen && !animate && isVisible();
        }

        public SlotTab updatePos() {
            return updateSlotPosition.updatePos(this);
        }

        public SlotTab setXY(int x, int y) {
            return new SlotTab(this, updateSlotPosition, x, y);
        }
    }

    public interface UpdateSlotPositionInterface {
        SlotTab updatePos(SlotTab slotTab);
    }
}
