package dev.tauri.jsg.core.common.symbol;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SymbolUtil {

    @NotNull
    public static SymbolInterface getSymbolFromNameIndexOrThrow(SymbolType<?> symbolType, Object nameIndex) throws IllegalArgumentException {
        var symbol = getSymbolFromNameIndex(symbolType, nameIndex);
        if (symbol == null)
            throw new IllegalArgumentException("bad argument (symbol name/index invalid) (tried: " + nameIndex + ")");
        return symbol;
    }

    @Nullable
    public static SymbolInterface getSymbolFromNameIndex(SymbolType<?> symbolType, Object nameIndex) {
        SymbolInterface symbol = null;
        if (nameIndex instanceof Integer)
            symbol = symbolType.valueOf((Integer) nameIndex);
        else if (nameIndex instanceof Double)
            symbol = symbolType.valueOf(((Double) nameIndex).intValue());
        else if (nameIndex instanceof String)
            symbol = symbolType.fromEnglishName((String) nameIndex);
        else if (nameIndex instanceof byte[])
            symbol = symbolType.fromEnglishName(new String((byte[]) nameIndex));
        if (symbol == null)
            symbol = symbolType.fromEnglishName(nameIndex.toString());
        return symbol;
    }
}
