package dev.tauri.jsg.core.common.recipe.notebook;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class NotebookCreationRecipe extends ShapelessRecipe {
    public NotebookCreationRecipe() {
        super("notebook_creation", CraftingBookCategory.MISC,
                NotebookRecipeUtils.NOTEBOOK.copy(),
                NonNullList.of(
                        Ingredient.of(ItemStack.EMPTY),
                        Ingredient.of(NotebookRecipeUtils.PAGE1.copy()),
                        Ingredient.of(NotebookRecipeUtils.PAGE2.copy())
                )
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(CraftingInput inv, Level pLevel) {
        int matchCount = 0;

        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_PAGE_FILLED.get()) {
                matchCount++;
            } else if (!stack.isEmpty())
                return false;
        }

        return matchCount >= 2;
    }

    @NotNull
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider pHolderLookup.Provider) {
        var pages = new ListTag();

        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_PAGE_FILLED.get()) {
                CompoundTag compound = ItemNBT.getTag(stack);

                if (!NotebookRecipeUtils.tagListContains(pages, compound)) {
                    pages.add(compound);
                }
            }
        }
        var output = NotebookItem.createNotebook(pages);
        output.setCount(1);
        return output;
    }
}
