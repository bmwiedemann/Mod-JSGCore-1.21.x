package dev.tauri.jsg.core.common.integration.jei.category;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CauldronRecipeType {
    interface CauldronRecipeTypeItem extends CauldronRecipeType {
        default ResourceLocation getId() {
            return JSGMapping.rl(JSGCore.MOD_ID, "cauldron_item");
        }
    }

    interface CauldronRecipeTypeFluid extends CauldronRecipeType {
        default ResourceLocation getId() {
            return JSGMapping.rl(JSGCore.MOD_ID, "cauldron_fluid");
        }
    }

    RecipeType<CauldronRecipeType> CAULDRON_RECIPE_ITEM_TYPE = RecipeType.create(JSGCore.MOD_ID, "cauldron_item", CauldronRecipeTypeItem.class);
    RecipeType<CauldronRecipeType> CAULDRON_RECIPE_FLUID_TYPE = RecipeType.create(JSGCore.MOD_ID, "cauldron_fluid", CauldronRecipeTypeFluid.class);

    @NotNull ItemStack getItem();

    @Nullable FluidStack getFluid();

    ResourceLocation getId();

    boolean needsHeating();

    void setResult(IRecipeLayoutBuilder builder, CauldronRecipeType recipe, IFocusGroup focuses);
}
