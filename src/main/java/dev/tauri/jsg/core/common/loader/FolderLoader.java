package dev.tauri.jsg.core.common.loader;

import dev.tauri.jsg.core.JSGCore;
import net.minecraftforge.fml.ModList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FolderLoader {

    public static List<String> getAllFiles(Class<?> clazz, String modId, String path, String... suffixes) throws IOException {
        List<String> out = new ArrayList<>();
        AtomicReference<File> jarFile = new AtomicReference<>();
        ModList.get().getModContainerById(modId).ifPresentOrElse(container -> jarFile.set(container.getModInfo().getOwningFile().getFile().getFilePath().toFile()), () -> {
        });
        if (jarFile.get() == null) return out;
        String classPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();

        int separatorIndex = classPath.lastIndexOf("!");

        // Separator found, we're inside a JAR file.
        if (separatorIndex != -1 && classPath.lastIndexOf(".jar") != -1) {

            JarFile jar = new JarFile(jarFile.get());
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();

                if (name.startsWith(path) && endsWith(name, suffixes)) {
                    out.add(name);
                }
            }

            jar.close();
        }

        // No separator, it's a debug environment.
        else {
            JSGCore.logger.info("We are in dev environment... Opening normal folder...");
            getAllFilesDev(jarFile, out, path, suffixes);
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

    private static void getAllFilesDev(AtomicReference<File> jarFile, List<String> out, String path, String... suffixes) throws IOException {
        var resDir = new File(jarFile.get().getParentFile(), "main/");
        var resDirScan = new File(resDir, path + "/");
        try (var stream = Files.walk(resDirScan.toPath())) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(f -> endsWith(f.toFile().getName(), suffixes))
                    .map(f -> resDir.toPath().relativize(f))
                    .forEach(f -> out.add(f.toString().replace('\\', '/')));
        }
    }
}
