package dev.tauri.jsg.core.client.listener;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CoreClientForgeEventHandler {
    @SubscribeEvent
    public static void onDrawHighlight(RenderHighlightEvent.Block event) {
        var level = event.getCamera().getEntity().level();
        var pos = event.getTarget().getBlockPos();
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        boolean cancelled = false;

        if (block instanceof dev.tauri.jsg.core.common.block.util.IHighlightBlock hb)
            cancelled = !hb.renderHighlight(state, level, pos);

        event.setCanceled(cancelled);
    }
}
