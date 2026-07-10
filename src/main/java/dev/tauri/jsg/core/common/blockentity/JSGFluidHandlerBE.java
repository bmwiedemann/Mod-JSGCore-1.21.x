package dev.tauri.jsg.core.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Fluid-tank block entity. On NeoForge the handler is no longer exposed through a
 * getCapability override; register the block entity type via
 * {@link dev.tauri.jsg.core.common.capability.CoreCapabilities#registerFluidHandlerBE}.
 */
public abstract class JSGFluidHandlerBE extends BlockEntity {
    protected FluidTank tank;

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

    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return tank;
    }
}
