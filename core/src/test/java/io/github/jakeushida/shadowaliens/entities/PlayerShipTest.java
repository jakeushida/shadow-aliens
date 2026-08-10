package io.github.jakeushida.shadowaliens.entities;

import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.CooldownEffect;
import io.github.jakeushida.shadowaliens.powerups.EngineEffect;
import io.github.jakeushida.shadowaliens.powerups.ShieldEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerShipTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testPlayerShipInitialization() {
        PlayerShip player = new PlayerShip(100f, 50f);
        assertEquals(100f, player.getX());
        assertEquals(50f, player.getY());
        assertEquals(3, player.getLives());
        assertFalse(player.isShielded());
        assertEquals(1f, player.getShotCooldownMultiplier());
        assertEquals(1f, player.getEngineMultiplier());
    }

    @Test
    public void testPlayerMovement() {
        PlayerShip player = new PlayerShip(100f, 50f);
        player.setSpeedX(10f);
        player.setSpeedY(5f);
        player.update(1.0f);
        assertEquals(110f, player.getX(), 0.001f);
        assertEquals(55f, player.getY(), 0.001f);
    }

    @Test
    public void testPlayerMovementWithEngineMultiplier() {
        PlayerShip player = new PlayerShip(100f, 50f);
        player.setEngineMultiplier(2f);
        player.setSpeedX(10f);
        player.update(1.0f);
        // Speed should be doubled: 10 * 2 * 1.0 = 20
        assertEquals(120f, player.getX(), 0.001f);
    }

    @Test
    public void testSetLivesUpdateGameSession() {
        PlayerShip player = new PlayerShip(100f, 50f);
        player.setLives(5);
        assertEquals(5, player.getLives());
        assertEquals(5, GameSession.getInstance().getCurrentLives());
    }

    @Test
    public void testIncrementLives() {
        PlayerShip player = new PlayerShip(100f, 50f);
        int before = player.getLives();
        player.incrementLives(2);
        assertEquals(before + 2, player.getLives());
    }

    @Test
    public void testShieldProtection() {
        PlayerShip player = new PlayerShip(100f, 50f);
        assertFalse(player.isShielded());

        player.setShielded(true);
        assertTrue(player.isShielded());

        // Collision with shield should not reduce lives
        int livesBefore = player.getLives();
        player.onCollision(new RegularEnemy(0f, 0f, 0));
        assertEquals(livesBefore, player.getLives());
    }

    @Test
    public void testCollisionWithoutShield() {
        PlayerShip player = new PlayerShip(100f, 50f);
        player.setShielded(false);
        int livesBefore = player.getLives();
        player.onCollision(new RegularEnemy(0f, 0f, 0));
        assertEquals(livesBefore - 1, player.getLives());
    }

    @Test
    public void testBuffApplication() {
        PlayerShip player = new PlayerShip(100f, 50f);
        ShieldEffect shield = new ShieldEffect();
        player.setBuff(shield);
        assertTrue(player.isShielded());
    }

    @Test
    public void testBuffReplacement() {
        PlayerShip player = new PlayerShip(100f, 50f);

        // Apply shield buff
        ShieldEffect shield = new ShieldEffect();
        player.setBuff(shield);
        assertTrue(player.isShielded());

        // Replace with engine buff (should remove shield)
        EngineEffect engine = new EngineEffect();
        player.setBuff(engine);
        assertFalse(player.isShielded());
        assertEquals(1.5f, player.getEngineMultiplier());
    }

    @Test
    public void testRemoveBuff() {
        PlayerShip player = new PlayerShip(100f, 50f);
        CooldownEffect cooldown = new CooldownEffect();
        player.setBuff(cooldown);
        assertEquals(0.5f, player.getShotCooldownMultiplier());

        player.setBuff(null);
        assertEquals(1f, player.getShotCooldownMultiplier());
    }

    @Test
    public void testGetBoundingBox() {
        PlayerShip player = new PlayerShip(100f, 50f);
        assertNotNull(player.getBoundingBox());
        assertEquals(100f, player.getBoundingBox().x);
        assertEquals(50f, player.getBoundingBox().y);
    }

    @Test
    public void testShootMethod() {
        PlayerShip player = new PlayerShip(100f, 50f);
        // Shoot method exists but doesn't throw exceptions
        assertDoesNotThrow(() -> player.shoot());
    }

    @Test
    public void testMovableInterfaceImplementation() {
        PlayerShip player = new PlayerShip(100f, 50f);
        player.setSpeedX(20f);
        player.setSpeedY(30f);
        assertEquals(20f, player.getSpeedX());
        assertEquals(30f, player.getSpeedY());
    }
}
