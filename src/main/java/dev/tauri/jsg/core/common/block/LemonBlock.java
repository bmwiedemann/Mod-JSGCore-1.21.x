package dev.tauri.jsg.core.common.block;

import com.mojang.serialization.MapCodec;
import dev.tauri.jsg.core.common.block.util.IItemBlock;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.item.LemonItem;
import dev.tauri.jsg.core.common.registry.tag.CoreBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class LemonBlock extends FallingBlock implements ITabbedItem, IItemBlock {
    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.FallingBlock> codec() {
        return simpleCodec(properties -> this);
    }

    public LemonBlock() {
        super(Properties.of().sound(SoundType.CROP).randomTicks().noOcclusion());
        this.registerDefaultState(
                defaultBlockState()
                        .setValue(BlockStateProperties.AGE_1, 0)
                        .setValue(BlockStateProperties.FACING, Direction.DOWN)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        // unplaceable
        return null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        pLevel.scheduleTick(pPos, this, 1);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AGE_1);
        builder.add(BlockStateProperties.FACING);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (canDropAndUpdateShape(pLevel, pPos, pState, true) && pPos.getY() >= pLevel.getMinBuildHeight()) {
            falling(FallingBlockEntity.fall(pLevel, pPos, pState.setValue(BlockStateProperties.FACING, Direction.DOWN)));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(16) == 0) {
            if (canDropAndUpdateShape(pLevel, pPos, pState, false)) {
                ParticleUtils.spawnParticleBelow(pLevel, pPos, pRandom, new BlockParticleOption(ParticleTypes.FALLING_DUST, pState));
            }
        }
    }

    @SuppressWarnings("all")
    public boolean canDropAndUpdateShape(Level level, BlockPos pos, BlockState state, boolean updateShape) {
        if (!state.hasProperty(BlockStateProperties.AGE_1) || state.getValue(BlockStateProperties.AGE_1) < 1) {
            var dirOpt = state.getOptionalValue(BlockStateProperties.FACING);
            if (dirOpt.isPresent() && dirOpt.get() != Direction.DOWN) {
                if (level.getBlockState(pos.relative(dirOpt.get())).is(CoreBlockTags.SUPPORT_LEMON))
                    return false;
            } else {
                for (var dir : Direction.values()) {
                    if (dir == Direction.DOWN) continue;
                    if (level.getBlockState(pos.relative(dir)).is(CoreBlockTags.SUPPORT_LEMON)) {
                        level.setBlock(pos, state.setValue(BlockStateProperties.FACING, dir), 3);
                        return false;
                    }
                }
            }
        }
        var bellow = level.getBlockState(pos.below());
        return bellow.isAir() || bellow.is(BlockTags.FIRE) || bellow.liquid() || bellow.canBeReplaced();
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isClientSide && (!pState.hasProperty(BlockStateProperties.AGE_1) || pState.getValue(BlockStateProperties.AGE_1) < 1) && pRandom.nextFloat() < 0.3f)
            pLevel.setBlock(pPos, pState.setValue(BlockStateProperties.AGE_1, 1), 3);
    }

    @Override
    @ParametersAreNonnullByDefault
    @NotNull
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, 1);
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }


    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        var facing = blockState.getOptionalValue(BlockStateProperties.FACING).orElse(Direction.DOWN);
        if (facing == Direction.DOWN)
            return Shapes.create(0.4, 0, 0.3, 0.6, 0.3, 0.7);
        if (facing == Direction.UP)
            return Shapes.create(0.3, 0.5, 0.3, 0.7, 1, 0.7);
        var vec = facing.getNormal();
        return Shapes.create(0.3, 0, 0.3, 0.7, 1, 0.7).move(vec.getX() * 0.3f, 0, vec.getZ() * 0.3f);
    }

    @Override
    public JSGBlockItem getItemBlock() {
        return new LemonItem(this);
    }
}
