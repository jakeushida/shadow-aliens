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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(filepath).read()))) {
            properties.clear();
            properties.load(reader);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load configuration file: " + filepath, exception);
        }
    }

    public void loadDifficulty(String level) {
        load(level.toLowerCase() + ".properties");
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

    public String getString(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value;
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }
}
