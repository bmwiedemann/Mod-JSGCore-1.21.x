package dev.tauri.jsg.core.common.block.crystal;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

public class CrystalBuddingBlock extends BuddingAmethystBlock implements ITabbedItem {
    public final ICrystalColor color;
    public final Function<CrystalBudType, Block> blockBySizeGetter;

    public CrystalBuddingBlock(ICrystalColor color, Function<CrystalBudType, Block> blockBySizeGetter, BlockBehaviour.Properties properties) {
        super(properties.mapColor(color.getColor()));
        this.color = color;
        this.blockBySizeGetter = blockBySizeGetter;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(5) == 0) {
            tryGrow(pLevel, pPos, pRandom);
        }
    }

    public void tryGrow(ServerLevel level, BlockPos buddingPos, RandomSource random) {
        if (blockBySizeGetter == null) return;
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        BlockPos blockpos = buddingPos.relative(direction);
        BlockState currentState = level.getBlockState(blockpos);
        Block block = null;
        if (canClusterGrowAtState(currentState)) {
            block = blockBySizeGetter.apply(CrystalBudType.SMALL);
        } else if (currentState.is(blockBySizeGetter.apply(CrystalBudType.SMALL)) && currentState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = blockBySizeGetter.apply(CrystalBudType.MEDIUM);
        } else if (currentState.is(blockBySizeGetter.apply(CrystalBudType.MEDIUM)) && currentState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = blockBySizeGetter.apply(CrystalBudType.LARGE);
        } else if (currentState.is(blockBySizeGetter.apply(CrystalBudType.LARGE)) && currentState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = blockBySizeGetter.apply(CrystalBudType.CLUSTER);
        }

        if (block != null) {
            BlockState newState = block.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction).setValue(AmethystClusterBlock.WATERLOGGED, currentState.getFluidState().getType() == Fluids.WATER);
            level.setBlockAndUpdate(blockpos, newState);
        }
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_BUILDING_BLOCKS;
    }
}
