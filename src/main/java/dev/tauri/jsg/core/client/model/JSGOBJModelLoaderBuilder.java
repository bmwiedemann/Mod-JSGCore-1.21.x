package dev.tauri.jsg.core.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public class JSGOBJModelLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    public static final ItemDisplayContext[] DEFAULT_RENDER_TYPES = new ItemDisplayContext[]{
            ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
            ItemDisplayContext.HEAD,
            ItemDisplayContext.GROUND,
            ItemDisplayContext.FIXED,
            ItemDisplayContext.GUI
    };

    protected List<ItemDisplayContext> renderTypes = new ArrayList<>();

    public JSGOBJModelLoaderBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(JSGMapping.rl(JSGCore.MOD_ID, "handheld_item_model"), parent, existingFileHelper, false);
    }

    public JSGOBJModelLoaderBuilder<T> renderTypes(ItemDisplayContext... renderTypes) {
        this.renderTypes.addAll(List.of(renderTypes));
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        var renderTypesJSON = new JsonArray();
        renderTypes.forEach(rt -> renderTypesJSON.add(rt.name()));
        json.add("custom_render_types", renderTypesJSON);
        return json;
    }
}
