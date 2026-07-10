package dev.tauri.jsg.core.common.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.Rotation.*;

public class BlockPosHelper {
    public static double dist(Vec3i vec1, int vec2X, int vec2Y, int vec2Z) {
        return dist(vec1, new Vec3i(vec2X, vec2Y, vec2Z));
    }

    public static double dist(Vec3i vec1, Vec3i vec2) {
        double d0 = (vec1.getX() - vec2.getX());
        double d1 = (vec1.getY() - vec2.getY());
        double d2 = (vec1.getZ() - vec2.getZ());
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }


    public static int getIntRotation(Direction facing, boolean inverted) {
        return switch (facing) {
            case EAST, DOWN -> (inverted ? 90 : 270);
            case SOUTH -> (!inverted ? 180 : 0);
            case WEST, UP -> (!inverted ? 90 : 270);
            default -> (inverted ? 180 : 0);
        };
    }

    public static int getIntDHDRotationFromFacing(Direction facing, boolean inverted) {
        return (int) Math.floor((getIntRotation(facing, inverted) / 360f) * 16);
    }

    public static Direction rotateDir(Direction dir, Rotation rot) {
        int count = 0;
        if (rot == CLOCKWISE_90)
            count = 1;
        if (rot == CLOCKWISE_180)
            count = 2;
        if (rot == COUNTERCLOCKWISE_90)
            count = 3;

        for (int i = 0; i < count; i++) {
            dir = dir.getClockWise();
        }
        return dir;
    }

    public static int rotateDHDDir(int dir, Rotation rot) {
        int count = 0;
        if (rot == CLOCKWISE_90)
            count = 1;
        if (rot == CLOCKWISE_180)
            count = 2;
        if (rot == COUNTERCLOCKWISE_90)
            count = 3;

        for (int i = 0; i < count; i++) {
            dir += 4;
            dir = (dir % 16);
        }
        return dir;
    }

    public static Direction flipDir(Direction dir, Mirror mirror) {
        if (mirror == null) return dir;
        if (mirror == Mirror.FRONT_BACK && dir.getAxis() == Direction.Axis.X) return dir.getOpposite();
        if (mirror == Mirror.LEFT_RIGHT && dir.getAxis() == Direction.Axis.Z) return dir.getOpposite();
        return dir;
    }


    public static BlockPos getPos(Vec3 vector) {
        return new BlockPos((int) vector.x, (int) vector.y, (int) vector.z);
    }

    public static Vec3 getVec3(Vec3i vector) {
        return new Vec3(vector.getX(), vector.getY(), vector.getZ());
    }
}
