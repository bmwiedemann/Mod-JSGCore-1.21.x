package dev.tauri.jsg.core.common.item.notebook;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.client.renderer.item.NotebookItemBEWLR;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

public class NotebookItem extends JSGItem {
    public NotebookItem() {
        super(new Item.Properties().rarity(Rarity.COMMON), CoreTabs.TAB_TOOLS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final NotebookItemBEWLR instance = new NotebookItemBEWLR();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return instance;
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide) return;
        if (ItemNBT.hasTag(pStack)) {
            var compound = ItemNBT.getOrCreateTag(pStack);
            if (!compound.contains("addressList")) return;
            if (compound.contains("pages")) return;
            ListTag newPagesList = null;
            var list = compound.getList("addressList", Tag.TAG_COMPOUND);

            for (var item : list) {
                var newPageTag = NotebookPageType.getFixedTag((CompoundTag) item);
                if (newPageTag == null) continue;
                if (newPagesList == null) newPagesList = new ListTag();
                newPagesList.add(newPageTag);
            }
            if (newPagesList != null) {
                compound.put("pages", newPagesList);
                compound.putInt("selected", 0);
                ItemNBT.setTag(pStack, compound);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        if (ItemNBT.hasTag(stack)) {
            var compound = ItemNBT.getOrCreateTag(stack);
            var list = compound.getList("pages", Tag.TAG_COMPOUND);

            for (var item : list) {
                var pageTag = (CompoundTag) item;
                components.add(Component.literal(ChatFormatting.AQUA + PageNotebookItemFilled.getNameFromCompound(pageTag)));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() != newStack.getItem())
            return true;

        if (!ItemNBT.hasTag(oldStack) || !ItemNBT.hasTag(newStack))
            return true;

        int oldSelected = ItemNBT.getOrCreateTag(oldStack).getInt("selected");
        int newSelected = ItemNBT.getOrCreateTag(newStack).getInt("selected");

        return oldSelected != newSelected;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
                var page = popCurrentPage(player.getItemInHand(hand));
                if (!page.isEmpty()) {
                    player.addItem(page);
                }
            }
        }

        return super.use(world, player, hand);
    }


    // ------------------------------------------------------------------------------------------------------------
    // NBT handlers

    public static CompoundTag getSelectedPageFromCompound(CompoundTag compound) {
        int selected = compound.getInt("selected");
        var list = compound.getList("pages", Tag.TAG_COMPOUND);
        if (list.isEmpty()) return null;
        return list.getCompound(selected);
    }

    public static void setNameForIndex(ListTag list, int index, String name) {
        var page = list.getCompound(index);
        PageNotebookItemFilled.setName(page, name);
    }

    public static ItemStack createNotebook(@Nonnull ListTag pages) {
        var output = new ItemStack(CoreItems.NOTEBOOK_ITEM.get(), 1);
        var compound = new CompoundTag();
        compound.put("pages", pages);
        compound.putInt("selected", 0);
        ItemNBT.setTag(output, compound);

        return output;
    }

    @Nonnull
    public static ItemStack popCurrentPage(ItemStack notebook) {
        var tag = ItemNBT.getOrCreateTag(notebook);
        var index = tag.getInt("selected");
        return popPage(notebook, index);
    }

    @Nonnull
    public static ItemStack popPage(ItemStack notebook, int index) {
        try {
            var tag = ItemNBT.getOrCreateTag(notebook);
            var list = tag.getList("pages", Tag.TAG_COMPOUND);
            var pageNBT = (CompoundTag) list.get(index);
            var page = new ItemStack(CoreItems.NOTEBOOK_PAGE_FILLED.get());
            ItemNBT.setTag(page, pageNBT);
            list.remove(index);
            tag.put("pages", list);
            var currentIndex = tag.getInt("selected");
            if (currentIndex >= list.size()) {
                currentIndex = (list.size() - 1);
                tag.putInt("selected", currentIndex);
            }
            ItemNBT.setTag(notebook, tag);
            if (list.isEmpty() || currentIndex < 0) notebook.setCount(0);
            return page;
        } catch (IndexOutOfBoundsException ignored) {
            notebook.setCount(0);
        }
        return ItemStack.EMPTY;
    }
}
