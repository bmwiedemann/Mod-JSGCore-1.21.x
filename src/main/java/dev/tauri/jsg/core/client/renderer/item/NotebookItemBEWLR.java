package dev.tauri.jsg.core.client.renderer.item;

import dev.tauri.jsg.core.common.util.ItemNBT;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class NotebookItemBEWLR extends BlockEntityWithoutLevelRenderer {
    public NotebookItemBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        if (ItemNBT.hasTag(itemStack)) {
            var compound = ItemNBT.getOrCreateTag(itemStack);
            var pageTag = NotebookItem.getSelectedPageFromCompound(compound);

            final var list = compound.getList("addressList", Tag.TAG_COMPOUND);
            final int selected = Math.min(list.size() - 1, compound.getInt("selected"));

            var layers = new ArrayList<ResourceLocation>();
            layers.add(JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/notebook/background.png"));
            layers.add(JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/notebook/page_third.png"));
            if (selected >= 1) layers.add(JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/notebook/page_second.png"));
            if (selected >= 2) layers.add(JSGMapping.rl(JSGCore.MOD_ID, "textures/gui/notebook/page_top.png"));

            final float scale = 0.5f;

            PageRenderer.renderByCompound(stack, bufferSource, light, itemDisplayContext, pageTag, () -> {
                boolean first = (selected == 0);
                boolean last = (selected == list.size() - 1);

                String namePrev = null, nameNext = null;

                if (!first)
                    namePrev = "< " + PageNotebookItemFilled.getNameFromCompound(list.getCompound(selected - 1));

                if (!last)
                    nameNext = PageNotebookItemFilled.getNameFromCompound(list.getCompound(selected + 1)) + " >";

                if (namePrev != null) {
                    stack.pushPose();
                    stack.translate(-33, 9, 0);
                    stack.scale(scale, scale, scale);
                    Minecraft.getInstance().font.drawInBatch(namePrev, 0, 0, 0x383228, false, stack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                    stack.popPose();
                }

                if (nameNext != null) {
                    final float width = Minecraft.getInstance().font.width(nameNext);
                    stack.pushPose();
                    stack.translate(33, 9, 0);
                    stack.scale(scale, scale, scale);
                    stack.translate(-width, 0, 0);
                    Minecraft.getInstance().font.drawInBatch(nameNext, 0, 0, 0x383228, false, stack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, light);
                    stack.popPose();
                }
            }, layers);
        }
    }
}
