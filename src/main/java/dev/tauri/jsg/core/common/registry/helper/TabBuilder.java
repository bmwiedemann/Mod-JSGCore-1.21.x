package dev.tauri.jsg.core.common.registry.helper;

import dev.tauri.jsg.core.common.entity.tab.CoreCreativeTab;
import dev.tauri.jsg.core.common.item.IMultiItem;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.ForgeRegistry;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TabBuilder {
    public static TabBuilder create(ResourceLocation id) {
        return new TabBuilder(id);
    }

    protected final ResourceLocation id;
    protected CoreCreativeTab.Builder builder;
    protected Supplier<Collection<? extends ItemLike>> iconsListSupplier;
    protected List<Supplier<? extends ItemLike>> additionalItems = new ArrayList<>();

    protected TabBuilder(ResourceLocation id) {
        this.id = id;
        this.builder = new CoreCreativeTab.Builder();
    }

    public TabBuilder withIcons(Supplier<Collection<? extends ItemLike>> iconSupplier) {
        iconsListSupplier = iconSupplier;
        return this;
    }

    public TabBuilder withIcons(Collection<Supplier<RegistryObject<? extends ItemLike>>> iconSupplier) {
        builder.withIcons(iconSupplier.stream().map((i) -> ((Supplier<ItemStack>) () -> new ItemStack(i.get().get(), 1))).toList());
        return this;
    }

    public TabBuilder withIcon(Supplier<RegistryObject<? extends ItemLike>> iconSupplier) {
        return this.withItemStackIcon(() -> new ItemStack(iconSupplier.get().get(), 1));
    }

    public TabBuilder withItemStackIcon(Supplier<ItemStack> iconSupplier) {
        builder.icon(iconSupplier);
        return this;
    }

    public TabBuilder withItem(Supplier<? extends ItemLike> item) {
        additionalItems.add(item);
        return this;
    }

    public TabBuilder withItems(List<Supplier<? extends ItemLike>> items) {
        additionalItems.addAll(items);
        return this;
    }

    public TabBuilder searchable() {
        builder.withSearchBar();
        return this;
    }

    @SuppressWarnings("all")
    public Supplier<CoreCreativeTab> build() {
        builder.title(Component.translatable("itemGroup." + id.getNamespace() + "." + id.getPath()));
        builder.displayItems((parameters, output) -> {
            List<Map.Entry<ResourceKey<Item>, Item>> list = new ArrayList<>();
            for (var entry : ForgeRegistries.ITEMS.getEntries()) {
                var item = entry.getValue();
                if (!(item instanceof ITabbedItem tabbedItem)) continue;
                var tabs = tabbedItem.getTabs();
                if (tabs == null || tabs.isEmpty()) continue;
                if (tabs.stream().map(RegistryObject::getId).noneMatch(id::equals))
                    continue;
                list.add(entry);
            }
            list = list.stream().sorted((l, r) -> {
                if (ForgeRegistries.ITEMS instanceof ForgeRegistry<?> registry) {
                    return Integer.compare(registry.getID(l.getKey().location()), registry.getID(r.getKey().location()));
                }
                return l.getKey().location().compareNamespaced(r.getKey().location());
            }).toList();
            for (var item : list) {
                output.accept(new ItemStack(item.getValue()));
                if (item.getValue() instanceof IMultiItem multiItem) {
                    multiItem.addAdditional(output);
                }
            }
        });
        return () -> {
            if (iconsListSupplier != null)
                builder.withIcons(iconsListSupplier.get().stream().map((i) -> (Supplier<ItemStack>) () -> new ItemStack(i)).toList());
            return builder.build();
        };
    }
}
