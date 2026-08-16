package io.github.jakeushida.shadowaliens.managers;

import com.badlogic.gdx.Gdx;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public final class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Properties properties = new Properties();

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public void load(String filepath) {
        load(filepath, true);
    }

    private void load(String filepath, boolean clearExisting) {
        // Primary attempt: use libGDX file handle (works in runtime)
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(filepath).read()))) {
                if (clearExisting) {
                    properties.clear();
                }
                properties.load(reader);
                return;
            }
        } catch (RuntimeException | IOException ignored) {
            // Fall through to the fallback below
        }

        // Fallback for unit tests and environments where Gdx.files isn't configured to point to
        // the project assets directory: attempt to locate an "assets" folder upwards from the
        // current working directory and read <assets>/<filepath>.
        java.io.File dir = new java.io.File(System.getProperty("user.dir"));
        while (dir != null) {
            java.io.File candidate = new java.io.File(dir, "assets" + java.io.File.separator + filepath);
            if (candidate.exists() && candidate.isFile()) {
                try (BufferedReader reader = new BufferedReader(new java.io.FileReader(candidate))) {
                    if (clearExisting) {
                        properties.clear();
                    }
                    properties.load(reader);
                    return;
                } catch (IOException exception) {
                    throw new IllegalStateException("Failed to load configuration file from fallback: " + candidate.getPath(), exception);
                }
            }
            dir = dir.getParentFile();
        }

        throw new IllegalStateException("Failed to load configuration file: " + filepath + " (looked in libGDX internal and project assets folders)");
    }

    /**
     * Reloads the global defaults and layers the chosen difficulty on top.
     *
     * <p>The global file is reloaded first so that switching difficulty cannot
     * leave stale keys behind. Previously only the difficulty file was layered
     * on, so going from hard to easy kept hard's extra wave rows (for example
     * {@code wave.3.enemy.3.*}) and spawned enemies the easy wave never defined.
     */
    public void loadDifficulty(String level) {
        load("global.properties", true);
        load(level.toLowerCase() + ".properties", false);
    }

    /**
     * Helper to load properties from an arbitrary InputStream. Useful for unit tests.
     */
    public void loadFromStream(java.io.InputStream in) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(in))) {
            properties.clear();
            properties.load(reader);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Failed to load configuration from stream", exception);
        }
    }

    /**
     * Clears all currently loaded properties. Useful for unit tests to isolate state.
     */
    public void reset() {
        properties.clear();
    }

    public String getString(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value;
    }

    public int getInt(String key) {
        // Handle both integer and float formats (e.g., "400" or "400.0")
        return (int) getFloat(key);
    }

    public float getFloat(String key) {
        return Float.parseFloat(getString(key).trim());
    }

    /** True when {@code key} is present, so callers can probe optional keys without catching. */
    public boolean has(String key) {
        return properties.getProperty(key) != null;
    }

    /** Returns {@code key} as an int, or {@code fallback} when the key is absent. */
    public int getInt(String key, int fallback) {
        return has(key) ? getInt(key) : fallback;
    }
}
