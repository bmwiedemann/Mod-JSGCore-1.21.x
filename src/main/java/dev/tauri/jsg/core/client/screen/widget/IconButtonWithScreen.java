package dev.tauri.jsg.core.client.screen.widget;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

import static dev.tauri.jsg.core.client.screen.util.GuiHelper.isPointInRegion;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IconButtonWithScreen extends ButtonWithIcon implements ForegroundRenderable {
    public ResourceLocation texture;
    public final int u;
    public final int v;
    public final int texSize;

    public final SubScreen subScreen;
    public final int subScreenInitialX;
    public final int subScreenInitialY;

    public IconButtonWithScreen(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration, ResourceLocation iconTexture, int iconU, int iconV, int iconTextureWidth, int iconTextureHeight, ResourceLocation texture, int texSize, int u, int v, SubScreen subScreen) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration, iconTexture, iconU, iconV, iconTextureWidth, iconTextureHeight);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.texSize = texSize;
        this.subScreen = subScreen;
        subScreen.setX(pX + getWidth() + subScreen.getX());
        subScreen.setY(pY + getHeight() + subScreen.getY());
        this.subScreenInitialX = subScreen.getX();
        this.subScreenInitialY = subScreen.getY();
    }

    @Override
    public void setX(int pX) {
        super.setX(pX);
        subScreen.setX(pX + subScreenInitialX);
    }

    @Override
    public void setY(int pY) {
        super.setY(pY);
        subScreen.setY(pY + subScreenInitialY);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }

    public boolean showScreen() {
        return isHovered();
    }

    public boolean updateHover(int mouseX, int mouseY) {
        var hover = isPointInRegion(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY);
        if (isHovered()) {
            hover |= isPointInRegion(subScreen.getX(), subScreen.getY(), subScreen.getWidth(), subScreen.getHeight(), mouseX, mouseY);
            hover |= isPointInRegion(Math.min(getX(), subScreen.getX()), getY(), getWidth() + Math.abs(getX() - subScreen.getX()), getHeight(), mouseX, mouseY);
        }
        if (hover && !isHovered()) {
            subScreen.visitWidgetsInternal(subScreen::addRenderableWidget);
        } else if (!hover && isHovered()) {
            subScreen.visitWidgetsInternal(subScreen::removeWidget);
        }
        return hover;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        this.isHovered = this.visible && updateHover(mouseX, mouseY);
        super.renderWidget(graphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    public boolean renderForeground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        return false;
    }

    @Override
    public boolean renderTooltips(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        if (otherRendered) return false;
        if (!showScreen()) return false;
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 40);
        GuiHelper.blitNineSliced(graphics, texture, subScreen.getX() - 2, subScreen.getY() - 2, subScreen.getWidth() + 2, subScreen.getHeight() + 2,
                2, 2, 2, 2, getWidth(), getHeight(), u, v, texSize, texSize);
        subScreen.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.pose().popPose();
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (showScreen() && subScreen.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (showScreen() && subScreen.mouseReleased(pMouseX, pMouseY, pButton)) return true;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (showScreen() && subScreen.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) return true;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (showScreen() && subScreen.mouseScrolled(pMouseX, pMouseY, pDelta)) return true;
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if (showScreen())
            subScreen.mouseMoved(pMouseX, pMouseY);
        super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (showScreen() && subScreen.charTyped(pCodePoint, pModifiers)) return true;
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (showScreen() && subScreen.keyPressed(pKeyCode, pScanCode, pModifiers)) return true;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
