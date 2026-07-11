package dev.tauri.jsg.core.common.loader;

import net.neoforged.fml.ModList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FolderLoader {

    /**
     * Lists all files under {@code path} (e.g. {@code assets/jsg/textures/tesr}) inside the mod
     * file that owns {@code modId}. Walks the mod file's resource tree, which resolves both
     * production jars and the dev-time union of classes/resources directories (the 1.20.1
     * code-source guessing broke on ModDevGradle, where those are separate folders).
     */
    public static List<String> getAllFiles(Class<?> clazz, String modId, String path, String... suffixes) throws IOException {
        List<String> out = new ArrayList<>();
        var container = ModList.get().getModContainerById(modId).orElse(null);
        if (container == null) return out;
        Path root = container.getModInfo().getOwningFile().getFile().findResource(path);
        if (!Files.exists(root)) return out;
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(f -> endsWith(f.getFileName().toString(), suffixes))
                    .forEach(f -> out.add(path + "/" + root.relativize(f).toString().replace('\\', '/')));
        }
        return out;
    }

    private static boolean endsWith(String in, String... suffixes) {
        for (String suffix : suffixes) {
            if (in.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
