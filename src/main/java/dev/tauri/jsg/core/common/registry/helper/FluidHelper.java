package dev.tauri.jsg.core.common.registry.helper;

import dev.tauri.jsg.core.common.block.cauldron.JSGFluidCauldron;
import dev.tauri.jsg.core.common.item.JSGBucketItem;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidHelper {
    private static final Map<ResourceLocation, MoltenFluid> MOLTEN_FLUIDS = new HashMap<>();

    public static Map<ResourceLocation, MoltenFluid> getMoltenFluids() {
        return new HashMap<>(MOLTEN_FLUIDS);
    }

    private final Supplier<DeferredRegister<Fluid>> fluidRegister;
    private final Supplier<DeferredRegister<FluidType>> fluidTypeRegister;
    private final Supplier<DeferredRegister<Item>> itemRegister;
    private final Supplier<DeferredRegister<Block>> blockRegister;

    public FluidHelper(Supplier<DeferredRegister<Fluid>> fluidRegister, Supplier<DeferredRegister<FluidType>> fluidTypeRegister,
                       Supplier<DeferredRegister<Item>> itemRegister, Supplier<DeferredRegister<Block>> blockRegister) {
        this.fluidRegister = fluidRegister;
        this.fluidTypeRegister = fluidTypeRegister;
        this.itemRegister = itemRegister;
        this.blockRegister = blockRegister;
    }


    public MoltenFluid createGenericFluid(ResourceLocation name, int color) {
        RegistryObject<FluidType> ft = fluidTypeRegister.get().register(name.getPath(), () -> new FluidType(FluidType.Properties.create()
                .density(100000)
                .canSwim(false)
                .rarity(Rarity.RARE)
                .supportsBoating(false)
                .viscosity(100000)
                .descriptionId("fluid." + name.getNamespace() + "." + name.getPath())) {
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return JSGMapping.rl(name.getNamespace(), "block/fluid/" + name.getPath() + "_still");
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return JSGMapping.rl(name.getNamespace(), "block/fluid/" + name.getPath() + "_flow");
                    }

                    @Override
                    public @NotNull ResourceLocation getOverlayTexture() {
                        return JSGMapping.rl("misc/underwater");
                    }

                    @Override
                    public int getTintColor() {
                        return color;
                    }
                });
            }
        });
        var fluid = new MoltenFluid(this, ft, name.getPath());
        MOLTEN_FLUIDS.put(name, fluid);
        return fluid;
    }

    public static class MoltenFluid {
        public RegistryObject<FluidType> type;
        public RegistryObject<FlowingFluid> still;
        public RegistryObject<FlowingFluid> flowing;

        public RegistryObject<Item> bucket;

        public RegistryObject<LiquidBlock> block;

        public RegistryObject<JSGFluidCauldron> cauldron;
        public final Map<Item, CauldronInteraction> cauldronInteractionMap = CauldronInteraction.newInteractionMap();

        public ForgeFlowingFluid.Properties properties;
        public String name;


        public MoltenFluid(FluidHelper helper, RegistryObject<FluidType> type, String name) {
            this.type = type;
            this.name = name;


            still = helper.fluidRegister.get().register(name + "_still",
                    () -> new ForgeFlowingFluid.Source(getProps()));
            flowing = helper.fluidRegister.get().register(name + "_flowing",
                    () -> new ForgeFlowingFluid.Flowing(getProps()));

            block = helper.blockRegister.get().register(name,
                    () -> new LiquidBlock(still, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));

            bucket = helper.itemRegister.get().register(name + "_bucket",
                    () -> new JSGBucketItem(still, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

            cauldron = helper.blockRegister.get().register(name + "_cauldron", () -> new JSGFluidCauldron(type, cauldronInteractionMap));

            properties = new ForgeFlowingFluid.Properties(
                    type, still, flowing)
                    .bucket(bucket)
                    .block(block)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2);
        }

        public ForgeFlowingFluid.Properties getProps() {
            return properties;
        }

        public Fluid get() {
            return still.get();
        }
    }


    @SuppressWarnings("deprecation")
    public static boolean isLiquidBlock(BlockState state) {
        return state.liquid();
    }
}
