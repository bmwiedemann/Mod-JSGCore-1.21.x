package dev.tauri.jsg.core.common.raycaster;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.raycaster.util.RayCastedButton;
import dev.tauri.jsg.core.common.raycaster.util.RaycasterVertex;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public abstract class Raycaster {
    public static boolean checkInstancesAndActivate(BlockState clickedBlock, Level level, BlockPos pos, Player player, InteractionHand hand) {
        var raycasters = JSGCoreRegistries.R_RAYCASTER.get().stream().toList();
        if (raycasters == null) return false;
        for (var raycaster : raycasters) {
            if (raycaster.testBlockState(clickedBlock))
                return raycaster.onActivated(level, pos, player, hand);
        }
        return false;
    }

    public abstract boolean testBlockState(BlockState blockState);

    protected abstract List<RayCastedButton> getButtons();

    public abstract Vector3f getTranslation(Level level, BlockPos pos);

    protected abstract boolean buttonClicked(Level level, Player player, int buttonId, BlockPos pos, InteractionHand hand);

    public abstract float getRotation(Level level, BlockPos pos, Player player);

    public boolean isButtonEnabled(Level level, Player player, int buttonId, BlockPos pos, InteractionHand hand) {
        return true;
    }

    public boolean onActivated(Level level, BlockPos pos, Player player, InteractionHand hand) {
        var btn = getRaycastedButton(level, pos, player, hand);
        if (btn == null) return false;
        return buttonClicked(level, player, btn.buttonId, pos, hand);
    }

    @Nullable
    public RayCastedButton getRaycastedButton(Level level, BlockPos pos, Player player, InteractionHand hand) {
        var rotation = getRotation(level, pos, player);
        Vec3 lookVec = player.getLookAngle();
        for (RayCastedButton btn : getButtons()) {
            if (!isButtonEnabled(level, player, btn.buttonId, pos, hand)) continue;
            List<Vector3f> veritices = btn.vectors;
            List<Vec3> polygon = new ArrayList<>();
            for (var v : veritices) {
                polygon.add(getTransposed(v, rotation, level, pos, player).getVec3());
            }
            int n = veritices.size();

            if (doesRayIntersectPolygon(player.position().add(0, player.getEyeHeight(player.getPose()), 0), player.getLookAngle(), polygon)) {
                return btn;
            }
        }
        return null;
    }

    public float getScale() {
        return 1f;
    }

    public RaycasterVertex getTransposed(Vector3f v, float rotation, Level Level, BlockPos pos, Player player) {
        RaycasterVertex current = new RaycasterVertex(v.x, v.y, v.z);

        return current.rotate(rotation).localToGlobal(pos, getTranslation(Level, pos), getScale());
    }

    public boolean doesRayIntersectPolygon(Vec3 eye, Vec3 direction, List<Vec3> polygon) {
        if (polygon.size() < 3) return false;

        Vec3 normal = polygon.get(1).subtract(polygon.get(0)).cross(polygon.get(2).subtract(polygon.get(0))).normalize();
        double denom = normal.dot(direction);

        if (Math.abs(denom) < 1e-6) return false; // Paprsek je rovnoběžný s rovinou

        double t = -normal.dot(eye.subtract(polygon.get(0))) / denom;
        if (t < 0) return false; // Průsečík je za pozicí pozorovatele

        Vec3 intersection = eye.add(direction.scale(t));
        return isPointInPolygonBarycentric(intersection, polygon);
    }

    public boolean isPointInPolygonBarycentric(Vec3 point, List<Vec3> polygon) {
        Vec3 p0 = polygon.get(0);
        for (int i = 1; i < polygon.size() - 1; i++) {
            Vec3 p1 = polygon.get(i);
            Vec3 p2 = polygon.get(i + 1);
            if (isPointInTriangle(point, p0, p1, p2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPointInTriangle(Vec3 p, Vec3 a, Vec3 b, Vec3 c) {
        Vec3 v0 = c.subtract(a);
        Vec3 v1 = b.subtract(a);
        Vec3 v2 = p.subtract(a);

        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        onRightClick(event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        onRightClick(event);
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        onRightClick(event);
    }

    private static void onRightClick(PlayerInteractEvent event) {
        Player player = event.getEntity();
        Level world = player.level();
        if (!world.isClientSide()) return;

        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        if (!player.isSpectator()) {
            BlockPos pos = player.blockPosition();
            Direction playerFacing = player.getDirection();

            Direction left = playerFacing.getCounterClockWise();
            Direction right = playerFacing.getClockWise();

            Iterable<BlockPos> blocks = BlockPos.betweenClosed(pos.offset(left.getNormal()).below().offset(playerFacing.getOpposite().getNormal()), pos.offset(right.getNormal()).above(2).offset(playerFacing.getNormal().multiply(2)));

            for (BlockPos activatedBlock : blocks) {
                var block = world.getBlockState(activatedBlock);
                if (event instanceof net.neoforged.bus.api.ICancellableEvent cancellable && !cancellable.isCanceled() && Raycaster.checkInstancesAndActivate(block, world, activatedBlock, player, event.getHand())) {
                    cancellable.setCanceled(true);
                    player.swing(event.getHand());
                    break;
                }
            }
        }
    }
}