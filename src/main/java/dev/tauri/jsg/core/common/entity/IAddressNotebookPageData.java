package dev.tauri.jsg.core.common.entity;

import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public interface IAddressNotebookPageData extends INotebookPageData {
    IAddress getAddress();

    int[] getSymbolsToDisplay();

    @Nullable
    PointOfOrigin getOrigin();

    void setSymbolsToDisplay(int[] symbolsToDisplay);

    void setOrigin(@Nullable PointOfOrigin origin);

    void setAddress(IAddress address);
}
