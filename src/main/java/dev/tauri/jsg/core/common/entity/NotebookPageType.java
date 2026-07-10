package dev.tauri.jsg.core.common.entity;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.client.entity.NotebookPageRenderable;
import dev.tauri.jsg.core.client.entity.NotebookPageRenderableWrapper;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.registry.tag.CoreBiomeTags;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NotebookPageType<D extends INotebookPageData> {
    public final Supplier<NotebookPageRenderable<D>> renderable;
    public final @Nullable Function<CompoundTag, D> deserializer;
    public final @Nullable Function<D, CompoundTag> serializer;
    public final HoverConsumer<D> hoverConsumer;
    public final DataGenerator<D> randomDataGenerator;
    public final BiFunction<RegistryAccess, ResourceKey<Biome>, Integer> colorGetter;

    public NotebookPageType(Supplier<NotebookPageRenderable<D>> renderable,
                            @Nullable Function<CompoundTag, D> deserializer,
                            @Nullable Function<D, CompoundTag> serializer,
                            HoverConsumer<D> hoverConsumer,
                            DataGenerator<D> randomDataGenerator,
                            BiFunction<RegistryAccess, ResourceKey<Biome>, Integer> colorGetter) {
        this.renderable = renderable;
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.hoverConsumer = hoverConsumer;
        this.randomDataGenerator = randomDataGenerator;
        this.colorGetter = colorGetter;
    }

    public NotebookPageType(Supplier<NotebookPageRenderable<D>> renderable,
                            @Nullable Function<CompoundTag, D> deserializer,
                            @Nullable Function<D, CompoundTag> serializer,
                            TriConsumer<ItemStack, Level, Pair<List<Component>, TooltipFlag>> hoverConsumer,
                            DataGenerator<D> randomDataGenerator,
                            BiFunction<RegistryAccess, ResourceKey<Biome>, Integer> colorGetter) {
        this(renderable, deserializer, serializer, (stack, level, components, flag, data) -> hoverConsumer.accept(stack, level, Pair.of(components, flag)), randomDataGenerator, colorGetter);
    }

    public NotebookPageType(Supplier<NotebookPageRenderable<D>> renderable, Function<CompoundTag, D> deserializer,
                            Function<D, CompoundTag> serializer,
                            DataGenerator<D> randomDataGenerator,
                            TriConsumer<ItemStack, Level, Pair<List<Component>, TooltipFlag>> hoverConsumer) {
        this(renderable, deserializer, serializer, randomDataGenerator, (stack, level, components, flag, data) -> hoverConsumer.accept(stack, level, Pair.of(components, flag)));
    }

    public NotebookPageType(Supplier<NotebookPageRenderable<D>> renderable, Function<CompoundTag, D> deserializer,
                            Function<D, CompoundTag> serializer,
                            DataGenerator<D> randomDataGenerator,
                            HoverConsumer<D> hoverConsumer) {
        this(renderable, deserializer, serializer, hoverConsumer, randomDataGenerator, (access, biome) -> {
            int color = 0x303000;
            if (access == null) return color;
            var getter = access.asGetterLookup().lookup(Registries.BIOME);
            if (getter.isEmpty()) return color;
            var holderOpt = getter.get().get(biome);
            if (holderOpt.isEmpty()) return color;
            var holder = holderOpt.get();

            if (holder.is(CoreBiomeTags.IS_OCEAN) || holder.is(BiomeTags.IS_RIVER)) color = 0x2131A0;
            else if (holder.is(BiomeTags.IS_HILL)) color = 0x736150;
            else if (holder.is(BiomeTags.IS_FOREST)) color = 0x507341;
            else if (holder.is(CoreBiomeTags.HAS_PODZOL)) color = 0x7BA9A9; // taiga
            else if (holder.is(CoreBiomeTags.IS_SWAMP)) color = 0x6B7337;
            else if (holder.is(CoreBiomeTags.IS_NETHER)) color = 0x962A0B;
            else if (holder.is(CoreBiomeTags.IS_COLD)) color = 0x67897A;
            else if (holder.is(CoreBiomeTags.IS_FUNGI)) color = 0x544B4D;
            else if (holder.is(CoreBiomeTags.IS_MOSSY)) color = 0x104004;
            else if (holder.is(BiomeTags.IS_SAVANNA)) color = 0x104004;
            else if (holder.is(CoreBiomeTags.IS_BADLANDS)) color = 0x104004;
            else if (holder.is(CoreBiomeTags.IS_SANDY)) color = 0x9FA251;
            else if (holder.is(CoreBiomeTags.IS_TEMPERATE)) color = 0x48703D;

            return color;
        });
    }

    public Supplier<NotebookPageRenderable<D>> renderable() {
        return renderable;
    }

    public @Nullable Function<CompoundTag, D> deserializer() {
        return deserializer;
    }

    public @Nullable Function<D, CompoundTag> serializer() {
        return serializer;
    }

    public HoverConsumer<D> hoverConsumer() {
        return hoverConsumer;
    }

    public DataGenerator<D> randomDataGenerator() {
        return randomDataGenerator;
    }

    public BiFunction<RegistryAccess, ResourceKey<Biome>, Integer> colorGetter() {
        return colorGetter;
    }

    public interface DataGenerator<DATA extends INotebookPageData> {
        @Nullable
        DATA apply(Level level, BlockPos pos, RandomSource random, CompoundTag tag);
    }

    public interface HoverConsumer<DATA extends INotebookPageData> {
        void accept(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag, @Nullable DATA data);
    }

    public void serializeNBT(CompoundTag compound, D data) {
        if (serializer == null) return;
        var nbt = serializer.apply(data);
        if (nbt == null) return;
        compound.put("data", nbt);
    }

    @Nullable
    public D deserializeNBT(CompoundTag compound) {
        if (deserializer == null || !compound.contains("data")) return null;
        return deserializer.apply(compound.getCompound("data"));
    }

    @Nullable
    public D generateData(Level level, BlockPos pos, RandomSource random, CompoundTag extraData) {
        return randomDataGenerator.apply(level, pos, random, extraData);
    }

    public ItemStack createPage(@Nullable D data, ResourceKey<Biome> biome) {
        return uploadToPage(new ItemStack(CoreItems.NOTEBOOK_PAGE_FILLED.get()), data, biome);
    }

    public CompoundTag createCompoundTag(@Nullable D data, ResourceKey<Biome> biome) {
        var tag = createCompoundTag(data);
        tag.putString("biome", biome.location().toString());
        return tag;
    }

    public CompoundTag createCompoundTag(@Nullable D data) {
        var tag = new CompoundTag();
        tag.putString("pageTypeId", Optional.ofNullable(JSGCoreRegistries.R_NOTEBOOK_PAGE_TYPE.get().getKey(this)).orElseThrow().toString());
        if (serializer == null || data == null) return tag;
        serializeNBT(tag, data);
        return tag;
    }

    public ItemStack uploadToPage(ItemStack page, @Nullable D data, ResourceKey<Biome> biome) {
        ItemNBT.setTag(page, createCompoundTag(data, biome));
        return page;
    }

    public NotebookPageRenderableWrapper<D, ?> renderWrapper(CompoundTag compound) {
        return new NotebookPageRenderableWrapper<>(renderable.get(), deserializeNBT(compound));
    }

    public DataWrapper<D> dataWrapper(CompoundTag compound) {
        return new DataWrapper<>(this, deserializeNBT(compound));
    }

    public record DataWrapper<D extends INotebookPageData>(NotebookPageType<D> type, @Nullable D data) {
        public CompoundTag createCompoundTag(ResourceKey<Biome> biome) {
            return type.createCompoundTag(data, biome);
        }

        public CompoundTag createCompoundTag() {
            return type.createCompoundTag(data);
        }

        public DataWrapper<D> generateData(Level level, BlockPos pos, RandomSource random, CompoundTag extraData) {
            return new DataWrapper<>(type, type.randomDataGenerator.apply(level, pos, random, extraData));
        }

        public ItemStack createPage(ResourceKey<Biome> biome) {
            return uploadToPage(new ItemStack(CoreItems.NOTEBOOK_PAGE_FILLED.get()), biome);
        }

        public ItemStack uploadToPage(ItemStack page, ResourceKey<Biome> biome) {
            ItemNBT.setTag(page, createCompoundTag(biome));
            return page;
        }
    }

    @Nullable
    public static NotebookPageType<?> pageTypeFromCompound(CompoundTag compound) {
        var typeId = compound.getString("pageTypeId");
        if (typeId.isEmpty()) return null;
        return JSGCoreRegistries.R_NOTEBOOK_PAGE_TYPE.get().get(JSGMapping.rl(typeId));
    }

    @Nullable
    public static DataWrapper<?> pageDataFromCompound(CompoundTag compound) {
        var type = pageTypeFromCompound(compound);
        if (type == null) return null;
        return type.dataWrapper(compound);
    }

    public static ResourceKey<Biome> getBiome(CompoundTag compound) {
        if (!compound.contains("biome")) return Biomes.FOREST;
        var biomeLocation = JSGMapping.rl(compound.getString("biome"));
        return ResourceKey.create(Registries.BIOME, biomeLocation);
    }


    /**
     * Fix old NBT of pages generated by JSG -> now we have page type registry, so the NBT is different
     *
     * @param oldTag the current page tag
     * @return new fixed page tag or null if it has been already fixed
     */
    @Nullable
    public static CompoundTag getFixedTag(CompoundTag oldTag) {
        if (!oldTag.contains("addressType")) return null;
        if (oldTag.contains("data")) return null;
        if (oldTag.contains("pageTypeId")) return null;
        var symbolTypeString = oldTag.getString("symbolType");
        SymbolType<?> symbolType = SymbolType.byId(JSGMapping.rl(symbolTypeString));
        if (symbolType == null)
            symbolType = SymbolType.byId(JSGMapping.rl("jsg", symbolTypeString.replace(":", "")));
        if (symbolType == null)
            symbolType = SymbolType.byId(JSGMapping.rl("jsg_rings", symbolTypeString.replace(":", "")));
        if (symbolType == null) return new CompoundTag();
        var type = symbolType.getSymbolUsage().pageTypeSupplier();
        if (type == null) return new CompoundTag();
        var addressTag = oldTag.getCompound("address");
        int[] symbolsToDisplay = oldTag.contains("symbolsToDisplay") ? oldTag.getIntArray("symbolsToDisplay") : new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        var biome = getBiome(oldTag);
        var origin = oldTag.contains("origin") ? oldTag.getCompound("origin") : null;
        var oldName = PageNotebookItemFilled.getNameFromCompoundOptional(oldTag);
        var newTag = new CompoundTag();
        newTag.putString("pageTypeId", Objects.requireNonNull(JSGCoreRegistries.R_NOTEBOOK_PAGE_TYPE.get().getKey(type.get())).toString());
        newTag.putString("biome", biome.location().toString());
        var data = new CompoundTag();
        data.putIntArray("symbolsToDisplay", symbolsToDisplay);
        addressTag.putInt("size", 9);
        data.put("address", addressTag);
        if (origin != null)
            data.put("pointOfOrigin", origin);
        newTag.put("data", data);
        oldName.ifPresent(s -> PageNotebookItemFilled.setName(newTag, s));
        return newTag;
    }
}
