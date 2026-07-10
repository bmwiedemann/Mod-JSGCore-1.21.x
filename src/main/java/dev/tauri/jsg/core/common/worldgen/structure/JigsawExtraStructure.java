package dev.tauri.jsg.core.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tauri.jsg.core.common.registry.CoreStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
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
public class JigsawExtraStructure extends Structure {
    public static final int MAX_TOTAL_STRUCTURE_RANGE = 2048;
    public static final Codec<JigsawExtraStructure> CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec((p_227640_) ->
            p_227640_.group(settingsCodec(p_227640_),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((p_227656_) -> p_227656_.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((p_227654_) -> p_227654_.startJigsawName),
                    Codec.intRange(0, 50).fieldOf("size").forGetter((p_227652_) -> p_227652_.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter((p_227649_) -> p_227649_.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter((p_227646_) -> p_227646_.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((p_227644_) -> p_227644_.projectStartToHeightmap),
                    Codec.intRange(1, MAX_TOTAL_STRUCTURE_RANGE).fieldOf("max_distance_from_center").forGetter((p_227642_) -> p_227642_.maxDistanceFromCenter)).apply(p_227640_, JigsawExtraStructure::new)), JigsawExtraStructure::verifyRange).codec();
    public final Holder<StructureTemplatePool> startPool;
    public final Optional<ResourceLocation> startJigsawName;
    public final int maxDepth;
    public final HeightProvider startHeight;
    public final boolean useExpansionHack;
    public final Optional<Heightmap.Types> projectStartToHeightmap;
    public final int maxDistanceFromCenter;

    public static DataResult<JigsawExtraStructure> verifyRange(JigsawExtraStructure structure) {
        int i = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX -> 12;
        };
        return structure.maxDistanceFromCenter + i > MAX_TOTAL_STRUCTURE_RANGE ? DataResult.error(() -> ("Structure size including terrain adaptation must not exceed " + MAX_TOTAL_STRUCTURE_RANGE)) : DataResult.success(structure);
    }

    public JigsawExtraStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int maxDepth, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    public JigsawExtraStructure(Structure.StructureSettings pSettings, Holder<StructureTemplatePool> pStartPool, int pMaxDepth, HeightProvider pStartHeight, boolean pUseExpansionHack, Heightmap.Types pProjectStartToHeightmap) {
        this(pSettings, pStartPool, Optional.empty(), pMaxDepth, pStartHeight, pUseExpansionHack, Optional.of(pProjectStartToHeightmap), 80);
    }

    public JigsawExtraStructure(Structure.StructureSettings pSettings, Holder<StructureTemplatePool> pStartPool, int pMaxDepth, HeightProvider pStartHeight, boolean pUseExpansionHack) {
        this(pSettings, pStartPool, Optional.empty(), pMaxDepth, pStartHeight, pUseExpansionHack, Optional.empty(), 80);
    }

    public @NotNull Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        ChunkPos chunkpos = pContext.chunkPos();
        int i = this.startHeight.sample(pContext.random(), new WorldGenerationContext(pContext.chunkGenerator(), pContext.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());
        return JigsawPlacement.addPieces(pContext, this.startPool, this.startJigsawName, this.maxDepth, blockpos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    @NotNull
    public StructureType<JigsawExtraStructure> type() {
        return CoreStructureTypes.JIGSAW_EXTRA.get();
    }
}
