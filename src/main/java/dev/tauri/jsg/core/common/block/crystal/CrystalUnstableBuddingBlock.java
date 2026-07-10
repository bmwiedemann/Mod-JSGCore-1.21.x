package dev.tauri.jsg.core.common.block.crystal;

import dev.tauri.jsg.core.common.registry.tag.CoreItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class CrystalUnstableBuddingBlock extends CrystalBuddingBlock {

    public final Supplier<Block> stableBudding;
    public final Supplier<BlockState> originalBlockSupplier;

    public CrystalUnstableBuddingBlock(ICrystalColor color, Properties properties, Supplier<Block> stableBudding, Supplier<BlockState> originalBlockSupplier) {
        super(color, null, properties);
        this.stableBudding = stableBudding;
        this.originalBlockSupplier = originalBlockSupplier;
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.AGE_2, 0));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AGE_2);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide) return;
        if (pRandom.nextInt(5) == 0) {
            growUp(pState, pLevel, pPos, pRandom, true);
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    @NotNull
    protected ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack heldStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).is(CoreItemTags.CRYSTAL_FERTILIZERS)) {
            if (!pLevel.isClientSide) {
                if (!pPlayer.isCreative())
                    pPlayer.getItemInHand(pHand).shrink(1);
                growUp(pState, pLevel, pPos, pLevel.random, false);
                pLevel.playSound(null, pPos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 1, 1);
                return ItemInteractionResult.SUCCESS;
            } else return ItemInteractionResult.sidedSuccess(true);
        }
        return super.useItemOn(heldStack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    protected void growUp(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom, boolean chanceToDestroy) {
        if (chanceToDestroy && pRandom.nextFloat() < 0.3) {
            pLevel.setBlock(pPos, originalBlockSupplier.get(), 3);
            return;
        }
        var state = pState.getOptionalValue(BlockStateProperties.AGE_2).map(a -> a >= 2 ? stableBudding.get().defaultBlockState() : pState.setValue(BlockStateProperties.AGE_2, a + 1)).orElse(originalBlockSupplier.get());
        pLevel.setBlock(pPos, state, 3);
    }
}
