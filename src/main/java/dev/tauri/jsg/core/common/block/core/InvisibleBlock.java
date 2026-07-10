package dev.tauri.jsg.core.common.block.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class InvisibleBlock extends Block implements SimpleWaterloggedBlock, dev.tauri.jsg.core.common.block.util.IHighlightBlock {
    protected static final BlockBehaviour.Properties INVISIBLE_BLOCK_PROPS = BlockBehaviour.Properties.of()
            .emissiveRendering((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .isValidSpawn((BlockState pState, BlockGetter pLevel, BlockPos pPos, EntityType<?> pValue) -> false)
            .noOcclusion()
            .isSuffocating((BlockState pState, BlockGetter pLevel, BlockPos pPos) -> false)
            .isRedstoneConductor((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false)
            .noParticlesOnBreak()
            .pushReaction(PushReaction.BLOCK);

    public InvisibleBlock() {
        super(INVISIBLE_BLOCK_PROPS);
        this.registerDefaultState(
                defaultBlockState()
                        .setValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.HAS_COLLISIONS, true)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    public boolean renderHighlight(BlockState blockState) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        return (!state.hasProperty(dev.tauri.jsg.core.common.blockstate.JSGProperties.HAS_COLLISIONS) || state.getValue(dev.tauri.jsg.core.common.blockstate.JSGProperties.HAS_COLLISIONS) ? super.getCollisionShape(state, p_60573_, p_60574_, p_60575_) : Shapes.empty());
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(dev.tauri.jsg.core.common.blockstate.JSGProperties.HAS_COLLISIONS);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState p_152045_) {
        return p_152045_.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152045_);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.block();
    }


    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }
}
