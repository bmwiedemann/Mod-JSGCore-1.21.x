package dev.tauri.jsg.core.common.integration.jei.category;

import dev.tauri.jsg.core.common.integration.jei.JSGDoubleItemIcon;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class FluidCauldronRecipeCategory extends AbstractCauldronRecipeCategory {
    protected final IDrawable icon;

    public FluidCauldronRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("gui.jei.category.cauldron.smelting"));
        this.icon = new JSGDoubleItemIcon(guiHelper, () -> new ItemStack(Items.CAULDRON), () -> new ItemStack(CoreFluids.MOLTEN_NAQUADAH_ALLOY.bucket.get())); //guiHelper.createDrawable(backgroundLocation, 116, 16, 16, 16);
    }

    @Override
    public int getVOffset() {
        return height;
    }

    @Override
    public @NotNull RecipeType<CauldronRecipeType> getRecipeType() {
        return CauldronRecipeType.CAULDRON_RECIPE_FLUID_TYPE;
    }

    @Override
    @NotNull
    public IDrawable getIcon() {
        return icon;
    }
}
