package dev.tauri.jsg.core.common.integration.jei.recipes.cauldron;

import dev.tauri.jsg.core.common.integration.jei.category.CauldronRecipeType;
import dev.tauri.jsg.core.common.registry.tag.CoreItemTags;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record ItemCauldronRecipe(Supplier<ItemStack> inputSupplier, Supplier<FluidStack> fluid,
                                 Supplier<ItemStack> outputSupplier,
                                 boolean needsHeating) implements CauldronRecipeType.CauldronRecipeTypeItem {
    @Override
    public @NotNull ItemStack getItem() {
        return inputSupplier.get();
    }

    @Override
    public @Nullable FluidStack getFluid() {
        return fluid.get();
    }

    @Override
    public void setResult(IRecipeLayoutBuilder builder, CauldronRecipeType recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19).addItemStack(outputSupplier.get());
        if (recipe.needsHeating())
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(Ingredient.of(CoreItemTags.FLUID_CAULDRON_HEATING));
    }
}
