package dev.tauri.jsg.core.common.item.notebook;

import dev.tauri.jsg.core.common.helper.RayTraceHelper;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PageNotebookItemEmpty extends JSGItem {
    public PageNotebookItemEmpty() {
        super(new Item.Properties(), List.of(CoreTabs.TAB_TOOLS));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.translatable("item.jsg_core.page_notebook.empty").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!world.isClientSide) {
            var rayTrace = RayTraceHelper.rayTracePos(player, 5);
            if (rayTrace != null) {
                if (world.getBlockState(rayTrace).getBlock() instanceof NotebookPageModifier modifier && modifier.consume(world, rayTrace, player, hand)) {
                    return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), false);
                }
            }
        }

        return super.use(world, player, hand);
    }
}
