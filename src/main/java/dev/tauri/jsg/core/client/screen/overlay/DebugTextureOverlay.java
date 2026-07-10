package dev.tauri.jsg.core.client.screen.overlay;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.client.loader.texture.Texture;
import dev.tauri.jsg.core.client.loader.texture.TextureLoader;
import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.overlay.ForgeGui;

public class DebugTextureOverlay {
    public static void render(ForgeGui forgeGui, GuiGraphics graphics, float partialTicks, int packedLight, int packedOverlay) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (player.isSpectator()) return;
        var item = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!item.is(Items.DEBUG_STICK)) return;
        var tag = ItemNBT.getTag(item);
        if (tag == null) return;
        if (!tag.contains("debugTexturePath")) return;
        var texPath = JSGMapping.rl(tag.getString("debugTexturePath"));
        graphics.pose().pushPose();
        if (tag.getBoolean("debugTextureBindMC"))
            Texture.bindTextureWithMc(texPath);
        else
            TextureLoader.getTextureTryAllMods(texPath).bindTexture();
        GuiHelper.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 100, 100, 100, 100);
        graphics.pose().popPose();
    }
}
