package io.github.jakeushida.shadowaliens.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigManagerTest {

    private InputStream createPropertiesStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    public void setUp() {
        // Clear properties by loading an empty stream
        ConfigManager.getInstance().loadFromStream(createPropertiesStream(""));
    }

    @Test
    public void testBasicKeyValueParsing() {
        InputStream stream = createPropertiesStream("name=Shadow Aliens\n");
        ConfigManager config = ConfigManager.getInstance();

        config.loadFromStream(stream);

        assertEquals("Shadow Aliens", config.getString("name"));
    }

    @Test
    public void testMultipleKeyValuePairs() {
        String content = "a=1\nb=2\nc=3\n";
        ConfigManager.getInstance().loadFromStream(createPropertiesStream(content));
        assertEquals("1", ConfigManager.getInstance().getString("a"));
        assertEquals("2", ConfigManager.getInstance().getString("b"));
        assertEquals("3", ConfigManager.getInstance().getString("c"));
    }

    @Test
    public void testNumericValueGetInt() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("maxLives=5\n"));
        assertEquals(5, ConfigManager.getInstance().getInt("maxLives"));
    }

    @Test
    public void testNegativeNumberGetInt() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("offset=-10\n"));
        assertEquals(-10, ConfigManager.getInstance().getInt("offset"));
    }

    @Test
    public void testCommentsIgnored() {
        String content = "# comment line\nkey=value\n!another comment\nkey2=value2\n";
        ConfigManager.getInstance().loadFromStream(createPropertiesStream(content));
        assertEquals("value", ConfigManager.getInstance().getString("key"));
        assertEquals("value2", ConfigManager.getInstance().getString("key2"));
    }

    @Test
    public void testEmptyStreamLoads() {
        // loading empty stream should not throw
        assertDoesNotThrow(() -> ConfigManager.getInstance().loadFromStream(createPropertiesStream("")));
    }

    @Test
    public void testMissingKeyThrowsException() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("a=1\n"));
        assertThrows(IllegalArgumentException.class, () -> ConfigManager.getInstance().getString("missing"));
    }

    @Test
    public void testInvalidIntegerThrowsException() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("value=notanumber\n"));
        assertThrows(NumberFormatException.class, () -> ConfigManager.getInstance().getInt("value"));
    }

    @Test
    public void testEmptyValue() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("emptyKey=\n"));
        assertEquals("", ConfigManager.getInstance().getString("emptyKey"));
    }

    @Test
    public void testCaseSensitivityOfKeys() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("MyKey=value\n"));
        assertEquals("value", ConfigManager.getInstance().getString("MyKey"));
        assertThrows(IllegalArgumentException.class, () -> ConfigManager.getInstance().getString("mykey"));
    }

    @Test
    public void testSpecialCharactersInValue() {
        ConfigManager.getInstance().loadFromStream(createPropertiesStream("path=/usr/local/bin\nunicode=こんにちは\n"));
        assertEquals("/usr/local/bin", ConfigManager.getInstance().getString("path"));
        assertEquals("こんにちは", ConfigManager.getInstance().getString("unicode"));
    }
}
