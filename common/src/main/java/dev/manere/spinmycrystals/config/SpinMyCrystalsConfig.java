package dev.manere.spinmycrystals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpinMyCrystalsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("spinmycrystals");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("spinmycrystals.json");

    private static SpinMyCrystalsConfig instance = new SpinMyCrystalsConfig();

    public boolean enabled = true;

    public float outerGlass = 0.8F;
    public float innerGlass = 0.8F;
    public float cube = 0.8F;

    public float outerGlassSpeed = 0.8F;
    public float innerGlassSpeed = 0.8F;
    public float cubeSpeed = 0.8F;

    public boolean noShade = true;
    public boolean culled = true;

    public float offsetX = 0.02F;
    public float offsetY = -0.02F;
    public float offsetZ = 0.0F;

    @NonNull
    public static SpinMyCrystalsConfig get() {
        return instance;
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
            LOGGER.error("Failed to load SpinMyCrystals config", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(instance, writer);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save SpinMyCrystals config", e);
        }
    }

    public void resetAll() {
        this.enabled = true;
        this.outerGlass = 0.8F;
        this.innerGlass = 0.8F;
        this.cube = 0.8F;
        this.outerGlassSpeed = 0.8F;
        this.innerGlassSpeed = 0.8F;
        this.cubeSpeed = 0.8F;
        this.noShade = true;
        this.culled = true;
        this.offsetX = 0.02F;
        this.offsetY = -0.02F;
        this.offsetZ = 0.0F;
    }

    public boolean isModifiedFromDefaults() {
        return !this.enabled
            || Math.abs(this.outerGlass - 0.8F) > 0.001F
            || Math.abs(this.innerGlass - 0.8F) > 0.001F
            || Math.abs(this.cube - 0.8F) > 0.001F
            || Math.abs(this.outerGlassSpeed - 0.8F) > 0.001F
            || Math.abs(this.innerGlassSpeed - 0.8F) > 0.001F
            || Math.abs(this.cubeSpeed - 0.8F) > 0.001F
            || !this.noShade
            || !this.culled
            || Math.abs(this.offsetX - 0.02F) > 0.001F
            || Math.abs(this.offsetY - (-0.02F)) > 0.001F
            || Math.abs(this.offsetZ - 0.0F) > 0.001F;
    }
}
