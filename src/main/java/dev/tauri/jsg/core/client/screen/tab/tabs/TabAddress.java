package dev.tauri.jsg.core.client.screen.tab.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.client.screen.tab.ITabAddress;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.blockentity.IAddressProvider;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TabAddress extends dev.tauri.jsg.core.client.screen.tab.tabs.TabWithSlot implements ITabAddress {

    // Gate's address
    private final IAddressProvider addressProvider;
    private final SymbolType<?> symbolType;
    private final int progressColor;
    private int maxSymbols;

    protected TabAddress(TabAddressBuilder builder) {
        super(builder);

        this.addressProvider = builder.addressProvider;
        this.symbolType = builder.symbolType;
        this.progressColor = builder.progressColor;
        this.maxSymbols = 6;
    }

    public static TabAddressBuilder builder() {
        return new TabAddressBuilder();
    }

    public void setMaxSymbols(int maxSymbols) {
        this.maxSymbols = maxSymbols;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);

        // Draw page slot
        if (isVisible() && addressProvider.getAddress(symbolType) != null) {
            ITexture.bindTextureWithMc(bgTexLocation);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            GuiHelper.drawModalRectWithCustomSizedTexture(guiLeft + currentOffsetX + 105, guiTop + defaultY + 86, 6, 179, 18, 18, textureSize, textureSize);

            int shadow = 2;

            PointOfOrigin origin = addressProvider.getPointOfOrigin(symbolType);
            for (int i = 0; i < maxSymbols; i++) {
                SymbolInterface symbol = Objects.requireNonNull(addressProvider.getAddress(symbolType)).get(i);

                symbol.bindIconTexture(origin);

                SymbolCoords symbolCoords = getSymbolCoords(i);
                GuiHelper.drawTexturedRectWithShadow(symbolCoords.x(), symbolCoords.y(), shadow, shadow, symbolType.getIconWidth(), symbolType.getIconHeight(), 0);
            }
            RenderSystem.enableBlend();

            ITexture.bindTextureWithMc(bgTexLocation);
            int progress = addressProvider.getPageProgress();
            Color c = new Color(progressColor);
            float red = c.getRed();
            float green = c.getGreen();
            float blue = c.getBlue();
            RenderSystem.setShaderColor(red, green, blue, 1);

            GuiHelper.drawModalRectWithCustomSizedTexture(guiLeft + currentOffsetX + 97, guiTop + defaultY + 86 + (18 - progress), 0, 179 + (18 - progress), 6, progress, textureSize, textureSize);

            RenderSystem.disableBlend();
        }
    }

    @Override
    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderFg(graphics, mouseX, mouseY);

        if (isVisible() && isOpen() && addressProvider.getAddress(symbolType) != null) {
            for (int i = 0; i < maxSymbols; i++) {
                SymbolCoords symbolCoords = getSymbolCoords(i);

                if (GuiHelper.isPointInRegion(symbolCoords.x(), symbolCoords.y(), symbolType.getIconWidth(), symbolType.getIconHeight(), mouseX, mouseY)) {
                    graphics.renderTooltip(Minecraft.getInstance().font, List.of(
                            Component.literal(Objects.requireNonNull(addressProvider.getAddress(symbolType)).get(i).getEnglishName(addressProvider.getPointOfOrigin(symbolType)))
                    ), Optional.empty(), mouseX - guiLeft, mouseY - guiTop);
                    break;
                }
            }
        }
    }

    public SymbolCoords getSymbolCoords(int symbol) {
        var got = symbolType.getSymbolCoords(symbol);
        return new SymbolCoords(got.x() + guiLeft + currentOffsetX, got.y() + guiTop + defaultY);
    }

    // ------------------------------------------------------------------------------------------------
    // Builder

    public static class TabAddressBuilder extends TabBuilder {

        // Gate's TileEntity reference
        private IAddressProvider addressProvider;
        private SymbolType<?> symbolType;
        private int progressColor;

        public TabAddressBuilder setAddressProvider(IAddressProvider addressProvider) {
            this.addressProvider = addressProvider;
            return this;
        }

        public TabAddressBuilder setSymbolType(SymbolType<?> symbolType) {
            this.symbolType = symbolType;
            return this;
        }

        public TabAddressBuilder setProgressColor(int color) {
            this.progressColor = color;
            return this;
        }

        @Override
        public TabAddress build() {
            return new TabAddress(this);
        }
    }
}
