package dev.tauri.jsg.core.common.entity;

import dev.tauri.jsg.core.client.entity.AddressPageRenderable;
import dev.tauri.jsg.core.client.entity.NotebookPageRenderable;
import dev.tauri.jsg.core.common.util.I18n;
import dev.tauri.jsg.core.common.util.TooltipUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AddressNotebookPageType<DATA extends IAddressNotebookPageData> extends NotebookPageType<DATA> {
    public AddressNotebookPageType(Function<CompoundTag, DATA> deserializer, Function<DATA, CompoundTag> serializer, DataGenerator<DATA> randomDataGenerator) {
        this(AddressPageRenderable::new, deserializer, serializer, randomDataGenerator,
                (stack, level, components, flag, data) -> {
                    var displayIds = TooltipUtils.showAdvancedTooltip(flag);
                    String text = I18n.format("item.jsg_core.page_notebook_filled.hold_shift");
                    text = text.replace("%key%", TooltipUtils.getShiftKeyName());
                    components.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
                    try {
                        if (data == null) return;
                        var stargateAddress = data.getAddress();
                        int[] symbolsToDisplay = data.getSymbolsToDisplay();

                        Map<Integer, Boolean> hashedSymbols = new HashMap<>();
                        for (int symbolId : symbolsToDisplay) {
                            hashedSymbols.put(symbolId, true);
                        }

                        for (int i = 0; i < data.getAddress().getSize(); i++) {
                            if (hashedSymbols.get(i + 1) == null || !hashedSymbols.get(i + 1)) continue;
                            components.add(Component.literal(ChatFormatting.ITALIC + "" + (i < 6 ? ChatFormatting.AQUA : ChatFormatting.DARK_PURPLE) + stargateAddress.get(i).getEnglishName(data.getOrigin()) + (displayIds ? (ChatFormatting.GRAY + " (" + stargateAddress.get(i).getId() + ")") : "")));
                        }
                    } catch (Exception ignored) {
                    }
                }
        );
    }

    public AddressNotebookPageType(Supplier<NotebookPageRenderable<DATA>> renderable, @Nullable Function<CompoundTag, DATA> deserializer, @Nullable Function<DATA, CompoundTag> serializer, HoverConsumer<DATA> hoverConsumer, DataGenerator<DATA> randomDataGenerator, BiFunction<RegistryAccess, ResourceKey<Biome>, Integer> colorGetter) {
        super(renderable, deserializer, serializer, hoverConsumer, randomDataGenerator, colorGetter);
    }

    public AddressNotebookPageType(Supplier<NotebookPageRenderable<DATA>> renderable, @Nullable Function<CompoundTag, DATA> deserializer, @Nullable Function<DATA, CompoundTag> serializer, TriConsumer<ItemStack, net.minecraft.world.item.Item.TooltipContext, Pair<List<Component>, TooltipFlag>> hoverConsumer, DataGenerator<DATA> randomDataGenerator, BiFunction<RegistryAccess, ResourceKey<Biome>, Integer> colorGetter) {
        super(renderable, deserializer, serializer, hoverConsumer, randomDataGenerator, colorGetter);
    }

    public AddressNotebookPageType(Supplier<NotebookPageRenderable<DATA>> renderable, Function<CompoundTag, DATA> deserializer, Function<DATA, CompoundTag> serializer, DataGenerator<DATA> randomDataGenerator, TriConsumer<ItemStack, net.minecraft.world.item.Item.TooltipContext, Pair<List<Component>, TooltipFlag>> hoverConsumer) {
        super(renderable, deserializer, serializer, randomDataGenerator, hoverConsumer);
    }

    public AddressNotebookPageType(Supplier<NotebookPageRenderable<DATA>> renderable, Function<CompoundTag, DATA> deserializer, Function<DATA, CompoundTag> serializer, DataGenerator<DATA> randomDataGenerator, HoverConsumer<DATA> hoverConsumer) {
        super(renderable, deserializer, serializer, randomDataGenerator, hoverConsumer);
    }
}
