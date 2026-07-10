package dev.tauri.jsg.core.common.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.ArrayList;
import java.util.List;

public class LootTableInjector {
    public interface IAddition {
        void inject(LootDataManager lootDataManager, List<LootPool> targetLootPools);
    }

    public record ElementAddition(ResourceLocation tablePath, LootPool pool) implements IAddition {
        @Override
        public void inject(LootDataManager lootDataManager, List<LootPool> targetLootPools) {
            targetLootPools.add(pool);
        }
    }

    public record TableAddition(ResourceLocation tablePath, ResourceLocation sourceTable) implements IAddition {
        @Override
        public void inject(LootDataManager lootDataManager, List<LootPool> targetLootPools) {
            targetLootPools.addAll(lootDataManager.getLootTable(sourceTable).pools);
        }
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
        var lootDataManager = server.getLootData();
        for (var builder : BUILDERS) {
            for (var targetPool : builder.targets) {
                var target = lootDataManager.getLootTable(targetPool);
                if (builder.clearPoolFirst) {
                    target.pools.clear();
                }
                for (var addition : builder.additions) {
                    addition.inject(lootDataManager, target.pools);
                }
            }
        }
    }
}
