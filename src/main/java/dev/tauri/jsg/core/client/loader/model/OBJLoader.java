package dev.tauri.jsg.core.client.loader.model;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.util.vectors.Vector2f;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class OBJLoader {

    public static OBJModel loadModel(String modelPath, Class<?> clazz) {
        return loadModel(Objects.requireNonNull(clazz.getClassLoader().getResourceAsStream(modelPath)));
    }

    public static OBJModel loadModel(InputStream stream) {
        BufferedReader reader;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        Map<Vertex, Integer> vertexIndexMap = new LinkedHashMap<>();

        int current = 0;

        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("v ")) vertices.add(splitToVector(line));

                else if (line.startsWith("vt ")) textures.add(splitToVector2(line));

                else if (line.startsWith("vn ")) normals.add(splitToVector(line));

                else if (line.startsWith("f ")) {
                    String[] face = line.split(" ");

                    for (String verticle : face) {
                        if (verticle.equals("f")) continue;

                        String[] vtn = verticle.split("/");
                        Vertex vertex = new Vertex(vtn);

                        Integer index = vertexIndexMap.get(vertex);

                        if (index != null) {
                            // Index found -> duplicate -> add index to list
                            indices.add(index);
                        } else {
                            // No index found -> new one -> add to Map with <current> index
                            vertexIndexMap.put(vertex, current);
                            indices.add(current);

                            current++;
                        }
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            JSGCore.logger.error("Error while loading model", e);
        }

        // ----------------------------------------------------------------------------------

        boolean hasTex = !textures.isEmpty();

        int size = vertexIndexMap.size();
        int index = 0;

        float[] v = new float[size * 3];
        float[] t = new float[size * 2];
        float[] n = new float[size * 3];

        for (Vertex key : vertexIndexMap.keySet()) {

            Vector3f ver = vertices.get(key.vId);
            Vector3f norm = normals.get(key.nId);

            v[index * 3] = ver.getX();
            v[index * 3 + 1] = ver.getY();
            v[index * 3 + 2] = ver.getZ();

            n[index * 3] = norm.getX();
            n[index * 3 + 1] = norm.getY();
            n[index * 3 + 2] = norm.getZ();

            if (hasTex) {
                Vector2f tex = textures.get(key.tId);

                t[index * 2] = tex.x;
                t[index * 2 + 1] = -tex.y;
            }

            index++;
        }

        int[] ind = new int[indices.size()];

        for (int k = 0; k < indices.size(); k++) {
            ind[k] = indices.get(k);
        }

        return new OBJModel(v, t, n, ind);
    }

    private static class Vertex {
        int vId;
        int tId;
        int nId;

        public Vertex(String[] vtn) {
            if (vtn.length == 3) {
                vId = Integer.parseInt(vtn[0]) - 1;

                if (!vtn[1].isEmpty()) tId = Integer.parseInt(vtn[1]) - 1;

                if (!vtn[2].isEmpty()) nId = Integer.parseInt(vtn[2]) - 1;
            }
        }

        public String toString() {
            return vId + "/" + tId + "/" + nId;
        }
    }

    private static Vector3f splitToVector(String line) {
        String[] splitted = line.split(" ");

        float x = Float.parseFloat(splitted[1]);
        float y = Float.parseFloat(splitted[2]);
        float z = Float.parseFloat(splitted[3]);

        return new Vector3f(x, y, z);
    }

    private static Vector2f splitToVector2(String line) {
        String[] splitted = line.split(" ");

        float x = Float.parseFloat(splitted[1]);
        float y = Float.parseFloat(splitted[2]);

        return new Vector2f(x, y);
    }
}
