package com.floweytf.monumentapaper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.vectrix.ignite.Ignite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.jar.Manifest;

public class Monumenta {
    public static final String IDENTIFIER = "Monumenta";
    public static final String MOD_ID = "monumenta";
    public static final Logger LOGGER = LogManager.getLogger(IDENTIFIER);
    public static final String VER_HASH;
    public static final String VER_BRANCH;
    public static final String VER_VERSION;

    // we create this dummy file object that never exists
    // this tricks MC into not reading from file
    public static final File FAKE_FILE = new File("") {
        @Override
        public boolean exists() {
            return false;
        }
    };
    private static final Map<ClassLoader, Manifest> MANIFESTS = Collections.synchronizedMap(new WeakHashMap<>());
    // State management
    public static ThreadLocal<Function<? super Double, Double>> IFRAME_FUNC = new ThreadLocal<>();
    public static ThreadLocal<Double> IFRAME_VALUE = new ThreadLocal<>();

    static {
        var hash = "Unknown";
        var branch = "Unknown";
        var version = "Unknown";

        try {
            var mod = Ignite.mods().container(MOD_ID);
            if (mod.isPresent()) {
                version = mod.get().version();
            }

            var manifest = manifest(Monumenta.class);
            if (manifest != null) {
                hash = manifest.getMainAttributes().getValue("Git-Hash");
                branch = manifest.getMainAttributes().getValue("Git-Branch");
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to obtain version information: ", e);
        }

        VER_HASH = hash;
        VER_BRANCH = branch;
        VER_VERSION = version;
    }

    public static @Nullable Manifest manifest(final @NotNull Class<?> clazz) {
        return MANIFESTS.computeIfAbsent(clazz.getClassLoader(), classLoader -> {
            final String classLocation = "/" + clazz.getName().replace(".", "/") + ".class";
            final URL resource = clazz.getResource(classLocation);

            if (resource == null) {
                return null;
            }

            final String classFilePath = resource.toString().replace("\\", "/");
            final String archivePath = classFilePath.substring(0, classFilePath.length() - classLocation.length());

            try (final InputStream stream = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
                return new Manifest(stream);
            } catch (final IOException ex) {
                return null;
            }
        });
    }

    public static String getIdentifier() {
        return String.format(
            "MonumentaPaper (%s) v%s (%s-%s)", MOD_ID, VER_VERSION, VER_BRANCH, VER_HASH.substring(0, 7)
        );
    }
}