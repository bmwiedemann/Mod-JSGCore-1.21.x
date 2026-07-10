package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;

import javax.annotation.Nullable;

/**
 * Specifies that block entity should provide it's address
 */
public interface IAddressProvider {
    int getPageProgress();

    IAddress getAddress(SymbolType<?> symbolType);

    @Nullable
    PointOfOrigin getPointOfOrigin(SymbolType<?> symbolType);
}
