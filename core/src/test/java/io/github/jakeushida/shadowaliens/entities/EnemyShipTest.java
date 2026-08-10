package io.github.jakeushida.shadowaliens.entities;

import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.rendering.RenderLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyShipTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testRegularEnemyInitialization() {
        RegularEnemy enemy = new RegularEnemy(200f, 400f, 60);
        assertEquals(200f, enemy.getX());
        assertEquals(400f, enemy.getY());
        assertEquals(60, enemy.getArrivalTime());
        assertEquals(RenderLayer.SHIPS, enemy.getLayer());
        assertEquals(-80f, enemy.getSpeedY());
    }

    @Test
    public void testRegularEnemyMovement() {
        RegularEnemy enemy = new RegularEnemy(200f, 400f, 0);
        enemy.update(1.0f);
        // SpeedY is -80, so after 1 second y should decrease by 80
        assertEquals(320f, enemy.getY(), 0.001f);
        // X should not change for regular enemy
        assertEquals(200f, enemy.getX(), 0.001f);
    }

    @Test
    public void testStrafingEnemyInitialization() {
        StrafingEnemy enemy = new StrafingEnemy(300f, 500f, 120);
        assertEquals(300f, enemy.getX());
        assertEquals(500f, enemy.getY());
        assertEquals(120, enemy.getArrivalTime());
        assertEquals(-60f, enemy.getSpeedY());
        assertEquals(50f, enemy.getSpeedX());
    }

    @Test
    public void testStrafingEnemySinusoidalMovement() {
        StrafingEnemy enemy = new StrafingEnemy(300f, 500f, 0);
        float initialX = enemy.getX();
        float initialY = enemy.getY();

        // Update multiple times to see sinusoidal movement
        enemy.update(0.5f);
        float x1 = enemy.getX();
        float y1 = enemy.getY();

        // Y should decrease (moving down)
        assertTrue(y1 < initialY);

        // X should change due to sine wave
        assertNotEquals(initialX, x1);

        // Continue movement
        enemy.update(0.5f);
        float x2 = enemy.getX();
        float y2 = enemy.getY();

        // Y continues to decrease
        assertTrue(y2 < y1);

        // X changes based on sine pattern
        assertNotEquals(x1, x2);
    }

    @Test
    public void testShootingEnemyInitialization() {
        ShootingEnemy enemy = new ShootingEnemy(150f, 450f, 90);
        assertEquals(150f, enemy.getX());
        assertEquals(450f, enemy.getY());
        assertEquals(90, enemy.getArrivalTime());
        assertEquals(-45f, enemy.getSpeedY());
    }

    @Test
    public void testShootingEnemyMovement() {
        ShootingEnemy enemy = new ShootingEnemy(150f, 450f, 0);
        enemy.update(2.0f);
        // SpeedY is -45, so after 2 seconds y should decrease by 90
        assertEquals(360f, enemy.getY(), 0.001f);
        // X should not change
        assertEquals(150f, enemy.getX(), 0.001f);
    }

    @Test
    public void testShootingEnemyImplementsShooter() {
        ShootingEnemy enemy = new ShootingEnemy(150f, 450f, 0);
        // Should have shoot method and not throw exception
        assertDoesNotThrow(() -> enemy.shoot());
    }

    @Test
    public void testEnemyBoundingBox() {
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);
        assertNotNull(enemy.getBoundingBox());
        assertEquals(100f, enemy.getBoundingBox().x);
        assertEquals(200f, enemy.getBoundingBox().y);
        assertTrue(enemy.getBoundingBox().width > 0);
        assertTrue(enemy.getBoundingBox().height > 0);
    }

    @Test
    public void testEnemyBoundingBoxFollowsPosition() {
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);
        enemy.update(1.0f);
        float newY = enemy.getY();
        assertEquals(100f, enemy.getBoundingBox().x);
        assertEquals(newY, enemy.getBoundingBox().y);
    }

    @Test
    public void testEnemyMovableInterface() {
        StrafingEnemy enemy = new StrafingEnemy(100f, 200f, 0);
        enemy.setSpeedX(100f);
        enemy.setSpeedY(-100f);
        assertEquals(100f, enemy.getSpeedX());
        assertEquals(-100f, enemy.getSpeedY());
    }

    @Test
    public void testEnemyOnCollision() {
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);
        PlayerShip player = new PlayerShip(100f, 200f);
        // Should not throw exception
        assertDoesNotThrow(() -> enemy.onCollision(player));
    }

    @Test
    public void testDifferentEnemySpeedPatterns() {
        RegularEnemy regular = new RegularEnemy(100f, 300f, 0);
        StrafingEnemy strafing = new StrafingEnemy(100f, 300f, 0);
        ShootingEnemy shooting = new ShootingEnemy(100f, 300f, 0);

        // Each enemy type has different speed
        assertTrue(Math.abs(regular.getSpeedY()) > Math.abs(strafing.getSpeedY()));
        assertTrue(Math.abs(strafing.getSpeedY()) > Math.abs(shooting.getSpeedY()));
    }

    @Test
    public void testEnemyUpdateDoesNotThrow() {
        RegularEnemy regular = new RegularEnemy(100f, 300f, 0);
        StrafingEnemy strafing = new StrafingEnemy(100f, 300f, 0);
        ShootingEnemy shooting = new ShootingEnemy(100f, 300f, 0);

        assertDoesNotThrow(() -> {
            regular.update(1.0f);
            strafing.update(1.0f);
            shooting.update(1.0f);
        });
    }
}
