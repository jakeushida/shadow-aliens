package io.github.jakeushida.shadowaliens.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;

public class ConfigManagerFileTest {

    @BeforeEach
    public void setUp() {
        // Initialize headless backend so Gdx.files is available in tests
        Gdx.files = new HeadlessFiles();
        ConfigManager.getInstance().reset();
    }

    @Test
    public void testLoadExistingFile() {
        ConfigManager.getInstance().load("medium.properties");
        // medium.properties contains player.initialLives = 3
        assertEquals("3", ConfigManager.getInstance().getString("player.initialLives"));
        assertEquals(3, ConfigManager.getInstance().getInt("player.initialLives"));
    }

    @Test
    public void testLoadNonExistentFileThrows() {
        assertThrows(IllegalStateException.class, () -> ConfigManager.getInstance().load("nonexistent.properties"));
    }

    @Test
    public void testLoadDifficultyCaseInsensitive() {
        ConfigManager.getInstance().loadDifficulty("EASY");
        // easy.properties should exist in assets
        assertNotNull(ConfigManager.getInstance().getString("player.initialLives"));
    }

    @Test
    public void testReloadClearsPreviousProperties() {
        ConfigManager.getInstance().load("easy.properties");
        String easyLives = ConfigManager.getInstance().getString("player.initialLives");
        ConfigManager.getInstance().load("hard.properties");
        String hardLives = ConfigManager.getInstance().getString("player.initialLives");
        assertNotEquals(easyLives, hardLives);
    }
}
