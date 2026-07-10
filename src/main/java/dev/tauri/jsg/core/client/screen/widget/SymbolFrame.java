package dev.tauri.jsg.core.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.math.MathFunctionImpl;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class SymbolFrame extends AbstractWidget implements dev.tauri.jsg.core.client.screen.widget.ForegroundRenderable {
    public SymbolInterface symbol = null;
    public Supplier<PointOfOrigin> pooSupplier;
    public final List<Vector2i> path = new ArrayList<>();
    public boolean incoming;
    public boolean offline;
    public int index;
    public boolean animating;
    public long animationStart;
    public int gateCenterX;
    public int gateCenterY;

    protected final Function<SymbolFrame, Integer> colorSupplier;

    public SymbolFrame(int pX, int pY, int gateCenterX, int gateCenterY, int index, Function<SymbolFrame, Integer> colorSupplier, Supplier<PointOfOrigin> pooSupplier) {
        super(pX, pY, 25, 25, Component.empty());
        this.index = index;
        this.gateCenterX = gateCenterX;
        this.gateCenterY = gateCenterY;
        this.colorSupplier = colorSupplier;
        this.pooSupplier = pooSupplier;
    }

    public SymbolFrame setSymbol(@Nullable SymbolInterface symbol) {
        this.symbol = symbol;
        return this;
    }

    public SymbolFrame setPath(Vector2i... path) {
        return setPath(Arrays.asList(path));
    }

    public SymbolFrame setPath(List<Vector2i> path) {
        this.path.clear();
        this.path.addAll(path.stream().map(l -> new Vector2i(l.x + getX(), l.y + getY())).toList());
        return this;
    }

    public void engage(SymbolInterface symbol) {
        this.symbol = symbol;
        this.animationStart = JSGMinecraftHelper.getGUITicks() + 5;
        this.animating = true;
    }

    public static final MathFunctionImpl SCALE_FUNCTION = new MathFunctionImpl((x) -> {
        var a = 0.460667f;
        if (x < 0) x = 0;
        if (x > 1f) x = 1f;
        if (x <= 0.260667f) {
            return (float) (Math.sin((x + 0.39896f) * Math.PI * 4f) + Math.cos(((x + 0.39896f) * Math.PI * 2f)) + 1.76017f) / 1.391161f;
        }
        if (x <= a)
            return 1.530505f;
        if (x <= (a - 0.260667f + 0.441413f)) {
            return (float) (Math.sin((x + 0.39896f - (a - 0.260667f)) * Math.PI * 4f) + Math.cos((x + 0.39896f - (a - 0.260667f)) * Math.PI * 2f) + 1.76017f) / 1.391161f;
        }
        return 1f;
    });
    public static final MathFunctionImpl POS_FUNCTION = new MathFunctionImpl((x) -> {
        var a = 0.4f;
        if (x < 0) x = 0;
        if (x > 1f) x = 1f;
        if (x < a) return 1f;
        return (float) (Math.sin((x - a) * (1f / (1f - a)) * Math.PI + (Math.PI / 2f)) / 2f + 0.5f);
    });
    public static final int ANIMATION_LENGTH = 2 * 20;

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        GuiHelper.renderPath(graphics, path, 1, getColor(), !offline);
        GuiHelper.renderOutline(graphics, getX(), getY(), getWidth(), getHeight(), getColor(), 2, !offline);
        if (incoming)
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), getColor());
        else if (symbol != null) {
            var coef = animating ? (((JSGMinecraftHelper.getGUITicks() + partialTick) - animationStart) / (float) ANIMATION_LENGTH) : 1f;
            if (coef > 1) {
                animating = false;
                coef = 1f;
            }
            var symbolX = getX() + ((float) getWidth() / 2f);
            var symbolY = getY() + ((float) getHeight() / 2f);
            var scaleCoef = SCALE_FUNCTION.apply(coef);
            var symbolSize = (int) (24f * scaleCoef);
            var posCoef = 1f - POS_FUNCTION.apply(coef);
            var x = gateCenterX + (symbolX - gateCenterX) * posCoef;
            var y = gateCenterY + (symbolY - gateCenterY) * posCoef;
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            RenderSystem.setShaderColor(0, 0, 0, 0.2f);
            GuiHelper.renderSymbolCentered(graphics, x + 1, y + 1, symbolSize, symbol, null);
            RenderSystem.setShaderColor(0, 0, 0, 1);
            GuiHelper.renderSymbolCentered(graphics, x, y, symbolSize, symbol, null);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            graphics.pose().popPose();
        }
    }

    public int getColor() {
        return colorSupplier.apply(this);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean renderForeground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        return false;
    }

    @Override
    public boolean renderTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean otherRendered) {
        if (otherRendered) return false;
        if (isHovered() && symbol != null) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(symbol.getEnglishName(pooSupplier.get())), pMouseX, pMouseY);
            return true;
        }
        return false;
    }
}
