package dev.tauri.jsg.core.common.item.notebook;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.client.renderer.item.PageNotebookBEWLR;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.item.JSGItem;
import dev.tauri.jsg.core.common.util.I18n;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PageNotebookItemFilled extends JSGItem {

    public PageNotebookItemFilled() {
        super(new Item.Properties().rarity(Rarity.COMMON));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private static final PageNotebookBEWLR instance = new PageNotebookBEWLR();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return instance;
            }
        });
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide) return;
        var compound = NotebookPageType.getFixedTag(ItemNBT.getOrCreateTag(pStack));
        if (compound == null) return;
        ItemNBT.setTag(pStack, compound);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        if (ItemNBT.hasTag(stack)) {
            Optional.ofNullable(NotebookPageType.pageTypeFromCompound(ItemNBT.getOrCreateTag(stack)))
                    .map((t) -> t.dataWrapper(ItemNBT.getOrCreateTag(stack)))
                    .ifPresent(dw -> dw.type().hoverConsumer().accept(stack, context, components, tooltipFlag, dw.data()));
        }
    }


    /**
     * Returns color from the Biome
     *
     * @return color
     */
    public static int getColorForBiome(@NotNull ItemStack stack, @Nullable RegistryAccess level, @NotNull ResourceKey<Biome> biome) {
        if (ItemNBT.hasTag(stack)) {
            return Optional.ofNullable(NotebookPageType.pageTypeFromCompound(ItemNBT.getOrCreateTag(stack)))
                    .map(NotebookPageType::colorGetter)
                    .map(colorGetter -> colorGetter.apply(level, biome)).orElse(0x303000);
        }
        return 0x303000;
    }

    public static ResourceKey<Biome> getBiomeKeyFromWorld(@Nullable Level world, BlockPos pos) {
        if (world == null) return Biomes.FOREST;
        return world.getBiome(pos).unwrapKey().orElse(Biomes.FOREST);
    }

    private static final String UNNAMED = "item.jsg.notebook.unnamed";

    public static String getUnnamedLocalized() {
        return I18n.format(UNNAMED);
    }

    public static void setName(CompoundTag page, String name) {
        CompoundTag display = new CompoundTag();
        display.putString("Name", Component.Serializer.toJson(Component.literal(name)));
        page.put("display", display);
    }

    public static String getNameFromCompound(@Nullable CompoundTag compound) {
        return getNameFromCompoundOptional(compound).orElseGet(PageNotebookItemFilled::getUnnamedLocalized);
    }

    public static Optional<String> getNameFromCompoundOptional(@Nullable CompoundTag compound) {
        if (compound != null && compound.contains("display")) {
            CompoundTag display = compound.getCompound("display");
            if (display.contains("Name")) {
                return Optional.of(Objects.requireNonNull(Component.Serializer.fromJson(display.getString("Name"))).getString());
            }
        }

        return Optional.empty();
    }
}
