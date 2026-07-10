package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.registry.CoreBlockEntities;
import dev.tauri.jsg.core.common.registry.tag.CoreBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

public class FluidCauldronBE extends JSGFluidHandlerBE implements ITickable {
    public FluidCauldronBE(BlockPos pPos, BlockState pBlockState) {
        super(CoreBlockEntities.FLUID_CAULDRON.get(), pPos, pBlockState, 1000);
    }

    @Override
    public void onFluidChanged() {
        var level = getLevel();
        if (level == null) return;
        var targetLevel = (int) (tank.getFluidAmount() / (double) tank.getCapacity() * 3f);
        if (tank.isEmpty()) {
            level.setBlock(getBlockPos(), Blocks.CAULDRON.defaultBlockState(), 3);
            return;
        }
        var currentLevel = level.getBlockState(getBlockPos()).getOptionalValue(LayeredCauldronBlock.LEVEL);
        if (currentLevel.isEmpty()) return;
        if (targetLevel == currentLevel.get()) return;
        if (targetLevel <= 0)
            targetLevel = 1;
        level.setBlock(getBlockPos(), getBlockState().setValue(LayeredCauldronBlock.LEVEL, targetLevel), 3);
    }

    @ParametersAreNonnullByDefault
    public static boolean isCauldronHeated(Level level, BlockPos pos) {
        var blockBelow = level.getBlockState(pos.below());

        // campfire is not lit... return false
        if (!blockBelow.getOptionalValue(BlockStateProperties.LIT).orElse(true))
            return false;
        // ------

        return blockBelow.is(CoreBlockTags.FLUID_CAULDRON_HEATING);
    }

    public boolean isHeated() {
        var level = getLevel();
        if (level == null) return false;
        return isCauldronHeated(level, getBlockPos());
    }

    @Override
    public void tick(@NotNull Level level) {

    }

    @Override
    public void onLoad() {
        super.onLoad();
        Optional.ofNullable(getLevel()).ifPresent(this::onLoad);
    }

    @Override
    public void onLoad(@NotNull Level level) {
        ITickable.super.onLoad(level);
    }
}
