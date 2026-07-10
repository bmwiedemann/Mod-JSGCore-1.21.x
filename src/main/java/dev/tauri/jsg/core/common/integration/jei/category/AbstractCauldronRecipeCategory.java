package dev.tauri.jsg.core.common.integration.jei.category;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractCauldronRecipeCategory implements IRecipeCategory<CauldronRecipeType> {
    public static final int width = 116;
    public static final int height = 54;


    protected final IDrawable background;
    protected final ResourceLocation backgroundLocation = JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/jei/cauldron_recipe_container.png");
    protected final IDrawable fireIcon;
    protected final Component localizedName;

    public AbstractCauldronRecipeCategory(IGuiHelper guiHelper, Component localizedName) {
        this.background = guiHelper.createDrawable(backgroundLocation, 0, getVOffset(), width, height);
        this.fireIcon = guiHelper.createDrawable(backgroundLocation, 118, 36, 12, 11);
        this.localizedName = localizedName;
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public ResourceLocation getRegistryName(CauldronRecipeType recipe) {
        return recipe.getId();
    }

    public abstract int getVOffset();

    @Override
    public @NotNull Component getTitle() {
        return localizedName;
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipeType recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addItemStack(recipe.getItem());
        var fluid = recipe.getFluid();
        if (fluid != null)
            builder.addSlot(RecipeIngredientRole.INPUT, 19, 35).addFluidStack(fluid.getFluid(), 1000).setFluidRenderer(1000, false, 16, 11);
        recipe.setResult(builder, recipe, focuses);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void draw(CauldronRecipeType recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        if (recipe.needsHeating()) {
            fireIcon.draw(guiGraphics, 21, 41);
        }
    }
}
