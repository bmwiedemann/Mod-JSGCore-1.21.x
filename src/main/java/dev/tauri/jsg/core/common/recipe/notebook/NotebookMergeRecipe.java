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
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class NotebookMergeRecipe extends ShapelessRecipe {
    public NotebookMergeRecipe() {
        super("notebook_merge", CraftingBookCategory.MISC,
                NotebookRecipeUtils.NOTEBOOK5.copy(),
                NonNullList.of(
                        Ingredient.of(ItemStack.EMPTY),
                        Ingredient.of(NotebookRecipeUtils.NOTEBOOK.copy()),
                        Ingredient.of(NotebookRecipeUtils.NOTEBOOK4.copy())
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

            if (item == CoreItems.NOTEBOOK_ITEM.get()) {
                matchCount++;
            } else if (!stack.isEmpty())
                return false;
        }

        return matchCount >= 2;
    }

    @Override
    @NotNull
    @ParametersAreNonnullByDefault
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider pHolderLookup.Provider) {
        int outputCount = 0;
        var pages = new ListTag();

        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getItem(i);
            var item = stack.getItem();

            if (item == CoreItems.NOTEBOOK_ITEM.get()) {
                if (ItemNBT.hasTag(stack)) {
                    var notebookTags = ItemNBT.getOrCreateTag(stack).getList("pages", Tag.TAG_COMPOUND);

                    for (var tag : notebookTags) {
                        if (!NotebookRecipeUtils.tagListContains(pages, (CompoundTag) tag)) {
                            pages.add(tag);
                        }
                    }
                }

                outputCount++;
            }
        }

        if (outputCount == 0)
            outputCount = 1;

        var output = NotebookItem.createNotebook(pages);
        output.setCount(outputCount);

        return output;
    }
}
