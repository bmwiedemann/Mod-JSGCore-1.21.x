package dev.tauri.jsg.core.common.helper;

import dev.tauri.jsg.core.common.blockentity.ILinkableBE;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public class LinkingHelper {

    /**
     * Finds closest block of the given type within given radius.
     *
     * @param world        World instance.
     * @param startPos     Starting position.
     * @param radius       Radius. Subtracted and added to the startPos.
     * @param allowedBlock Searched block instance. Must provide {@link BlockEntity} and {@link BlockEntity} should implement {@link ILinkableBE}.
     * @return Found block's {@link BlockPos}. Null if not found.
     */

    @SuppressWarnings("unused")
    @Nullable
    public static BlockPos findClosestUnlinked(Level world, BlockPos startPos, BlockPos radius, TagKey<Block> allowedBlock) {
        double closestDistance = Double.MAX_VALUE;
        BlockPos closest = null;

        for (BlockPos target : BlockPos.betweenClosed(startPos.subtract(radius), startPos.offset(radius))) {
            var imPos = target.immutable();
            if (world.getBlockState(imPos).is(allowedBlock)) {
                if ((world.getBlockEntity(imPos) instanceof ILinkableBE<?> linkedTile) && (linkedTile.canLinkTo())) {
                    double distanceSq = startPos.distSqr(imPos);

                    if (distanceSq < closestDistance) {
                        closestDistance = distanceSq;
                        closest = imPos;
                    }
                }
            }
        }

        return closest;
    }

    @Nullable
    public static BlockPos findClosestPos(Level world, BlockPos startPos, BlockPos radius, TagKey<Block> allowedBlock, ArrayList<BlockPos> blacklist) {
        return findClosestPos(world, startPos, radius, allowedBlock, blacklist, (pos) -> true);
    }

    @Nullable
    public static BlockPos findClosestPos(Level world, BlockPos startPos, BlockPos radius, TagKey<Block> allowedBlock, ArrayList<BlockPos> blacklist, Predicate<BlockPos> posPredicate) {
        return findClosestPos(world, startPos, radius, allowedBlock, blacklist, posPredicate, false);
    }

    @Nullable
    public static BlockPos findClosestPos(Level world, BlockPos startPos, BlockPos radius, TagKey<Block> allowedBlock, ArrayList<BlockPos> blacklist, Predicate<BlockPos> posPredicate, boolean bypassWatchdog) {
        double closestDistance = Double.MAX_VALUE;
        BlockPos closest = null;

        for (BlockPos target : BlockPos.betweenClosed(startPos.subtract(radius), startPos.offset(radius))) {
            // prevent server from shutdown caused by WatchDog
            if (bypassWatchdog && world instanceof ServerLevel sl)
                sl.getServer().nextTickTimeNanos = Util.getNanos();

            if (world.getBlockState(target).is(allowedBlock) && !(blacklist.contains(target)) && posPredicate.test(target)) {
                double distanceSq = startPos.distSqr(target);

                if (distanceSq < closestDistance) {
                    closestDistance = distanceSq;
                    closest = target.immutable();
                }
            }
        }

        return closest;
    }

    @Nullable
    public static <T> T findClosestTile(Level world, BlockPos startPos, TagKey<Block> allowedBlocks, Class<T> tileClass, int radiusXZ, int radiusY) {
        return findClosestTile(world, startPos, allowedBlocks, tileClass, radiusXZ, radiusY, (pos) -> true);
    }

    @Nullable
    public static <T> T findClosestTile(Level world, BlockPos startPos, TagKey<Block> allowedBlocks, Class<T> tileClass, int radiusXZ, int radiusY, Predicate<BlockPos> posPredicate) {
        return findClosestTile(world, startPos, allowedBlocks, tileClass, radiusXZ, radiusY, posPredicate, false);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T findClosestTile(Level world, BlockPos startPos, TagKey<Block> allowedBlocks, Class<T> tileClass, int radiusXZ, int radiusY, Predicate<BlockPos> posPredicate, boolean bypassWatchdog) {
        var closestPos = LinkingHelper.findClosestPos(world, startPos, new BlockPos(radiusXZ, radiusY, radiusXZ), allowedBlocks, new ArrayList<>(), posPredicate.and((pos) -> {
            var tile = world.getBlockEntity(pos);
            if (tile == null) return false;
            return tileClass.isInstance(tile);
        }), bypassWatchdog);
        if (closestPos == null) return null;
        return (T) world.getBlockEntity(closestPos);
    }
}
