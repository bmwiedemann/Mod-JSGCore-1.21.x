package dev.tauri.jsg.core.common.util;

import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTank extends net.neoforged.neoforge.fluids.capability.templates.FluidTank {
    public FluidTank(FluidStack stack, int capacity) {
        super(capacity, (e) -> e.getFluid() == stack.getFluid());
    }
}
