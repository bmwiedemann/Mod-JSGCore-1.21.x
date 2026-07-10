package dev.tauri.jsg.core.common.symbol.pointoforigin;

import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class PointOfOrigin implements StringRepresentable, INBTSerializable<CompoundTag> {
    @Nullable
    public static PointOfOrigin fromNBT(CompoundTag compound, @Nullable Supplier<PointOfOrigin> fallback) {
        var id = JSGMapping.rl(compound.getString("id"));
        var namespace = compound.getString("namespace");
        var type = PointOfOriginsLoader.INSTANCE.getPoOType(JSGMapping.rl(namespace));
        if (type.isEmpty()) return (fallback == null ? null : fallback.get());
        return PointOfOriginsLoader.INSTANCE.getOriginByIdOrElse(type.get(), id, fallback);
    }

    @Nullable
    public static PointOfOrigin fromBytes(FriendlyByteBuf buf, @Nullable Supplier<PointOfOrigin> fallback) {
        var id = buf.readResourceLocation();
        var type = PointOfOriginsLoader.INSTANCE.getPoOType(buf.readResourceLocation());
        if (type.isEmpty()) return (fallback == null ? null : fallback.get());
        return PointOfOriginsLoader.INSTANCE.getOriginByIdOrElse(type.get(), id, fallback);
    }

    public final ResourceLocation id;
    public final IPointOfOriginType forType;

    public PointOfOrigin(ResourceLocation id, IPointOfOriginType forType) {
        this.id = id;
        this.forType = forType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PointOfOrigin origin)) return false;
        return origin.id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode() * 31;
    }

    @Override
    public String getSerializedName() {
        return id.toString();
    }

    public ResourceLocation getPath(String variant, boolean model) {
        return JSGMapping.rl(id.getNamespace(), (model ? "models" : "textures") + "/point_of_origins/" + forType.getPoONamespaceIdentifier().getNamespace() + "/" + forType.getPoONamespaceIdentifier().getPath() + "/" + id.getPath() + "/" + variant);
    }

    
    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        return serializeNBT();
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag compound) {
        deserializeNBT(compound);
    }

    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putString("id", id.toString());
        compound.putString("namespace", forType.getPoONamespaceIdentifier().toString());
        return compound;
    }

    public Component getName(String fallback) {
        return Component.translatableWithFallback("poo." + id.getNamespace() + "." + forType.getPoONamespaceIdentifier().getNamespace() + "." + forType.getPoONamespaceIdentifier().getPath().replace('/', '.') + "." + id.getPath().replace('/', '.'), fallback);
    }

    
    public void deserializeNBT(CompoundTag compound) {
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeResourceLocation(forType.getPoONamespaceIdentifier());
    }
}
