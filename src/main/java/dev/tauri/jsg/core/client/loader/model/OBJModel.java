package dev.tauri.jsg.core.client.loader.model;

import dev.tauri.jsg.core.client.model.AbstractOBJModel;
import dev.tauri.jsg.core.client.model.IOBJModelRenderer;
import dev.tauri.jsg.core.client.renderer.obj.GUIOBJModelRenderer;
import dev.tauri.jsg.core.client.renderer.obj.InGameOBJModelRenderer;
import dev.tauri.jsg.core.client.renderer.obj.LegacyOBJModelRenderer;

import java.util.HashMap;
import java.util.Map;

public class OBJModel extends AbstractOBJModel {
    public final float[] vertices;
    public final float[] textureCoords;
    public final float[] normals;
    public final int[] indices;
    public final boolean isEmpty;

    public OBJModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.isEmpty = (vertices.length == 0);
    }

    protected final Map<EnumOBJRenderMethod, IOBJModelRenderer<?>> renderers = new HashMap<>();

    @Override
    public IOBJModelRenderer<?> renderer(EnumOBJRenderMethod method) {
        if (method == EnumOBJRenderMethod.GUI) {
            var renderer = renderers.get(method);
            if (renderer == null) {
                renderer = new GUIOBJModelRenderer(this);
                renderers.put(method, renderer);
            }
            return renderer;
        }

        if (method == EnumOBJRenderMethod.LEGACY) {
            var renderer = renderers.get(method);
            if (renderer == null) {
                renderer = new LegacyOBJModelRenderer(this);
                renderers.put(method, renderer);
            }
            return renderer;
        }

        var renderer = renderers.get(method);
        if (renderer == null) {
            renderer = new InGameOBJModelRenderer(this);
            renderers.put(method, renderer);
        }
        return renderer;
    }

    @Override
    public boolean isEmpty() {
        return vertices.length < 1;
    }
}
