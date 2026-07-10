package dev.tauri.jsg.core.common.integration.jei.category;

import dev.tauri.jsg.core.common.integration.jei.JSGDoubleItemIcon;
import dev.tauri.jsg.core.common.registry.CoreItems;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ItemCauldronRecipeCategory extends dev.tauri.jsg.core.common.integration.jei.category.AbstractCauldronRecipeCategory {
    protected final IDrawable icon;

    public ItemCauldronRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("gui.jei.category.cauldron.casting"));
        this.icon = new JSGDoubleItemIcon(guiHelper, () -> new ItemStack(Items.CAULDRON), () -> new ItemStack(CoreItems.NAQUADAH_ALLOY.get())); //guiHelper.createDrawable(backgroundLocation, 116, 0, 16, 16);
    }

    @Override
    public int getVOffset() {
        return 0;
    }

    @Override
    public @NotNull RecipeType<CauldronRecipeType> getRecipeType() {
        return CauldronRecipeType.CAULDRON_RECIPE_ITEM_TYPE;
    }

    @Override
    @NotNull
    public IDrawable getIcon() {
        return icon;
    }
}
