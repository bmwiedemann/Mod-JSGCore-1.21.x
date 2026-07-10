package dev.tauri.jsg.core.common.listener;

import dev.tauri.jsg.core.common.block.core.InvisibleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoreEventHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
        if (block instanceof InvisibleBlock) {
            event.setCanceled(true);
        }
    }
}
