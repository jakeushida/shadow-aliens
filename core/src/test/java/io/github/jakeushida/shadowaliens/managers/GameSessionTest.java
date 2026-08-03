package io.github.jakeushida.shadowaliens.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameSessionTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testDefaultResetValues() {
        GameSession s = GameSession.getInstance();
        assertEquals(0, s.getCurrentScore());
        assertEquals(3, s.getCurrentLives());
        assertEquals(1, s.getCurrentWave());
        assertEquals(1.0f, s.getTimeScale());
        assertFalse(s.isInvincibilityMode());
    }

    @Test
    public void testSettersAndGetters() {
        GameSession s = GameSession.getInstance();
        s.setCurrentScore(42);
        s.setCurrentLives(2);
        s.setCurrentWave(5);
        s.setTimeScale(0.5f);
        s.setInvincibilityMode(true);

        assertEquals(42, s.getCurrentScore());
        assertEquals(2, s.getCurrentLives());
        assertEquals(5, s.getCurrentWave());
        assertEquals(0.5f, s.getTimeScale());
        assertTrue(s.isInvincibilityMode());
    }
}
