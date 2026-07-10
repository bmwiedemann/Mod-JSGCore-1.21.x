package dev.tauri.jsg.core.client.screen.widget;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class ScrollableWidget extends AbstractWidget {
    protected double scrollAmount;
    protected boolean scrolling;

    public ScrollableWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    public void renderDecorations(GuiGraphics pGuiGraphics) {
        if (this.scrollbarVisible()) {
            this.renderScrollBar(pGuiGraphics);
        }
    }

    public boolean scrollbarVisible() {
        return this.getInnerHeight() > this.getHeight();
    }

    public void renderScrollBar(GuiGraphics pGuiGraphics) {
        int height = this.getScrollBarHeight();
        int x = this.getX() + this.getWidth() - 8;
        int maxX = this.getX() + this.getWidth();
        int y = Math.max(this.getY(), (int) this.getScrollAmount() * (this.getHeight() - height) / this.getMaxScrollAmount() + this.getY());
        int maxY = y + height;
        pGuiGraphics.fill(x, y, maxX, maxY, 0xFF626161);
        pGuiGraphics.fill(x, y, maxX - 1, maxY - 1, 0xFFA2A2A2);
    }

    public int getMaxScrollAmount() {
        return Math.max(0, this.getContentHeight() - this.getHeight());
    }

    public int getContentHeight() {
        return this.getInnerHeight() + innerPadding();
    }

    public int innerPadding() {
        return 0;
    }

    public int getInnerHeight() {
        return this.getHeight();
    }

    public double scrollRate() {
        return 4.5f;
    }

    public int getScrollBarHeight() {
        return Mth.clamp((int) ((float) (this.getHeight() * this.getHeight()) / (float) this.getContentHeight()), 32, this.getHeight());
    }

    public double getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(double pScrollAmount) {
        this.scrollAmount = Mth.clamp(pScrollAmount, 0.0D, this.getMaxScrollAmount());
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean flag1 = this.scrollbarVisible() && pMouseX >= (double) (this.getX() + this.getWidth() - 8) && pMouseX <= (double) (this.getX() + this.getWidth()) && pMouseY >= (double) this.getY() && pMouseY < (double) (this.getY() + this.getHeight());
        if (flag1 && pButton == 0) {
            this.scrolling = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            if (pMouseY < (double) this.getY()) {
                this.setScrollAmount(0.0D);
            } else if (pMouseY > (double) (this.getY() + this.getHeight())) {
                this.setScrollAmount(this.getMaxScrollAmount());
            } else {
                int i = this.getScrollBarHeight();
                double d0 = Math.max(1, this.getMaxScrollAmount() / (this.getHeight() - i));
                this.setScrollAmount(this.getScrollAmount() + pDragY * d0);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.scrolling = false;
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double pDelta) {
        if (GuiHelper.isPointInRegion(getX(), getY(), getWidth(), getHeight(), pMouseX, pMouseY)) {
            this.setScrollAmount(this.getScrollAmount() - pDelta * this.scrollRate());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean flag = pKeyCode == 265;
        boolean flag1 = pKeyCode == 264;
        if (flag || flag1) {
            double scrollAmountOriginal = this.getScrollAmount();
            this.setScrollAmount(this.getScrollAmount() + (double) (flag ? -1 : 1) * this.scrollRate());
            return scrollAmountOriginal != this.getScrollAmount();
        }
        return false;
    }
}
