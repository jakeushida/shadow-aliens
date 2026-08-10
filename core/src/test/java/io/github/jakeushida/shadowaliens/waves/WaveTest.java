package io.github.jakeushida.shadowaliens.waves;

import io.github.jakeushida.shadowaliens.entities.*;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.ShieldEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WaveTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testWaveInitialization() {
        Wave wave = new Wave();
        assertNotNull(wave.getEnemies());
        assertNotNull(wave.getPowerups());
        assertNotNull(wave.getEnemyProjectiles());
        assertTrue(wave.getEnemies().isEmpty());
        assertTrue(wave.getPowerups().isEmpty());
        assertTrue(wave.getEnemyProjectiles().isEmpty());
    }

    @Test
    public void testWaveIsCompleteWhenEmpty() {
        Wave wave = new Wave();
        assertTrue(wave.isComplete());
    }

    @Test
    public void testWaveIsNotCompleteWithEnemies() {
        Wave wave = new Wave();
        wave.getEnemies().add(new RegularEnemy(100f, 200f, 0));
        assertFalse(wave.isComplete());
    }

    @Test
    public void testWaveBecomesCompleteWhenEnemiesCleared() {
        Wave wave = new Wave();
        wave.getEnemies().add(new RegularEnemy(100f, 200f, 0));
        wave.getEnemies().add(new StrafingEnemy(150f, 250f, 60));
        assertFalse(wave.isComplete());

        wave.getEnemies().clear();
        assertTrue(wave.isComplete());
    }

    @Test
    public void testWaveUpdateEnemies() {
        Wave wave = new Wave();
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);
        float initialY = enemy.getY();
        wave.getEnemies().add(enemy);

        wave.update(1.0f);

        // Enemy should have moved
        assertNotEquals(initialY, enemy.getY());
    }

    @Test
    public void testWaveUpdatePowerups() {
        Wave wave = new Wave();
        PowerupEntity powerup = new PowerupEntity(100f, 300f, new ShieldEffect());
        float initialY = powerup.getY();
        wave.getPowerups().add(powerup);

        wave.update(1.0f);

        // Powerup should have moved
        assertNotEquals(initialY, powerup.getY());
    }

    @Test
    public void testWaveUpdateEnemyProjectiles() {
        Wave wave = new Wave();
        EnemyProjectile projectile = new EnemyProjectile(100f, 400f);
        float initialY = projectile.getY();
        wave.getEnemyProjectiles().add(projectile);

        wave.update(1.0f);

        // Projectile should have moved
        assertNotEquals(initialY, projectile.getY());
    }

    @Test
    public void testWaveUpdateMultipleEntities() {
        Wave wave = new Wave();

        RegularEnemy enemy1 = new RegularEnemy(100f, 200f, 0);
        StrafingEnemy enemy2 = new StrafingEnemy(200f, 300f, 30);
        PowerupEntity powerup = new PowerupEntity(150f, 250f, new ShieldEffect());
        EnemyProjectile projectile = new EnemyProjectile(180f, 220f);

        wave.getEnemies().add(enemy1);
        wave.getEnemies().add(enemy2);
        wave.getPowerups().add(powerup);
        wave.getEnemyProjectiles().add(projectile);

        assertDoesNotThrow(() -> wave.update(0.5f));

        // All entities should still be in the wave
        assertEquals(2, wave.getEnemies().size());
        assertEquals(1, wave.getPowerups().size());
        assertEquals(1, wave.getEnemyProjectiles().size());
    }

    @Test
    public void testWaveAddRemoveEnemies() {
        Wave wave = new Wave();
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);

        wave.getEnemies().add(enemy);
        assertEquals(1, wave.getEnemies().size());
        assertFalse(wave.isComplete());

        wave.getEnemies().remove(enemy);
        assertEquals(0, wave.getEnemies().size());
        assertTrue(wave.isComplete());
    }

    @Test
    public void testWaveAddRemovePowerups() {
        Wave wave = new Wave();
        PowerupEntity powerup = new PowerupEntity(100f, 200f, new ShieldEffect());

        wave.getPowerups().add(powerup);
        assertEquals(1, wave.getPowerups().size());

        wave.getPowerups().remove(powerup);
        assertEquals(0, wave.getPowerups().size());
    }

    @Test
    public void testWaveAddRemoveProjectiles() {
        Wave wave = new Wave();
        EnemyProjectile projectile = new EnemyProjectile(100f, 200f);

        wave.getEnemyProjectiles().add(projectile);
        assertEquals(1, wave.getEnemyProjectiles().size());

        wave.getEnemyProjectiles().remove(projectile);
        assertEquals(0, wave.getEnemyProjectiles().size());
    }

    @Test
    public void testWaveMixedEnemyTypes() {
        Wave wave = new Wave();
        wave.getEnemies().add(new RegularEnemy(100f, 200f, 0));
        wave.getEnemies().add(new StrafingEnemy(150f, 250f, 30));
        wave.getEnemies().add(new ShootingEnemy(200f, 300f, 60));

        assertEquals(3, wave.getEnemies().size());
        assertFalse(wave.isComplete());

        wave.update(1.0f);

        // All enemies should still be present after update
        assertEquals(3, wave.getEnemies().size());
    }

    @Test
    public void testWaveCompletionIgnoresPowerupsAndProjectiles() {
        Wave wave = new Wave();
        wave.getPowerups().add(new PowerupEntity(100f, 200f, new ShieldEffect()));
        wave.getEnemyProjectiles().add(new EnemyProjectile(150f, 250f));

        // Wave is complete if no enemies, regardless of powerups/projectiles
        assertTrue(wave.isComplete());
    }

    @Test
    public void testWaveUpdateWithNoEntities() {
        Wave wave = new Wave();
        assertDoesNotThrow(() -> wave.update(1.0f));
        assertTrue(wave.isComplete());
    }

    @Test
    public void testWaveLargeUpdate() {
        Wave wave = new Wave();
        RegularEnemy enemy = new RegularEnemy(100f, 500f, 0);
        wave.getEnemies().add(enemy);

        float initialY = enemy.getY();
        wave.update(10.0f); // Large time step

        // Enemy should have moved significantly
        assertTrue(Math.abs(enemy.getY() - initialY) > 100f);
    }

    @Test
    public void testWaveZeroUpdate() {
        Wave wave = new Wave();
        RegularEnemy enemy = new RegularEnemy(100f, 200f, 0);
        float initialY = enemy.getY();
        wave.getEnemies().add(enemy);

        wave.update(0f);

        // With zero delta, position shouldn't change
        assertEquals(initialY, enemy.getY(), 0.001f);
    }
}
