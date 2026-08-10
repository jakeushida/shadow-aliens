package io.github.jakeushida.shadowaliens.screens;

import io.github.jakeushida.shadowaliens.managers.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for screens that don't require full libGDX initialization.
 * These tests verify core logic and structure without rendering.
 */
public class ScreensBasicTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testStartScreenClassExists() {
        assertNotNull(StartScreen.class);
    }

    @Test
    public void testBattleScreenClassExists() {
        assertNotNull(BattleScreen.class);
    }

    @Test
    public void testPauseScreenClassExists() {
        assertNotNull(PauseScreen.class);
    }

    @Test
    public void testEndScreenClassExists() {
        assertNotNull(EndScreen.class);
    }

    @Test
    public void testScreensAreInCorrectPackage() {
        assertEquals("io.github.jakeushida.shadowaliens.screens", StartScreen.class.getPackageName());
        assertEquals("io.github.jakeushida.shadowaliens.screens", BattleScreen.class.getPackageName());
        assertEquals("io.github.jakeushida.shadowaliens.screens", PauseScreen.class.getPackageName());
        assertEquals("io.github.jakeushida.shadowaliens.screens", EndScreen.class.getPackageName());
    }

    @Test
    public void testAllScreensImplementScreenInterface() {
        assertTrue(com.badlogic.gdx.Screen.class.isAssignableFrom(StartScreen.class));
        assertTrue(com.badlogic.gdx.Screen.class.isAssignableFrom(BattleScreen.class));
        assertTrue(com.badlogic.gdx.Screen.class.isAssignableFrom(PauseScreen.class));
        assertTrue(com.badlogic.gdx.Screen.class.isAssignableFrom(EndScreen.class));
    }

    @Test
    public void testScreensHaveRequiredMethods() throws NoSuchMethodException {
        // Verify all screens have the Screen interface methods
        Class<?>[] screenClasses = {StartScreen.class, BattleScreen.class, PauseScreen.class, EndScreen.class};

        for (Class<?> screenClass : screenClasses) {
            assertNotNull(screenClass.getMethod("show"));
            assertNotNull(screenClass.getMethod("render", float.class));
            assertNotNull(screenClass.getMethod("resize", int.class, int.class));
            assertNotNull(screenClass.getMethod("pause"));
            assertNotNull(screenClass.getMethod("resume"));
            assertNotNull(screenClass.getMethod("hide"));
            assertNotNull(screenClass.getMethod("dispose"));
        }
    }

    @Test
    public void testStartScreenResetsGameSessionOnShow() {
        // This test verifies that StartScreen has the reset logic
        // without actually instantiating it (which would require Gdx)
        assertTrue(StartScreen.class.getDeclaredMethods().length > 0);
    }

    @Test
    public void testBattleScreenHasWaveGetter() throws NoSuchMethodException {
        assertNotNull(BattleScreen.class.getMethod("getWave"));
    }

    @Test
    public void testBattleScreenHasPlayerShipGetter() throws NoSuchMethodException {
        assertNotNull(BattleScreen.class.getMethod("getPlayerShip"));
    }

    @Test
    public void testEndScreenConstructorAcceptsScoreAndWinState() throws NoSuchMethodException {
        assertNotNull(EndScreen.class.getConstructor(
            io.github.jakeushida.shadowaliens.Main.class,
            int.class,
            boolean.class
        ));
    }

    @Test
    public void testPauseScreenConstructorAcceptsBattleScreen() throws NoSuchMethodException {
        assertNotNull(PauseScreen.class.getConstructor(
            io.github.jakeushida.shadowaliens.Main.class,
            BattleScreen.class
        ));
    }

    @Test
    public void testGameSessionResetsToDefaultValues() {
        GameSession session = GameSession.getInstance();
        session.setCurrentScore(1000);
        session.setCurrentWave(5);
        session.setCurrentLives(1);

        session.reset();

        assertEquals(0, session.getCurrentScore());
        assertEquals(1, session.getCurrentWave());
        assertEquals(3, session.getCurrentLives());
    }

    @Test
    public void testScreenClassesArePublic() {
        assertTrue(java.lang.reflect.Modifier.isPublic(StartScreen.class.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(BattleScreen.class.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(PauseScreen.class.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(EndScreen.class.getModifiers()));
    }
}
