package dev.tauri.jsg.core.common.worldgen.structure;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tauri.jsg.core.common.registry.CoreStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("all")
public class FixedRotationStructure extends Structure {
    public static final int MAX_TOTAL_STRUCTURE_RANGE = 2048;
    public static final MapCodec<FixedRotationStructure> CODEC = RecordCodecBuilder.<FixedRotationStructure>mapCodec((p_227640_) ->
            p_227640_.group(settingsCodec(p_227640_),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((p_227656_) -> p_227656_.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((p_227654_) -> p_227654_.startJigsawName),
                    Codec.intRange(0, 50).fieldOf("size").forGetter((p_227652_) -> p_227652_.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter((p_227649_) -> p_227649_.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter((p_227646_) -> p_227646_.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((p_227644_) -> p_227644_.projectStartToHeightmap),
                    Codec.intRange(1, MAX_TOTAL_STRUCTURE_RANGE).fieldOf("max_distance_from_center").forGetter((p_227642_) -> p_227642_.maxDistanceFromCenter), Codec.STRING.xmap(Rotation::valueOf, Rotation::name).fieldOf("structure_rotation").forGetter((p_227642_) -> p_227642_.structureRotation)).apply(p_227640_, FixedRotationStructure::new)).validate(FixedRotationStructure::verifyRange);
    public final Holder<StructureTemplatePool> startPool;
    public final Optional<ResourceLocation> startJigsawName;
    public final int maxDepth;
    public final HeightProvider startHeight;
    public final boolean useExpansionHack;
    public final Optional<Heightmap.Types> projectStartToHeightmap;
    public final int maxDistanceFromCenter;
    public final Rotation structureRotation;

    public static DataResult<FixedRotationStructure> verifyRange(FixedRotationStructure structure) {
        int i = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX -> 12;
        };
        return structure.maxDistanceFromCenter + i > MAX_TOTAL_STRUCTURE_RANGE ? DataResult.error(() -> ("Structure size including terrain adaptation must not exceed " + MAX_TOTAL_STRUCTURE_RANGE)) : DataResult.success(structure);
    }

    public FixedRotationStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int maxDepth, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter, Rotation structureRotation) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.structureRotation = structureRotation;
    }

    public FixedRotationStructure(Structure.StructureSettings pSettings, Holder<StructureTemplatePool> pStartPool, int pMaxDepth, HeightProvider pStartHeight, boolean pUseExpansionHack, Heightmap.Types pProjectStartToHeightmap) {
        this(pSettings, pStartPool, Optional.empty(), pMaxDepth, pStartHeight, pUseExpansionHack, Optional.of(pProjectStartToHeightmap), 80, Rotation.NONE);
    }

    public FixedRotationStructure(Structure.StructureSettings pSettings, Holder<StructureTemplatePool> pStartPool, int pMaxDepth, HeightProvider pStartHeight, boolean pUseExpansionHack) {
        this(pSettings, pStartPool, Optional.empty(), pMaxDepth, pStartHeight, pUseExpansionHack, Optional.empty(), 80, Rotation.NONE);
    }

    public @NotNull Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        ChunkPos chunkpos = pContext.chunkPos();
        int i = this.startHeight.sample(pContext.random(), new WorldGenerationContext(pContext.chunkGenerator(), pContext.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());
        return JSGJigsawPlacement.addPieces(pContext, this.startPool, this.startJigsawName, this.maxDepth, blockpos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter, this.structureRotation);
    }

    @NotNull
    public StructureType<FixedRotationStructure> type() {
        return CoreStructureTypes.FIXED_ROTATION_STRUCTURE.get();
    }
}
