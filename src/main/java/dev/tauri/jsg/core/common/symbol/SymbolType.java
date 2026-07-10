package dev.tauri.jsg.core.common.symbol;

import dev.tauri.jsg.core.client.model.IModelLoader;
import dev.tauri.jsg.core.client.screen.tab.ITab;
import dev.tauri.jsg.core.client.screen.tab.ITabAddress;
import dev.tauri.jsg.core.client.texture.ITextureLoader;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class SymbolType<T extends SymbolInterface> {
    @NotNull
    public static SymbolType<?> getNext(SymbolType<?> current, boolean previous) {
        SymbolType<?> prev = null;
        SymbolType<?> next = null;
        SymbolType<?> last = current;
        boolean getLast = false;
        for (var type : JSGCoreRegistries.R_SYMBOL_TYPE.get().getEntries()) {
            if (getLast) {
                last = type.getValue();
                continue;
            }
            if (next == null) next = type.getValue();
            if (previous) {
                if (type.getValue() == current) {
                    if (prev == null) {
                        getLast = true;
                        continue;
                    }
                    return prev;
                }
            } else {
                if (prev == current)
                    return type.getValue();
            }
            prev = type.getValue();
        }
        if (!previous) return (next == null ? current : next);
        return last;
    }


    @Nonnull
    public static SymbolType<?> getRandom() {
        var index = new Random().nextInt(JSGCoreRegistries.R_SYMBOL_TYPE.get().getEntries().size());
        int i = 0;
        for (var type : JSGCoreRegistries.R_SYMBOL_TYPE.get().getEntries()) {
            if (i == index)
                return type.getValue();
            i++;
        }
        throw new IllegalStateException("Random Symbol Type not found!");
    }

    public static List<? extends SymbolType<?>> values(SymbolUsage symbolUsage) {
        return JSGCoreRegistries.R_SYMBOL_TYPE.get().getEntries().stream()
                .filter(e -> e.getValue().getSymbolUsage().equals(symbolUsage))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public String toString() {
        return getId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof SymbolType<?> s)) return false;
        return Objects.equals(s.getId(), getId());
    }

    public boolean hasOrigin() {
        return getOrigin() != null;
    }

    public ResourceLocation getId() {
        return JSGCoreRegistries.R_SYMBOL_TYPE.get().getKey(this);
    }

    @Nullable
    public static SymbolType<?> byId(ResourceLocation id) {
        return JSGCoreRegistries.R_SYMBOL_TYPE.get().getValue(id);
    }

    @OnlyIn(Dist.CLIENT)
    public ITab.ITabBuilder finalizeAddressTab(ITab.ITabBuilder builder) {
        var id = getId();
        return builder.setTexture(JSGMapping.rl(id.getNamespace(), "textures/gui/tab/" + id.getPath() + "_addess_tab.png"), 256)
                .setBackgroundTextureLocation(0, 22)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract ITabAddress.SymbolCoords getSymbolCoords(int symbol);

    public abstract SymbolUsage getSymbolUsage();

    public abstract T[] getValues();

    public abstract Block getBaseBlock();

    public abstract Item getGlyphUpgrade();

    public abstract Block getDHDBlock();

    public abstract T getBRB();

    public abstract int getIconWidth();

    public abstract int getIconHeight();

    public abstract T getRandomSymbol(Random random);

    public abstract T getOrigin();

    public abstract int getMaxSymbolsDisplay(boolean hasUpgrade);

    public abstract int getMinimalSymbolCountTo(SymbolType<?> symbolType, boolean localDial);

    public abstract boolean validateDialedAddress(IAddress address);

    public abstract float getAnglePerGlyph();

    public float getAngleByAngIndex(int index) {
        return index;
    }

    public float getAngleOfNearest(float angle) {
        return 0;
    }

    public abstract T getSymbolByAngle(float angle, float bounds);

    public T getSymbolByAngle(float angle) {
        return getSymbolByAngle(angle, 360);
    }

    public abstract T getTopSymbol();

    public T valueOf(int id) {
        return ID_MAP.get(id);
    }

    public T fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }

    public abstract T getFirstValidForAddress();

    public abstract ITextureLoader getTextureLoader();

    public abstract IModelLoader getModelLoader();

    public abstract IPointOfOriginType getPointOfOriginType();

    protected final Map<Integer, T> ID_MAP = new HashMap<>();
    protected final Map<String, T> ENGLISH_NAME_MAP = new HashMap<>();

    {
        for (T symbol : getValues()) {
            ID_MAP.put(symbol.getId(), symbol);
            ENGLISH_NAME_MAP.put(symbol.getEnglishName(null).toLowerCase(), symbol);
            for (var poo : PointOfOriginsLoader.INSTANCE.getLoadedOrigins(getPointOfOriginType()).orElse(new HashMap<>()).values())
                ENGLISH_NAME_MAP.put(symbol.getEnglishName(poo).toLowerCase(), symbol);
        }
        ENGLISH_NAME_MAP.put("point of origin", getOrigin());
    }
}
