package dev.tauri.jsg.core.common.blockstate;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nullable;
import java.util.Arrays;

public class JSGProperties {
    //public static final EnumProperty<JUBCableVariant> JUB_VARIANT = EnumProperty.create("jub_variant", JUBCableVariant.class);
    public static final IntegerProperty ROTATION_PROPERTY = IntegerProperty.create("rotation_property", 0, 15);
    public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");
    public static final IntegerProperty CARTOUCHE_BLOCK_INDEX = IntegerProperty.create("cartouche_block_index", 0, 2);
    public static final BooleanProperty RENDER_BLOCK_PROPERTY = BooleanProperty.create("render_block");
    public static final DirectionProperty FACING_HORIZONTAL_PROPERTY = DirectionProperty.create("facing", Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));

    /**
     * 0 - normal facing
     * 1 - up facing
     * 2 - down facing
     */
    public static final IntegerProperty FACING_VERTICAL_PROPERTY = IntegerProperty.create("facing_vertical", 0, 2);
    public static final BooleanProperty HAS_COLLISIONS = BooleanProperty.create("has_collisions");
    public static final BooleanProperty ORLIN_BROKEN = BooleanProperty.create("orlin_broken");

    /**
     * Used by CapacitorBlock to display energy level.
     */
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 10);

    public static int getVerticalFacingByDirection(Direction dir) {
        return (dir == Direction.UP ? 1 : (dir == Direction.DOWN ? 2 : 0));
    }

    public static Direction getVerticalDirectionByPitch(float pitch) {
        if (pitch > 60)
            return Direction.DOWN;
        if (pitch < -60)
            return Direction.UP;
        return Direction.SOUTH;
    }

    @Nullable
    public static Direction getDirectionByVerticalFacing(int facing) {
        return switch (facing) {
            case 1 -> Direction.UP;
            case 2 -> Direction.DOWN;
            default -> null;
        };
    }
}
