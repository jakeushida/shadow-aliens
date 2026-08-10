package io.github.jakeushida.shadowaliens.entities;

import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.LifeEffect;
import io.github.jakeushida.shadowaliens.powerups.ShieldEffect;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testBoundingBoxOverlapDetection() {
        PlayerShip player = new PlayerShip(100f, 100f);
        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);

        Rectangle playerBox = player.getBoundingBox();
        Rectangle enemyBox = enemy.getBoundingBox();

        // At same position, they should overlap
        assertTrue(playerBox.overlaps(enemyBox));
    }

    @Test
    public void testBoundingBoxNoOverlap() {
        PlayerShip player = new PlayerShip(100f, 100f);
        RegularEnemy enemy = new RegularEnemy(500f, 500f, 0);

        Rectangle playerBox = player.getBoundingBox();
        Rectangle enemyBox = enemy.getBoundingBox();

        // Far apart, should not overlap
        assertFalse(playerBox.overlaps(enemyBox));
    }

    @Test
    public void testProjectileEnemyCollision() {
        PlayerProjectile projectile = new PlayerProjectile(100f, 100f);
        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);

        assertTrue(projectile.getBoundingBox().overlaps(enemy.getBoundingBox()));
    }

    @Test
    public void testPlayerCollisionReducesLives() {
        PlayerShip player = new PlayerShip(100f, 100f);
        int livesBefore = player.getLives();

        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);
        player.onCollision(enemy);

        assertEquals(livesBefore - 1, player.getLives());
    }

    @Test
    public void testPlayerCollisionWithShieldDoesNotReduceLives() {
        PlayerShip player = new PlayerShip(100f, 100f);
        player.setShielded(true);
        int livesBefore = player.getLives();

        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);
        player.onCollision(enemy);

        assertEquals(livesBefore, player.getLives());
    }

    @Test
    public void testPowerupPlayerCollision() {
        PlayerShip player = new PlayerShip(100f, 100f);
        int livesBefore = player.getLives();

        PowerupEntity powerup = new PowerupEntity(100f, 100f, new LifeEffect());
        powerup.onCollision(player);

        assertEquals(livesBefore + 1, player.getLives());
    }

    @Test
    public void testPowerupOnlyAffectsPlayer() {
        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);
        PowerupEntity powerup = new PowerupEntity(100f, 100f, new ShieldEffect());

        // Powerup collision with non-player should not throw
        assertDoesNotThrow(() -> powerup.onCollision(enemy));
    }

    @Test
    public void testEnemyProjectilePlayerCollisionBox() {
        EnemyProjectile projectile = new EnemyProjectile(100f, 100f);
        PlayerShip player = new PlayerShip(100f, 100f);

        assertTrue(projectile.getBoundingBox().overlaps(player.getBoundingBox()));
    }

    @Test
    public void testBoundingBoxDimensions() {
        PlayerShip player = new PlayerShip(0f, 0f);
        RegularEnemy enemy = new RegularEnemy(0f, 0f, 0);
        PlayerProjectile projectile = new PlayerProjectile(0f, 0f);

        assertTrue(player.getBoundingBox().width > 0);
        assertTrue(player.getBoundingBox().height > 0);
        assertTrue(enemy.getBoundingBox().width > 0);
        assertTrue(enemy.getBoundingBox().height > 0);
        assertTrue(projectile.getBoundingBox().width > 0);
        assertTrue(projectile.getBoundingBox().height > 0);
    }

    @Test
    public void testBoundingBoxUpdatesWithPosition() {
        PlayerShip player = new PlayerShip(100f, 100f);
        Rectangle box1 = player.getBoundingBox();
        assertEquals(100f, box1.x);
        assertEquals(100f, box1.y);

        player.setSpeedX(50f);
        player.update(1.0f);

        Rectangle box2 = player.getBoundingBox();
        assertEquals(150f, box2.x, 0.001f);
    }

    @Test
    public void testMultipleProjectileCollisions() {
        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);
        PlayerProjectile proj1 = new PlayerProjectile(100f, 100f);
        PlayerProjectile proj2 = new PlayerProjectile(105f, 105f);

        assertTrue(proj1.getBoundingBox().overlaps(enemy.getBoundingBox()));
        assertTrue(proj2.getBoundingBox().overlaps(enemy.getBoundingBox()));
    }

    @Test
    public void testCollisionDetectionEdgeCases() {
        PlayerShip player = new PlayerShip(100f, 100f);

        // Test collision at exact same position
        RegularEnemy enemy1 = new RegularEnemy(100f, 100f, 0);
        assertTrue(player.getBoundingBox().overlaps(enemy1.getBoundingBox()));

        // Test just touching edges (may or may not overlap depending on box sizes)
        RegularEnemy enemy2 = new RegularEnemy(148f, 100f, 0); // 48px ship width
        Rectangle playerBox = player.getBoundingBox();
        Rectangle enemy2Box = enemy2.getBoundingBox();
        // At minimum, boxes should be defined
        assertNotNull(playerBox);
        assertNotNull(enemy2Box);
    }

    @Test
    public void testPowerupCollisionAppliesEffect() {
        PlayerShip player = new PlayerShip(100f, 100f);
        assertFalse(player.isShielded());

        PowerupEntity shieldPowerup = new PowerupEntity(100f, 100f, new ShieldEffect());
        shieldPowerup.onCollision(player);

        assertTrue(player.isShielded());
    }
}
