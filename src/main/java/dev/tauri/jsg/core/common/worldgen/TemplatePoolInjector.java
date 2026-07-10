package dev.tauri.jsg.core.common.worldgen;

import com.mojang.datafixers.util.Pair;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

public class TemplatePoolInjector {
    public interface IAddition {
        void inject(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, List<Pair<StructurePoolElement, Integer>> raw, ObjectArrayList<StructurePoolElement> templates);
    }

    public record ElementAddition(ResourceLocation structurePath, int weight,
                                  boolean matchTerrain, ResourceLocation processor) implements IAddition {
        public ElementAddition(ResourceLocation structurePath, int weight, boolean matchTerrain) {
            this(structurePath, weight, matchTerrain, JSGMapping.rl("minecraft", "empty"));
        }

        @Override
        public void inject(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, List<Pair<StructurePoolElement, Integer>> raw, ObjectArrayList<StructurePoolElement> templates) {
            Holder<StructureProcessorList> processorHolder = processorListRegistry.getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, processor));
            SinglePoolElement piece = SinglePoolElement.single(structurePath.toString(), processorHolder).apply(matchTerrain ? StructureTemplatePool.Projection.TERRAIN_MATCHING : StructureTemplatePool.Projection.RIGID);
            for (int i = 0; i < weight; i++) {
                templates.add(piece);
            }
            raw.add(new Pair<>(piece, weight));
        }
    }

    public record PoolAddition(ResourceLocation poolPath) implements IAddition {
        @Override
        public void inject(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, List<Pair<StructurePoolElement, Integer>> raw, ObjectArrayList<StructurePoolElement> templates) {
            var sourcePool = templatePoolRegistry.get(poolPath);
            if (sourcePool == null) return;
            templates.addAll(sourcePool.templates);
            raw.addAll(sourcePool.rawTemplates);
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
        Registry<StructureTemplatePool> templatePoolRegistry = server.registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = server.registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

        for (var builder : BUILDERS) {
            for (var targetPool : builder.targets) {
                StructureTemplatePool pool = templatePoolRegistry.get(targetPool);
                if (pool == null) continue;
                if (builder.clearPoolFirst) {
                    pool.templates.clear();
                    pool.rawTemplates = new ArrayList<>();
                }

                var raw = new ArrayList<>(pool.rawTemplates);
                for (var addition : builder.additions) {
                    addition.inject(templatePoolRegistry, processorListRegistry, raw, pool.templates);
                }
                pool.rawTemplates = raw;
            }
        }
    }
}
