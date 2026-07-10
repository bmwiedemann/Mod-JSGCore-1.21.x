package dev.tauri.jsg.core.client.screen.widget;

import com.google.common.collect.Lists;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.client.gui.screens.Screen.hasShiftDown;

public class SubScreen extends dev.tauri.jsg.core.client.screen.widget.ScrollableWidget implements ContainerEventHandler {
    @Nullable
    protected GuiEventListener focused;
    protected boolean isDragging;
    public final boolean scissor;
    protected final List<GuiEventListener> children = Lists.newArrayList();
    protected final List<NarratableEntry> narratables = Lists.newArrayList();
    public final List<Renderable> renderables = Lists.newArrayList();

    public SubScreen(int pX, int pY, int pWidth, int pHeight, Component title, boolean scissor) {
        super(pX, pY, pWidth, pHeight, title);
        this.scissor = scissor;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void visitWidgets(Consumer<AbstractWidget> pConsumer) {
        pConsumer.accept(this);
    }

    public void visitWidgetsInternal(Consumer<AbstractWidget> pConsumer) {
    }

    public boolean isMouseInside(double pMouseX, double pMouseY) {
        return GuiHelper.isPointInRegion(this.getX(), this.getY(), this.getWidth(), this.getHeight(), pMouseX, pMouseY);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.pose().pushPose();
        if (scissor)
            graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
        graphics.pose().pushPose();
        renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        for (var renderable : this.renderables) {
            graphics.pose().pushPose();
            renderable.render(graphics, pMouseX, pMouseY, pPartialTick);
            graphics.pose().popPose();
        }
        var fgRendered = false;
        for (var renderable : this.renderables) {
            if (renderable instanceof ForegroundRenderable foregroundRenderable) {
                graphics.pose().pushPose();
                fgRendered |= foregroundRenderable.renderForeground(graphics, pMouseX, pMouseY, pPartialTick, fgRendered);
                graphics.pose().popPose();
            }
        }
        graphics.pose().popPose();
        if (scissor)
            graphics.disableScissor();
        fgRendered = false;
        for (var renderable : this.renderables) {
            if (renderable instanceof ForegroundRenderable foregroundRenderable) {
                graphics.pose().pushPose();
                fgRendered |= foregroundRenderable.renderTooltips(graphics, pMouseX, pMouseY, pPartialTick, fgRendered);
                graphics.pose().popPose();
            }
        }
        graphics.pose().popPose();
        renderDecorations(graphics);
    }

    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
        narratables.forEach(entry -> entry.updateNarration(pNarrationElementOutput));
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    public final boolean isDragging() {
        return this.isDragging;
    }

    public final void setDragging(boolean pDragging) {
        this.isDragging = pDragging;
    }

    @Nullable
    public GuiEventListener getFocused() {
        return this.focused;
    }

    public void setFocused(@Nullable GuiEventListener pListener) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (pListener != null) {
            pListener.setFocused(true);
        }

        this.focused = pListener;
    }

    public FocusNavigationEvent.TabNavigation createTabEvent() {
        boolean flag = !hasShiftDown();
        return new FocusNavigationEvent.TabNavigation(flag);
    }

    public FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection pDirection) {
        return new FocusNavigationEvent.ArrowNavigation(pDirection);
    }

    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T pWidget) {
        this.renderables.add(pWidget);
        return this.addWidget(pWidget);
    }

    public <T extends Renderable> T addRenderableOnly(T pRenderable) {
        this.renderables.add(pRenderable);
        return pRenderable;
    }

    public <T extends GuiEventListener & NarratableEntry> T addWidget(T pListener) {
        this.children.add(pListener);
        this.narratables.add(pListener);
        return pListener;
    }

    public void removeWidget(GuiEventListener pListener) {
        if (pListener instanceof Renderable) {
            this.renderables.remove((Renderable) pListener);
        }

        if (pListener instanceof NarratableEntry) {
            this.narratables.remove((NarratableEntry) pListener);
        }

        this.children.remove(pListener);
    }

    public void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
        this.narratables.clear();
    }

    public void tick() {

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!isMouseInside(pMouseX, pMouseY)) {
            setFocused(null);
            return false;
        }
        if (super.mouseClicked(pMouseX, pMouseY, pButton))
            return true;
        var flag = false;
        GuiEventListener focused = null;
        for (var renderable : new ArrayList<>(this.children)) {
            flag |= renderable.mouseClicked(pMouseX, pMouseY, pButton);
            if (renderable.isFocused())
                focused = renderable;
        }
        setFocused(focused);
        return flag;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))
            return true;
        var flag = false;
        for (var renderable : new ArrayList<>(this.children)) {
            flag |= renderable.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return flag;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseReleased(pMouseX, pMouseY, pButton))
            return true;
        var flag = false;
        for (var renderable : new ArrayList<>(this.children)) {
            flag |= renderable.mouseReleased(pMouseX, pMouseY, pButton);
        }
        return flag;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double pDelta) {
        if (ContainerEventHandler.super.mouseScrolled(pMouseX, pMouseY, scrollX, pDelta))
            return true;
        return super.mouseScrolled(pMouseX, pMouseY, scrollX, pDelta);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (super.keyPressed(pKeyCode, pScanCode, pModifiers))
            return true;
        return ContainerEventHandler.super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
