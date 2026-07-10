package dev.tauri.jsg.core.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

@SuppressWarnings("all")
public class TagFetcher {
    public static List<Item> getItemsInTag(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getTag(tag)
                .map(named -> named.stream().map(Holder::value).toList())
                .orElse(List.of());
    }

    public static List<Block> getBlocksInTag(TagKey<Block> tag) {
        return BuiltInRegistries.BLOCK.getTag(tag)
                .map(named -> named.stream().map(Holder::value).toList())
                .orElse(List.of());
    }

    public static List<Biome> getBiomesInTag(TagKey<Biome> tag) {
        // Biomes are a datapack registry on NeoForge; resolvable only with a running server.
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return List.of();
        return server.registryAccess().registryOrThrow(Registries.BIOME).getTag(tag)
                .map(named -> named.stream().map(Holder::value).toList())
                .orElse(List.of());
    }
}
