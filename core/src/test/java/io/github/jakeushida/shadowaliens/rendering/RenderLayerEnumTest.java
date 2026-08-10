package io.github.jakeushida.shadowaliens.rendering;

import io.github.jakeushida.shadowaliens.entities.*;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLayerEnumTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testRenderLayerEnumExists() {
        assertNotNull(RenderLayer.BACKGROUND);
        assertNotNull(RenderLayer.SHIPS);
        assertNotNull(RenderLayer.PROJECTILES);
        assertNotNull(RenderLayer.UI);
    }

    @Test
    public void testRenderLayerValues() {
        RenderLayer[] layers = RenderLayer.values();
        assertEquals(4, layers.length);

        assertEquals(RenderLayer.BACKGROUND, layers[0]);
        assertEquals(RenderLayer.SHIPS, layers[1]);
        assertEquals(RenderLayer.PROJECTILES, layers[2]);
        assertEquals(RenderLayer.UI, layers[3]);
    }

    @Test
    public void testRenderLayerValueOf() {
        assertEquals(RenderLayer.BACKGROUND, RenderLayer.valueOf("BACKGROUND"));
        assertEquals(RenderLayer.SHIPS, RenderLayer.valueOf("SHIPS"));
        assertEquals(RenderLayer.PROJECTILES, RenderLayer.valueOf("PROJECTILES"));
        assertEquals(RenderLayer.UI, RenderLayer.valueOf("UI"));
    }

    @Test
    public void testRenderLayerToString() {
        assertEquals("BACKGROUND", RenderLayer.BACKGROUND.toString());
        assertEquals("SHIPS", RenderLayer.SHIPS.toString());
        assertEquals("PROJECTILES", RenderLayer.PROJECTILES.toString());
        assertEquals("UI", RenderLayer.UI.toString());
    }

    @Test
    public void testPlayerShipUsesShipsLayer() {
        PlayerShip player = new PlayerShip(100f, 100f);
        assertEquals(RenderLayer.SHIPS, player.getLayer());
    }

    @Test
    public void testEnemyShipsUseShipsLayer() {
        RegularEnemy regular = new RegularEnemy(100f, 100f, 0);
        StrafingEnemy strafing = new StrafingEnemy(100f, 100f, 0);
        ShootingEnemy shooting = new ShootingEnemy(100f, 100f, 0);

        assertEquals(RenderLayer.SHIPS, regular.getLayer());
        assertEquals(RenderLayer.SHIPS, strafing.getLayer());
        assertEquals(RenderLayer.SHIPS, shooting.getLayer());
    }

    @Test
    public void testProjectilesUseProjectilesLayer() {
        PlayerProjectile playerProj = new PlayerProjectile(100f, 100f);
        EnemyProjectile enemyProj = new EnemyProjectile(100f, 100f);

        assertEquals(RenderLayer.PROJECTILES, playerProj.getLayer());
        assertEquals(RenderLayer.PROJECTILES, enemyProj.getLayer());
    }

    @Test
    public void testPowerupsUseProjectilesLayer() {
        PowerupEntity powerup = new PowerupEntity(100f, 100f, null);
        assertEquals(RenderLayer.PROJECTILES, powerup.getLayer());
    }

    @Test
    public void testUIElementsUseUILayer() {
        TextElement text = new TextElement(100f, 100f, "Test", null, null);
        Explosion explosion = new Explosion(100f, 100f, 60);
        LivesDisplay lives = new LivesDisplay(100f, 100f, null, 5f);

        assertEquals(RenderLayer.UI, text.getLayer());
        assertEquals(RenderLayer.UI, explosion.getLayer());
        assertEquals(RenderLayer.UI, lives.getLayer());
    }

    @Test
    public void testRenderLayerOrdering() {
        // Test that layers have proper ordinal ordering for z-order rendering
        assertTrue(RenderLayer.BACKGROUND.ordinal() < RenderLayer.SHIPS.ordinal());
        assertTrue(RenderLayer.SHIPS.ordinal() < RenderLayer.PROJECTILES.ordinal());
        assertTrue(RenderLayer.PROJECTILES.ordinal() < RenderLayer.UI.ordinal());
    }

    @Test
    public void testRenderLayerComparison() {
        assertTrue(RenderLayer.BACKGROUND.compareTo(RenderLayer.SHIPS) < 0);
        assertTrue(RenderLayer.SHIPS.compareTo(RenderLayer.PROJECTILES) < 0);
        assertTrue(RenderLayer.PROJECTILES.compareTo(RenderLayer.UI) < 0);
        assertEquals(0, RenderLayer.SHIPS.compareTo(RenderLayer.SHIPS));
    }

    @Test
    public void testRenderLayerEquality() {
        assertEquals(RenderLayer.BACKGROUND, RenderLayer.BACKGROUND);
        assertNotEquals(RenderLayer.BACKGROUND, RenderLayer.SHIPS);
        assertNotEquals(RenderLayer.SHIPS, RenderLayer.UI);
    }

    @Test
    public void testRenderLayerEnumType() {
        assertTrue(RenderLayer.BACKGROUND instanceof RenderLayer);
        assertTrue(RenderLayer.SHIPS instanceof RenderLayer);
        assertTrue(RenderLayer.PROJECTILES instanceof RenderLayer);
        assertTrue(RenderLayer.UI instanceof RenderLayer);
    }

    @Test
    public void testAllEntitiesHaveRenderLayer() {
        GameEntity[] entities = {
            new PlayerShip(0f, 0f),
            new RegularEnemy(0f, 0f, 0),
            new PlayerProjectile(0f, 0f),
            new EnemyProjectile(0f, 0f),
            new PowerupEntity(0f, 0f, null),
            new Explosion(0f, 0f, 1),
            new TextElement(0f, 0f, "", null, null),
            new LivesDisplay(0f, 0f, null, 0f)
        };

        for (GameEntity entity : entities) {
            assertNotNull(entity.getLayer());
            assertTrue(entity.getLayer() instanceof RenderLayer);
        }
    }

    @Test
    public void testRenderLayerIsImmutable() {
        RenderLayer layer = RenderLayer.SHIPS;
        assertEquals(RenderLayer.SHIPS, layer);

        // Enum values should be singleton constants
        assertSame(RenderLayer.SHIPS, RenderLayer.valueOf("SHIPS"));
    }

    @Test
    public void testInvalidRenderLayerThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            RenderLayer.valueOf("INVALID_LAYER");
        });
    }
}
