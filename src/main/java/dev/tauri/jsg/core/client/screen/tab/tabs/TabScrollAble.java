package dev.tauri.jsg.core.client.screen.tab.tabs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public abstract class TabScrollAble extends Tab {
    private double scrollAmount;
    private boolean scrolling;

    protected TabScrollAble(TabBuilder builder) {
        super(builder);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);
        if (!isVisible())
            return;
        graphics.enableScissor(this.getX() - 2, this.getY() - 1, this.getX() + this.getWidth() + 1, this.getY() + this.getHeight() + 1);
        graphics.pose().pushPose();
        this.renderContents(graphics, mouseX, mouseY);
        graphics.pose().popPose();
        graphics.disableScissor();
        this.renderContentsFg(graphics, mouseX, mouseY);
        this.renderDecorations(graphics);
    }

    @Override
    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderFg(graphics, mouseX, mouseY);
    }

    @Override
    public void closeTab() {
        super.closeTab();
        this.scrolling = false;
        this.scrollAmount = 0;
    }

    public void mouseClicked(int pMouseX, int pMouseY, int pButton) {
        if (!isVisible()) {
            return;
        } else {
            boolean flag1 = this.scrollbarVisible() && pMouseX >= (double) (this.getX() + this.getWidth()) && pMouseX <= (double) (this.getX() + this.getWidth() + 8) && pMouseY >= (double) this.getY() && pMouseY < (double) (this.getY() + this.getHeight());
            if (flag1 && pButton == 0) {
                this.scrolling = true;
                return;
            }
        }
        super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean flag = pKeyCode == 265;
        boolean flag1 = pKeyCode == 264;
        if (flag || flag1) {
            double scrollAmountOriginal = this.scrollAmount;
            this.setScrollAmount(this.scrollAmount + (double) (flag ? -1 : 1) * this.scrollRate());
            return scrollAmountOriginal != this.scrollAmount;
        }

        return false;
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (!this.isVisible()) {
            return false;
        } else {
            this.setScrollAmount(this.scrollAmount - pDelta * this.scrollRate());
            return true;
        }
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.isVisible() && this.scrolling) {
            if (pMouseY < (double) this.getY()) {
                this.setScrollAmount(0.0D);
            } else if (pMouseY > (double) (this.getY() + this.getHeight())) {
                this.setScrollAmount(this.getMaxScrollAmount());
            } else {
                int i = this.getScrollBarHeight();
                double d0 = Math.max(1, this.getMaxScrollAmount() / (this.getHeight() - i));
                this.setScrollAmount(this.scrollAmount + pDragY * d0);
            }

            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.scrolling = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    protected boolean withinContentAreaTopBottom(int pTop, int pBottom) {
        return (double) pBottom - this.scrollAmount >= (double) this.getY() && (double) pTop - this.scrollAmount <= (double) (this.getY() + this.getHeight());
    }

    protected boolean withinContentAreaPoint(double pX, double pY) {
        return pX >= (double) this.getX() && pX < (double) (this.getX() + this.getWidth()) && pY >= (double) this.getY() && pY < (double) (this.getY() + this.getHeight());
    }

    public int getY() {
        return guiTop + defaultY + 20;
    }

    public int getX() {
        return guiLeft + currentOffsetX + iconWidth + innerPadding();
    }

    public int getWidth() {
        return width - iconWidth - innerPadding() - 10;
    }

    public int getHeight() {
        return height - 6 - 20;
    }

    protected boolean scrollbarVisible() {
        return this.getInnerHeight() > this.getHeight();
    }

    protected void renderScrollBar(GuiGraphics pGuiGraphics) {
        int height = this.getScrollBarHeight();
        int x = this.getX() + this.getWidth();
        int maxX = this.getX() + this.getWidth() + 8;
        int y = Math.max(this.getY(), (int) this.scrollAmount * (this.getHeight() - height) / this.getMaxScrollAmount() + this.getY());
        int maxY = y + height;
        pGuiGraphics.fill(x, y, maxX, maxY,  0xFF626161);
        pGuiGraphics.fill(x, y, maxX - 1, maxY - 1, 0xFFA2A2A2);
    }

    public int getScrollBarHeight() {
        return Mth.clamp((int) ((float) (this.getHeight() * this.getHeight()) / (float) this.getContentHeight()), 32, this.getHeight());
    }

    protected void renderDecorations(GuiGraphics pGuiGraphics) {
        if (this.scrollbarVisible()) {
            this.renderScrollBar(pGuiGraphics);
        }
    }

    public int innerPadding() {
        return 8;
    }

    protected int totalInnerPadding() {
        return this.innerPadding() * 2;
    }

    protected double scrollAmount() {
        return this.scrollAmount;
    }

    protected void setScrollAmount(double pScrollAmount) {
        this.scrollAmount = Mth.clamp(pScrollAmount, 0.0D, this.getMaxScrollAmount());
    }

    public int getContentHeight() {
        return this.getInnerHeight() + innerPadding();
    }

    protected int getMaxScrollAmount() {
        return Math.max(0, this.getContentHeight() - this.getHeight());
    }

    public abstract int getInnerHeight();

    public abstract double scrollRate();

    protected abstract void renderContents(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY);

    protected abstract void renderContentsFg(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY);
}
