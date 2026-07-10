package dev.tauri.jsg.core.common.helper;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNullableByDefault;

public class VectorHelper {
    Direction horizontalOrigin;
    Direction verticalOrigin;
    Direction horizontalTarget;
    Direction verticalTarget;
    public VectorHelper(Direction horizontalOrigin, Direction verticalOrigin, Direction horizontalTarget, Direction verticalTarget){
        if(horizontalOrigin == null) horizontalOrigin = Direction.SOUTH;
        if(verticalOrigin == null) verticalOrigin = Direction.SOUTH;
        if(horizontalTarget == null) horizontalTarget = Direction.SOUTH;
        if(verticalTarget == null) verticalTarget = Direction.SOUTH;

        this.horizontalOrigin = horizontalOrigin;
        this.verticalOrigin = verticalOrigin;
        this.horizontalTarget = horizontalTarget;
        this.verticalTarget = verticalTarget;
    }

    public interface RotationFunction {
        Vec3 doStuff(Vec3 southVector);
    }

    @ParametersAreNullableByDefault
    public Vec3 rotate(Vec3 origin, RotationFunction function) {
        if(function == null) function = (Vec3 southVector) -> southVector;
        if(origin == null) return null;
        // horizontal
        Direction.Axis hAxisSource = horizontalOrigin.getAxis();
        Vec3i hVectorSource = horizontalOrigin.getNormal();
        Direction.Axis hAxisTarget = horizontalTarget.getAxis();
        Vec3i hVectorTarget = horizontalTarget.getNormal();
        // vertical
        Direction.Axis vAxisSource = verticalOrigin.getAxis();
        Vec3i vVectorSource = verticalOrigin.getNormal();
        Direction.Axis vAxisTarget = verticalTarget.getAxis();
        Vec3i vVectorTarget = verticalTarget.getNormal();
        // -----------------
        double x = 0;
        double y = 0;
        double z = 0;

        // to south
        if (vAxisSource == Direction.Axis.Y) {
            z = origin.y() * vVectorSource.getY() * -1;
            if (hAxisSource == Direction.Axis.X) {
                x = origin.z() * hVectorSource.getX() * vVectorSource.getY() * -1;
                y = origin.x() * hVectorSource.getX() * vVectorSource.getY() * -1;
            }
            if (hAxisSource == Direction.Axis.Z) {
                x = origin.x() * hVectorSource.getZ() * vVectorSource.getY();
                y = origin.z() * hVectorSource.getZ() * vVectorSource.getY() * -1;
            }
        } else {
            y = origin.y();
            if (hAxisSource == Direction.Axis.X) {
                x = origin.z() * hVectorSource.getX() * -1;
                z = origin.x() * hVectorSource.getX() * -1;
            }
            if (hAxisSource == Direction.Axis.Z) {
                x = origin.x() * hVectorSource.getZ();
                z = origin.z() * hVectorSource.getZ() * -1;
            }
        }

        Vec3 compiledVector = function.doStuff(new Vec3(x, y, z));
        x = compiledVector.x();
        y = compiledVector.y();
        z = compiledVector.z();

        Vec3 result = compiledVector;

        // to target
        if (vAxisTarget == Direction.Axis.Y) {
            if (hAxisTarget == Direction.Axis.X) {
                result = new Vec3(
                        y * hVectorTarget.getX() * vVectorTarget.getY() * -1,
                        z * vVectorTarget.getY(),
                        x * hVectorTarget.getX() * vVectorTarget.getY()
                );
            }
            if (hAxisTarget == Direction.Axis.Z) {
                result = new Vec3(
                        x * hVectorTarget.getZ() * vVectorTarget.getY(),
                        z * vVectorTarget.getY(),
                        y * hVectorTarget.getZ() * vVectorTarget.getY() * -1
                );
            }
        } else {
            if (hAxisTarget == Direction.Axis.X) {
                result = new Vec3(
                        z * hVectorTarget.getX(),
                        y,
                        x * hVectorTarget.getX()
                );
            }
            if (hAxisTarget == Direction.Axis.Z) {
                result = new Vec3(
                        x * hVectorTarget.getZ(),
                        y,
                        z * hVectorTarget.getZ()
                );
            }
        }

        return result;
    }
}
