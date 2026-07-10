package dev.tauri.jsg.core.common.recipe.notebook;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.registry.CoreItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class NotebookRecipeUtils {
    public static final ItemStack PAGE1 = getRandomPageWithNameColor("Plains", Biomes.PLAINS);
    public static final ItemStack PAGE2 = getRandomPageWithNameColor("Tundra", Biomes.SNOWY_TAIGA);
    public static final ItemStack PAGE3 = getRandomPageWithNameColor("Forest", Biomes.FOREST);
    public static final ItemStack PAGE4 = getRandomPageWithNameColor("Ocean", Biomes.OCEAN);
    public static final ItemStack PAGE5 = getRandomPageWithNameColor("End", Biomes.THE_END);

    public static final ItemStack NOTEBOOK = getNotebookWithPages(1, PAGE1, PAGE2);
    public static final ItemStack NOTEBOOK2 = getNotebookWithPages(2, PAGE1, PAGE2);
    public static final ItemStack NOTEBOOK3 = getNotebookWithPages(1, PAGE1, PAGE2, PAGE3);
    public static final ItemStack NOTEBOOK4 = getNotebookWithPages(1, PAGE3, PAGE4);
    public static final ItemStack NOTEBOOK5 = getNotebookWithPages(2, PAGE1, PAGE2, PAGE3, PAGE4);


    public static ItemStack getRandomPageWithNameColor(String name, ResourceKey<Biome> biome) {
        return getRandomPageWithNameColor(name);
    }

    public static ItemStack getRandomPageWithNameColor(String name) {
        var pageStack = new ItemStack(CoreItems.NOTEBOOK_PAGE_FILLED.get(), 1);
        var compound = new CompoundTag();
        PageNotebookItemFilled.setName(compound, name);
        ItemNBT.setTag(pageStack, compound);
        return pageStack;
    }

    public static ItemStack getNotebookWithPages(int quantity, ItemStack... pages) {
        var notebook = new ItemStack(CoreItems.NOTEBOOK_ITEM.get(), quantity);
        var compound = new CompoundTag();
        var list = new ListTag();

        compound.put("pages", list);
        ItemNBT.setTag(notebook, compound);

        for (ItemStack stack : pages) {
            list.add(ItemNBT.getTag(stack));
        }

        return notebook;
    }

    public static boolean tagListContains(ListTag tagList, CompoundTag compound) {
        if (compound == null)
            return false;

        for (var tag : tagList) {
            if (tag.equals(compound)) {
                return true;
            }
        }

        return false;
    }
}
