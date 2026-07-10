package dev.tauri.jsg.core.common.listener;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.common.block.core.InvisibleBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class CoreEventHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        if (block instanceof InvisibleBlock) {
            event.setCanceled(true);
        }
    }
}
