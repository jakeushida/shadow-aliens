package io.github.jakeushida.shadowaliens.entities;

import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UIElementsTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testLivesDisplayInitialization() {
        LivesDisplay livesDisplay = new LivesDisplay(10f, 20f, null, 5f);

        assertEquals(10f, livesDisplay.getX());
        assertEquals(20f, livesDisplay.getY());
        assertEquals(RenderLayer.UI, livesDisplay.getLayer());
    }

    @Test
    public void testLivesDisplayUpdate() {
        LivesDisplay livesDisplay = new LivesDisplay(10f, 20f, null, 5f);

        // Update should not change position
        float beforeX = livesDisplay.getX();
        float beforeY = livesDisplay.getY();

        livesDisplay.update(1.0f);

        assertEquals(beforeX, livesDisplay.getX());
        assertEquals(beforeY, livesDisplay.getY());
    }

    @Test
    public void testLivesDisplayReflectsGameSession() {
        GameSession.getInstance().setCurrentLives(5);
        LivesDisplay livesDisplay = new LivesDisplay(10f, 20f, null, 5f);

        // Should not throw when drawing (though we can't test actual rendering without GDX)
        assertDoesNotThrow(() -> livesDisplay.update(1.0f));
    }

    @Test
    public void testExplosionInitialization() {
        Explosion explosion = new Explosion(100f, 200f, 60);

        assertEquals(100f, explosion.getX());
        assertEquals(200f, explosion.getY());
        assertEquals(60, explosion.getDuration());
        assertEquals(RenderLayer.UI, explosion.getLayer());
        assertFalse(explosion.isFinished());
    }

    @Test
    public void testExplosionLifecycle() {
        Explosion explosion = new Explosion(100f, 200f, 2);

        assertFalse(explosion.isFinished());

        explosion.update(1.0f);
        assertFalse(explosion.isFinished());

        explosion.update(1.5f);
        assertTrue(explosion.isFinished());
    }

    @Test
    public void testExplosionDurationTracking() {
        Explosion explosion = new Explosion(100f, 200f, 5);

        assertFalse(explosion.isFinished());

        explosion.update(4.9f);
        assertFalse(explosion.isFinished());

        explosion.update(0.2f);
        assertTrue(explosion.isFinished());
    }

    @Test
    public void testExplosionZeroDuration() {
        Explosion explosion = new Explosion(100f, 200f, 0);

        // Should be immediately finished
        assertTrue(explosion.isFinished());
    }

    @Test
    public void testExplosionMultipleUpdates() {
        Explosion explosion = new Explosion(100f, 200f, 3);

        explosion.update(1.0f);
        assertFalse(explosion.isFinished());

        explosion.update(1.0f);
        assertFalse(explosion.isFinished());

        explosion.update(1.0f);
        assertTrue(explosion.isFinished());
    }

    @Test
    public void testExplosionPositionDoesNotChange() {
        Explosion explosion = new Explosion(100f, 200f, 5);

        explosion.update(2.0f);

        // Explosion stays at initial position
        assertEquals(100f, explosion.getX());
        assertEquals(200f, explosion.getY());
    }

    @Test
    public void testMultipleExplosions() {
        Explosion exp1 = new Explosion(100f, 200f, 2);
        Explosion exp2 = new Explosion(150f, 250f, 3);

        exp1.update(2.5f);
        exp2.update(1.0f);

        assertTrue(exp1.isFinished());
        assertFalse(exp2.isFinished());
    }

    @Test
    public void testUIElementsHaveCorrectLayer() {
        Explosion explosion = new Explosion(100f, 200f, 5);
        LivesDisplay lives = new LivesDisplay(10f, 20f, null, 5f);

        assertEquals(RenderLayer.UI, explosion.getLayer());
        assertEquals(RenderLayer.UI, lives.getLayer());
    }
}
