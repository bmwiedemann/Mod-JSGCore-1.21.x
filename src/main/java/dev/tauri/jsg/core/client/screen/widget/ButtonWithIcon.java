package dev.tauri.jsg.core.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class ButtonWithIcon extends Button {
    protected ResourceLocation iconTexture;
    protected int iconU;
    protected int iconV;
    protected int iconTextureWidth;
    protected int iconTextureHeight;

    protected ButtonWithIcon(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration, ResourceLocation iconTexture, int iconU, int iconV, int iconTextureWidth, int iconTextureHeight) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
        this.iconTexture = iconTexture;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconTextureWidth = iconTextureWidth;
        this.iconTextureHeight = iconTextureHeight;
    }

    protected ButtonWithIcon(Builder builder) {
        super(builder);
        this.iconTexture = builder.iconTexture;
        this.iconU = builder.iconU;
        this.iconV = builder.iconV;
        this.iconTextureWidth = builder.iconTextureWidth;
        this.iconTextureHeight = builder.iconTextureHeight;

        this.active = builder.active;
    }

    @ParametersAreNonnullByDefault
    public static Builder builder(Component pMessage, Button.OnPress pOnPress) {
        return new Builder(pMessage, pOnPress);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        if (iconTexture != null) {
            RenderSystem.enableBlend();
            graphics.blit(iconTexture, getX() + 3, getY() + 3, this.getHeight() - 6, this.getHeight() - 6, iconU, iconV, iconTextureHeight, iconTextureHeight, iconTextureWidth, iconTextureHeight);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderScrollingString(GuiGraphics graphics, Font font, int hPadding, int color) {
        if (iconTexture == null) {
            super.renderScrollingString(graphics, font, hPadding, color);
            return;
        }
        int minX = this.getX() + this.getHeight();
        int maxX = this.getX() + this.getWidth() - hPadding;
        GuiHelper.renderScrollingStringLeftAligned(graphics, font, this.getMessage(), minX, maxX, this.getY(), this.getY() + this.getHeight(), color, true);
    }

    public static class Builder extends Button.Builder {
        protected ResourceLocation iconTexture;
        protected int iconU;
        protected int iconV;
        protected int iconTextureWidth;
        protected int iconTextureHeight;

        protected boolean active = true;

        public Builder(Component pMessage, OnPress pOnPress) {
            super(pMessage, pOnPress);
        }

        public Builder setIcon(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {
            this.iconTexture = texture;
            this.iconU = u;
            this.iconV = v;
            this.iconTextureWidth = textureWidth;
            this.iconTextureHeight = textureHeight;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        @Override
        public @NotNull ButtonWithIcon build() {
            return new ButtonWithIcon(this);
        }
    }
}
