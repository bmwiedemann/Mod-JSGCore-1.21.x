package dev.tauri.jsg.core.client.screen.tab.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabBiomeOverlay extends TabWithSlot {
    private final int slotTexX;
    private final int slotTexY;

    protected TabBiomeOverlay(TabBiomeOverlayBuilder builder) {
        super(builder);

        slotTexX = builder.slotTexX;
        slotTexY = builder.slotTexY;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        super.render(graphics, mouseX, mouseY);

        // Draw page slot
        ITexture.bindTextureWithMc(bgTexLocation);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GuiHelper.drawModalRectWithCustomSizedTexture(guiLeft + currentOffsetX + 5, guiTop + defaultY + 24, slotTexX, slotTexY, 18, 18, textureSize, textureSize);
    }

    @Override
    public void renderFg(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderFg(graphics, mouseX, mouseY);

        if (isVisible() && isOpen()) {
            if (GuiHelper.isPointInRegion(guiLeft + currentOffsetX + 6, guiTop + defaultY + 25, 16, 16, mouseX, mouseY) && !slot.hasItem()) {
                List<Component> text = new ArrayList<>();
                text.add(Component.translatable("gui.biome_overlay.help"));

                for (BiomeOverlayInstance biomeOverlay : BiomeOverlayInstance.values()) {
                    var line = Component.translatable(biomeOverlay.unlocalizedName()).withStyle(Style.EMPTY.withColor(biomeOverlay.color()));

                    line = line.append(": ");

                    var blocksLine = String.join(", ", biomeOverlay.getOverlayItems().stream().map(ItemStack::new).map(i -> i.getItem().getName(i).getString()).toList());
                    line.append(blocksLine);

                    text.add(line);
                }

                graphics.renderTooltip(Minecraft.getInstance().font, text, Optional.empty(), mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

    @Override
    public SlotTab createAndSaveSlot(SlotHandler slot) {
        this.slot = new SlotTab(slot, (slotTab) -> {
            int x = currentOffsetX + 6;
            int y = defaultY + 25;
            return slotTab.setXY(x, y);
        });

        return this.slot;
    }

    // ------------------------------------------------------------------------------------------------
    // Builder

    public static TabBiomeOverlayBuilder builder() {
        return new TabBiomeOverlayBuilder();
    }

    public static class TabBiomeOverlayBuilder extends TabBuilder {
        private int slotTexX;
        private int slotTexY;

        public TabBiomeOverlayBuilder setSlotTexture(int x, int y) {
            slotTexX = x;
            slotTexY = y;

            return this;
        }

        @Override
        public TabBiomeOverlay build() {
            return new TabBiomeOverlay(this);
        }
    }
}
