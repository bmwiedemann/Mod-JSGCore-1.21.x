package dev.tauri.jsg.core.common.block.cartouche;

import dev.tauri.jsg.core.common.block.TickableBEBlock;
import dev.tauri.jsg.core.common.block.util.IItemBlock;
import dev.tauri.jsg.core.common.blockentity.CartoucheBE;
import dev.tauri.jsg.core.common.blockstate.JSGProperties;
import dev.tauri.jsg.core.common.entity.IAddressNotebookPageData;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.helper.BlockPosHelper;
import dev.tauri.jsg.core.common.item.CartoucheItem;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.item.notebook.NotebookItem;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

public class CartoucheBlock extends TickableBEBlock implements SimpleWaterloggedBlock, ITabbedItem, IItemBlock {
    public final Supplier<BlockState> material;
    public final CartoucheType type;

    public CartoucheBlock(Supplier<BlockState> material, CartoucheType type) {
        super(Properties.copy(material.get().getBlock()).pushReaction(PushReaction.DESTROY).isViewBlocking((BlockState state, BlockGetter getter, BlockPos pos) -> false).noOcclusion().requiresCorrectToolForDrops());
        this.registerDefaultState(
                defaultBlockState()
                        .setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, Direction.NORTH)
                        .setValue(JSGProperties.FACING_VERTICAL_PROPERTY, 0)
                        .setValue(JSGProperties.CARTOUCHE_BLOCK_INDEX, 0)
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
        this.material = material;
        this.type = type;
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(JSGProperties.FACING_HORIZONTAL_PROPERTY);
        builder.add(JSGProperties.FACING_VERTICAL_PROPERTY);
        builder.add(JSGProperties.CARTOUCHE_BLOCK_INDEX);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var level = ctx.getLevel();
        if (level.isClientSide()) return null;
        var hitFace = ctx.getClickedFace();
        var hitPos = ctx.getClickedPos();
        var hitState = level.getBlockState(hitPos.offset(hitFace.getOpposite().getNormal()));
        if (!hitState.isFaceSturdy(level, hitPos, hitFace)) return null;
        var horizontalFacing = hitFace;
        var verticalFacing = Direction.SOUTH;
        if (hitFace.getAxis() == Direction.Axis.Y) {
            horizontalFacing = (ctx.getPlayer() == null ? Direction.SOUTH : ctx.getPlayer().getDirection().getOpposite());
            verticalFacing = hitFace;
        }
        var rotation = RotationUtil.getRotation(verticalFacing, horizontalFacing);
        for (var y = 1; y <= (type.height - 1); y++) {
            BlockPos topPos = RotationUtil.rotate(new BlockPos(0, y, 0), rotation).offset(hitPos);
            if (!level.getBlockState(topPos).canBeReplaced()) return null;
        }
        return defaultBlockState()
                .setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, horizontalFacing)
                .setValue(JSGProperties.FACING_VERTICAL_PROPERTY, JSGProperties.getVerticalFacingByDirection(verticalFacing))
                .setValue(JSGProperties.CARTOUCHE_BLOCK_INDEX, 0)
                .setValue(BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    @ParametersAreNonnullByDefault
    @NotNull
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!canSurvive(pState, pLevel, pCurrentPos)) {
            var rotation = RotationUtil.getRotation(pState);
            int height = pState.getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(-1);
            if (height < 0) return Blocks.AIR.defaultBlockState();
            for (var i = 0; i <= (type.height - 1); i++) {
                var y = (i - height);
                if (y == 0) continue;
                BlockPos topPos = RotationUtil.rotate(new BlockPos(0, y, 0), rotation).offset(pCurrentPos);
                pLevel.setBlock(topPos, Blocks.AIR.defaultBlockState(), 3);
            }
            return Blocks.AIR.defaultBlockState();
        }
        return pState;
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        var verticalFacing = JSGProperties.getDirectionByVerticalFacing(pState.getOptionalValue(JSGProperties.FACING_VERTICAL_PROPERTY).orElse(0));
        var horizontalFacing = pState.getOptionalValue(JSGProperties.FACING_HORIZONTAL_PROPERTY).orElse(Direction.SOUTH);
        var rotation = RotationUtil.getRotation(verticalFacing, horizontalFacing);
        BlockPos wallPos = RotationUtil.rotate(new BlockPos(0, 0, -1), rotation).offset(pPos);
        BlockState wallState = pLevel.getBlockState(wallPos);
        if (!wallState.isFaceSturdy(pLevel, wallPos, (verticalFacing != null ? verticalFacing : horizontalFacing)))
            return false;
        int height = pState.getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(-1);
        if (height < 0) return false;
        if (height == 0) {
            for (var y = 1; y <= (type.height - 1); y++) {
                var posOver = RotationUtil.rotate(new BlockPos(0, y, 0), rotation).offset(pPos);
                var stateOver = pLevel.getBlockState(posOver);
                if (!stateOver.is(this)) continue; //return false;
                if (stateOver.getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(-1) != y) return false;
            }
            return true;
        }
        for (var y = 1; y <= height; y++) {
            var posUnder = RotationUtil.rotate(new BlockPos(0, -y, 0), rotation).offset(pPos);
            var stateUnder = pLevel.getBlockState(posUnder);
            if (!stateUnder.is(this)) return false;
            if (stateUnder.getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(-1) != (height - y))
                return false;
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        var rotation = RotationUtil.getRotation(pState);
        for (var y = 1; y <= (type.height - 1); y++) {
            BlockPos topPos = RotationUtil.rotate(new BlockPos(0, y, 0), rotation).offset(pPos);
            pLevel.setBlock(topPos, pState.setValue(JSGProperties.CARTOUCHE_BLOCK_INDEX, y), (y == (type.height - 1)) ? 3 : 2);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide) {
            preventCreativeDropFromBottomPart(pLevel, pPos, pState, pPlayer);
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        var height = state.getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(0);
        var rotation = RotationUtil.getRotation(state);

        var bottomPos = RotationUtil.rotate(new BlockPos(0, -height, 0), rotation).offset(pos);
        var bottomState = level.getBlockState(bottomPos);
        if (!bottomState.is(state.getBlock())) return;
        var maxY = ((CartoucheBlock) bottomState.getBlock()).type.height;
        for (var y = 0; y <= (maxY - 1); y++) {
            var partPos = RotationUtil.rotate(new BlockPos(0, y, 0), rotation).offset(bottomPos);
            var partState = level.getBlockState(partPos);
            if (!partState.is(state.getBlock())) continue;
            if (partState.getValue(JSGProperties.CARTOUCHE_BLOCK_INDEX) != y) continue;

            var newState = partState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            level.setBlock(partPos, newState, 35);
            level.levelEvent(player, 2001, partPos, Block.getId(partState));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @NotNull
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var height = state.getOptionalValue(JSGProperties.CARTOUCHE_BLOCK_INDEX).orElse(0);
        var rotation = RotationUtil.getRotation(state);
        var bePos = RotationUtil.rotate(new BlockPos(0, -height, 0), rotation).offset(pos);
        var be = level.getBlockEntity(bePos);
        if (be instanceof CartoucheBE cartouche) {
            var usedStack = player.getItemInHand(hand);
            if (usedStack.getItem() instanceof DyeItem dye) {
                if (!level.isClientSide()) {
                    cartouche.setColor(dye.getDyeColor());
                    level.playSound(null, cartouche.getBlockPos(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.isCreative())
                        usedStack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            if (usedStack.getItem() instanceof GlowInkSacItem) {
                if (!level.isClientSide()) {
                    cartouche.setShiny(true);
                    level.playSound(null, cartouche.getBlockPos(), SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.isCreative())
                        usedStack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            var mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            var offStack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (mainStack.getItem() == CoreItems.BLACK_CHALK.get() && offStack.getItem() == CoreItems.NOTEBOOK_PAGE_EMPTY.get()) {
                if (!level.isClientSide) {
                    if (cartouche.notebookPageDataWrapper != null) {
                        var stack = cartouche.getNoteBookPage(level, cartouche.getBlockPos());
                        player.addItem(stack);
                        offStack.shrink(1);
                        level.playSound(null, player.blockPosition(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.PLAYERS, 1f, 1f);
                        mainStack.hurt(1, level.random, (ServerPlayer) player);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            if (mainStack.getItem() == CoreItems.JSG_HAMMER.get() && offStack.hasTag()) {
                var pageTag = offStack.getTag();
                if (pageTag != null) {
                    CompoundTag addressTag;
                    if (pageTag.contains("pages"))
                        addressTag = NotebookItem.getSelectedPageFromCompound(pageTag);
                    else
                        addressTag = pageTag;
                    if (addressTag != null) {
                        var pageData = NotebookPageType.pageDataFromCompound(addressTag);
                        if (pageData != null) {
                            if (pageData.data() instanceof IAddressNotebookPageData) {
                                if (!level.isClientSide) {
                                    cartouche.setAddress(pageData);
                                    level.playSound(null, player.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1f, 1f);
                                    mainStack.hurt(1, level.random, (ServerPlayer) player);
                                }
                                return InteractionResult.sidedSuccess(level.isClientSide());
                            }
                        }
                    }
                }
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }


    @Nullable
    public RegistryObject<CreativeModeTab> getTab() {
        return CoreTabs.TAB_CARTOUCHES;
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.rotateDir(blockState.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY), rotation));
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(JSGProperties.FACING_HORIZONTAL_PROPERTY, BlockPosHelper.flipDir(blockState.getValue(JSGProperties.FACING_HORIZONTAL_PROPERTY), mirror));
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull FluidState getFluidState(BlockState p_152045_) {
        return p_152045_.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_152045_);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter blockGetter, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        //ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CartoucheBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    @NotNull
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        var rotation = RotationUtil.getRotation(pState);
        var aabb = RotationUtil.rotate(type.box(false), rotation, new Vec3(0.5, 0.5, 0.5));
        return Shapes.create(aabb);
    }

    @Override
    public JSGBlockItem getItemBlock() {
        return new CartoucheItem(this, new Item.Properties(), getTabs());
    }
}
