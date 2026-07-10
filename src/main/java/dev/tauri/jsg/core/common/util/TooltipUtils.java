package dev.tauri.jsg.core.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;

public class TooltipUtils {
    public static int getShiftKey() {
        return InputConstants.KEY_LSHIFT;
    }

    public static String getShiftKeyName() {
        return InputConstants.Type.KEYSYM.getOrCreate(getShiftKey()).getDisplayName().getString();
    }

    public static boolean showAdvancedTooltip(TooltipFlag tooltipFlag) {
        return tooltipFlag.isAdvanced() || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), getShiftKey());
    }
}
