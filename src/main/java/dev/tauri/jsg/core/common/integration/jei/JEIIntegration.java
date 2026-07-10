package dev.tauri.jsg.core.common.integration.jei;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.cauldron.CauldronRecipe;
import dev.tauri.jsg.core.common.integration.jei.category.CauldronRecipeType;
import dev.tauri.jsg.core.common.integration.jei.category.FluidCauldronRecipeCategory;
import dev.tauri.jsg.core.common.integration.jei.category.ItemCauldronRecipeCategory;
import dev.tauri.jsg.core.common.integration.jei.recipes.cauldron.FluidCauldronRecipe;
import dev.tauri.jsg.core.common.integration.jei.recipes.cauldron.ItemCauldronRecipe;
import dev.tauri.jsg.core.common.item.IMultiItem;
import dev.tauri.jsg.core.mapping.JSGMapping;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@JeiPlugin
@SuppressWarnings("unused")
public final class JEIIntegration implements IModPlugin {
    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        for (var recipe : CauldronRecipe.RECIPES) {
            if (recipe instanceof CauldronRecipe.FluidResult fluidRecipe) {
                registration.addRecipes(CauldronRecipeType.CAULDRON_RECIPE_FLUID_TYPE, List.of(
                        new FluidCauldronRecipe(() -> new ItemStack(fluidRecipe.getFluidInput().get()), fluidRecipe.getFluidBaseFluid(), fluidRecipe.getFluidResult(), recipe.getRequireHeating())
                ));
            }
            if (recipe instanceof CauldronRecipe.ItemResult itemResult) {
                registration.addRecipes(CauldronRecipeType.CAULDRON_RECIPE_ITEM_TYPE, List.of(
                        new ItemCauldronRecipe(() -> new ItemStack(itemResult.getItemInput().get()), itemResult.getItemBaseFluid(), itemResult.getItemResult(), recipe.getRequireHeating())
                ));
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof IMultiItem multiItem) {
                multiItem.addAdditional((stack, visibility) -> registration.addExtraItemStacks(List.of(stack)));
            }
        });
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new ItemCauldronRecipeCategory(guiHelper));
        registration.addRecipeCategories(new FluidCauldronRecipeCategory(guiHelper));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.CAULDRON), CauldronRecipeType.CAULDRON_RECIPE_ITEM_TYPE, CauldronRecipeType.CAULDRON_RECIPE_FLUID_TYPE);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return JSGMapping.rl(JSGCore.MOD_ID, "jei_plugin");
    }
}
