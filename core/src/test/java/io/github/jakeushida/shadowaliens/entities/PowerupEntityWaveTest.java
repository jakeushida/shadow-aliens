package io.github.jakeushida.shadowaliens.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.github.jakeushida.shadowaliens.powerups.LifeEffect;
import io.github.jakeushida.shadowaliens.waves.Wave;

public class PowerupEntityWaveTest {

    @BeforeEach
    public void setUp() {
        io.github.jakeushida.shadowaliens.managers.GameSession.getInstance().reset();
    }

    @Test
    public void testPowerupAppliesLifeEffect() {
        PlayerShip player = new PlayerShip(0f,0f);
        int before = player.getLives();
        PowerupEntity powerup = new PowerupEntity(0f,0f, new LifeEffect());
        powerup.onCollision(player);
        assertEquals(before + 1, player.getLives());
    }

    @Test
    public void testWaveUpdateAndCompletion() {
        Wave wave = new Wave();
        RegularEnemy enemy = new RegularEnemy(0f,100f,0);
        wave.getEnemies().add(enemy);
        // update should run without error
        wave.update(0.1f);
        assertFalse(wave.isComplete());
        // clear enemies to simulate they were destroyed
        wave.getEnemies().clear();
        assertTrue(wave.isComplete());
    }
}
