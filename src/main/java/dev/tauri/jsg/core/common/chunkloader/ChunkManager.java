package dev.tauri.jsg.core.common.chunkloader;

import dev.tauri.jsg.core.JSGCore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import dev.tauri.jsg.core.mapping.JSGMapping;

import java.util.UUID;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ChunkManager {
    private static final UUID JSG_CHUNKS = UUID.fromString("ebe3ef80-f613-48a8-b10d-7a28406224d1");
    private static final TicketController CONTROLLER = new TicketController(JSGMapping.rl(JSGCore.MOD_ID, "chunks"));

    @SubscribeEvent
    public static void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(CONTROLLER);
    }


    public static void forceChunk(Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) return;
        forceChunk(serverLevel, new ChunkPos(pos), false);
    }

    public static void forceChunk(ServerLevel world, ChunkPos chunk) {
        forceChunk(world, chunk, false);
    }

    public static void forceChunk(ServerLevel world, ChunkPos chunk, boolean quiet) {
        boolean forced = CONTROLLER.forceChunk(world, JSG_CHUNKS, chunk.x, chunk.z, true, true);
        if (!quiet && forced)
            JSGCore.logger.debug("Forcing chunk {}, in world {}", chunk, world.dimension().location().toString());
    }

    public static void unforceChunk(Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) return;
        unforceChunk(serverLevel, new ChunkPos(pos), false);
    }

    public static void unforceChunk(ServerLevel world, ChunkPos chunk) {
        unforceChunk(world, chunk, false);
    }

    public static void unforceChunk(ServerLevel world, ChunkPos chunk, boolean quiet) {
        boolean forced = CONTROLLER.forceChunk(world, JSG_CHUNKS, chunk.x, chunk.z, false, true);
        if (!quiet && forced)
            JSGCore.logger.debug("Un-forcing chunk {}, in world {}", chunk, world.dimension().location().toString());
    }
}
