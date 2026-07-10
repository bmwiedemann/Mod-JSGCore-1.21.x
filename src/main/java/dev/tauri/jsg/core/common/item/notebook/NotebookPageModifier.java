package dev.tauri.jsg.core.common.item.notebook;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

public interface NotebookPageModifier {
    @ParametersAreNonnullByDefault
    boolean consume(Level world, BlockPos pos, Player player, InteractionHand hand);
}
