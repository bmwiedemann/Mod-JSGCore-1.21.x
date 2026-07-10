package dev.tauri.jsg.core.common.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * @author Create dev team
 */
public class JSGDoubleItemIcon implements IDrawable {
    private final Supplier<ItemStack> primarySupplier;
    private final Supplier<ItemStack> secondarySupplier;
    private ItemStack primaryStack;
    private ItemStack secondaryStack;

    private final IGuiHelper guiHelper;

    @ParametersAreNonnullByDefault
    public JSGDoubleItemIcon(IGuiHelper guiHelper, Supplier<ItemStack> primary, Supplier<ItemStack> secondary) {
        this.primarySupplier = primary;
        this.secondarySupplier = secondary;
        this.guiHelper = guiHelper;
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        if (primaryStack == null) {
            primaryStack = primarySupplier.get();
            secondaryStack = secondarySupplier.get();
        }

        RenderSystem.enableDepthTest();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);

        matrixStack.pushPose();
        matrixStack.translate(1, 1, 0);
        guiHelper.createDrawableItemStack(primaryStack).draw(graphics);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(10, 10, 100);
        matrixStack.scale(.5f, .5f, .5f);
        guiHelper.createDrawableItemStack(secondaryStack).draw(graphics);
        matrixStack.popPose();

        matrixStack.popPose();
    }
}
