package dev.tauri.jsg.core.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tauri.jsg.core.common.registry.CoreStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("all")
public class VoidDimensionStructure extends Structure {

    public static final MapCodec<VoidDimensionStructure> CODEC = RecordCodecBuilder.<VoidDimensionStructure>mapCodec(instance ->
            instance.group(VoidDimensionStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightMap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, VoidDimensionStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightMap;
    private final int maxDistanceFromCenter;

    public VoidDimensionStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool,
                                  Optional<ResourceLocation> startJigsawName, int size, HeightProvider startHeight,
                                  Optional<Heightmap.Types> projectStartToHeightMap, int maxDistanceFromCenter) {

        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightMap = projectStartToHeightMap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    public @NotNull Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {

        ChunkPos chunkPos = context.chunkPos();
        int landHeight = context.chunkGenerator().getFirstOccupiedHeight(
                chunkPos.getMinBlockX(),
                chunkPos.getMinBlockZ(),
                Heightmap.Types.WORLD_SURFACE,
                context.heightAccessor(),
                context.randomState());

        NoiseColumn column = context.chunkGenerator().getBaseColumn(chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), context.heightAccessor(), context.randomState());

        if (column.getBlock(landHeight).isAir()) {
            return Optional.empty();
        }

        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        return JigsawPlacement.addPieces(
                context, this.startPool,
                this.startJigsawName,
                this.size,
                blockPos,
                false,
                this.projectStartToHeightMap,
                this.maxDistanceFromCenter);
    }

    @NotNull
    public StructureType<?> type() {
        return CoreStructureTypes.VOID_DIMENSION_STRUCTURE.get();
    }
}
