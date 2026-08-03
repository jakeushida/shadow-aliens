package io.github.jakeushida.shadowaliens.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigManagerTest {

    @BeforeEach
    public void setUp() {
        // Because ConfigManager is a Singleton, it remembers data across different tests.
        // We must wipe it clean before every single test runs so they don't interfere with each other.
        ConfigManager.getInstance().loadFromStream(new ByteArrayInputStream("".getBytes()));
    }

    @Test
    public void testParseStringCorrectly() {
        // We create a fake properties file entirely in computer memory
        String simulatedFileContent = "start.title.text=SHADOW ALIENS\n";
        InputStream stream = new ByteArrayInputStream(simulatedFileContent.getBytes(StandardCharsets.UTF_8));
        ConfigManager config = ConfigManager.getInstance();

        config.loadFromStream(stream);

        assertEquals("SHADOW ALIENS", config.getString("start.title.text"), "String values should be parsed exactly as written.");
    }
}
