package io.github.jakeushida.shadowaliens.contracts;

import io.github.jakeushida.shadowaliens.entities.*;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContractsTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    // Test Collidable interface
    @Test
    public void testCollidableInterface() {
        Collidable player = new PlayerShip(100f, 100f);
        Collidable enemy = new RegularEnemy(200f, 200f, 0);
        Collidable projectile = new PlayerProjectile(150f, 150f);

        // All should have bounding boxes
        assertNotNull(player.getBoundingBox());
        assertNotNull(enemy.getBoundingBox());
        assertNotNull(projectile.getBoundingBox());

        // All should handle collisions
        assertDoesNotThrow(() -> player.onCollision(enemy));
        assertDoesNotThrow(() -> enemy.onCollision(player));
        assertDoesNotThrow(() -> projectile.onCollision(enemy));
    }

    @Test
    public void testPlayerShipImplementsCollidable() {
        PlayerShip player = new PlayerShip(100f, 100f);

        assertTrue(player instanceof Collidable);
        assertNotNull(player.getBoundingBox());

        // Test collision handler
        int livesBefore = player.getLives();
        player.onCollision(new RegularEnemy(100f, 100f, 0));
        assertEquals(livesBefore - 1, player.getLives());
    }

    @Test
    public void testEnemyShipImplementsCollidable() {
        EnemyShip enemy = new RegularEnemy(100f, 100f, 0);

        assertTrue(enemy instanceof Collidable);
        assertNotNull(enemy.getBoundingBox());
        assertDoesNotThrow(() -> enemy.onCollision(new PlayerShip(100f, 100f)));
    }

    @Test
    public void testProjectileImplementsCollidable() {
        PlayerProjectile playerProj = new PlayerProjectile(100f, 100f);
        EnemyProjectile enemyProj = new EnemyProjectile(150f, 150f);

        assertTrue(playerProj instanceof Collidable);
        assertTrue(enemyProj instanceof Collidable);

        assertNotNull(playerProj.getBoundingBox());
        assertNotNull(enemyProj.getBoundingBox());
    }

    // Test Movable interface
    @Test
    public void testMovableInterface() {
        Movable player = new PlayerShip(100f, 100f);
        Movable enemy = new RegularEnemy(200f, 200f, 0);
        Movable projectile = new PlayerProjectile(150f, 150f);

        // All should support speed setters/getters
        player.setSpeedX(10f);
        player.setSpeedY(5f);
        assertEquals(10f, player.getSpeedX());
        assertEquals(5f, player.getSpeedY());

        enemy.setSpeedX(-20f);
        enemy.setSpeedY(-30f);
        assertEquals(-20f, enemy.getSpeedX());
        assertEquals(-30f, enemy.getSpeedY());

        // All should support move
        assertDoesNotThrow(() -> player.move(1.0f));
        assertDoesNotThrow(() -> enemy.move(1.0f));
        assertDoesNotThrow(() -> projectile.move(1.0f));
    }

    @Test
    public void testPlayerShipImplementsMovable() {
        PlayerShip player = new PlayerShip(100f, 100f);

        assertTrue(player instanceof Movable);

        player.setSpeedX(50f);
        player.setSpeedY(25f);
        assertEquals(50f, player.getSpeedX());
        assertEquals(25f, player.getSpeedY());

        float initialX = player.getX();
        player.move(1.0f);
        assertTrue(player.getX() > initialX);
    }

    @Test
    public void testEnemyShipImplementsMovable() {
        EnemyShip enemy = new StrafingEnemy(100f, 200f, 0);

        assertTrue(enemy instanceof Movable);

        enemy.setSpeedX(10f);
        enemy.setSpeedY(-20f);
        assertEquals(10f, enemy.getSpeedX());
        assertEquals(-20f, enemy.getSpeedY());
    }

    @Test
    public void testProjectileImplementsMovable() {
        PlayerProjectile projectile = new PlayerProjectile(100f, 100f);

        assertTrue(projectile instanceof Movable);

        float initialSpeed = projectile.getSpeedY();
        float initialY = projectile.getY();

        projectile.move(1.0f);
        assertEquals(initialY + initialSpeed, projectile.getY(), 0.001f);
    }

    @Test
    public void testPowerupImplementsMovable() {
        PowerupEntity powerup = new PowerupEntity(100f, 200f, null);

        assertTrue(powerup instanceof Movable);

        powerup.setSpeedX(5f);
        powerup.setSpeedY(-10f);
        assertEquals(5f, powerup.getSpeedX());
        assertEquals(-10f, powerup.getSpeedY());

        assertDoesNotThrow(() -> powerup.move(1.0f));
    }

    // Test Shooter interface
    @Test
    public void testShooterInterface() {
        Shooter player = new PlayerShip(100f, 100f);
        Shooter shootingEnemy = new ShootingEnemy(200f, 200f, 0);

        // Both should have shoot method
        assertDoesNotThrow(() -> player.shoot());
        assertDoesNotThrow(() -> shootingEnemy.shoot());
    }

    @Test
    public void testPlayerShipImplementsShooter() {
        PlayerShip player = new PlayerShip(100f, 100f);

        assertTrue(player instanceof Shooter);
        assertDoesNotThrow(() -> player.shoot());
    }

    @Test
    public void testShootingEnemyImplementsShooter() {
        ShootingEnemy enemy = new ShootingEnemy(100f, 200f, 0);

        assertTrue(enemy instanceof Shooter);
        assertDoesNotThrow(() -> enemy.shoot());
    }

    @Test
    public void testNonShootingEnemiesDoNotImplementShooter() {
        RegularEnemy regular = new RegularEnemy(100f, 200f, 0);
        StrafingEnemy strafing = new StrafingEnemy(150f, 250f, 0);

        assertFalse(regular instanceof Shooter);
        assertFalse(strafing instanceof Shooter);
    }

    // Test multiple interface implementation
    @Test
    public void testPlayerShipImplementsAllThreeInterfaces() {
        PlayerShip player = new PlayerShip(100f, 100f);

        assertTrue(player instanceof Collidable);
        assertTrue(player instanceof Movable);
        assertTrue(player instanceof Shooter);
    }

    @Test
    public void testRegularEnemyImplementsTwoInterfaces() {
        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);

        assertTrue(enemy instanceof Collidable);
        assertTrue(enemy instanceof Movable);
        assertFalse(enemy instanceof Shooter);
    }

    @Test
    public void testShootingEnemyImplementsAllThreeInterfaces() {
        ShootingEnemy enemy = new ShootingEnemy(100f, 100f, 0);

        assertTrue(enemy instanceof Collidable);
        assertTrue(enemy instanceof Movable);
        assertTrue(enemy instanceof Shooter);
    }

    @Test
    public void testProjectileImplementsTwoInterfaces() {
        PlayerProjectile projectile = new PlayerProjectile(100f, 100f);

        assertTrue(projectile instanceof Collidable);
        assertTrue(projectile instanceof Movable);
        assertFalse(projectile instanceof Shooter);
    }

    // Test interface method chaining
    @Test
    public void testMovableMethodChaining() {
        Movable entity = new PlayerShip(100f, 100f);

        entity.setSpeedX(10f);
        entity.setSpeedY(20f);
        entity.move(1.0f);

        assertEquals(10f, entity.getSpeedX());
        assertEquals(20f, entity.getSpeedY());
    }

    @Test
    public void testCollidableWithDifferentEntities() {
        Collidable player = new PlayerShip(100f, 100f);
        Collidable enemy = new RegularEnemy(100f, 100f, 0);
        Collidable projectile = new PlayerProjectile(100f, 100f);

        // Test cross-collisions
        assertTrue(player.getBoundingBox().overlaps(enemy.getBoundingBox()));
        assertTrue(player.getBoundingBox().overlaps(projectile.getBoundingBox()));
        assertTrue(enemy.getBoundingBox().overlaps(projectile.getBoundingBox()));
    }

    @Test
    public void testInterfacePolymorphism() {
        // Test that entities can be used polymorphically
        Movable[] movables = {
            new PlayerShip(100f, 100f),
            new RegularEnemy(200f, 200f, 0),
            new PlayerProjectile(150f, 150f)
        };

        for (Movable movable : movables) {
            movable.setSpeedX(10f);
            assertDoesNotThrow(() -> movable.move(1.0f));
        }
    }

    @Test
    public void testCollidablePolymorphism() {
        Collidable[] collidables = {
            new PlayerShip(100f, 100f),
            new RegularEnemy(200f, 200f, 0),
            new EnemyProjectile(150f, 150f)
        };

        for (Collidable collidable : collidables) {
            assertNotNull(collidable.getBoundingBox());
            assertDoesNotThrow(() -> collidable.onCollision(collidables[0]));
        }
    }
}
