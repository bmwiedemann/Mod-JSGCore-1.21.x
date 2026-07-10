package dev.tauri.jsg.core.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JSGBlockModel extends BlockModel implements IUnbakedGeometry<JSGBlockModel> {

    @OnlyIn(Dist.CLIENT)
    public static final class Loader implements IGeometryLoader<JSGBlockModel> {
        public static final JSGBlockModel.Loader INSTANCE = new JSGBlockModel.Loader();

        @Override
        public JSGBlockModel read(JsonObject pJson, JsonDeserializationContext pContext) {
            JsonObject jsonobject = pJson.getAsJsonObject();
            List<BlockElement> list = this.getElements(pContext, jsonobject);
            String s = this.getParentName(jsonobject);
            Map<String, Either<Material, String>> map = this.getTextureMap(jsonobject);
            Boolean obool = this.getAmbientOcclusion(jsonobject);
            ItemTransforms itemtransforms = ItemTransforms.NO_TRANSFORMS;
            if (jsonobject.has("display")) {
                JsonObject display = GsonHelper.getAsJsonObject(jsonobject, "display");
                itemtransforms = pContext.deserialize(display, ItemTransforms.class);
            }

            List<ItemOverride> list1 = this.getOverrides(pContext, jsonobject);
            BlockModel.GuiLight blockmodel$guilight = null;
            if (jsonobject.has("gui_light")) {
                blockmodel$guilight = BlockModel.GuiLight.getByName(GsonHelper.getAsString(jsonobject, "gui_light"));
            }

            ResourceLocation resourcelocation = s.isEmpty() ? null : JSGMapping.rl(s);

            List<ItemDisplayContext> customRenderTypes = new ArrayList<>();
            if (jsonobject.has("custom_render_types")) {
                var renderTypes = jsonobject.getAsJsonArray("custom_render_types");
                for (JsonElement rt : renderTypes) {
                    customRenderTypes.add(ItemDisplayContext.valueOf(rt.getAsString()));
                }
            }

            boolean ignoreJsonTransformations = jsonobject.has("override_transformations") && jsonobject.get("override_transformations").getAsBoolean();

            return new JSGBlockModel(customRenderTypes, resourcelocation, list, map, obool, blockmodel$guilight, itemtransforms, list1, ignoreJsonTransformations);
        }

        private List<ItemOverride> getOverrides(JsonDeserializationContext pContext, JsonObject pJson) {
            List<ItemOverride> list = Lists.newArrayList();
            if (pJson.has("overrides")) {
                for (JsonElement jsonelement : GsonHelper.getAsJsonArray(pJson, "overrides")) {
                    list.add(pContext.deserialize(jsonelement, ItemOverride.class));
                }
            }

            return list;
        }

        private Map<String, Either<Material, String>> getTextureMap(JsonObject pJson) {
            ResourceLocation resourcelocation = InventoryMenu.BLOCK_ATLAS;
            Map<String, Either<Material, String>> map = Maps.newHashMap();
            if (pJson.has("textures")) {
                JsonObject jsonobject = GsonHelper.getAsJsonObject(pJson, "textures");

                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    map.put(entry.getKey(), parseTextureLocationOrReference(resourcelocation, entry.getValue().getAsString()));
                }
            }

            return map;
        }

        private static Either<Material, String> parseTextureLocationOrReference(ResourceLocation pLocation, String pName) {
            if (pName.charAt(0) == '#') {
                return Either.right(pName.substring(1));
            } else {
                ResourceLocation resourcelocation = ResourceLocation.tryParse(pName);
                if (resourcelocation == null) {
                    throw new JsonParseException(pName + " is not valid resource location");
                } else {
                    return Either.left(new Material(pLocation, resourcelocation));
                }
            }
        }

        private String getParentName(JsonObject pJson) {
            return GsonHelper.getAsString(pJson, "parent", "");
        }

        @Nullable
        private Boolean getAmbientOcclusion(JsonObject pJson) {
            return pJson.has("ambientocclusion") ? GsonHelper.getAsBoolean(pJson, "ambientocclusion") : null;
        }

        private List<BlockElement> getElements(JsonDeserializationContext pContext, JsonObject pJson) {
            List<BlockElement> list = Lists.newArrayList();
            if (pJson.has("elements")) {
                for (JsonElement jsonelement : GsonHelper.getAsJsonArray(pJson, "elements")) {
                    list.add(pContext.deserialize(jsonelement, BlockElement.class));
                }
            }

            return list;
        }
    }

    public final List<ItemDisplayContext> customRenderTypes;
    public final boolean ignoreJsonTransformations;

    public JSGBlockModel(List<ItemDisplayContext> customRenderTypes, @Nullable ResourceLocation pParentLocation, List<BlockElement> pElements, Map<String, Either<Material, String>> pTextureMap, @Nullable Boolean pHasAmbientOcclusion, @Nullable BlockModel.GuiLight pGuiLight, ItemTransforms pTransforms, List<ItemOverride> pOverrides, boolean ignoreJsonTransformations) {
        super(pParentLocation, pElements, pTextureMap, pHasAmbientOcclusion, pGuiLight, pTransforms, pOverrides);
        this.customRenderTypes = customRenderTypes;
        this.ignoreJsonTransformations = ignoreJsonTransformations;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        return bake(baker, this, spriteGetter, modelState, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    @NotNull
    public BakedModel bake(ModelBaker pBaker, BlockModel pModel, Function<Material, TextureAtlasSprite> pSpriteGetter, ModelState pState, boolean pGuiLight3d) {
        return new dev.tauri.jsg.core.client.model.JSGBakedModelWrapper(super.bake(pBaker, pModel, pSpriteGetter, pState, pGuiLight3d), customRenderTypes, ignoreJsonTransformations);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        resolveParents(modelGetter);
    }
}
