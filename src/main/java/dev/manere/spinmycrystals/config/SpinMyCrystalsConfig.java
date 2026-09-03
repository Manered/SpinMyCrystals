package dev.manere.spinmycrystals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpinMyCrystalsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("spinmycrystals.json");
    private static SpinMyCrystalsConfig instance = new SpinMyCrystalsConfig();

    public boolean enabled = true;
    public float outerGlass = 0.8f;
    public float innerGlass = 0.8f;
    public float cube = 0.8f;

    public float outerGlassSpeed = 0.8f;
    public float innerGlassSpeed = 0.8f;
    public float cubeSpeed = 0.8f;

    public float offsetX = 0.02f;
    public float offsetY = -0.02f;
    public float offsetZ = 0.0f;

    public boolean noShade = true;
    public boolean culled = true;

    public static SpinMyCrystalsConfig get() {
        return instance;
    }

    public void resetAll() {
        enabled = true;
        outerGlass = 0.8f;
        innerGlass = 0.8f;
        cube = 0.8f;
        outerGlassSpeed = 0.8f;
        innerGlassSpeed = 0.8f;
        cubeSpeed = 0.8f;
        offsetX = 0.02f;
        offsetY = -0.02f;
        offsetZ = 0.0f;
        noShade = true;
        culled = true;
    }

    public boolean isModifiedFromDefaults() {
        return !enabled
            || Math.abs(outerGlass - 0.8f) > 0.001f
            || Math.abs(innerGlass - 0.8f) > 0.001f
            || Math.abs(cube - 0.8f) > 0.001f
            || Math.abs(outerGlassSpeed - 0.8f) > 0.001f
            || Math.abs(innerGlassSpeed - 0.8f) > 0.001f
            || Math.abs(cubeSpeed - 0.8f) > 0.001f
            || Math.abs(offsetX - 0.02f) > 0.001f
            || Math.abs(offsetY - (-0.02f)) > 0.001f
            || Math.abs(offsetZ - 0.0f) > 0.001f
            || !noShade
            || !culled;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            SpinMyCrystalsConfig loaded = GSON.fromJson(reader, SpinMyCrystalsConfig.class);
            if (loaded != null) {
                instance = loaded;
            }
        } catch (Exception e) {
            // woof
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(instance, writer);
            }
        } catch (Exception e) {
            // who cares.
        }
    }
}
