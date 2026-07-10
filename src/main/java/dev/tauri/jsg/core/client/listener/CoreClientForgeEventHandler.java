package dev.tauri.jsg.core.client.listener;

import dev.tauri.jsg.core.JSGCore;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JSGCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
