package dev.tauri.jsg.core.client.listener;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.common.item.notebook.NotebookActionEnum;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.NotebookActionPacketToServer;
import dev.tauri.jsg.core.common.registry.CoreItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import javax.annotation.Nullable;

import static com.mojang.blaze3d.platform.InputConstants.*;

@EventBusSubscriber(value = Dist.CLIENT)
public class InputHandler {
    private static final KeyMapping ADDRESS_SCROLL = new KeyMapping("config.jsg_core.address_scroll", KEY_LSHIFT, "key.categories.jsg_core");
    private static final KeyMapping ADDRESS_UP = new KeyMapping("config.jsg_core.address_up", KEY_NUMPAD8, "key.categories.jsg_core");
    private static final KeyMapping ADDRESS_DOWN = new KeyMapping("config.jsg_core.address_down", KEY_NUMPAD2, "key.categories.jsg_core");

    public static final KeyMapping[] KEY_BINDINGS = {
            ADDRESS_SCROLL,
            ADDRESS_UP,
            ADDRESS_DOWN
    };

    @SubscribeEvent
    public static void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        boolean next = event.getScrollDeltaY() < 0;
        if (checkForItem(CoreItems.NOTEBOOK_ITEM.get())) {
            var hand = getHand(CoreItems.NOTEBOOK_ITEM.get());
            NotebookActionEnum action = null;


            if (hand != null) {
                if (ADDRESS_SCROLL.isDown())
                    action = NotebookActionEnum.ADDRESS_CHANGE;


                // ---------------------------------------------
                if (action != null) {
                    event.setCanceled(true);
                    JSGCorePacketHandler.sendToServer(new NotebookActionPacketToServer(action, hand, next));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onKeyboardEvent(InputEvent.Key event) {
        if (event.getAction() != PRESS) return;
        var player = Minecraft.getInstance().player;

        if (player == null) return;

        if (checkForItem(CoreItems.NOTEBOOK_ITEM.get())) {
            var hand = getHand(CoreItems.NOTEBOOK_ITEM.get());
            NotebookActionEnum action = null;
            boolean next = false;

            if (ADDRESS_UP.isDown()) {
                action = NotebookActionEnum.ADDRESS_CHANGE;
            } else if (ADDRESS_DOWN.isDown()) {
                action = NotebookActionEnum.ADDRESS_CHANGE;
                next = true;
            }

            // ---------------------------------------------
            if (action != null) {
                JSGCorePacketHandler.sendToServer(new NotebookActionPacketToServer(action, hand, next));
            }
        }
    }

    // Get hand holding item
    @Nullable
    public static InteractionHand getHand(Item item) {
        var player = Minecraft.getInstance().player;
        InteractionHand hand = null;

        if (player == null)
            return null;

        if (player.getMainHandItem().getItem() == item)
            hand = InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() == item)
            hand = InteractionHand.OFF_HAND;

        return hand;
    }

    @Nullable
    public static ItemStack getItemStack(Player player, Item item) {
        InteractionHand hand = getHand(item);

        if (hand != null) {
            return player.getItemInHand(hand);
        }

        return null;
    }

    // Check for item in both hands
    public static boolean checkForItem(Item item) {
        return getHand(item) != null;
    }
}
