package dev.tauri.jsg.core.common.integration.cctweaked;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.ComputerDeviceHolder;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.AbstractCCMethods;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@SuppressWarnings("unused")
public class CCIntegrationLoaded implements dev.tauri.jsg.core.common.integration.cctweaked.CCIntegrationWrapper {

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public ICCDevice createDevice(ComputerDeviceProvider provider, String deviceType) {
        return (AbstractCCMethods<?>) CCDevice.valueOf(deviceType.toUpperCase()).constructor.construct((BlockEntity) provider);
    }

    @Override
    public void registerPeripheralBE(RegisterCapabilitiesEvent event, BlockEntityType<?> beType) {
        event.registerBlockEntity(PeripheralCapability.get(), beType, (be, side) -> {
            if (!(be instanceof ComputerDeviceProvider provider)) return null;
            if (!(provider.getDeviceHolder() instanceof ComputerDeviceHolder holder)) return null;
            return (IPeripheral) holder.getOrCreateCCDevice();
        });
    }
}
