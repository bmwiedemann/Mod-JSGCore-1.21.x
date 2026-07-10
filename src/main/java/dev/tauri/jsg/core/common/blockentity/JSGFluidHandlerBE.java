package dev.tauri.jsg.core.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.FluidHandlerBlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JSGFluidHandlerBE extends FluidHandlerBlockEntity {
    public JSGFluidHandlerBE(@NotNull BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int capacity) {
        super(blockEntityType, pos, state);
        tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                JSGFluidHandlerBE.this.onFluidChanged();
            }
        };
    }

    public void onFluidChanged() {

    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }
}
