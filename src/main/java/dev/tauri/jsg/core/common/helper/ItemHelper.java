package dev.tauri.jsg.core.common.helper;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tauri.jsg.core.common.util.I18n;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemHelper {
    public static void applyGenericToolTip(String itemName, List<Component> components, TooltipFlag tooltipFlag) {
        applyToolTip(
                List.of(Component.translatable(itemName + ".tooltip").withStyle(ChatFormatting.GRAY)),
                I18n.getAdvancedTooltip(itemName + ".tooltip.extended", (i, line) -> line.withStyle(ChatFormatting.GRAY)),
                components, tooltipFlag
        );
    }

    public static void applyToolTip(@Nullable List<Component> tooltip, @Nullable I18n.AdvancedTooltip tooltipAdvanced, List<Component> components, TooltipFlag tooltipFlag) {
        if (tooltip == null) return;
        int key = InputConstants.KEY_LSHIFT;
        components.addAll(tooltip);
        boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
        if ((isKeyDown || tooltipFlag.isAdvanced()) && tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            int width = tooltipAdvanced.getWidth() + 2;
            components.add(Component.literal(" ".repeat(width)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.STRIKETHROUGH));
            components.addAll(tooltipAdvanced.formatLines());
            components.add(Component.literal(" ".repeat(width)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.STRIKETHROUGH));
        } else if (tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            String text = Component.translatable("tooltip.general.hold_shift").getString();
            text = text.replaceAll("%key%", InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString());
            components.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public static ItemStack getMapForTarget(TagKey<Structure> structure, Component displayName, Holder<MapDecorationType> destinationType, ServerLevel level, BlockPos origin) {
        BlockPos blockpos = level.findNearestMapStructure(structure, origin, 100, true);
        if (blockpos == null)
            return null;
        ItemStack itemstack = MapItem.create(level, blockpos.getX(), blockpos.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, itemstack);
        MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", destinationType);
        itemstack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, displayName);
        return itemstack;
    }
}
