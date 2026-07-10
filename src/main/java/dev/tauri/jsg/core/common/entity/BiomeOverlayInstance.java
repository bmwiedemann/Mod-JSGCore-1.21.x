package dev.tauri.jsg.core.common.entity;

import dev.tauri.jsg.core.common.helper.BlockHelper;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.util.TagFetcher;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public record BiomeOverlayInstance(String unlocalizedName, String suffix, int color) {
    public BiomeOverlayInstance(String unlocalizedName, String suffix, int color) {
        this.suffix = (suffix.isEmpty() ? "" : "_" + suffix);
        this.unlocalizedName = "gui.biome_overlay." + unlocalizedName;
        this.color = color;
    }

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByItem(ItemStack stack) {
        return Objects.requireNonNull(getBiomeOverlayByItem(stack, false));
    }

    public static BiomeOverlayInstance getBiomeOverlayByItem(ItemStack stack, boolean canBeNull) {
        for (var overlay : JSGCoreRegistries.R_BIOME_OVERLAY.get().getEntries()) {
            var id = overlay.getKey().location();
            var tag = ItemTags.create(JSGMapping.rl(id.getNamespace(), "biome_overlay/" + id.getPath()));
            if (stack.is(tag)) return overlay.getValue();
        }
        return (canBeNull ? null : CoreBiomeOverlays.NORMAL.get());
    }

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByBiome(Holder<Biome> biome) {
        for (var overlay : JSGCoreRegistries.R_BIOME_OVERLAY.get().getEntries()) {
            var id = overlay.getKey().location();
            var tag = TagKey.create(Registries.BIOME, JSGMapping.rl(id.getNamespace(), "biome_overlay/" + id.getPath()));
            if (biome.is(tag)) return overlay.getValue();
        }
        return CoreBiomeOverlays.NORMAL.get();
    }

    @NotNull
    public static List<BiomeOverlayInstance> values() {
        return new ArrayList<>(JSGCoreRegistries.R_BIOME_OVERLAY.get().getValues());
    }

    @Override
    public @NonNull String toString() {
        return Optional.ofNullable(JSGCoreRegistries.R_BIOME_OVERLAY.get().getKey(this)).map(ResourceLocation::toString).orElse("NOT REGISTERED (" + unlocalizedName + ")");
    }

    @NotNull
    public static BiomeOverlayInstance getUpdatedBiomeOverlay(Level world, BlockPos topmostBlock) {
        return getBiomeOverlayByBlockPos(world, topmostBlock);
    }

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByBlockPos(Level world, BlockPos topmostBlock) {
        return getBiomeOverlayByBlockPos(world, topmostBlock, true);
    }

    @NotNull
    public static BiomeOverlayInstance getBiomeOverlayByBlockPos(Level world, BlockPos topmostBlock, boolean checkUnderSky) {
        Holder<Biome> biome = world.getBiome(topmostBlock);

        // If not Nether and block not under sky
        if (checkUnderSky && world.dimension() != Level.NETHER && !BlockHelper.isBlockDirectlyUnderSky(world, topmostBlock))
            return CoreBiomeOverlays.NORMAL.get();

        if (biome.value().coldEnoughToSnow(topmostBlock))
            return CoreBiomeOverlays.FROST.get();

        return getBiomeOverlayByBiome(biome);

    }

    @NotNull
    public static BiomeOverlayInstance byId(ResourceLocation id) {
        return Optional.ofNullable(JSGCoreRegistries.R_BIOME_OVERLAY.get().getValue(id)).orElseGet(CoreBiomeOverlays.NORMAL);
    }

    public ResourceLocation getId() {
        return JSGCoreRegistries.R_BIOME_OVERLAY.get().getKey(this);
    }

    public Collection<Item> getOverlayItems() {
        var id = JSGCoreRegistries.R_BIOME_OVERLAY.get().getKey(this);
        if (id == null) return Collections.emptyList();
        var tag = ItemTags.create(JSGMapping.rl(id.getNamespace(), "biome_overlay/" + id.getPath()));
        return TagFetcher.getItemsInTag(tag);
    }

    public TagKey<Item> getOverlayItemsTag() {
        var id = JSGCoreRegistries.R_BIOME_OVERLAY.get().getKey(this);
        if (id == null) return null;
        return ItemTags.create(JSGMapping.rl(id.getNamespace(), "biome_overlay/" + id.getPath()));
    }

    public TagKey<Biome> getOverlayBiomesTag() {
        var id = JSGCoreRegistries.R_BIOME_OVERLAY.get().getKey(this);
        if (id == null) return null;
        return TagKey.create(Registries.BIOME, JSGMapping.rl(id.getNamespace(), "biome_overlay/" + id.getPath()));
    }

    @Nullable
    public ResourceLocation getOverlayTexture() {
        var id = JSGCoreRegistries.R_BIOME_OVERLAY.get().getKey(this);
        if (id == null) return null;
        return JSGMapping.rl(id.getNamespace(), "textures/biome_overlay/" + id.getPath());
    }
}
