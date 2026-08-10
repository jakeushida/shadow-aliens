package io.github.jakeushida.shadowaliens.integration;

import io.github.jakeushida.shadowaliens.entities.*;
import io.github.jakeushida.shadowaliens.managers.GameSession;
import io.github.jakeushida.shadowaliens.powerups.*;
import io.github.jakeushida.shadowaliens.waves.Wave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameFlowTest {

    @BeforeEach
    public void setUp() {
        GameSession.getInstance().reset();
    }

    @Test
    public void testCompleteWaveFlow() {
        Wave wave = new Wave();

        // Add enemies to wave
        wave.getEnemies().add(new RegularEnemy(100f, 500f, 0));
        wave.getEnemies().add(new StrafingEnemy(200f, 500f, 30));

        assertFalse(wave.isComplete());

        // Simulate destroying all enemies
        wave.getEnemies().clear();

        assertTrue(wave.isComplete());
    }

    @Test
    public void testPlayerLifecycleWithPowerups() {
        PlayerShip player = new PlayerShip(100f, 50f);

        // Initial state
        assertEquals(3, player.getLives());
        assertFalse(player.isShielded());

        // Apply shield powerup
        ShieldEffect shield = new ShieldEffect();
        player.setBuff(shield);
        assertTrue(player.isShielded());

        // Take damage with shield
        int livesBefore = player.getLives();
        player.onCollision(new RegularEnemy(100f, 50f, 0));
        assertEquals(livesBefore, player.getLives());

        // Remove shield
        player.setBuff(null);
        assertFalse(player.isShielded());

        // Take damage without shield
        player.onCollision(new RegularEnemy(100f, 50f, 0));
        assertEquals(livesBefore - 1, player.getLives());
    }

    @Test
    public void testCombatScenario() {
        PlayerShip player = new PlayerShip(100f, 50f);
        Wave wave = new Wave();

        // Add enemy
        RegularEnemy enemy = new RegularEnemy(100f, 100f, 0);
        wave.getEnemies().add(enemy);

        // Create player projectile at same position to ensure collision
        PlayerProjectile projectile = new PlayerProjectile(100f, 100f);

        // Verify they overlap
        assertTrue(projectile.getBoundingBox().overlaps(enemy.getBoundingBox()));

        // Simulate projectile hitting enemy
        if (projectile.getBoundingBox().overlaps(enemy.getBoundingBox())) {
            wave.getEnemies().remove(enemy);
        }

        assertTrue(wave.isComplete());
    }

    @Test
    public void testPowerupCollection() {
        PlayerShip player = new PlayerShip(100f, 100f);
        Wave wave = new Wave();

        // Add various powerups
        wave.getPowerups().add(new PowerupEntity(100f, 100f, new ShieldEffect()));
        wave.getPowerups().add(new PowerupEntity(150f, 150f, new LifeEffect()));

        assertEquals(2, wave.getPowerups().size());

        // Collect shield powerup
        PowerupEntity shieldPowerup = wave.getPowerups().get(0);
        shieldPowerup.onCollision(player);
        assertTrue(player.isShielded());

        // Collect life powerup
        int livesBefore = player.getLives();
        PowerupEntity lifePowerup = wave.getPowerups().get(1);
        lifePowerup.onCollision(player);
        assertEquals(livesBefore + 1, player.getLives());
    }

    @Test
    public void testTimeScaleEffects() {
        GameSession session = GameSession.getInstance();
        RegularEnemy enemy = new RegularEnemy(100f, 500f, 0);

        // Normal time scale
        session.setTimeScale(1f);
        float initialY = enemy.getY();
        enemy.update(1.0f * session.getTimeScale());
        float normalMove = initialY - enemy.getY();

        // Reset enemy
        enemy = new RegularEnemy(100f, 500f, 0);

        // Double time scale
        session.setTimeScale(2f);
        initialY = enemy.getY();
        enemy.update(1.0f * session.getTimeScale());
        float fastMove = initialY - enemy.getY();

        // Fast movement should be approximately double
        assertTrue(fastMove > normalMove);
    }

    @Test
    public void testInvincibilityMode() {
        GameSession session = GameSession.getInstance();
        PlayerShip player = new PlayerShip(100f, 50f);

        assertFalse(session.isInvincibilityMode());

        session.setInvincibilityMode(true);
        assertTrue(session.isInvincibilityMode());

        // In a real game, invincibility would be checked before applying damage
        // Here we just verify the flag works
        int livesBefore = player.getLives();

        // Game logic would check: if (!session.isInvincibilityMode()) { apply damage }
        if (!session.isInvincibilityMode()) {
            player.onCollision(new RegularEnemy(100f, 50f, 0));
        }

        // Lives should not change when invincible
        assertEquals(livesBefore, player.getLives());
    }

    @Test
    public void testMultipleBuffApplications() {
        PlayerShip player = new PlayerShip(100f, 50f);

        // Apply shield
        player.setBuff(new ShieldEffect());
        assertTrue(player.isShielded());
        assertEquals(1f, player.getEngineMultiplier());

        // Replace with engine boost
        player.setBuff(new EngineEffect());
        assertFalse(player.isShielded());
        assertEquals(1.5f, player.getEngineMultiplier());

        // Replace with cooldown
        player.setBuff(new CooldownEffect());
        assertEquals(1f, player.getEngineMultiplier());
        assertEquals(0.5f, player.getShotCooldownMultiplier());

        // Clear buff
        player.setBuff(null);
        assertEquals(1f, player.getShotCooldownMultiplier());
    }

    @Test
    public void testWaveProgression() {
        GameSession session = GameSession.getInstance();

        // Start at wave 1
        assertEquals(1, session.getCurrentWave());

        // Complete wave 1
        session.setCurrentWave(2);
        assertEquals(2, session.getCurrentWave());

        // Complete wave 2
        session.setCurrentWave(3);
        assertEquals(3, session.getCurrentWave());
    }

    @Test
    public void testGameOver() {
        PlayerShip player = new PlayerShip(100f, 50f);

        // Reduce lives to 0
        player.setLives(1);
        player.onCollision(new RegularEnemy(100f, 50f, 0));

        // Check if game should be over
        assertTrue(player.getLives() <= 0);
    }

    @Test
    public void testScoreTracking() {
        GameSession session = GameSession.getInstance();

        assertEquals(0, session.getCurrentScore());

        session.setCurrentScore(100);
        assertEquals(100, session.getCurrentScore());

        // Simulate scoring
        session.setCurrentScore(session.getCurrentScore() + 50);
        assertEquals(150, session.getCurrentScore());
    }

    @Test
    public void testComplexCombatScenario() {
        PlayerShip player = new PlayerShip(400f, 50f);
        Wave wave = new Wave();

        // Setup wave with multiple enemy types
        wave.getEnemies().add(new RegularEnemy(100f, 500f, 0));
        wave.getEnemies().add(new StrafingEnemy(200f, 500f, 30));
        wave.getEnemies().add(new ShootingEnemy(300f, 500f, 60));
        wave.getPowerups().add(new PowerupEntity(250f, 400f, new ShieldEffect()));

        // Initial state
        assertEquals(3, wave.getEnemies().size());
        assertEquals(1, wave.getPowerups().size());
        assertFalse(wave.isComplete());

        // Collect powerup
        PowerupEntity powerup = wave.getPowerups().get(0);
        powerup.onCollision(player);
        assertTrue(player.isShielded());

        // Destroy enemies one by one
        wave.getEnemies().remove(0);
        assertEquals(2, wave.getEnemies().size());
        assertFalse(wave.isComplete());

        wave.getEnemies().remove(0);
        assertEquals(1, wave.getEnemies().size());
        assertFalse(wave.isComplete());

        wave.getEnemies().remove(0);
        assertEquals(0, wave.getEnemies().size());
        assertTrue(wave.isComplete());
    }

    @Test
    public void testEntityMovementIntegration() {
        // Create various entities
        PlayerShip player = new PlayerShip(100f, 50f);
        RegularEnemy enemy1 = new RegularEnemy(150f, 400f, 0);
        StrafingEnemy enemy2 = new StrafingEnemy(200f, 450f, 30);
        PlayerProjectile projectile = new PlayerProjectile(110f, 100f);
        PowerupEntity powerup = new PowerupEntity(180f, 300f, new LifeEffect());

        // Update all entities
        float delta = 1.0f;
        player.setSpeedX(10f);
        player.update(delta);
        enemy1.update(delta);
        enemy2.update(delta);
        projectile.update(delta);
        powerup.update(delta);

        // Verify all entities moved
        assertEquals(110f, player.getX(), 0.001f);
        assertTrue(enemy1.getY() < 400f); // Moved down
        assertTrue(enemy2.getY() < 450f); // Moved down
        assertTrue(projectile.getY() > 100f); // Moved up
        assertTrue(powerup.getY() < 300f); // Moved down
    }

    @Test
    public void testExplosionIntegration() {
        Explosion explosion = new Explosion(100f, 200f, 3);

        assertFalse(explosion.isFinished());

        // Simulate multiple game frames
        for (int i = 0; i < 3; i++) {
            explosion.update(1.0f);
        }

        assertTrue(explosion.isFinished());
    }

    @Test
    public void testFullGameSession() {
        GameSession session = GameSession.getInstance();

        // Initial state
        assertEquals(0, session.getCurrentScore());
        assertEquals(3, session.getCurrentLives());
        assertEquals(1, session.getCurrentWave());
        assertEquals(1f, session.getTimeScale());

        // Play through wave 1
        session.setCurrentScore(250);
        session.setCurrentWave(2);

        // Take damage
        session.setCurrentLives(2);

        // Adjust time scale
        session.setTimeScale(1.5f);

        // Verify state
        assertEquals(250, session.getCurrentScore());
        assertEquals(2, session.getCurrentLives());
        assertEquals(2, session.getCurrentWave());
        assertEquals(1.5f, session.getTimeScale());

        // Reset for new game
        session.reset();
        assertEquals(0, session.getCurrentScore());
        assertEquals(3, session.getCurrentLives());
        assertEquals(1, session.getCurrentWave());
    }
}
