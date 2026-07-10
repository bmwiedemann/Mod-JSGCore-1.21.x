package dev.tauri.jsg.core.common.entity.vehicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JSGBoat<T extends Enum<T> & JSGBoatTypeWrapper.Type & StringRepresentable> extends Boat {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(JSGBoat.class, EntityDataSerializers.INT);

    public final JSGBoatTypeWrapper<T> boatTypeWrapper;

    public JSGBoat(JSGBoatTypeWrapper<T> boatTypeWrapper, EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.boatTypeWrapper = boatTypeWrapper;
    }

    public JSGBoat(JSGBoatTypeWrapper<T> boatTypeWrapper, EntityType<? extends Boat> pEntityType, Level level, double pX, double pY, double pZ) {
        this(boatTypeWrapper, pEntityType, level);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    public @NotNull Item getDropItem() {
        return getCustomVariant().getDrop(false);
    }

    public void setVariant(T pVariant) {
        this.entityData.set(DATA_ID_TYPE, pVariant.ordinal());
    }

    public T getCustomVariant() {
        return boatTypeWrapper.byId.apply(entityData.get(DATA_ID_TYPE));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, 0);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putString("Type", getCustomVariant().getSerializedName());
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Type", 8)) {
            this.setVariant(Objects.requireNonNull(boatTypeWrapper.codec.byName(pCompound.getString("Type"))));
        }
    }
}
