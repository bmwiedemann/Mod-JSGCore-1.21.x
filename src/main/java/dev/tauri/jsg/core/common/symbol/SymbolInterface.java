package dev.tauri.jsg.core.common.symbol;

import dev.tauri.jsg.core.client.CoreLoadersHolder;
import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.texture.ITexture;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface SymbolInterface {
    boolean origin();

    float getAngle();

    int getAngleIndex();

    int getId();

    default boolean brb() {
        return false;
    }

    default String getEnglishName(@Nullable PointOfOrigin origin) {
        if (origin != null && origin())
            return origin.getName(getEnglishName()).getString();
        return getEnglishName();
    }

    String getEnglishName();

    ResourceLocation getIconResource(@Nullable PointOfOrigin origin);

    ResourceLocation getModelResource(IPointOfOriginType type, @Nullable PointOfOrigin origin, String variant);

    SymbolType<?> getSymbolType();

    boolean isValidForAddress();

    SymbolInterface getNext(boolean previous);

    default boolean renderIconByMinecraft() {
        return !origin();
    }

    default boolean canBePressed() {
        return true;
    }

    default void bindIconTexture(@Nullable PointOfOrigin origin) {
        var location = getIconResource(origin);
        if (renderIconByMinecraft())
            ITexture.bindTextureWithMc(location);
        else {
            var loader = getSymbolType().getTextureLoader();
            if (origin() && CoreLoadersHolder.INSTANCE.texture().isTextureLoaded(location)) // origins are loaded and saved inside JSG texture loader by Origins Loader
                loader = CoreLoadersHolder.INSTANCE.texture();
            loader.getTexture(location).bindTexture();
        }
    }

    @NotNull
    default AbstractOBJModel getModel(IPointOfOriginType type, @Nullable PointOfOrigin origin, String variant) {
        var location = getModelResource(type, origin, variant);
        var loader = getSymbolType().getModelLoader();
        if (origin() && CoreLoadersHolder.INSTANCE.model().isModelLoaded(location)) // origins are loaded and saved inside JSG texture loader by Origins Loader
            loader = CoreLoadersHolder.INSTANCE.model();
        return loader.getModel(location);
    }
}
