package dev.tauri.jsg.core.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

public class LootTableInjector {
    public interface IAddition {
        void inject(MinecraftServer server, List<LootPool> targetLootPools);
    }

    public record ElementAddition(ResourceLocation tablePath, LootPool pool) implements IAddition {
        @Override
        public void inject(MinecraftServer server, List<LootPool> targetLootPools) {
            targetLootPools.add(pool);
        }
    }

    public record TableAddition(ResourceLocation tablePath, ResourceLocation sourceTable) implements IAddition {
        @Override
        public void inject(MinecraftServer server, List<LootPool> targetLootPools) {
            targetLootPools.addAll(getTable(server, sourceTable).pools);
        }
    }

    private static LootTable getTable(MinecraftServer server, ResourceLocation id) {
        return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
    }

    public static class Builder {
        protected List<ResourceLocation> targets;
        protected List<IAddition> additions = new ArrayList<>();
        protected boolean clearPoolFirst = false;

        protected Builder(List<ResourceLocation> targets) {
            this.targets = targets;
        }

        public static Builder forTargets(ResourceLocation... targets) {
            return forTargets(List.of(targets));
        }

        public static Builder forTargets(List<ResourceLocation> targets) {
            return new Builder(targets);
        }

        public Builder add(IAddition... additions) {
            return this.add(List.of(additions));
        }

        public Builder add(List<IAddition> additions) {
            this.additions.addAll(additions);
            return this;
        }

        public Builder clearPoolFirst() {
            this.clearPoolFirst = true;
            return this;
        }


        public void submit() {
            BUILDERS.add(this);
        }
    }

    private static final List<Builder> BUILDERS = new ArrayList<>();

    public static void inject(MinecraftServer server) {
        for (var builder : BUILDERS) {
            for (var targetPool : builder.targets) {
                var target = getTable(server, targetPool);
                if (target == LootTable.EMPTY) continue;
                // loot tables are codec-built with immutable pool lists; swap in a mutable copy (field is AT'd non-final)
                target.pools = new ArrayList<>(target.pools);
                if (builder.clearPoolFirst) {
                    target.pools.clear();
                }
                for (var addition : builder.additions) {
                    addition.inject(server, target.pools);
                }
            }
        }
    }
}
