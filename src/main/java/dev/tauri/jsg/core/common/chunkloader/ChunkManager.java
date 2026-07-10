package dev.tauri.jsg.core.common.chunkloader;

import dev.tauri.jsg.core.JSGCore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.ForgeChunkManager;

import java.util.UUID;

public class ChunkManager {
    private static final UUID JSG_CHUNKS = UUID.fromString("ebe3ef80-f613-48a8-b10d-7a28406224d1");


    public static void forceChunk(Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) return;
        forceChunk(serverLevel, new ChunkPos(pos), false);
    }

    public static void forceChunk(ServerLevel world, ChunkPos chunk) {
        forceChunk(world, chunk, false);
    }

    public static void forceChunk(ServerLevel world, ChunkPos chunk, boolean quiet) {
        boolean forced = ForgeChunkManager.forceChunk(world, JSGCore.MOD_ID, JSG_CHUNKS, chunk.x, chunk.z, true, true);
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
        boolean forced = ForgeChunkManager.forceChunk(world, JSGCore.MOD_ID, JSG_CHUNKS, chunk.x, chunk.z, false, true);
        if (!quiet && forced)
            JSGCore.logger.debug("Un-forcing chunk {}, in world {}", chunk, world.dimension().location().toString());
    }
}
