package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.worldgen.structure.FixedRotationStructure;
import dev.tauri.jsg.core.common.worldgen.structure.JigsawExtraStructure;
import dev.tauri.jsg.core.common.worldgen.structure.VoidDimensionStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;
import dev.tauri.jsg.core.common.registry.RegistryObject;

public class CoreStructureTypes {
    private static final JSGDeferredRegister<StructureType<?>> REGISTER = JSGCore.REGISTRY_HELPER.structureType();

    public static final RegistryObject<StructureType<FixedRotationStructure>> FIXED_ROTATION_STRUCTURE = REGISTER.register("fixed_rotation_structure", () -> () -> FixedRotationStructure.CODEC);
    public static final RegistryObject<StructureType<JigsawExtraStructure>> JIGSAW_EXTRA = REGISTER.register("jigsaw_extra", () -> () -> JigsawExtraStructure.CODEC);
    public static final RegistryObject<StructureType<VoidDimensionStructure>> VOID_DIMENSION_STRUCTURE = REGISTER.register("void_structure", () -> () -> VoidDimensionStructure.CODEC);

    public static void init() {
    }
}
